package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;

import java.util.Set;

public interface AccountsRepository {
    void addAccount(BaseAccount account);

    Set<BaseAccount> getAccounts();

    Set<UserAccount> getUserAccounts();

    Set<BusinessAccount> getBusinessAccounts();

    // User
    void addUser(UserAccount user);

    void changePassword(BaseAccount acc, String newPW);

    void setShareLocation(UserAccount user, boolean shareLocation);

    void setDisplayName(UserAccount acc, String displayName);

    // Business
    void addBusiness(BusinessAccount business);
}
