package com.namics.distrelec.b2b.core.service.vatEU.impl;

public class VatEUServiceException extends Exception {
    private String errorKey;
 
    public VatEUServiceException(String key, String message) {
        super(message);
        this.errorKey = key;
    }
 
    public String getErrorKey() {
        return errorKey;
    }
}