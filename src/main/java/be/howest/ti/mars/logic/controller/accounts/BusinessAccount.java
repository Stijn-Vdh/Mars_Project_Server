package be.howest.ti.mars.logic.controller.accounts;

public class BusinessAccount extends BaseAccount {
    private int largePodsUsed;
    private int smallPodsUsed;

    public BusinessAccount(String username, String password, int homeAddressEndpoint, String address) {
        super(username, password, address, homeAddressEndpoint);
    }

    public BusinessAccount(String name) {
        super(name);
    }

    public BusinessAccount(String name, String password, String address, int endpointId, int subscriptionId, int smallPodsUsed, int largePodsUsed) {
        super(name, password, address, endpointId);
        this.subscriptionId = subscriptionId;
        this.smallPodsUsed = smallPodsUsed;
        this.largePodsUsed = largePodsUsed;
    }

    public int getLargePodsUsed() {
        return largePodsUsed;
    }

    public void setLargePodsUsed(int largePodsUsed) {
        this.largePodsUsed = largePodsUsed;
    }

    public int getSmallPodsUsed() {
        return smallPodsUsed;
    }

    public void setSmallPodsUsed(int smallPodsUsed) {
        this.smallPodsUsed = smallPodsUsed;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        repo.setBusinessSubscription(this, subscriptionId);
        this.subscriptionId = subscriptionId;
    }
}
