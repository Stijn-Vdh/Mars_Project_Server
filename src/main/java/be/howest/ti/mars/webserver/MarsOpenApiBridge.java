package be.howest.ti.mars.webserver;

import be.howest.ti.mars.logic.controller.BaseAccount;
import be.howest.ti.mars.logic.controller.MarsController;
import be.howest.ti.mars.logic.controller.security.UserToken;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.logging.Logger;

class MarsOpenApiBridge {
    private final MarsController controller;
    private static final Logger logger = Logger.getLogger(MarsOpenApiBridge.class.getName());
    public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";

    MarsOpenApiBridge() {
        this.controller = new MarsController();
    }

    public Object getMessage(RoutingContext ctx) {
        return controller.getMessage();
    }

    public Object createAccount(RoutingContext ctx) {
        logger.info("createAccount");

        JsonObject json = ctx.getBodyAsJson();
        controller.createUser(json.getString("name"),
                json.getString("password"),
                json.getString("homeAddress"),
                String.valueOf(json.getInteger("homeEndpointID")),
                json.getBoolean("businessAccount")
        );

        return null; // doesnt allow void
    }

    public Object login(RoutingContext ctx) {
        logger.info("login");

        JsonObject json = ctx.getBodyAsJson();
        return controller.login(json.getString("name"), json.getString("password"));
    }

    public Object logout(RoutingContext ctx) {
        controller.logout(getAccount(ctx));
        return null;
    }

    public Object viewSubscriptions(RoutingContext ctx) {
        return controller.getSubscriptions();
    }

    public boolean verifyAccountToken(RoutingContext ctx) {
        return getAccount(ctx) != null;
    }

    private BaseAccount getAccount(RoutingContext ctx) {
        UserToken userToken = Json.decodeValue(new JsonObject().put("token", getBearerToken(ctx)).toString(), UserToken.class);
        return controller.getAccounts().stream()
                .filter(acc -> userToken.equals(acc.getUserToken()))
                .findAny()
                .orElse(null);
    }

    public String getBearerToken(RoutingContext ctx) {
        String header = ctx.request().getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(AUTHORIZATION_TOKEN_PREFIX)) {
            return null;
        } else {
            return header.substring(AUTHORIZATION_TOKEN_PREFIX.length());
        }
    }
}
