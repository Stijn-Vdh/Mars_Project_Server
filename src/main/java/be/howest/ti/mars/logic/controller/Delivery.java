package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;

public class Delivery {

    private final DeliveryType deliveryType;
    private final ShortEndpoint source;
    private final ShortEndpoint destination;
    private final String dateTime;
    private final String sender;

    public Delivery(DeliveryType deliveryType, ShortEndpoint source, ShortEndpoint destination, String dateTime, String sender) {
        this.deliveryType = deliveryType;
        this.source = source;
        this.destination = destination;
        this.dateTime = dateTime;
        this.sender = sender;
    }

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
