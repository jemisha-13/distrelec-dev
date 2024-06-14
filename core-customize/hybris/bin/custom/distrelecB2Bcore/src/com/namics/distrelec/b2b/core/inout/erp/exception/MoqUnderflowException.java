package com.namics.distrelec.b2b.core.inout.erp.exception;

import de.hybris.platform.order.exceptions.CalculationException;

public class MoqUnderflowException extends CalculationException {

    public MoqUnderflowException(final String message) {
        super(message);
    }

    public MoqUnderflowException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MoqUnderflowException(final Throwable cause) {
        super(cause);
    }
}
