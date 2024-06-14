/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * {@code JdbcConnectionAspect}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public class JdbcConnectionAspect {

    private static final Logger LOG = LogManager.getLogger(JdbcConnectionAspect.class);

    /**
     * Invokes a method and measures the execution time.
     * 
     * @param pjp
     * @return result of the invocation
     * @throws Throwable
     */
    public Object measure(final ProceedingJoinPoint pjp) throws Throwable {
        final long start = System.currentTimeMillis();
        final Object result = pjp.proceed();
        if (LOG.isInfoEnabled()) {
            LOG.info("Time to get new connection using method [{}.{}]: {}ms", pjp.getTarget().getClass().getSimpleName(), pjp.getSignature().getName(), (System.currentTimeMillis() - start));
            }
        return result;
    }
}
