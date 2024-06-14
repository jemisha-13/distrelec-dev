/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.bomtool.exception;

/**
 * ImportToolException
 * 
 * @author Ajinkya, Distrelec
 * @since Distrelec 3.0
 * 
 */
public class BomToolFacadeException extends Exception {

    private int line;
    private ErrorSource errorSource;

    public BomToolFacadeException(final String message, final ErrorSource errorSource) {
        super(message);
        this.errorSource = errorSource;
    }

    public BomToolFacadeException(final String message, final int line, final ErrorSource errorSource) {
        super(message);
        this.line = line;
        this.errorSource = errorSource;
    }

    public BomToolFacadeException(final String message, final Throwable cause, final ErrorSource errorSource) {
        super(message, cause);
        this.errorSource = errorSource;
    }

    public BomToolFacadeException(final String message, final Throwable cause, final int line, final ErrorSource errorSource) {
        super(message, cause);
        this.line = line;
        this.errorSource = errorSource;
    }

    public BomToolFacadeException(final Throwable cause, final int line, final ErrorSource errorSource) {
        super(cause);
        this.line = line;
        this.errorSource = errorSource;
    }

    public BomToolFacadeException(final Throwable cause, final ErrorSource errorSource) {
        super(cause);
        this.errorSource = errorSource;
    }

    public int getLine() {
        return line;
    }

    public void setLine(final int line) {
        this.line = line;
    }

    public ErrorSource getErrorSource() {
        return errorSource;
    }

    public void setErrorSource(final ErrorSource errorSource) {
        this.errorSource = errorSource;
    }

    /**
     * What was the source of the error.
     * 
     * @author dathusir, Distrelec
     * @since Distrelec 3.0
     * 
     */
    public enum ErrorSource {
        TOO_MANY_LINES, FIELD_SEPARATOR_NOT_FOUND, COPY_PAST_SEPARATOR_NOT_FOUND, NO_DATA, FILE_EMPTY, IO_EXCEPTION, ARTICLE_NUMBER_FIELD, QUANTITY_FIELD, CUSTOMER_REFERENCE_FIELD;
    }
}
