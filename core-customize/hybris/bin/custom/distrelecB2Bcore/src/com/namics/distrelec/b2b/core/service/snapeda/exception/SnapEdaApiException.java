package com.namics.distrelec.b2b.core.service.snapeda.exception;

public class SnapEdaApiException extends RuntimeException {

    public SnapEdaApiException(Throwable cause) {
        super(cause);
    }

    public SnapEdaApiException(String message) {
        super(message);
    }

    public SnapEdaApiException(String message, Throwable cause) {
        super(message, cause);
    }

}
