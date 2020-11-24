package be.howest.ti.mars.logic.controller.enums;

import java.util.Arrays;

public enum DeliveryType {
    SMALL, LARGE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static DeliveryType enumOf(String name) {
        return Arrays.stream(DeliveryType.values()).filter(m -> m.toString().equals(name)).findAny().orElse(null);
    }
}
