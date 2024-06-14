package com.namics.distrelec.b2b.storefront.excepions;

import javax.servlet.ServletException;

public class DistrelecBadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DistrelecBadRequestException() {
    }

    public DistrelecBadRequestException(String message) {
        super(message);
    }

    public DistrelecBadRequestException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public DistrelecBadRequestException(Throwable rootCause) {
        super(rootCause);
    }

    public Throwable getRootCause() {
        return this.getCause();
    }
}
