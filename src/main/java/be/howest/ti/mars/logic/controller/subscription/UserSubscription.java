package be.howest.ti.mars.logic.controller.subscription;

public class UserSubscription {
    private final int id;
    private final String name;
    private final boolean unlimitedTravels;
    private final boolean unlimitedPackages;

    public UserSubscription(int id, String name, boolean unlimitedTravels, boolean unlimitedPackages) {
        this.id = id;
        this.name = name;
        this.unlimitedTravels = unlimitedTravels;
        this.unlimitedPackages = unlimitedPackages;
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
}
