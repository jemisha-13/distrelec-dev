package com.distrelec.solrfacetsearch.exception;

public class FusionIntegrationException extends RuntimeException {

    public FusionIntegrationException(final String message) {
        super(message);
    }

    public FusionIntegrationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
