package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.AccountToken;
import be.howest.ti.mars.logic.data.MarsRepository;
import java.util.List;

public class UserAccount extends BaseAccount {
    MarsRepository repo = new MarsRepository();

    public UserAccount( String username, String password, int homeAddressEndpoint, String address) {
        super(homeAddressEndpoint, password, username, address);
    }

    public UserAccount(String username, String password, int homeAddressEndpoint, String address, AccountToken accountToken) {
        super(accountToken, homeAddressEndpoint, password, username, address);
    }

    public List<UserAccount> getFriends(){
        return repo.getFriends(this);
    }

    public UserAccount(String name) {
        super(name);
    }

    public UserAccount addFriend(UserAccount friend){
        repo.beFriend(getUsername(), friend.getUsername());
        return friend;
    }

    public UserAccount removeFriend(UserAccount friend){
        repo.removeFriend(getUsername(), friend.getUsername());
        return friend;
    }
}
