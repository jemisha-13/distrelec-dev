package com.namics.distrelec.b2b.core.bomtool;

public class BomToolServiceException extends RuntimeException {

    public BomToolServiceException() {
        super();
    }

    public BomToolServiceException(String message) {
        super(message);
    }

    public BomToolServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BomToolServiceException(Throwable cause) {
        super(cause);
    }

    protected BomToolServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
