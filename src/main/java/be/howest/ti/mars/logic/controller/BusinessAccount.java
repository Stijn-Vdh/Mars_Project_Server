package be.howest.ti.mars.logic.controller;

public class BusinessAccount extends BaseAccount {
    public BusinessAccount(String username, String password, int homeAddressEndpoint, String address) {
        super(null, homeAddressEndpoint, password, username, address);
    }

    public BusinessAccount(String name) {
        super(name);
    }
}
