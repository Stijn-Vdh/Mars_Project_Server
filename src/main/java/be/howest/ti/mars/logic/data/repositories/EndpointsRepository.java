package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.Endpoint;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.data.repoInterfaces.EndpointsRepoInt;

import java.util.Set;

public class EndpointsRepository implements EndpointsRepoInt {
    @Override
    public Set<ShortEndpoint> getEndpoints() {
        return null;
    }

    @Override
    public void addEndpoint(String endpoint) {

    }

    @Override
    public Endpoint getEndpoint(int id) {
        return null;
    }
}
