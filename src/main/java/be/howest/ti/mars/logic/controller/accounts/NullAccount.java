package be.howest.ti.mars.logic.controller.accounts;

public class NullAccount extends BaseAccount {

    public NullAccount(String name) {
        super(name);
        throw new IllegalArgumentException(" not used");
    }

    @Override
    public int getSubscriptionId() {
        return 0;
    }

    @Override
    public void setSubscriptionId(int subscriptionId) {
        //
    }
}
