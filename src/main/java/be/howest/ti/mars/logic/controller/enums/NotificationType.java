package be.howest.ti.mars.logic.controller.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {
    TRAVEL, PACKAGE;

    @JsonValue
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
