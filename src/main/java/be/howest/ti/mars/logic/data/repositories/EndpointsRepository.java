package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.Endpoint;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;

import java.util.Set;

public interface EndpointsRepository {
    Set<ShortEndpoint> getEndpoints();

    void addEndpoint(String endpoint);

    Endpoint getEndpoint(int id);

    ShortEndpoint getShortEndpoint(int id);
}
