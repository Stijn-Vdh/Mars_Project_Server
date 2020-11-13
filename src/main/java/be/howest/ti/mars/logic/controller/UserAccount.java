package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.UserToken;
import be.howest.ti.mars.logic.data.MarsRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UserAccount extends BaseAccount {
    MarsRepository repo = new MarsRepository();

    public UserAccount( String username, String password, int homeAddressEndpoint, String address) {
        super(homeAddressEndpoint, password, username, address);
    }

    public UserAccount(String username, String password, int homeAddressEndpoint, String address, UserToken userToken) {
        super(userToken, homeAddressEndpoint, password, username, address);
    }

    public List<UserAccount> getFriends(){
        return repo.getFriends(this);
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
