package edu.ifmg.com.services.exceptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException() {}

    public DatabaseException(String message) {
        super(message);
    }
}
