package be.howest.ti.mars.logic.controller.exceptions;

public class UsernameIsTakenException extends RuntimeException {
    public UsernameIsTakenException(String message) {
        super(message);
    }
}
