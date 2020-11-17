package be.howest.ti.mars.logic.controller;

import java.util.Objects;

public class Endpoint {

    private final int id;
    private final String name;
    private final boolean available;
    private final String location; //prob replaced with class eventually
    private final boolean privateEndpoint;


    public Endpoint(int id, String name, boolean available, String location, boolean privateEndpoint) {
        this.id = id;
        this.name = name;
        this.available = available;
        this.location = location;
        this.privateEndpoint = privateEndpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return id == endpoint.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getLocation() {
        return location;
    }

    public boolean isPrivateEndpoint() {
        return privateEndpoint;
    }
}
