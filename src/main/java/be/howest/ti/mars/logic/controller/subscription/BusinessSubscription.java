package be.howest.ti.mars.logic.controller.subscription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessSubscription {
    private final int id;
    private final String name;
    private final int smallPodsDaily;
    private final int largePodsDaily;
    private final int dedicatedPods;
    private final int priorityLevel;

    @JsonCreator
    public BusinessSubscription(@JsonProperty("id") int id, @JsonProperty("name") String name,
                                @JsonProperty("smallPodsDaily") int smallPodsDaily,
                                @JsonProperty("largePodsDaily") int largePodsDaily,
                                @JsonProperty("dedicatedPods") int dedicatedPods,
                                @JsonProperty("priorityLevel") int priorityLevel) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusinessSubscription that = (BusinessSubscription) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
