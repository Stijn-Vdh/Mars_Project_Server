package be.howest.ti.mars.logic.controller.accounts;

import be.howest.ti.mars.logic.controller.security.AccountToken;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public abstract class BaseAccount {
    protected static final AccountsRepository repo = Repositories.getAccountsRepo();
    private final String username; // needs to be unique
    @JsonIgnore
    protected AccountToken accountToken;
    @JsonIgnore
    protected int subscriptionId;
    private Integer homeEndpoint; //replace with endpoint class
    @JsonIgnore
    private String password; // needs to be replaced with Password class which will contain hashed version
    @JsonIgnore
    private String address; // just random info

    public BaseAccount(String username, String password, String address, int homeEndpoint) {
        this(null, homeEndpoint, password, username, address);
    }

    public BaseAccount(AccountToken accountToken, Integer homeEndpoint, String password, String username, String address) {
        this.accountToken = accountToken;
        this.homeEndpoint = homeEndpoint;
        this.password = password;
        this.username = username;
        this.address = address;
    }

    public BaseAccount(String name) {
        this(null, null, "", name, "");
    }

    public int getHomeEndpoint() {
        return homeEndpoint;
    }

    public void setHomeEndpoint(Integer homeEndpoint) {
        this.homeEndpoint = homeEndpoint;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public AccountToken getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(AccountToken accountToken) {
        this.accountToken = accountToken;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public abstract int getSubscriptionId();

    public abstract void setSubscriptionId(int subscriptionId);

    public abstract Object getAccountInformation();
}
