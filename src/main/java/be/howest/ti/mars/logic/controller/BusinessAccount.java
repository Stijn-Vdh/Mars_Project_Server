package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.AccountToken;

public class BusinessAccount extends BaseAccount {
    public BusinessAccount(String username, String password, int homeAddressEndpoint, String address, AccountToken accountToken) {
        super(accountToken, homeAddressEndpoint, password, username, address);
    }

    public BusinessAccount(String name) {
        super(name);
    }
}
