/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.error;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ErrorHandler;

/**
 * {@code DefaultMessageQueueErrorHandler}
 * 
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public class DefaultMessageQueueErrorHandler implements ErrorHandler {

    private static final Logger LOG = LogManager.getLogger(DefaultMessageQueueErrorHandler.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.util.ErrorHandler#handleError(java.lang.Throwable)
     */
    @Override
    public void handleError(final Throwable th) {
        LOG.error("Exception thrown while handling queued message:", th);
    }
}
