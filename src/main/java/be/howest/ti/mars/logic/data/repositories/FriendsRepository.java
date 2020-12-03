package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.UserAccount;

import java.util.Set;

public interface FriendsRepository {
    boolean friendExists(String name, UserAccount acc);

    Set<UserAccount> getFriends(UserAccount user);

    void beFriend(String name, String friendName);

    void removeFriend(String name, String friendName);
}
