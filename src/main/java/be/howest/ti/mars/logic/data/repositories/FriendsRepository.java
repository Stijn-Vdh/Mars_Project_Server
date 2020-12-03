package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.UserAccount;

import java.util.Set;

public interface FriendsRepository {
    Set<UserAccount> getFriends(UserAccount user, Set<UserAccount> users);

    void beFriend(String name, String friendName);

    void removeFriend(String name, String friendName);
}
