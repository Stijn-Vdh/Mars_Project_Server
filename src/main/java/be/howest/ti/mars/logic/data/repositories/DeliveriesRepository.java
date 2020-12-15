package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.Delivery;
import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;

import java.util.List;

public interface DeliveriesRepository {
    List<Delivery> getDeliveries(BusinessAccount acc);

    int addDelivery(Delivery delivery);

    Object getDeliveryInformation(BaseAccount acc, int id);
}
