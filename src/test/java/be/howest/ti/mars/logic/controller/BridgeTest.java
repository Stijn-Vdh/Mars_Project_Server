package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import be.howest.ti.mars.webserver.WebServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
public class BridgeTest {

    // i wrote my own chain method so that i could launch requests sync using these async methods

    // config
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    // Parameters and headers
    private static final String DEFAULT_PLAYER_NAME = "alice";
    private static final String DEFAULT_PASS_WORD = "test";
    private static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";
    private static final MTTSController controller = new MTTSController();
    private static final AccountsRepository repo = Repositories.getAccountsRepo();
    private static final JsonObject createAccountJson = new JsonObject()
            .put("name", "henk")
            .put("password", "test")
            .put("businessAccount", false)
            .put("homeAddress", " ")
            .put("homeEndpointId", 1);

    private static final JsonObject loginAccountJson = new JsonObject()
            .put("name", DEFAULT_PLAYER_NAME)
            .put("password", DEFAULT_PASS_WORD)
            .put("businessAccount", false)
            .put("homeAddress", " ")
            .put("homeEndpointId", 1);
    private static final JsonObject loginBodyJson = new JsonObject()
            .put("name", DEFAULT_PLAYER_NAME).put("password", DEFAULT_PASS_WORD);
    // Response body validators
    private static final Predicate<String> IGNORE_BODY = body -> true;
    private static final JsonObject INVALID_BODY = new JsonObject().put("random", "data");
    private static String token;

    // utils
    private Vertx vertx;
    private WebClient webClient;

    private static String trimBody(String body) {
        return body.substring(1, body.length() - 1);
    }

    private void login(final VertxTestContext testContext, Runnable runnable) {
        login(testContext, loginBodyJson, runnable);
    }

