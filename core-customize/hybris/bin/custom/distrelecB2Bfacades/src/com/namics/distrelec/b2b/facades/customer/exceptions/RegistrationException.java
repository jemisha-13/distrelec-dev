package com.namics.distrelec.b2b.facades.customer.exceptions;

public class RegistrationException extends RuntimeException {

    private final String messageCode;

    public RegistrationException(String messageCode) {
        this(messageCode, null);
    }

    public RegistrationException(String messageCode, Throwable t) {
        super(messageCode, t);
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

}
