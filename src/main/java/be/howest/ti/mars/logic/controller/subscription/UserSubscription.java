package be.howest.ti.mars.logic.controller.subscription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSubscription {
    private final int id;
    private final String name;
    private final boolean unlimitedTravels;
    private final boolean unlimitedPackages;
    private final float price;

    @JsonCreator
    public UserSubscription(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("unlimitedTravels") boolean unlimitedTravels, @JsonProperty("unlimitedPackages") boolean unlimitedPackages, @JsonProperty("price") float price) {
        this.id = id;
        this.name = name;
        this.unlimitedTravels = unlimitedTravels;
        this.unlimitedPackages = unlimitedPackages;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isUnlimitedTravels() {
        return unlimitedTravels;
    }

    public boolean isUnlimitedPackages() {
        return unlimitedPackages;
    }

    public float getPrice(){return price;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSubscription that = (UserSubscription) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
