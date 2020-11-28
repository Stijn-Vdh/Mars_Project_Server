package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.Delivery;
import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.data.repoInterfaces.DeliveriesRepoInt;

import java.util.List;

public class DeliveryRepository implements DeliveriesRepoInt {
    @Override
    public List<Delivery> getDeliveries(BusinessAccount acc) {
        return null;
    }

    @Override
    public int addDelivery(Delivery delivery) {
        return 0;
    }

    @Override
    public Object getDeliveryInformation(BaseAccount acc, int id) {
        return null;
    }
}
