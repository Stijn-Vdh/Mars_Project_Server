package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.UserToken;

public class UserAccount extends BaseAccount {


    public UserAccount(String homeAddressEndpoint, String password, String username, String address) {
        super(homeAddressEndpoint, password, username, address);
    }

    public UserAccount(UserToken userToken, String homeAddressEndpoint, String password, String username, String address) {
        super(userToken, homeAddressEndpoint, password, username, address);
    }
}
