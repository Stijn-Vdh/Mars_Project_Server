package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.controller.security.AccountToken;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public abstract class AuthController {
    protected AccountsRepository repo = Repositories.getAccountsRepo();
    protected Set<UserAccount> userAccounts = new HashSet<>();
    protected Set<BusinessAccount> businessAccounts = new HashSet<>();

    public Set<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public Set<BusinessAccount> getBusinessAccounts() {
        return businessAccounts;
    }

    public void createAccount(String name, String password, String address, int endpoint, boolean isBusiness) {

        Repositories.getEndpointsRepo().getEndpoint(endpoint);
        if (isBusiness) {
            BusinessAccount account = new BusinessAccount(name, password, endpoint, address);
            if (userAccounts.contains(new UserAccount(name)) || !businessAccounts.add(account)) { // username exists already
                throw new UsernameException("Username (" + name + ") is already taken");
            }
            repo.addBusiness(account);
        } else {
            UserAccount account = new UserAccount(name, password, endpoint, address);
            if (businessAccounts.contains(new BusinessAccount(name)) || !userAccounts.add(account)) { // username exists already
                throw new UsernameException("Username (" + name + ") is already taken");
            }
            repo.addUser(account);
        }
    }

    public byte[] login(String name, String password) {
        BaseAccount account = Stream.concat(
                userAccounts.stream(),
                businessAccounts.stream())
                .filter(acc -> acc.getUsername().equalsIgnoreCase(name) && acc.getPassword().equals(password))
                .findAny().orElseThrow(() -> new AuthenticationException("Credentials do not match!"));

        account.setAccountToken(new AccountToken(name)); // sets a new token, invalidates previous set token
        return account.getAccountToken().getToken();
    }

    public void logout(BaseAccount account) {
        account.setAccountToken(null);
    }


    public void changePassword(BaseAccount acc, String newPW) {
        repo.changePassword(acc, newPW);
        acc.setPassword(newPW);
    }
}
