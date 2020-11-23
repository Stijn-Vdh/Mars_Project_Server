package be.howest.ti.mars.logic.controller.subscription;

public class BusinessSubscription {
    private final int id;
    private final String name;
    private final int smallPodsDaily;
    private final int largePodsDaily;
    private final int dedicatedPods;
    private final int priorityLevel;

    public BusinessSubscription(int id, String name, int smallPodsDaily, int largePodsDaily, int dedicatedPods, int priorityLevel) {
        this.id = id;
        this.name = name;
        this.smallPodsDaily = smallPodsDaily;
        this.largePodsDaily = largePodsDaily;
        this.dedicatedPods = dedicatedPods;
        this.priorityLevel = priorityLevel;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSmallPodsDaily() {
        return smallPodsDaily;
    }

    public int getLargePodsDaily() {
        return largePodsDaily;
    }

    public int getDedicatedPods() {
        return dedicatedPods;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }
}
