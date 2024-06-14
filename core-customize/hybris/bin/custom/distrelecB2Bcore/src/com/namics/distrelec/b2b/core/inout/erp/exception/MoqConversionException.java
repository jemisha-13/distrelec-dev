package com.namics.distrelec.b2b.core.inout.erp.exception;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MoqConversionException extends ConversionException {
    private String code;
    private long moq;
    public MoqConversionException(final String exceptionMessage, final Throwable cause) {
        super(exceptionMessage, cause);
    }

    public MoqConversionException(final String exceptionMessage) {
        super(exceptionMessage);
    }
    public MoqConversionException(String code, Double moq) {
        super("Moq conversion exception: code=" + code + ", moq=" + moq);
        this.code = code;
        this.moq = moq.longValue();
    }

    public String getCode() {
        return code;
    }

    public long getMoq() {
        return moq;
    }
}
