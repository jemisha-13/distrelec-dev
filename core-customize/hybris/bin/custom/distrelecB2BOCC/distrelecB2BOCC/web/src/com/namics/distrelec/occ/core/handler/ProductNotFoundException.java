/**
 *
 */
package com.namics.distrelec.occ.core.handler;

/**
 * @author abhinayjadhav, Distrelec Ltd
 */

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super();
    }

    public ProductNotFoundException(final String message) {
        super(message);
    }

    public ProductNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProductNotFoundException(final Throwable cause) {
        super(cause);
    }

    protected ProductNotFoundException(final String message, final Throwable cause, final boolean enableSuppression,
                                       final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
