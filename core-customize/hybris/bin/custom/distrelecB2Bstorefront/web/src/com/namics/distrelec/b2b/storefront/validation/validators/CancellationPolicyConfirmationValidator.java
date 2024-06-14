/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.validation.validators;

import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.storefront.validation.annotations.CancellationPolicyConfirmation;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates cancellation policy confirmation.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.1
 */
public class CancellationPolicyConfirmationValidator implements ConstraintValidator<CancellationPolicyConfirmation, Boolean> {

    @Autowired
    private DistCheckoutFacade distCheckoutFacade;

    @Override
    public void initialize(final CancellationPolicyConfirmation cancellationPolicyConfirmation) {
        // NOP
    }

    @Override
    public boolean isValid(final Boolean value, final ConstraintValidatorContext constraintValidatorContext) {
        if (!distCheckoutFacade.isCancellationPolicyConfirmationRequired()) {
            return true;
        }

        return BooleanUtils.isTrue(value);
    }

}
