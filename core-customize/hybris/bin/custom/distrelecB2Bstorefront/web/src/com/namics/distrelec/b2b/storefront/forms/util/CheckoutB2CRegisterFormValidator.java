package com.namics.distrelec.b2b.storefront.forms.util;

import com.namics.distrelec.b2b.storefront.forms.CheckoutRegisterForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CheckoutB2CRegisterFormValidator  implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CheckoutRegisterForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CheckoutRegisterForm b2cForm = (CheckoutRegisterForm) target;
        if (b2cForm != null) {
            if (b2cForm.getExistingCustomer() != null && !b2cForm.getExistingCustomer() && b2cForm.getCurrencyCode() == null) {
                errors.rejectValue("currencyCode", "register.currency.invalid");
            }
            
        }
    }

}
