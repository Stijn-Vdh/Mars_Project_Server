package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.converters.Travel;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;

import java.util.List;

public interface TravelsRepository {
    List<Travel> getTravelHistory(UserAccount acc);

    int travel(UserAccount user, Travel travel);

    void cancelTravel(UserAccount user, int tripID);
}
