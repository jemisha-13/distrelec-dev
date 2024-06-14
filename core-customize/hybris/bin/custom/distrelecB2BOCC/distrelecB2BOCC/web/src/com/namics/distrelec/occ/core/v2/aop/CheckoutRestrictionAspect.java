package com.namics.distrelec.occ.core.v2.aop;

import javax.ws.rs.ForbiddenException;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.core.service.order.DistCommerceCartService;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;

@Aspect
@Component
public class CheckoutRestrictionAspect {

    @Autowired
    private DistCheckoutFacade distCheckoutFacade;

    @Autowired
    private DistCommerceCartService distCommerceCartService;

    @Before("@annotation(com.namics.distrelec.occ.core.v2.annotations.CheckoutRestriction)")
    public void checkIsAllowed() {
        if (distCheckoutFacade.isCurrentCustomerBlocked()) {
            throw new ForbiddenException("checkout.error.invalid.accountType");
        } else if (distCommerceCartService.isAddToCartDisabled()) {
            // todo translation update
            throw new ForbiddenException("checkout.error.invalid.accountType");
        }
    }
}
