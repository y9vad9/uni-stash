package com.mathpar.web.exceptions;

/**
 * Registration exception.
 *
 * @author ivan
 */
public class RegistrationException extends MathparException {
    public RegistrationException() {
    }

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationException(Throwable cause) {
        super(cause);
    }
}
