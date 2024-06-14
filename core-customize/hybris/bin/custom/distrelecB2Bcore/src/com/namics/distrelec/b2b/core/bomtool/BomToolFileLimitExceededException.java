package com.namics.distrelec.b2b.core.bomtool;

public class BomToolFileLimitExceededException extends BomToolServiceException {

    public BomToolFileLimitExceededException() {
        super();
    }

    public BomToolFileLimitExceededException(String message) {
        super(message);
    }

    public BomToolFileLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public BomToolFileLimitExceededException(Throwable cause) {
        super(cause);
    }

    protected BomToolFileLimitExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
