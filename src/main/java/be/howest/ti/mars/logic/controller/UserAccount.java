package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.UserToken;

public class UserAccount extends BaseAccount {


    public UserAccount( String username, String password, String homeAddressEndpoint, String address) {
        super(homeAddressEndpoint, password, username, address);
    }

    public UserAccount(String username, String password, String homeAddressEndpoint, String address, UserToken userToken) {
        super(userToken, homeAddressEndpoint, password, username, address);
    }
}
