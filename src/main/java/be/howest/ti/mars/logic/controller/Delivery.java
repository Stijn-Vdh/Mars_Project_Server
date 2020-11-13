package be.howest.ti.mars.logic.controller;

import java.util.Date;
import java.util.Objects;

public class Delivery {

    private final String deliveryType;
    private final String source;
    private final String destination;
    private final Date date;

    public Delivery(String deliveryType, String source, String destination, Date date) {
        this.deliveryType = deliveryType;
        this.source = source;
        this.destination = destination;
        this.date = date;
    }
    
    public String getDeliveryType() {
        return deliveryType;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(deliveryType, delivery.deliveryType) &&
                Objects.equals(source, delivery.source) &&
                Objects.equals(destination, delivery.destination) &&
                Objects.equals(date, delivery.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deliveryType, source, destination, date);
    }
}
