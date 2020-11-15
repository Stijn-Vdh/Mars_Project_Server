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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        controller.createAccount(json.getString("name"),
                SecureHash.getHashEncoded(json.getString("password")),
                json.getString("homeAddress"),
                json.getInteger("homeEndpointID"),
                json.getBoolean("businessAccount")
        );

        return null; // doesnt allow void
    }

    public Object sendPackage(RoutingContext ctx){
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
        return null;
    }

    public Object viewFriends(RoutingContext ctx) {
        UserAccount user = (UserAccount) getAccount(ctx);
        return user.getFriends().stream()
                .map(BaseAccount::getUsername)
                .collect(Collectors.toList());
    }

    public Object addFriend(RoutingContext ctx) {
        UserAccount user = (UserAccount) getAccount(ctx);
        String friendName = ctx.request().getParam("fName");
        return controller.addFriend(user, friendName);
    }

    public Object removeFriend(RoutingContext ctx) {
        UserAccount user = (UserAccount) getAccount(ctx);
        String friendName = ctx.request().getParam("fName");
        return controller.removeFriend(user, friendName);
    }

    public Object viewSubscriptions(RoutingContext ctx) {
        return controller.getSubscriptions();
    }

    public boolean verifyUserAccountToken(RoutingContext ctx) {
        return getUserAccount(ctx) != null;
    }

    public boolean verifyBusinessAccountToken(RoutingContext ctx) {
        return getBusinessAccount(ctx) != null;
    }

    private BaseAccount getAccount(RoutingContext ctx) {
        AccountToken accountToken = Json.decodeValue(new JsonObject().put("token", getBearerToken(ctx)).toString(), AccountToken.class);
        return Stream.concat(
                controller.getUserAccounts().stream(),
                controller.getBusinessAccounts().stream())
                .filter(acc -> accountToken.equals(acc.getUserToken()))
                .findAny()
                .orElse(null);
    }

    private UserAccount getUserAccount(RoutingContext ctx) {
        AccountToken accountToken = Json.decodeValue(new JsonObject().put("token", getBearerToken(ctx)).toString(), AccountToken.class);
        return controller.getUserAccounts().stream()
                .filter(acc -> accountToken.equals(acc.getUserToken()))
                .findAny()
                .orElse(null);
    }
    private BusinessAccount getBusinessAccount(RoutingContext ctx) {
        AccountToken accountToken = Json.decodeValue(new JsonObject().put("token", getBearerToken(ctx)).toString(), AccountToken.class);
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

}
