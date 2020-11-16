package be.howest.ti.mars.logic.controller;

import java.util.Objects;

public class Subscription {
    private final int id;
    private final String name;
    private final int remainingSmallPods_thisDay;
    private final int remainingLargePods_thisDay;
    private final int amountOfDedicatedPods;

    public Subscription(int id, String name) {
        this(id, name, 0,0, 0);
    }

    public Subscription (int id, String name, int remainingSmallPods_thisDay, int remainingLargePods_thisDay, int amountOfDedicatedPods){
        this.id = id;
        this.name = name;
        this.remainingSmallPods_thisDay = remainingSmallPods_thisDay;
        this.remainingLargePods_thisDay = remainingLargePods_thisDay;
        this.amountOfDedicatedPods = amountOfDedicatedPods;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRemainingSmallPods_thisDay() {
        return remainingSmallPods_thisDay;
    }

    public int getRemainingLargePods_thisDay() {
        return remainingLargePods_thisDay;
    }

    public int getAmountOfDedicatedPods() {
        return amountOfDedicatedPods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
