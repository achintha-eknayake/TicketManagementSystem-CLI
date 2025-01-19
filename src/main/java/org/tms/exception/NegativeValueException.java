package org.tms.exception;

public class NegativeValueException extends RuntimeException {
    public NegativeValueException(String message) {
        super(message);
    }
}
