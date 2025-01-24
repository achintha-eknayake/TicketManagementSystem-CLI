package org.tms.exception;
/**
 * NegativeValueException handles negative values of the user inputs
 **/
public class NegativeValueException extends RuntimeException {
    public NegativeValueException(String message) {
        super(message);
    }
}
