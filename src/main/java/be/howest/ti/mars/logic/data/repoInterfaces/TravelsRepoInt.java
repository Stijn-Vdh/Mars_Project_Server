package be.howest.ti.mars.logic.data.repoInterfaces;

import be.howest.ti.mars.logic.controller.Travel;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;

import java.util.List;

public interface TravelsRepoInt {
    List<Travel> getTravelHistory(UserAccount acc);

    int travel(UserAccount user, Travel travel);

    void cancelTravel(UserAccount user, int tripID);
}
