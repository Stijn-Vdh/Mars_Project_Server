package be.howest.ti.mars.logic.controller.subscription;

public class BusinessSubscriptionInfo {
    private final int id;
    private final String name;
    private final int smallPodsUsed;
    private final int largePodsUsed;

    public BusinessSubscriptionInfo(int id, String name, int smallPodsUsed, int largePodsUsed) {
        this.id = id;
        this.name = name;
        this.smallPodsUsed = smallPodsUsed;
        this.largePodsUsed = largePodsUsed;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSmallPodsUsed() {
        return smallPodsUsed;
    }

    public int getLargePodsUsed() {
        return largePodsUsed;
    }
}
