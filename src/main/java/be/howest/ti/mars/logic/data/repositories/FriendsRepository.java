package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.data.repoInterfaces.FriendsRepoInt;

import java.util.Set;

public class FriendsRepository implements FriendsRepoInt {
    @Override
    public Set<UserAccount> getFriends(UserAccount user, Set<UserAccount> users) {
        return null;
    }

    @Override
    public void beFriend(String name, String friendName) {

    }

    @Override
    public void removeFriend(String name, String friendName) {

    }
}
