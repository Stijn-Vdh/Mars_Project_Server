package be.howest.ti.mars.logic.controller;

import java.util.Objects;

public class Trip {
    private final int from;
    private final int destination;
    private final String podType;
    private final String date;

    public Trip(int from, int destination, String podType, String date) {
        this.from = from;
        this.destination = destination;
        this.podType = podType;
        this.date = date;
    }

    public int getFrom() {
        return from;
    }

    public int getDestination() {
        return destination;
    }

    public String getPodType() {
        return podType;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "from=" + from +
                ", destination=" + destination +
                ", podType='" + podType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return from == trip.from &&
                destination == trip.destination &&
                Objects.equals(podType, trip.podType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, destination, podType);
    }
}
