package com.namics.distrelec.occ.core.v2.aop;

import javax.ws.rs.ForbiddenException;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;

@Aspect
@Component
public class QuotationRestrictionAspect {

    @Autowired
    private DistrelecStoreSessionFacade distrelecStoreSessionFacade;

    @Before("@annotation(com.namics.distrelec.occ.core.v2.annotations.QuotationRestriction)")
    public void checkIsAllowed() {
        if (!distrelecStoreSessionFacade.areQuotationsEnabledForCurrentBaseStore()) {
            throw new ForbiddenException("Quotations are not enabled by current BaseStore!");
        }
    }

}
