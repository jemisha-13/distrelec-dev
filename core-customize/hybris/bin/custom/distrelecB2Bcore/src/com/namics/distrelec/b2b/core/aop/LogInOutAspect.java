/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;

import com.namics.distrelec.b2b.core.annotations.LogInOut;
import com.namics.distrelec.b2b.core.util.DistXmlUtils;

public class LogInOutAspect {
    private static final Logger LOG = LogManager.getLogger(LogInOutAspect.class);

    @Around("@annotation(com.namics.distrelec.b2b.core.annotations.LogInOut)")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Level level = getLogLevel(joinPoint);
        Object returnValue = null;
        Exception exception = null;
        final long startTime = System.currentTimeMillis();
        try {
            returnValue = joinPoint.proceed();
        } catch (final Exception e) {
            exception = e;
            throw e;
        } finally {
            final long timeTaken = System.currentTimeMillis() - startTime;
            final Object[] argumentsToString = Stream.of(joinPoint.getArgs()).map(a -> DistXmlUtils.soapToString(a)).toArray();
            LOG.log(level, "Time Taken by {} with parameters: {} is {} ms. Result is: {}", joinPoint.getSignature(), Arrays.toString(argumentsToString),
                    timeTaken, exception == null ? DistXmlUtils.soapToString(returnValue) : exception);
        }
        return returnValue;
    }

    private Level getLogLevel(final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final StandardLevel standardLevel = method.getAnnotation(LogInOut.class).value();
        final Level level = Arrays.stream(Level.values()).filter(l -> l.getStandardLevel().equals(standardLevel)).findFirst().orElse(Level.DEBUG);
        return level;
    }

}
