package be.howest.ti.mars.logic.controller.accounts;

import be.howest.ti.mars.logic.controller.security.AccountToken;
import be.howest.ti.mars.logic.data.MarsH2Repository;
import be.howest.ti.mars.logic.data.MarsRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public abstract class BaseAccount {
    protected static final MarsRepository repo = new MarsH2Repository();
    protected AccountToken accountToken;
    private Integer homeAddressEndpoint; //replace with endpoint class
    private String password; // needs to be replaced with Password class which will contain hashed version
    private final String username; // needs to be unique
    private String address; // just random info
    protected int subscriptionId;

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

    public void setPassword(String password) {
        this.password = password;
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
