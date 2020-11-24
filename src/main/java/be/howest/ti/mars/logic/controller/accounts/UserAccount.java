package be.howest.ti.mars.logic.controller.accounts;

public class UserAccount extends BaseAccount {
    private boolean sharesLocation;
    private String displayName;

    public UserAccount(String username, String password, int homeAddressEndpoint, String address) {
        super(homeAddressEndpoint, password, username, address);
        sharesLocation = false;
        displayName = username;
        subscriptionId = 0;
    }

    public UserAccount(String name) {
        super(name);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setSharesLocation(boolean sharesLocation) {
        repo.setShareLocation(this, sharesLocation);
        this.sharesLocation = sharesLocation;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        repo.setUserSubscription(this, subscriptionId);
        this.subscriptionId = subscriptionId;
    }

    public void setDisplayName(String displayName) {
        repo.setDisplayName(this, displayName);
        this.displayName = displayName;
    }

    public void addFriend(String friendName) {
        repo.beFriend(getUsername(), friendName);
    }

    public void removeFriend(String friendName) {
        repo.removeFriend(getUsername(), friendName);
    }

    public boolean isSharesLocation() {
        return sharesLocation;
    }

}
