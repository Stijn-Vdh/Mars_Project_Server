package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.Travel;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.data.repoInterfaces.TravelsRepoInt;

import java.util.List;

public class TravelsRepository implements TravelsRepoInt {
    @Override
    public List<Travel> getTravelHistory(UserAccount acc) {
        return null;
    }

    @Override
    public int travel(UserAccount user, Travel travel) {
        return 0;
    }

    @Override
    public void cancelTravel(UserAccount user, int tripID) {

    }
}
