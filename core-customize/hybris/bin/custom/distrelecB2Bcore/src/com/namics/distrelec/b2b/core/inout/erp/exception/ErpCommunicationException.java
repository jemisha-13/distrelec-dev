/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.exception;

/**
 * {@code ErpCommunicationException}
 * 
 * @author daebersanif, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 2.0
 */
public class ErpCommunicationException extends RuntimeException {

    /**
     * Constructs a new {@code ErpCommunicationException} runtime exception with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public ErpCommunicationException() {
        super();
    }

    /**
     * Constructs a new {@code ErpCommunicationException} runtime exception with the specified detail message. The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message
     *            the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public ErpCommunicationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ErpCommunicationException} runtime exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in this runtime exception's
     * detail message.
     *
     * @param message
     *            the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     */
    public ErpCommunicationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code ErpCommunicationException} runtime exception with the specified cause and a detail message of
     * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail message of <tt>cause</tt>). This
     * constructor is useful for runtime exceptions that are little more than wrappers for other throwables.
     *
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     */
    public ErpCommunicationException(Throwable cause) {
        super(cause);
    }
}