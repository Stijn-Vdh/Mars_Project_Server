package be.howest.ti.mars.logic.controller.enums;

import java.util.Arrays;

public enum PodType { // SonarLint doesnt like lowercase enums, uppercase enums causes issues which the below methods fix, also no abstract enums :/
    STANDARD, LUXURY;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static PodType enumOf(String name) {
        return Arrays.stream(PodType.values()).filter(m -> m.toString().equals(name)).findAny().orElse(null);
    }
}

