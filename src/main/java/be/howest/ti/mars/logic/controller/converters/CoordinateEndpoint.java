package be.howest.ti.mars.logic.controller.converters;

import be.howest.ti.mars.logic.controller.Coordinate;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CoordinateEndpoint extends ShortEndpoint {

    private final Coordinate coordinate;

    public CoordinateEndpoint(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("coordinate") Coordinate coordinate) {
        super(id, name);
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CoordinateEndpoint that = (CoordinateEndpoint) o;

        return Objects.equals(coordinate, that.coordinate);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (coordinate != null ? coordinate.hashCode() : 0);
        return result;
    }
}
