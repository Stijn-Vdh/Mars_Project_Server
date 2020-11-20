package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.MarsH2Repository;
import be.howest.ti.mars.logic.data.MarsRepository;

public class UserAccount extends BaseAccount {
    MarsRepository repo = new MarsH2Repository();
    private boolean sharesLocation;
    private String displayName;

    public UserAccount(String username, String password, int homeAddressEndpoint, String address) {
        super(homeAddressEndpoint, password, username, address);
        sharesLocation = false;
        displayName = username;
    }

    public UserAccount(String name) {
        super(name);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setSharesLocation(boolean sharesLocation) {
        repo.setShareLocation(this, sharesLocation);
        this.sharesLocation = sharesLocation;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void addFriend(String friendName) {
        repo.beFriend(getUsername(), friendName);
    }

    public void removeFriend(String friendName) {
        repo.removeFriend(getUsername(), friendName);
    }

    public boolean isSharesLocation() {
        return sharesLocation;
    }

}
