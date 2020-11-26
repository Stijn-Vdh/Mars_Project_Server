package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.enums.PodType;

public class Travel {
    private final int id;
    private final ShortEndpoint from;
    private final ShortEndpoint destination;
    private final PodType podType;
    private final String dateTime;

    public Travel(int id,ShortEndpoint from, ShortEndpoint destination, PodType podType, String dateTime) {
        this.id = id;
        this.from = from;
        this.destination = destination;
        this.podType = podType;
        this.dateTime = dateTime;
    }

    public int getId(){return id;}

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
