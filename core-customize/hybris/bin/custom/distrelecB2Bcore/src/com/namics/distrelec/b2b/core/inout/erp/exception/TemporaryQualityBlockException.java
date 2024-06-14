package com.namics.distrelec.b2b.core.inout.erp.exception;

public class TemporaryQualityBlockException extends RuntimeException {

    public TemporaryQualityBlockException() {
        super();
    }

    public TemporaryQualityBlockException(String message) {
        super(message);
    }

    public TemporaryQualityBlockException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemporaryQualityBlockException(Throwable cause) {
        super(cause);
    }
}
