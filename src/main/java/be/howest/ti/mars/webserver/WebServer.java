package be.howest.ti.mars.webserver;

import be.howest.ti.mars.logic.controller.exceptions.*;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WebServer extends AbstractVerticle {
    private static final Logger LOGGER = Logger.getLogger(WebServer.class.getName());
    private static final Integer DB_WEB_CONSOLE_FALLBACK = 9000;
    private static final String OPEN_API_SPEC = "openapi-group-15.yaml";
    private final MarsOpenApiBridge bridge;

    public WebServer(MarsOpenApiBridge bridge) {
        this.bridge = bridge;
    }

    public WebServer() {
        this(new MarsOpenApiBridge());
    }

    @Override
    public void start(Promise<Void> promise) {
        MarsOpenApiBridge.setVertx(vertx); // there should be a better way
        ConfigRetriever.create(vertx).getConfig(ar -> {
            if (ar.failed()) {
                LOGGER.warning("Config not available");
            } else {
                JsonObject properties = ar.result();
                JsonObject dbProperties = properties.getJsonObject("db");
                configureDatabase(dbProperties);
                int port = properties.getJsonObject("http").getInteger("port");
                LOGGER.info(String.format("Starting web server on port %s ", port));

                configureOpenApiServer(promise, OPEN_API_SPEC, port);
            }
        });
        bridge.startDailyResetCompanyPods();
    }

    @Override
    public void stop() {
        MarsConnection.getInstance().cleanUp();
    }

    private void configureDatabase(JsonObject dbProps) {
        try {
            MarsConnection.configure(dbProps.getString("url"),
                    dbProps.getString("username"),
                    dbProps.getString("password"),
                    dbProps.getInteger("webconsole.port", DB_WEB_CONSOLE_FALLBACK));
            LOGGER.info("Database webconsole started on port: " + dbProps.getInteger("webconsole.port"));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "DB web console is unavailable", ex);
        }
    }

    private void configureOpenApiServer(Promise<Void> promise, String apiSpecification, int port) {
        LOGGER.info(() -> String.format("Starting webserver with spec %s", apiSpecification));
        OpenAPI3RouterFactory.create(vertx,
                apiSpecification,
                ar -> {
                    if (ar.succeeded()) {
                        LOGGER.info("Successfully loaded API specification");
                        vertx.createHttpServer()
                                .requestHandler(createRequestHandler(ar.result()))
                                .listen(port, x -> listen(promise, x));
                    } else {
                        LOGGER.log(Level.SEVERE, " Failed to load API specification", ar.cause());
                        LOGGER.info("Shutting down");
                        vertx.close();
                    }
                });
    }

    private void listen(Promise<Void> promise, AsyncResult<HttpServer> result) {
        if (result.succeeded()) {
            LOGGER.info(() -> String.format("Listening at port %d", result.result().actualPort()));
            promise.complete();
        } else {
            LOGGER.log(Level.SEVERE, "Web server failed to start listening", result.cause());
            LOGGER.info("Shutting down the crucial martian application!");
            vertx.close();
        }
    }

    private Router createRequestHandler(OpenAPI3RouterFactory factory) {
        final String token = "Token";
        // Log all incoming requests
        factory.addGlobalHandler(LoggerHandler.create(LoggerFormat.TINY));

        // all expected responses are JSON
        factory.addGlobalHandler(ctx -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.next();
        });

        // Allow Cross-Origin Resource Sharing (CORS) from all clients
        factory.addGlobalHandler(createCorsHandler());

        // Verify the user token for all secured operations
        factory.addSecuritySchemaScopeValidator(token, "User", this::verifyUserAccountToken);

        // Verify the business token for all secured operations
        factory.addSecuritySchemaScopeValidator(token, "Business", this::verifyBusinessAccountToken);

        // Both tokens are allowed
        factory.addSecurityHandler(token, this::verifyAccountToken);

        // Add all route handlers
        addRoutes(factory);

        // Build the router
        Router router = factory.getRouter();
        router.route("/events/*").handler(createSockJsHandler());
        router.route("/favicon.ico").handler(FaviconHandler.create());

        // Install general error handlers
        installGeneralErrorHandlers(router);

        return router;
    }

    private void addRoutes(OpenAPI3RouterFactory factory) {
        addRouteWithCtxFunction(factory, "getMessage", arg -> bridge.getMessage());
        addRouteWithCtxFunction(factory, "createAccount", bridge::createAccount);
        addRouteWithCtxFunction(factory, "login", bridge::login);
        addRouteWithCtxFunction(factory, "viewSubscriptions", bridge::viewSubscriptions);
        addRouteWithCtxFunction(factory, "logout", bridge::logout);
        addRouteWithCtxFunction(factory, "viewFriends", bridge::viewFriends);
        addRouteWithCtxFunction(factory, "addFriend", bridge::addFriend);
        addRouteWithCtxFunction(factory, "removeFriend", bridge::removeFriend);
        addRouteWithCtxFunction(factory, "sendPackage", bridge::sendPackage);
        addRouteWithCtxFunction(factory, "getEndpoints", arg -> bridge.getEndpoints());
        addRouteWithCtxFunction(factory, "buySubscription", bridge::buySubscription);
        addRouteWithCtxFunction(factory, "stopSubscription", bridge::stopSubscription);
        addRouteWithCtxFunction(factory, "viewSubscriptionInfo", bridge::viewSubscriptionInfo);
        addRouteWithCtxFunction(factory, "shareLocation", bridge::shareLocation);
        addRouteWithCtxFunction(factory, "stopSharingLocation", bridge::stopSharingLocation);
        addRouteWithCtxFunction(factory, "getEndpoint", bridge::getEndpoint);
        addRouteWithCtxFunction(factory, "report", bridge::addReport);
        addRouteWithCtxFunction(factory, "reportSections", arg -> bridge.getReportSections());
        addRouteWithCtxFunction(factory, "favoriteEndpoint", bridge::favoriteEndpoint);
        addRouteWithCtxFunction(factory, "unfavoriteEndpoint", bridge::unFavoriteEndpoint);
        addRouteWithCtxFunction(factory, "getAccountInformation", bridge::getAccountInformation);
        addRouteWithCtxFunction(factory, "travel", bridge::travel);
        addRouteWithCtxFunction(factory, "getTravelHistory", bridge::getTravelHistory);
        addRouteWithCtxFunction(factory, "cancelTrip", bridge::cancelTrip);
        addRouteWithCtxFunction(factory, "changeDisplayName", bridge::setDisplayName);
        addRouteWithCtxFunction(factory, "changePassword", bridge::changePassword);
        addRouteWithCtxFunction(factory, "getCurrentRouteInfo", bridge::getCurrentRouteInfo);
        addRouteWithCtxFunction(factory, "getDeliveries", bridge::getDeliveries);
        addRouteWithCtxFunction(factory, "getDeliveryInformation", bridge::getDeliveryInformation);
    }

    private void addRouteWithCtxFunction(OpenAPI3RouterFactory factory, String operationId, Function<RoutingContext, Object> bridgeFunction) {
        factory.addHandlerByOperationId(operationId, ctx -> handleResult(bridgeFunction.apply(ctx), ctx));
    }

    private void handleResult(Object result, RoutingContext ctx) {
        ctx.response().setStatusCode(200).end(Json.encodePrettily(result));
    }

    private void installGeneralErrorHandlers(Router router) {
        router.errorHandler(400, this::onBadRequest)
                .errorHandler(401, this::onUnAuthorised)
                .errorHandler(403, this::onForbidden)
                .errorHandler(404, this::onNotFound)
                .errorHandler(500, this::onInternalServerError);

        router.route().handler(ctx -> ctx.fail(404, new RuntimeException()));
    }

    private CorsHandler createCorsHandler() {
        return CorsHandler.create(".*.")
                .allowedHeader("x-requested-with")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("origin")
                .allowedHeader("Content-Type")
                .allowedHeader("accept")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PUT);
    }

    private void onBadRequest(RoutingContext ctx) {
        ValidationException ex = (ValidationException) ctx.failure();
        String cause = String.format("%s in %s", ex.getMessage(), ex.parameterName());
        LOGGER.info(() -> String.format("onBadRequest %s: %s", ctx.request().absoluteURI(), cause));
        replyWithFailure(ctx, 400, "Bad Request", cause);
    }

    private void onNotFound(RoutingContext ctx) {
        LOGGER.info(() -> String.format("onNotFound at %s", ctx.request().absoluteURI()));
        replyWithFailure(ctx, 404, "Not Found", ctx.failure().getMessage());
    }

    private void onUnAuthorised(RoutingContext ctx) {
        replyWithFailure(ctx, 401, "Unauthorised", null);
    }

    private void onForbidden(RoutingContext ctx) {
        replyWithFailure(ctx, 403, "Forbidden", null);
    }

    private void onInternalServerError(RoutingContext ctx) {
        try {
            throw (Exception) ctx.failure();
        } catch (UsernameException | AuthenticationException | EndpointException ex) {
            replyWithFailure(ctx, 402, ex.getMessage(), ex.getMessage());
        } catch (EntityNotFoundException ex) {
            replyWithFailure(ctx, 422, "Entity couldn't be found", ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, () -> String.format("onInternalServerError at %s", ctx.request().absoluteURI()));
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            replyWithFailure(ctx, 500, "Internal Server Error", null);
        }
    }

    private void replyWithFailure(RoutingContext ctx, int statusCode, String message, String cause) {
        JsonObject reply = context2json(ctx).put("status", statusCode).put("message", message);

        if (cause != null) {
            reply.put("cause", cause);
        }

        ctx.response().setStatusCode(statusCode).end(Json.encodePrettily(reply));
    }

    private JsonObject context2json(RoutingContext ctx) {
        JsonObject result = new JsonObject()
                .put("method", ctx.request().method())
                .put("url", ctx.request().absoluteURI());

        if (StringUtils.isNotEmpty(ctx.getBodyAsString()))
            result.put("body", ctx.getBodyAsString());

        return result;
    }

    private void verifyUserAccountToken(RoutingContext ctx) {
        verifyToken(ctx, bridge::isUserAccountToken);
    }

    private void verifyBusinessAccountToken(RoutingContext ctx) {
        verifyToken(ctx, bridge::verifyBusinessAccountToken);
    }

    private void verifyToken(RoutingContext ctx, Predicate<RoutingContext> check) {

        if (bridge.getBearerToken(ctx) == null) {
            ctx.fail(401); // Unauthorized  due to wrong or absent header format
        } else if (check.test(ctx)) {
            ctx.next();
        } else {
            ctx.fail(403); // forbidden
        }
    }

    private void verifyAccountToken(RoutingContext ctx) {

        if (bridge.getBearerToken(ctx) == null) {
            ctx.fail(401); // Unauthorized  due to wrong or absent header format
        } else if (bridge.isUserAccountToken(ctx) || bridge.verifyBusinessAccountToken(ctx)) {
            ctx.next();
        } else {
            ctx.fail(403); // forbidden
        }
    }

    private SockJSHandler createSockJsHandler() {
        final SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        final PermittedOptions inbound = new PermittedOptions().setAddressRegex("events\\..+");
        final SockJSBridgeOptions options = new SockJSBridgeOptions()
                .addInboundPermitted(inbound)
                .addOutboundPermitted(inbound);

        sockJSHandler.bridge(options);
        return sockJSHandler;
    }
}