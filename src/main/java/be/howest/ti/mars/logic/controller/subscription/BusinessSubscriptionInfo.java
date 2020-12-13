package be.howest.ti.mars.logic.controller.subscription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessSubscriptionInfo {
    private final int id;
    private final String name;
    private final int smallPodsUsed;
    private final int largePodsUsed;

    @JsonCreator
    public BusinessSubscriptionInfo(@JsonProperty("id") int id, @JsonProperty("name") String name,
                                    @JsonProperty("smallPodsUsed") int smallPodsUsed, @JsonProperty("largePodsUsed") int largePodsUsed) {
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
