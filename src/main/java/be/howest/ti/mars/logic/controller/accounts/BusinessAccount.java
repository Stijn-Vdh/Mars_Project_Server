package be.howest.ti.mars.logic.controller.accounts;

public class BusinessAccount extends BaseAccount {

    public BusinessAccount(String username, String password, int homeAddressEndpoint, String address) {
        super(null, homeAddressEndpoint, password, username, address);
    }

    public BusinessAccount(String name) {
        super(name);
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        repo.setBusinessSubscription(this, subscriptionId);
        this.subscriptionId = subscriptionId;
    }
}
