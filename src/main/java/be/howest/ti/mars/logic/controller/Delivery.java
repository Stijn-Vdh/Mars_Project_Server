package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.enums.DeliveryType;

public class Delivery {
    private final int id;
    private final DeliveryType deliveryType;
    private final ShortEndpoint source;
    private final ShortEndpoint destination;
    private final String dateTime;
    private final String sender;

    public Delivery(int id,DeliveryType deliveryType, ShortEndpoint source, ShortEndpoint destination, String dateTime, String sender) {
        this.id = id;
        this.deliveryType = deliveryType;
        this.source = source;
        this.destination = destination;
        this.dateTime = dateTime;
        this.sender = sender;
    }

    public int getId(){return id;};

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public ShortEndpoint getSource() {
        return source;
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
