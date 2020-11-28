package be.howest.ti.mars.logic.data.repoInterfaces;

import be.howest.ti.mars.logic.controller.Delivery;
import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;

import java.util.List;

public interface DeliveriesRepoInt {
    List<Delivery> getDeliveries(BusinessAccount acc); // TODO: 21-11-2020 also not available in spec

    int addDelivery(Delivery delivery);

    Object getDeliveryInformation(BaseAccount acc, int id);
}
