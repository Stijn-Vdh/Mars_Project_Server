package be.howest.ti.mars.logic.controller.enums;

public enum NotificationType {
    POD, PACKAGE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
