package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.data.repoInterfaces.FavoritesRepoInt;

import java.util.Set;

public class FavoritesRepository implements FavoritesRepoInt {
    @Override
    public Set<ShortEndpoint> getFavoriteEndpoints(BaseAccount acc) {
        return null;
    }

    @Override
    public void favoriteEndpoint(BaseAccount acc, int id) {

    }

    @Override
    public void unFavoriteEndpoint(BaseAccount user, int id) {

    }
}
