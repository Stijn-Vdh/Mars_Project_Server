package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.UserToken;

import java.util.Objects;

public class BaseAccount {
    private UserToken userToken;
    private final int homeAddressEndpoint; //replace with endpoint class
    private final String password; // needs to be replaced with Password class which will contain hashed version
    private final String username; // needs to be unique
    private final String address; // just random info
    private int subscriptionID;

    public BaseAccount(int homeAddressEndpoint, String password, String username, String address) {
        this(new UserToken(username), homeAddressEndpoint, password, username, address);
    }

    public BaseAccount(UserToken userToken, int homeAddressEndpoint, String password, String username, String address) {
        this.userToken = userToken;
        this.homeAddressEndpoint = homeAddressEndpoint;
        this.password = password;
        this.username = username;
        this.address = address;
    }

    public UserToken getUserToken() {
        return userToken;
    }

    public int getHomeAddressEndpoint() {
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

    public void setUserToken(UserToken userToken) {
        this.userToken = userToken;
    }

    public void setSubscriptionID(int id){
        this.subscriptionID = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseAccount that = (BaseAccount) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }



}
