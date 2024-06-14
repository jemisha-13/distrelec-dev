package com.namics.distrelec.b2b.core.reconciliation.exception;

public class DistPriceReconciliationException extends Exception {

    public DistPriceReconciliationException() {
        super();
    }


    public DistPriceReconciliationException(final String message) {
        super(message);
    }


    public DistPriceReconciliationException(final String message, final Throwable cause) {
        super(message, cause);
    }

 
    public DistPriceReconciliationException(final Throwable cause) {
        super(cause);
    }

}