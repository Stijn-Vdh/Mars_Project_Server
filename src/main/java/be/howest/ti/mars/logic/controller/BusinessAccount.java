package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.UserToken;

public class BusinessAccount extends BaseAccount {
    public BusinessAccount(String username, String password, int homeAddressEndpoint, String address, UserToken userToken) {
        super(userToken, homeAddressEndpoint, password, username, address);
    }

    public BusinessAccount(String name) {
        super(name);
    }
}
