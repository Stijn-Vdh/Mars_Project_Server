package be.howest.ti.mars.logic.controller.converters;

import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.enums.DeliveryType;

public class Delivery {
    private final int id;
    private final DeliveryType deliveryType;
    private final ShortEndpoint from;
    private final ShortEndpoint destination;
    private final String dateTime;
    private final String sender;

    public Delivery(int id, DeliveryType deliveryType, ShortEndpoint from, ShortEndpoint destination, String dateTime, String sender) {
        this.id = id;
        this.deliveryType = deliveryType;
        this.from = from;
        this.destination = destination;
        this.dateTime = dateTime;
        this.sender = sender;
    }

    public int getId() {
        return id;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public ShortEndpoint getFrom() {
        return from;
    }

    public ShortEndpoint getDestination() {
        return destination;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getSender() {
        return sender;
    }
}
