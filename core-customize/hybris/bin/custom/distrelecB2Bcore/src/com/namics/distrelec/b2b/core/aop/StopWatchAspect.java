/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.aop;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StopWatch;

/**
 * Aspect for logging methods which take up too much time.
 * 
 * @author mullmann, Namics AG
 * @author bhauser, Namics AG
 * @author ceberle, Namics AG
 * @since Namics Extensions 1.0
 */
@SuppressWarnings({ "PMD" })
public class StopWatchAspect {

    /** Name of the logger. */
    private String loggerName;

    /** Ref to the logger. */
    private Logger logger;

    /** if this threshold is reached, detailed information has to be written to the log. */
    private double thresholdValue = 1.0;

    /**
     * Aspect method to capture the data.
     * 
     * @param pjp
     *            the proceeding join point
     * @return the object
     * @throws Throwable
     *             if call fails
     */
    // BEGIN SUPPRESS WARNING
    public Object invoke(final ProceedingJoinPoint pjp) throws Throwable {
        // END SUPPRESS WARNING
        Object returnValue;
        final StopWatch clock = new StopWatch(pjp.toShortString());
        try {
            clock.start();
            returnValue = pjp.proceed();
        } finally {
            clock.stop();
            final String logMessage = buildLogMessage(pjp, clock);
            if (clock.getTotalTimeSeconds() > thresholdValue) {
                getLogger().warn(logMessage);
            } else {
                getLogger().debug(logMessage);
            }
        }
        return returnValue;
    }

    protected String buildLogMessage(final ProceedingJoinPoint pjp, final StopWatch clock) {
        final StringBuilder logMessage = new StringBuilder();
        try {
            logMessage.append(pjp.getStaticPart().getSignature().getDeclaringType().getName());
            logMessage.append(" - ");
            logMessage.append(clock.shortSummary());
            if (ArrayUtils.isNotEmpty(pjp.getArgs())) {
                appendArguments(pjp.getArgs(), logMessage);
            }
            // BEGIN SUPPRESS WARNING
        } catch (final Exception e) {
            // END SUPPRESS WARNING
            getLogger().error("Long runner detected, but logging caused an error!", e);
        }
        return logMessage.toString();
    }

    protected void appendArguments(final Object[] args, final StringBuilder logMessage) {
        logMessage.append(System.getProperty("line.separator"));
        for (final Object o : args) {
            logMessage.append(ReflectionToStringBuilder.reflectionToString(o, ToStringStyle.MULTI_LINE_STYLE)).append("\n");
        }
    }

    private Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(loggerName);
        }
        return logger;
    }

    public String getLoggerName() {
        return loggerName;
    }

    @Required
    public void setLoggerName(final String loggerName) {
        this.loggerName = loggerName;
    }

    public double getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(final double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

}
