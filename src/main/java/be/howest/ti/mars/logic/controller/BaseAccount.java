package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.security.AccountToken;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public abstract class BaseAccount {
    private AccountToken accountToken;
    private Integer homeAddressEndpoint; //replace with endpoint class
    private final String password; // needs to be replaced with Password class which will contain hashed version
    private final String username; // needs to be unique
    private String address; // just random info

    public BaseAccount(int homeAddressEndpoint, String password, String username, String address) {
        this(null, homeAddressEndpoint, password, username, address);
    }

    public BaseAccount(AccountToken accountToken, Integer homeAddressEndpoint, String password, String username, String address) {
        this.accountToken = accountToken;
        this.homeAddressEndpoint = homeAddressEndpoint;
        this.password = password;
        this.username = username;
        this.address = address;
    }

    public BaseAccount(String name) {
        this(null, null, "", name, "");
    }

    public int getHomeAddressEndpoint() {
        return homeAddressEndpoint;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setAccountToken(AccountToken accountToken) {
        this.accountToken = accountToken;
    }
    @JsonIgnore
    public AccountToken getAccountToken() {
        return accountToken;
    }

    public void setHomeAddressEndpoint(Integer homeAddressEndpoint) {
        this.homeAddressEndpoint = homeAddressEndpoint;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseAccount that = (BaseAccount) o;
        return username.equalsIgnoreCase(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username.toLowerCase());
    }


}
