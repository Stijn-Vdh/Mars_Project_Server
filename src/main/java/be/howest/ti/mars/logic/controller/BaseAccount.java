package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.UserToken;

public class BaseAccount {
    private final UserToken userToken;
    private final String homeAddressEndpoint; //replace with endpoint class
    private final String password; // needs to be replaced with Password class which will contain hashed version
    private final String username; // needs to be unique
    private final String address; // just random info

    public BaseAccount(String homeAddressEndpoint, String password, String username, String address) {
        this(new UserToken(), homeAddressEndpoint, password, username, address);
    }

    public BaseAccount(UserToken userToken, String homeAddressEndpoint, String password, String username, String address) {
        this.userToken = userToken;
        this.homeAddressEndpoint = homeAddressEndpoint;
        this.password = password;
        this.username = username;
        this.address = address;
    }

    public UserToken getUserToken() {
        return userToken;
    }

    public String getHomeAddressEndpoint() {
        return homeAddressEndpoint;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }
}
