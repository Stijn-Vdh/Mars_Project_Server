package be.howest.ti.mars.logic.controller.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum PodType { // SonarLint doesnt like lowercase enums, uppercase enums causes issues which the below methods fix, also no abstract enums :/
    STANDARD, LUXURY;

    @JsonCreator
    public static PodType enumOf(String name) {
        return Arrays.stream(PodType.values()).filter(m -> m.toString().equals(name)).findAny().orElse(null);
    }

    @Override
    @JsonValue
    public String toString() {
        return super.toString().toLowerCase();
    }
}

