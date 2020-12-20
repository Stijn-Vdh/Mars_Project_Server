package be.howest.ti.mars.logic.controller.converters;

import be.howest.ti.mars.logic.controller.enums.PodType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Travel {
    private final int id;
    private final ShortEndpoint from;
    private final ShortEndpoint destination;
    private final PodType podType;
    private final String dateTime;
    private final int arrivalTime;

    @JsonCreator
    public Travel(@JsonProperty("id") int id, @JsonProperty("from") ShortEndpoint from, @JsonProperty("destination") ShortEndpoint destination,
                  @JsonProperty("podType") PodType podType, @JsonProperty("dateTime") String dateTime, @JsonProperty("arrivalTime") int arrivalTime) {
        this.id = id;
        this.from = from;
        this.destination = destination;
        this.podType = podType;
        this.dateTime = dateTime;
        this.arrivalTime = arrivalTime;
    }

    public int getId() {
        return id;
    }

    public ShortEndpoint getFrom() {
        return from;
    }

    public ShortEndpoint getDestination() {
        return destination;
    }

    public PodType getPodType() {
        return podType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public String toString() {
        return "Travel{" +
                "id=" + id +
                ", from=" + from +
                ", destination=" + destination +
                ", podType=" + podType +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}
