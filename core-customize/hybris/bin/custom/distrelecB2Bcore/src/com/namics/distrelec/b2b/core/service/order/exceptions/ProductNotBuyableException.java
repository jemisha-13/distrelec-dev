package com.namics.distrelec.b2b.core.service.order.exceptions;

public class ProductNotBuyableException extends RuntimeException {
    String message;

    public ProductNotBuyableException(String message) {
        this.message = message;
    }
}
