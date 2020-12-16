package be.howest.ti.mars.logic.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Endpoint {

    private final int id;
    private final String name;
    private final boolean available;
    private final Coordinate coordinate;
    private final boolean privateEndpoint;


    public Endpoint(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("available")
            boolean available, @JsonProperty("coordinate") Coordinate coordinate, @JsonProperty("privateEndpoint") boolean privateEndpoint) {
        this.id = id;
        this.name = name;
        this.available = available;
        this.coordinate = coordinate;
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


    public boolean isPrivateEndpoint() {
        return privateEndpoint;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
