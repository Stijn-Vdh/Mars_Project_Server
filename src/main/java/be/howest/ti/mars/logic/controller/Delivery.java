package be.howest.ti.mars.logic.controller;
import java.util.Objects;

public class Delivery {

    private final String deliveryType;
    private final int sourceID;
    private final int destinationID;
    private final String date;

    public Delivery(String deliveryType, int source, int destination, String date) {
        this.deliveryType = deliveryType;
        this.sourceID = source;
        this.destinationID = destination;
        this.date = date;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public int getSourceID() {
        return sourceID;
    }

    public int getDestinationID() {
        return destinationID;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(deliveryType, delivery.deliveryType) &&
                Objects.equals(sourceID, delivery.sourceID) &&
                Objects.equals(destinationID, delivery.destinationID) &&
                Objects.equals(date, delivery.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deliveryType, sourceID, destinationID, date);
    }
}
