package be.howest.ti.mars.logic.controller.converters;

import be.howest.ti.mars.logic.controller.Coordinate;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CoordinateEndpoint extends ShortEndpoint {

    private final Coordinate coordinate;

    public CoordinateEndpoint(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("coordinate") Coordinate coordinate) {
        super(id, name);
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
