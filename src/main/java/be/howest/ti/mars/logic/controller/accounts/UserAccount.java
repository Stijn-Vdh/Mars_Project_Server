package be.howest.ti.mars.logic.controller.accounts;

import be.howest.ti.mars.logic.controller.MTTSController;
import be.howest.ti.mars.logic.data.Repositories;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class UserAccount extends BaseAccount {
    private static final String CHNL_TO_CLIENT_NOTIFICATION = "events.client.";
    private boolean sharesLocation;
    private String displayName;

    public UserAccount(String username, String password, int homeAddressEndpoint, String address) {
        super(username, password, address, homeAddressEndpoint);
        sharesLocation = false;
        displayName = username;
        subscriptionId = 0;
    }

    public UserAccount(String name, String password, String address, int homeAddressEndpoint, String displayName, boolean sharesLocation, int subscriptionId) {
        super(name, password, address, homeAddressEndpoint);
        this.sharesLocation = sharesLocation;
        this.displayName = displayName;
        this.subscriptionId = subscriptionId;
    }

    public UserAccount(String name) {
        super(name);
    }

   public void addPotentialFriend(UserAccount friend){
        Repositories.getFriendsRepo().beFriend(getUsername(), friend.getUsername(), true);
   }
   public void removePotentialFriend(UserAccount friend){
       Repositories.getFriendsRepo().removeFriend(getUsername(), friend.getUsername(), true);

   }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        repo.setDisplayName(this, displayName);
        this.displayName = displayName;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    @Override
    public void setSubscriptionId(int subscriptionId) {
        Repositories.getSubscriptionRepo().setUserSubscription(this, subscriptionId);
        this.subscriptionId = subscriptionId;
    }

    public void addFriend(String friendName) {
        Repositories.getFriendsRepo().beFriend(getUsername(), friendName, false);
    }

    public void removeFriend(String friendName) {
        Repositories.getFriendsRepo().removeFriend(getUsername(), friendName, false);
    }

    public boolean isSharesLocation() {
        return sharesLocation;
    }

    public void setSharesLocation(boolean sharesLocation) {
        repo.setShareLocation(this, sharesLocation);
        this.sharesLocation = sharesLocation;
    }

    public void sendNotification(Vertx vertx, String subDomain, JsonObject message) {
        if (accountToken != null) {
            vertx.eventBus().send(CHNL_TO_CLIENT_NOTIFICATION + accountToken.getTokenBase64() + '.' + subDomain, message);
        }
    }

    @Override
    public boolean equals(Object o) {  // sonar +__+
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Object getAccountInformation() {
        return new MTTSController().getUserAccountInformation(this);
    }

}
