package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.converters.Endpoint;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;

import java.util.Set;

public interface EndpointsRepository {
    Set<ShortEndpoint> getEndpoints();

    void addEndpoint(String endpoint);

    Endpoint getEndpoint(int id);

    ShortEndpoint getShortEndpoint(int id);

    boolean endpointExists(int id);

    void turnEndpointPrivate(int id);

    Set<Endpoint> getTravelEndpoints(UserAccount user);

    Set<ShortEndpoint> getPackageEndpoints();


}
