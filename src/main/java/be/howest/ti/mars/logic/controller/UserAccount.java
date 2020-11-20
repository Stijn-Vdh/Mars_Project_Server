package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.MarsRepository;
import be.howest.ti.mars.logic.data.MarsH2Repository;

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

    public UserAccount addFriend(UserAccount friend) {
        repo.beFriend(getUsername(), friend.getUsername());
        return friend;
    }

    public UserAccount removeFriend(UserAccount friend) {
        repo.removeFriend(getUsername(), friend.getUsername());
        return friend;
    }

    public boolean isSharesLocation() {
        return sharesLocation;
    }

}
