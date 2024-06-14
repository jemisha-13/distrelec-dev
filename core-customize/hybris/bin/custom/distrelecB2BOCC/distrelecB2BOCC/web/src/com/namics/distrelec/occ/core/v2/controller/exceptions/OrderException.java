package com.namics.distrelec.occ.core.v2.controller.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

public class OrderException extends WebserviceException {

    public static final String TYPE = "OrderError";

    public static final String SUBJECT = "order";

    public OrderException(String message) {
        super(message);
    }

    public OrderException(String message, String reason) {
        super(message, reason);
    }

    public OrderException(String message, String reason, Throwable cause) {
        super(message, reason, cause);
    }

    public OrderException(String message, String reason, String subject) {
        super(message, reason, subject);
    }

    public OrderException(String message, String reason, String subject, Throwable cause) {
        super(message, reason, subject, cause);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getSubjectType() {
        return SUBJECT;
    }
}
