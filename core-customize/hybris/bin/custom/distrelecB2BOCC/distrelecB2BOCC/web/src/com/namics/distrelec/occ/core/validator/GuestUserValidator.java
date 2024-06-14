package com.namics.distrelec.occ.core.validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;

public class GuestUserValidator implements Validator {

    @Autowired
    private DistCustomerFacade distCustomerFacade;

    @Autowired
    private DistB2BOrderFacade distOrderFacade;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        String email = (String) target;
        validateEmail(errors, email);
        checkCustomerUid(errors, email);
    }

    private void checkCustomerUid(Errors errors, String email) {
        if (distCustomerFacade.doesCustomerExistForUid(email)) {
            errors.reject("login.checkout.guest.existing.address.error");
        }
        if (distOrderFacade.isNumberOfGuestSuccessfulPurchasesExceeded(email)) {
            errors.reject("login.checkout.guest.registration.info");
        }
    }

    private void validateEmail(final Errors errors, final String email) {
        if (StringUtils.isEmpty(email) || StringUtils.length(email) > 175 || !validateEmailAddress(email)) {
            errors.reject("register.email.invalid");
        }
    }

    private boolean validateEmailAddress(final String email) {
        EmailValidator emailValidator = new EmailValidator();
        return emailValidator.isValid(email, null);
    }
}
