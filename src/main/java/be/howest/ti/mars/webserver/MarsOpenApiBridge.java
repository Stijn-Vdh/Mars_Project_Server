package be.howest.ti.mars.webserver;

import be.howest.ti.mars.logic.controller.BaseAccount;
import be.howest.ti.mars.logic.controller.BusinessAccount;
import be.howest.ti.mars.logic.controller.MarsController;
import be.howest.ti.mars.logic.controller.UserAccount;
import be.howest.ti.mars.logic.controller.security.AccountToken;
import be.howest.ti.mars.logic.controller.security.SecureHash;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.logging.Logger;
import java.util.stream.Stream;

class MarsOpenApiBridge {
    private final MarsController controller;
    private static final Logger logger = Logger.getLogger(MarsOpenApiBridge.class.getName());
    public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN = "token";
    MarsOpenApiBridge() {
        this.controller = new MarsController();
    }

    public Object getMessage(RoutingContext ctx) {
        return controller.getMessage();
    }

    public Object createAccount(RoutingContext ctx) {
        logger.info("createAccount");

        JsonObject json = ctx.getBodyAsJson();
        controller.createAccount(
                json.getString("name"),
                SecureHash.getHashEncoded(json.getString("password")),
                json.getString("homeAddress"),
                json.getInteger("homeEndpointID"),
                json.getBoolean("businessAccount")
        );

        return "Successfully created an account";
    }

    public Object sendPackage(RoutingContext ctx) {
        logger.info("addDelivery");

        JsonObject json = ctx.getBodyAsJson();
        controller.createDelivery(json.getString("deliveryType"),
                json.getInteger("from"),
                json.getInteger("destination"),
                json.getString("date")
        );
        return null;
    }

    public Object login(RoutingContext ctx) {
        logger.info("login");

        JsonObject json = ctx.getBodyAsJson();
        return controller.login(json.getString("name"), SecureHash.getHashEncoded(json.getString("password")));
    }

    public Object logout(RoutingContext ctx) {
        controller.logout(getAccount(ctx));
        return "Bye bye";
    }

    public Object getAccountInformation(RoutingContext ctx) {
        return controller.getAccountInformation(getAccount(ctx), verifyUserAccountToken(ctx));
    }

    public Object viewFriends(RoutingContext ctx) {
        UserAccount user = getUserAccount(ctx);
        return controller.getFriends(user);
    }

    public Object addFriend(RoutingContext ctx) {
        UserAccount user = getUserAccount(ctx);
        String friendName = ctx.request().getParam("fName");
        return controller.addFriend(user, friendName);
    }

    public Object removeFriend(RoutingContext ctx) {
        UserAccount user = getUserAccount(ctx);
        String friendName = ctx.request().getParam("fName");
        return controller.removeFriend(user, friendName);
    }

    public Object viewSubscriptions(RoutingContext ctx) {
        return controller.getSubscriptions();
    }

    public Object buySubscription(RoutingContext ctx) {
        JsonObject json = ctx.getBodyAsJson();
        String subscriptionName = json.getString("subscriptionName");
        return controller.buySubscription(getAccount(ctx), subscriptionName, verifyUserAccountToken(ctx));
    }

    public Object stopSubscription(RoutingContext ctx) {
        return controller.stopSubscription(getAccount(ctx), verifyUserAccountToken(ctx));
    }

    public Object viewSubscriptionInfo(RoutingContext ctx) {
        return controller.viewSubscriptionInfo(getBusinessAccount(ctx));
    }

    public Object shareLocation(RoutingContext ctx) {
        controller.shareLocation(getUserAccount(ctx));
        return "Now sharing location with friends.";
    }

    public Object stopSharingLocation(RoutingContext ctx) {
        controller.stopSharingLocation(getUserAccount(ctx));
        return "Not sharing location anymore with friends.";
    }

    public Object getEndpoints(RoutingContext ctx) {
        return controller.getRepo().getEndpoints();
    }

    public Object getEndpoint(RoutingContext ctx) {
        return controller.getRepo().getEndpoint(Integer.parseInt(ctx.request().getParam("id")));
    }

    public Object favoriteEndpoint(RoutingContext ctx) {
        int endpointID = Integer.parseInt(ctx.request().getParam("id"));
        controller.favoriteEndpoint(getAccount(ctx), endpointID, verifyUserAccountToken(ctx));
        return null;
    }

    public Object unfavoriteEndpoint(RoutingContext ctx) {
        int endpointID = Integer.parseInt(ctx.request().getParam("id"));
        controller.unFavoriteEndpoint(getAccount(ctx), endpointID, verifyUserAccountToken(ctx));
        return null;
    }


    //------------------------------------------------------------------------------------------------------------------

    public boolean verifyUserAccountToken(RoutingContext ctx) {
        return getUserAccount(ctx) != null;
    }

    public boolean verifyBusinessAccountToken(RoutingContext ctx) {
        return getBusinessAccount(ctx) != null;
    }

    private BaseAccount getAccount(RoutingContext ctx) {
        AccountToken accountToken = Json.decodeValue(new JsonObject().put(TOKEN, getBearerToken(ctx)).toString(), AccountToken.class);
        return Stream.concat(
                controller.getUserAccounts().stream(),
                controller.getBusinessAccounts().stream())
                .filter(acc -> accountToken.equals(acc.getUserToken()))
                .findAny()
                .orElse(null);
    }

    private UserAccount getUserAccount(RoutingContext ctx) {
        AccountToken accountToken = Json.decodeValue(new JsonObject().put(TOKEN, getBearerToken(ctx)).toString(), AccountToken.class);
        return controller.getUserAccounts().stream()
                .filter(acc -> accountToken.equals(acc.getUserToken()))
                .findAny()
                .orElse(null);
    }

    private BusinessAccount getBusinessAccount(RoutingContext ctx) {
        AccountToken accountToken = Json.decodeValue(new JsonObject().put(TOKEN, getBearerToken(ctx)).toString(), AccountToken.class);
        return controller.getBusinessAccounts().stream()
                .filter(acc -> accountToken.equals(acc.getUserToken()))
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

    public Object addReport(RoutingContext ctx) {
        JsonObject json = ctx.getBodyAsJson();
        controller.getRepo().addReport(
                getAccount(ctx),
                json.getString("section"),
                json.getString("description")
        );
        return "Report has been received.";
    }

    public Object getReportSections(RoutingContext ctx) {
        return controller.getRepo().getReportSections();
    }
}
