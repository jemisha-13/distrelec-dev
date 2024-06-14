/*
 * Copyright 2000-2011 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.time;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility for time measurement.<br/>
 * <br/>
 * example:<br/>
 * private TimeMeasurementUtil timeMeasurement = new TimeMeasurementUtil();<br/>
 * long startTimeInMillis = System.currentTimeMillis();<br/>
 * String baseLoggingMessage = "Call getArtikelBestand() with artikelNummer12stellig[" + artikelNummer12stellig + "].";<br/>
 * LOG.info(timeMeasurement.increment(baseLoggingMessage, TimeMeasurementUtil.deltaToStartTime(startTimeInMillis)));
 * 
 * @author bhelfenberger, namics ag
 * @since MGB PIM 3.0
 * 
 */
public class TimeMeasurementUtil {
    private final AtomicLong count = new AtomicLong();
    private final AtomicLong totalMillis = new AtomicLong();

    /**
     * Returns the message.
     * 
     * @param timeInMillis
     *            timeInMillis
     * @return the message for logging.
     */
    public String increment(final long timeInMillis) {
        final StringBuilder messageBuilder = new StringBuilder();
        increment(messageBuilder, timeInMillis);
        return messageBuilder.toString();
    }

    /**
     * Returns the message.
     * 
     * @param baseMessage
     *            baseMessage
     * @param timeInMillis
     *            timeInMillis
     * @return the message for logging.
     */
    public String increment(final String baseMessage, final long timeInMillis) {
        final StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(baseMessage);
        messageBuilder.append(" ");
        increment(messageBuilder, timeInMillis);
        return messageBuilder.toString();
    }

    /**
     * Returns the message.
     * 
     * @param messageBuilder
     *            messageBuilder
     * @param timeInMillis
     *            timeInMillis
     */
    public void increment(final StringBuilder messageBuilder, final long timeInMillis) {
        messageBuilder.append("Timing -> current[");
        messageBuilder.append(timeInMillis);
        messageBuilder.append(" ms] | avg.[");
        messageBuilder.append(totalMillis.addAndGet(timeInMillis) / count.incrementAndGet());
        messageBuilder.append(" ms] | count[");
        messageBuilder.append(count.get());
        messageBuilder.append("].");
    }

    /**
     * Delta in millis to start time.
     * 
     * @param startTimeInMillis
     *            startTimeInMillis
     * @return delta
     */
    public static long deltaToStartTime(final long startTimeInMillis) {
        return getCurrentTimeInMillis() - startTimeInMillis;
    }

    /**
     * Get current time in millis.
     * 
     * @return current time in millis
     */
    public static long getCurrentTimeInMillis() {
        return (long) (System.nanoTime() / 1e6);
    }
}
