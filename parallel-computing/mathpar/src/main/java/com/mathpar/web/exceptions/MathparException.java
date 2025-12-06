package com.mathpar.web.exceptions;

import java.util.Map;

/**
 * The most general Mathpar exception.
 *
 * @author ivan
 */
public class MathparException extends RuntimeException {
    private ErrorCode errorCode;
    private Map<String, Object> properties;

    public MathparException() {
    }

    public MathparException(String message) {
        super(message);
    }

    public MathparException(String message, Throwable cause) {
        super(message, cause);
    }

    public MathparException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }
}
