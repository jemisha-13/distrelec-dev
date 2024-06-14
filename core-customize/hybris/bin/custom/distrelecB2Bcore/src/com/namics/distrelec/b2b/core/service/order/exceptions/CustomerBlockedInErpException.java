package com.namics.distrelec.b2b.core.service.order.exceptions;

public class CustomerBlockedInErpException extends RuntimeException {
    public CustomerBlockedInErpException(String message) {
        super(message);
    }
}
