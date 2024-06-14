/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.util;

import org.apache.logging.log4j.Logger;
import org.slf4j.helpers.MessageFormatter;

/**
 * {@code DistLogUtils}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.3
 */
public class DistLogUtils {

    /**
     * Create a new instance of {@code DistLogUtils}
     */
    private DistLogUtils() {
        super();
    }

    /**
     * Log the parameterized error message using the #Logger, message pattern, the throwable and the list of arguments.
     * 
     * @param logger
     *            the #Logger to use
     * @param messagePattern
     *            the message pattern
     * @param th
     *            the throwable exception
     * @param args
     *            the list of arguments
     */
    public static void logError(final Logger logger, final String messagePattern, final Throwable th, final Object... args) {
        if (th == null) {
            logger.error(messagePattern, args);
        } else {
            String formattedMsg = MessageFormatter.arrayFormat(messagePattern, args).getMessage();
            logger.error(formattedMsg, th);
        }
    }

    /**
     * Log the parameterized warning message using the #Logger, message pattern, the throwable and the list of arguments.
     * 
     * @param logger
     *            the #Logger to use
     * @param messagePattern
     *            the message pattern
     * @param th
     *            the throwable exception
     * @param args
     *            the list of arguments
     */
    public static void logWarn(final Logger logger, final String messagePattern, final Throwable th, final Object... args) {
        if (th == null) {
            logger.warn(messagePattern, args);
        } else {
            String formattedMsg = MessageFormatter.format(messagePattern, args).getMessage();
            logger.warn(formattedMsg, th);
        }
    }
}
