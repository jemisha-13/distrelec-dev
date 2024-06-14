package com.namics.distrelec.b2b.core.aop;

import de.hybris.platform.tx.Transaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CxTransactionAspect {
    private static final Logger LOG = LoggerFactory.getLogger(CxTransactionAspect.class);

    @Around("@annotation(com.namics.distrelec.b2b.core.annotations.CxTransaction)")
    public Object transaction(ProceedingJoinPoint pjp) throws Throwable {
        Transaction tx = Transaction.current();
        tx.begin();
        boolean success = false;
        try {
            Object result = pjp.proceed();
            success = true;
            return result;
        } catch (Exception e) {
            LOG.error("Transaction will be rollbacked", e);
            throw e;
        } finally {
            LOG.debug("Transaction was {}", success);
            if (success) {
                tx.commit();
            } else {
                tx.rollback();
            }
        }
    }
}
