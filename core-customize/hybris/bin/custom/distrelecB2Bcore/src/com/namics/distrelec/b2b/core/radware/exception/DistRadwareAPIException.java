package com.namics.distrelec.b2b.core.radware.exception;

public class DistRadwareAPIException extends Exception {

    public DistRadwareAPIException(final String message) {
        super(message);
    }

    public DistRadwareAPIException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
