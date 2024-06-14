/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.error;

/**
 * {@code DistMessageBrokerException}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @since Distrelec 6.1
 */
public class DistMessageBrokerException extends Exception {

    /**
     * Create a new instance of {@code DistMessageBrokerException}
     * 
     * @param message
     */
    public DistMessageBrokerException(final String message) {
        super(message);
    }

    /**
     * Create a new instance of {@code DistMessageBrokerException}
     * 
     * @param msg
     * @param throwable
     */
    public DistMessageBrokerException(final String msg, final Throwable throwable) {
        super(msg, throwable);
    }
}
