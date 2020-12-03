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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
public class BridgeTest {

    // config
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    // Parameters and headers
    private static final String DEFAULT_PLAYER_NAME = "alice";
    private static final String DEFAULT_PASS_WORD = "test";
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

    @BeforeEach
    void deploy(final VertxTestContext testContext) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(
                new WebServer(),
                testContext.succeedingThenComplete());
        webClient = WebClient.create(vertx);

        //add acc
        testRequest(testContext, HttpMethod.POST, "createAccount", null, loginAccountJson,
                200, IGNORE_BODY);
        //login acc
        login(testContext);
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

    private void testRequest(
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

    private void testRequest(
            final VertxTestContext testContext,
            HttpMethod method, String requestURI, String authorizationHeader,
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

    @Test
    void startWebServer(final VertxTestContext testContext) {
        testContext.completeNow();
    }

    @Test
    void getMessageReturnsAWelcomeMessage(final VertxTestContext testContext) {
        testRequest(testContext, HttpMethod.GET, "message", null, 200, body -> body.equals("\"SmellyEllie\""));
    }

    @Test
    void createAccount(final VertxTestContext testContext) {
        testRequest(testContext, HttpMethod.POST, "createAccount", null, createAccountJson,
                200, IGNORE_BODY);
    }

    @Test
    void createAccountInvalidBody(final VertxTestContext testContext) {
        testRequest(testContext, HttpMethod.POST, "createAccount", null, INVALID_BODY,
                400, IGNORE_BODY);
    }

    @Test
    void login(final VertxTestContext testContext) {
        testRequest(testContext, HttpMethod.POST, "login", null, loginBodyJson, 200, body -> {
            token = body;
            return true;
        });
    }
}