    @BeforeEach
    void deploy(final VertxTestContext testContext) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new WebServer());
        webClient = WebClient.create(vertx);

        createAccount(testContext, loginAccountJson, () -> login(testContext, testContext::completeNow));
    }

    @AfterEach
    void close(final VertxTestContext testContext) {
        vertx.close(testContext.succeedingThenComplete());
        webClient.close();
    }

    private HttpRequest<Buffer> makeRequest(HttpMethod method, String requestURI, String authorizationHeader) {
        io.vertx.ext.web.client.HttpRequest<io.vertx.core.buffer.Buffer> request = webClient.request(method, PORT, HOST, "/api/" + requestURI);
        if (authorizationHeader != null) {
            request.putHeader(
                    HttpHeaders.AUTHORIZATION.toString(),
                    authorizationHeader
            );
        }
        return request;
    }

    private void chainEnd(
            final VertxTestContext testContext,
            HttpMethod method, String requestURI, String authorizationHeader, Object body,
            int expectedStatusCode, Predicate<String> isExpectedBody
    ) {
        // Do as expected:
        makeRequest(method, requestURI, authorizationHeader).sendJson(body, testContext.succeeding(response ->
                testContext.verify(() -> {
                    assertEquals(expectedStatusCode, response.statusCode());
                    assertTrue(isExpectedBody.test(response.bodyAsString()));
                    testContext.completeNow();
                })
        ));
    }

    private void chainEnd(final VertxTestContext testContext, HttpMethod method, String requestURI, String authorizationHeader,
                          int expectedStatusCode, Predicate<String> isExpectedBody
    ) {
        makeRequest(method, requestURI, authorizationHeader).send(testContext.succeeding(response ->
                testContext.verify(() -> {
                    assertEquals(expectedStatusCode, response.statusCode());
                    String body = response.bodyAsString();
                    assertTrue(
                            isExpectedBody.test(body),
                            () -> String.format("Unexpected body: %s", body)
                    );
                    testContext.completeNow();
                })
        ));

    }

    private void chain(final VertxTestContext testContext, HttpMethod method, String requestURI, String authorizationHeader,
                       int expectedStatusCode, Predicate<String> isExpectedBody, Runnable runnable
    ) {
        makeRequest(method, requestURI, authorizationHeader).send(testContext.succeeding(response ->
                testContext.verify(() -> {
                    assertEquals(expectedStatusCode, response.statusCode());
                    String body = response.bodyAsString();
                    assertTrue(
                            isExpectedBody.test(body),
                            () -> String.format("Unexpected body: %s", body)
                    );
                    runnable.run();
                })
        ));
    }

    private void chain(
            final VertxTestContext testContext,
            HttpMethod method, String requestURI, String authorizationHeader, Object body,
            int expectedStatusCode, Predicate<String> isExpectedBody, Runnable runnable
    ) {
        // Do as expected:
        makeRequest(method, requestURI, authorizationHeader).sendJson(body, testContext.succeeding(response ->
                testContext.verify(() -> {
                    assertEquals(expectedStatusCode, response.statusCode());
                    assertTrue(isExpectedBody.test(response.bodyAsString()));
                    runnable.run();
                })
        ));
    }

    @Test
    public void startWebServer(final VertxTestContext testContext) {
        testContext.completeNow();
    }

    @Test
    @Disabled
    //example of why it was needed, it goes before -> after -> middle -> actually after, chaining allows me to execute methods when the previous async completes instead of firing at same time
    public void getMessageReturnsAWelcomeMessage(final VertxTestContext testContext) {
        System.out.println("before");
        chain(testContext, HttpMethod.GET, "message", null, 200, body -> trimBody(body).equals("SmellyEllie"), () -> {
            System.out.println("middle");
            chain(testContext, HttpMethod.GET, "message", null, 200, body -> trimBody(body).equals("SmellyEllie"), () -> {
                System.out.println("actually after");
                testContext.completeNow();
            });
        });
        System.out.println("after");
    }

    private void createAccount(VertxTestContext testContext, JsonObject body, Runnable runnable) { // Reusability
        chain(testContext, HttpMethod.POST, "createAccount", null, body, 200, IGNORE_BODY, runnable);
    }

    @Test
    public void createAccount(final VertxTestContext testContext) {
        createAccount(testContext, createAccountJson, testContext::completeNow);
    }

    @Test
    public void createAccountInvalidBody(final VertxTestContext testContext) {
        chainEnd(testContext, HttpMethod.POST, "createAccount", null, INVALID_BODY,
                400, IGNORE_BODY);
    }

    private void login(final VertxTestContext testContext, JsonObject loginBodyJson, Runnable runnable) {
        chain(testContext, HttpMethod.POST, "login", null, loginBodyJson, 200, body -> {
            token = AUTHORIZATION_TOKEN_PREFIX + trimBody(body);
            return true;
        }, runnable);
    }

    @Test
    public void login(final VertxTestContext testContext) {
        login(testContext, testContext::completeNow);
    }

    @Test
    public void logoutInvalidToken(final VertxTestContext testContext) {
        chainEnd(testContext, HttpMethod.DELETE, "login", null, 401, IGNORE_BODY);
    }

    @Test
    public void logout(final VertxTestContext testContext) {
        chainEnd(testContext, HttpMethod.DELETE, "login", token, 200, IGNORE_BODY);
    }

    private void shareLocation(final VertxTestContext testContext, HttpMethod method, int code, Runnable chain) {
        chain(testContext, HttpMethod.POST, "shareLocation", token, code, IGNORE_BODY, chain);
    }

    @Test
    public void shareLocation(final VertxTestContext testContext) {
        System.out.println("share loc: " + token);
        shareLocation(testContext, HttpMethod.POST, 200, testContext::completeNow);
    }

    private void stopShareLocation(final VertxTestContext testContext, int code, Runnable chain) {
        shareLocation(testContext, HttpMethod.DELETE, code, chain);
    }

    @Test
    public void stopShareLocation(final VertxTestContext testContext) {
        shareLocation(testContext, HttpMethod.POST, 200, () -> stopShareLocation(testContext, 200, testContext::completeNow));
    }


    @Test
    @Disabled
    // decided against validating these as they are harmless
    public void stopShareLocationInvalid(final VertxTestContext testContext) {
        stopShareLocation(testContext, 402, testContext::completeNow);
    }

    @Test
    @Disabled
    public void shareLocationInvalid(final VertxTestContext testContext) {
        shareLocation(testContext, HttpMethod.POST, 200, () -> shareLocation(testContext, HttpMethod.POST, 402, testContext::completeNow));
    }

    @Test
    public void changePassword(final VertxTestContext testContext) {
        chain(testContext, HttpMethod.POST, "changePassword", token, new JsonObject().put("newPassword", "jak"), 200, IGNORE_BODY, () -> {
            login(testContext, loginBodyJson.put("password", "jak"), testContext::completeNow);
        });
    }



}

