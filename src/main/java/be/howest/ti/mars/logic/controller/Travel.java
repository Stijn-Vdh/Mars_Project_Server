package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.enums.PodType;

public class Travel {
    private final ShortEndpoint from;
    private final ShortEndpoint destination;
    private final PodType podType;
    private final String dateTime;

    public Travel(ShortEndpoint from, ShortEndpoint destination, PodType podType, String dateTime) {
        this.from = from;
        this.destination = destination;
        this.podType = podType;
        this.dateTime = dateTime;
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
}
