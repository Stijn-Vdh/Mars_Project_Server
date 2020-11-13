package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.AccountToken;

import java.util.Objects;

public class BaseAccount {
    private AccountToken accountToken;
    private final int homeAddressEndpoint; //replace with endpoint class
    private final String password; // needs to be replaced with Password class which will contain hashed version
    private final String username; // needs to be unique
    private final String address; // just random info

    public BaseAccount(int homeAddressEndpoint, String password, String username, String address) {
        this(new AccountToken(username), homeAddressEndpoint, password, username, address);
    }

    public BaseAccount(AccountToken accountToken, int homeAddressEndpoint, String password, String username, String address) {
        this.accountToken = accountToken;
        this.homeAddressEndpoint = homeAddressEndpoint;
        this.password = password;
        this.username = username;
        this.address = address;
    }

    public BaseAccount(String name) {
        this(null,0,"",name,"");
    }

    public AccountToken getUserToken() {
        return accountToken;
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

    public void setUserToken(AccountToken accountToken) {
        this.accountToken = accountToken;
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
