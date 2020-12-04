package be.howest.ti.mars.logic.controller.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Friend {
    private int id;
    private String name;

    @JsonCreator
    public Friend(@JsonProperty("id") int id, @JsonProperty("name")String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
