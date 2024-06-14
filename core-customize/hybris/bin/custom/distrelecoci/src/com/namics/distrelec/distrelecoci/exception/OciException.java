/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.distrelecoci.exception;

/**
 * OciException
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public class OciException extends Exception {
    public static final int UNSPECIFIED_ERROR = -1;
    public static final int NOT_OCI_LOGIN = 1;
    public static final int NO_OUTBOUND_SECTION = 2;
    public static final int NO_HOOK_URL = 3;
    public static final int OUTBOUND_FIELDS_MISSING = 4;
    public static final int OCI_FIELD_MISSING_OR_NO_DATA = 6;
    public static final int NO_OCI_SESSION = 7;
    public static final int SRM_FIELD_MISSING = 8;
    public static final int LOGIN_FAILED = 9;
    private final int errorCode;

    public OciException() {
        this("Unspecified error.", -1);
    }

    public OciException(final String errorMessage) {
        this(errorMessage, -1);
    }

    public OciException(final String errorMessage, final Exception exception) {
        this(errorMessage, -1, exception);
    }

    public OciException(final String errorMessage, final int errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public OciException(final String errorMessage, final int errorCode, final Exception exception) {
        super(errorMessage, exception);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
