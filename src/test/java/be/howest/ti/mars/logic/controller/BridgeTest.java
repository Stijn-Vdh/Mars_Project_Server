package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.webserver.WebServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
public class BridgeTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(BridgeTest.class.getName());
    // i wrote my own chain method so that i could launch requests sync using these async methods
    // keys
    private static final String NAME = "name";
    private static final String DESTINATION = "destination";
    private static final String FROM = "from";
    private static final String DELIVERY_TYPE = "deliveryType";
    private static final String HOME_ADDRESS = "homeAddress";
    private static final String PASS_WORD = "password";
    private static final String BUSINESS_ACCOUNT = "businessAccount";
    // config
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    // Parameters and headers
    private static final String DEFAULT_USER_NAME = "alice";
    private static final String DEFAULT_BUSS_NAME = "compB";
    private static final String DEFAULT_PASS_WORD = "test";
    private static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";
    private static final String HOME_ENDPOINT_ID = "homeEndpointId";
    // BODIES
    private static final JsonObject createAccountJson = new JsonObject()
            .put(NAME, "henk")
            .put(PASS_WORD, "test")
            .put(BUSINESS_ACCOUNT, false)
            .put(HOME_ADDRESS, " ")
            .put(HOME_ENDPOINT_ID, 1);

    private static final JsonObject userAccountJson = new JsonObject()
            .put(NAME, DEFAULT_USER_NAME)
            .put(PASS_WORD, DEFAULT_PASS_WORD)
            .put(BUSINESS_ACCOUNT, false)
            .put(HOME_ADDRESS, " ")
            .put(HOME_ENDPOINT_ID, 1);

    private static final JsonObject businessAccountJson = new JsonObject()
            .put(NAME, DEFAULT_BUSS_NAME)
            .put(PASS_WORD, DEFAULT_PASS_WORD)
            .put(BUSINESS_ACCOUNT, true)
            .put(HOME_ADDRESS, " ")
            .put(HOME_ENDPOINT_ID, 2);

    private static final JsonObject loginBodyJson = new JsonObject()
            .put(NAME, DEFAULT_USER_NAME).put(PASS_WORD, DEFAULT_PASS_WORD);

    private static final JsonObject loginBusBodyJson = new JsonObject()
            .put(NAME, DEFAULT_BUSS_NAME).put(PASS_WORD, DEFAULT_PASS_WORD);

    private static final JsonObject validPackage = new JsonObject()
            .put(DELIVERY_TYPE, "small")
            .put(FROM, 1)
            .put(DESTINATION, 5);

    private static final JsonObject invalidRoutePackage = new JsonObject()
            .put(DELIVERY_TYPE, "small")
            .put(FROM, 1)
            .put(DESTINATION, 1);

    private static final JsonObject invalidPrivilegePackage = new JsonObject()
            .put(DELIVERY_TYPE, "large")
            .put(FROM, 5)
            .put(DESTINATION, 1);

    private static final JsonObject invalidEndpointIdPackage = new JsonObject()
            .put(DELIVERY_TYPE, "small")
            .put(FROM, 0)
            .put(DESTINATION, 1);

    // key List userAccountInformation
    private static final List<String> KEY_LIST_USER = Arrays.asList(NAME, HOME_ADDRESS, "homeEndpoint", "displayName", "shareLocation", "subscription", "friends", "travelHistory", "favouriteEndpoints");
    private static final List<String> KEY_LIST_BUSS = Arrays.asList(NAME, HOME_ADDRESS, "homeEndpoint", "subscription", "Current usage subscription", "favouriteEndpoints");


    // Response body validators
    private static final Predicate<String> IGNORE_BODY = body -> true;
    private static final Predicate<String> USERINFO_BODY = body -> {
        JsonObject jBody = new JsonObject(body);
        return KEY_LIST_USER.stream().allMatch(jBody::containsKey);
    };
    private static final Predicate<String> BUSS_INFO_BODY = body -> {
        JsonObject jBody = new JsonObject(body);
        return KEY_LIST_BUSS.stream().allMatch(jBody::containsKey);
    };
    private static final Predicate<String> FRIENDS_BODY_EMPTY = body -> {
        try {
            List<String> langList = objectMapper.readValue(body, new TypeReference<>() {
            });
            return langList.isEmpty();
        } catch (JsonProcessingException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException("Didnt receive an array of string");
        }
    };

    private static final Predicate<String> FRIENDS_BODY_NOT_EMPTY = Predicate.not(FRIENDS_BODY_EMPTY);
    // Random
    private static final JsonObject INVALID_BODY = new JsonObject().put("random", "data");
    private static final Runnable NO_END = () -> {
    };
    //tokens
    private static String userToken;
    private static String businessToken;
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

        //add user and login, add business and login
        createAccount(testContext, userAccountJson,
                () -> login(testContext,
                        () -> createAccount(testContext, businessAccountJson,
                                () -> loginBus(testContext, loginBusBodyJson, testContext::completeNow))));
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
                    System.out.println(response.bodyAsString());
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
                    System.out.println(response.bodyAsString());
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
                    System.out.println(response.bodyAsString());
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
                    System.out.println(response.bodyAsString());
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
            userToken = AUTHORIZATION_TOKEN_PREFIX + trimBody(body);
            return true;
        }, runnable);
    }

    private void loginBus(final VertxTestContext testContext, JsonObject loginBodyJson, Runnable runnable) {
        chain(testContext, HttpMethod.POST, "login", null, loginBodyJson, 200, body -> {
            businessToken = AUTHORIZATION_TOKEN_PREFIX + trimBody(body);
            return true;
        }, runnable);
    }

    @Test
    public void login(final VertxTestContext testContext) {
        login(testContext, testContext::completeNow);
    }

    @Test
    public void loginBus(final VertxTestContext testContext) {
        loginBus(testContext, loginBusBodyJson, testContext::completeNow);
    }

    @Test
    public void logoutInvalidToken(final VertxTestContext testContext) {
        chainEnd(testContext, HttpMethod.DELETE, "login", null, 401, IGNORE_BODY);
    }

    @Test
    public void logout(final VertxTestContext testContext) {
        chainEnd(testContext, HttpMethod.DELETE, "login", userToken, 200, IGNORE_BODY);
    }

    private void shareLocation(final VertxTestContext testContext, HttpMethod method, int code, Runnable chain) {
        chain(testContext, method, "shareLocation", userToken, code, IGNORE_BODY, chain);
    }

    @Test
    public void shareLocation(final VertxTestContext testContext) {
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
        chain(testContext, HttpMethod.POST, "changePassword", userToken, new JsonObject().put("newPassword", "jak"), 200, IGNORE_BODY,
                () -> login(testContext, new JsonObject().put(NAME, DEFAULT_USER_NAME).put(PASS_WORD, "jak"), testContext::completeNow)
        );
    }

    private void sendPackage(final VertxTestContext testContext, JsonObject body, int code, String token, Runnable chain) {
        chain(testContext, HttpMethod.POST, "sendPackage", token, body, code, IGNORE_BODY, chain);
    }

    @Test
    public void sendPackageAsUser(final VertxTestContext testContext) {
        sendPackage(testContext, validPackage, 200, userToken, testContext::completeNow);
    }

    @Test
    public void sendPackageAsBuss(final VertxTestContext testContext) {
        sendPackage(testContext, validPackage, 200, businessToken, testContext::completeNow);
    }

    @Test
    public void sendInvalidRoutePackage(final VertxTestContext testContext) {
        sendPackage(testContext, invalidRoutePackage, 402, userToken, testContext::completeNow);
    }

    @Test
    public void sendInvalidPrivilegePackage(final VertxTestContext testContext) {
        sendPackage(testContext, invalidPrivilegePackage, 402, userToken, testContext::completeNow);
    }

    @Test
    public void sendInvalidEndpointIdPackage(final VertxTestContext testContext) {
        sendPackage(testContext, invalidEndpointIdPackage, 402, businessToken, testContext::completeNow);
    }

    private void getAccountInformation(final VertxTestContext testContext, int code, String token, Predicate<String> isBody, Runnable chain) {
        chain(testContext, HttpMethod.GET, "accountInformation", token, code, isBody, chain);
    }

    @Test
    public void getUserAccountInformation(final VertxTestContext testContext) {
        getAccountInformation(testContext, 200, userToken, USERINFO_BODY, testContext::completeNow);
    }

    @Test
    public void getBusinessAccountInformation(final VertxTestContext testContext) {
        getAccountInformation(testContext, 200, businessToken, BUSS_INFO_BODY, testContext::completeNow);
    }

    private void getFriends(final VertxTestContext testContext, Predicate<String> isBody, Runnable chain) {
        chain(testContext, HttpMethod.GET, "friend", userToken, 200, isBody, chain);
    }

    @Test
    public void getFriends(final VertxTestContext testContext) {
        getFriends(testContext, FRIENDS_BODY_EMPTY, testContext::completeNow);
    }

    private void addFriend(final VertxTestContext testContext, int code, String friendName, Runnable chain) {
        chain(testContext, HttpMethod.POST, "friend/" + friendName, userToken, code, IGNORE_BODY, chain);
    }

    @Test
    public void addFriend(final VertxTestContext testContext) {
        createAccount(testContext, createAccountJson,
                () -> addFriend(testContext, 200, createAccountJson.getString(NAME),
                        () -> getFriends(testContext, FRIENDS_BODY_NOT_EMPTY, testContext::completeNow)
                ));
    }

    @Test
    public void addFriendInvalidName(final VertxTestContext testContext) { //dont use friendName with space or non URL allowed char it will shutdown the server.
        addFriend(testContext, 402, "NOT_EXIST", testContext::completeNow);
    }

    @Test
    public void addFriendWhichIsYou(final VertxTestContext testContext) {
        addFriend(testContext, 402, DEFAULT_USER_NAME, testContext::completeNow);
    }

    @Test

    public void addFriendWhichIsCompany(final VertxTestContext testContext) {
        addFriend(testContext, 402, DEFAULT_BUSS_NAME, testContext::completeNow);
    }

    @Test
    public void addFriendWhichIsAlreadyFriend(final VertxTestContext testContext) {
        createAccount(testContext, createAccountJson,
                () -> addFriend(testContext, 200, createAccountJson.getString(NAME),
                        () -> addFriend(testContext, 402, createAccountJson.getString(NAME), testContext::completeNow)
                ));
    }


}

