package be.howest.ti.mars.webserver;

import be.howest.ti.mars.logic.controller.MarsController;
import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.security.AccountToken;
import be.howest.ti.mars.logic.controller.security.SecureHash;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.logging.Logger;
import java.util.stream.Collectors;
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

    public Object sendPackage(RoutingContext ctx) { // TODO: 21-11-2020 add missing validation: only business can send large packages, etc , from != dest
        JsonObject json = ctx.getBodyAsJson();
        controller.createDelivery(json.getString("deliveryType"),
                json.getInteger("from"),
                json.getInteger("destination"),
                getAccount(ctx).getUsername()
        );
        return "Your pod is on route to your location.";
    }

    public Object login(RoutingContext ctx) {
        JsonObject json = ctx.getBodyAsJson();
        return controller.login(json.getString("name"), SecureHash.getHashEncoded(json.getString("password")));
    }

    public Object logout(RoutingContext ctx) {
        controller.logout(getAccount(ctx));
        return "Bye bye";
    }

    public Object getAccountInformation(RoutingContext ctx) {
        if (isUserAccountToken(ctx)) {
            return controller.getUserAccountInformation(getUserAccount(ctx));
        } else {
            return controller.getBusinessAccountInformation(getBusinessAccount(ctx));
        }
    }

    public Object viewFriends(RoutingContext ctx) {
        return controller.getRepo().getFriends(getUserAccount(ctx), controller.getUserAccounts())
                .stream()
                .map(UserAccount::getUsername)
                .collect(Collectors.toUnmodifiableList());
    }

    public Object addFriend(RoutingContext ctx) { // TODO: 21-11-2020 cant friend businesses
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
        if (isUserAccountToken(ctx)) {
            return controller.getRepo().getUserSubscriptions();
        } else {
            return controller.getRepo().getBusinessSubscriptions();
        }
    }

    public Object buySubscription(RoutingContext ctx) {
        int subscriptionId = ctx.getBodyAsJson().getInteger("subscriptionId");
        if (isUserAccountToken(ctx)) {
            getUserAccount(ctx).setSubscriptionId(subscriptionId);
        } else {
            getBusinessAccount(ctx).setSubscriptionId(subscriptionId);
        }
        return "Thank you for buying a subscription.";
    }

    public Object stopSubscription(RoutingContext ctx) {

        if (isUserAccountToken(ctx)) {
            getUserAccount(ctx).setSubscriptionId(0);
        } else {
            getBusinessAccount(ctx).setSubscriptionId(0);
        }
        return "We are sorry that you have discontinued your current subscription.";
    }

    public Object viewSubscriptionInfo(RoutingContext ctx) {
        return controller.getRepo().getBusinessSubscriptionInfo(getBusinessAccount(ctx));
    }

    public Object shareLocation(RoutingContext ctx) {

        getUserAccount(ctx).setSharesLocation(true);
        return "Now sharing location with friends.";
    }

    public Object stopSharingLocation(RoutingContext ctx) { // TODO: 21-11-2020 should we care if someone tries stopping his location sharing when it is already stopped ?
        getUserAccount(ctx).setSharesLocation(false);
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
        controller.favoriteEndpoint(getAccount(ctx), endpointID);
        return "Successfully favored this endpoint.";
    }

    public Object unFavoriteEndpoint(RoutingContext ctx) {
        int endpointID = Integer.parseInt(ctx.request().getParam("id"));
        controller.unFavoriteEndpoint(getAccount(ctx), endpointID);
        return "Successfully unfavored this endpoint.";
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

    public Object travel(RoutingContext ctx) {
        int from = ctx.getBodyAsJson().getInteger("from");
        int destination = ctx.getBodyAsJson().getInteger("destination");
        String podType = ctx.getBodyAsJson().getString("podType");
        controller.travel(getUserAccount(ctx), from, destination, podType);

        return "Your pod is on route to your location.";
    }

    public Object getTravelHistory(RoutingContext ctx) {
        return controller.getTravelHistory(getUserAccount(ctx));
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean isUserAccountToken(RoutingContext ctx) {
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
                .filter(acc -> accountToken.equals(acc.getAccountToken()))
                .findAny()
                .orElse(null);
    }

    private UserAccount getUserAccount(RoutingContext ctx) {
        AccountToken accountToken = Json.decodeValue(new JsonObject().put(TOKEN, getBearerToken(ctx)).toString(), AccountToken.class);
        return controller.getUserAccounts().stream()
                .filter(acc -> accountToken.equals(acc.getAccountToken()))
                .findAny()
                .orElse(null);
    }

    private BusinessAccount getBusinessAccount(RoutingContext ctx) {
        AccountToken accountToken = Json.decodeValue(new JsonObject().put(TOKEN, getBearerToken(ctx)).toString(), AccountToken.class);
        return controller.getBusinessAccounts().stream()
                .filter(acc -> accountToken.equals(acc.getAccountToken()))
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

    public Object ping(RoutingContext ctx) {
        return "pong";
    }
}
