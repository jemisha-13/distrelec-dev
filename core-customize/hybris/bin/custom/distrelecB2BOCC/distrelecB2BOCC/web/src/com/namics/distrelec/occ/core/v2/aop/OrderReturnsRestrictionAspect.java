package com.namics.distrelec.occ.core.v2.aop;

import javax.ws.rs.ForbiddenException;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.facades.rma.DistReturnRequestFacade;

@Aspect
@Component
public class OrderReturnsRestrictionAspect {

    @Autowired
    private DistReturnRequestFacade returnRequestFacade;

    @Before("@annotation(com.namics.distrelec.occ.core.v2.annotations.OrderReturnsRestriction)")
    public void checkIsAllowed() {
        if (returnRequestFacade.isOrderReturnDisabled()) {
            // todo translation update
            throw new ForbiddenException("checkout.error.invalid.accountType");
        }
    }
}
