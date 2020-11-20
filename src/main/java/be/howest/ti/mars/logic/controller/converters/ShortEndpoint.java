package be.howest.ti.mars.logic.controller.converters;

public class ShortEndpoint { //package could ve better name, these classes are just used for translating from db to json
    private final int id; //temp class
    private final String name;

    public ShortEndpoint(int id, String name) { // this class will be short until fully decided what this gonna contain
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShortEndpoint that = (ShortEndpoint) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

