package com.namics.distrelec.occ.core.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.namics.distrelec.occ.core.v2.forms.AddressChangeForm;

public class AddressChangeFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return AddressChangeForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AddressChangeForm addressChangeForm = (AddressChangeForm) target;
        if (StringUtils.isBlank(addressChangeForm.getCustomerNumber())) {
            errors.rejectValue("customerNumber", "address.change.customerNumber.invalid");
        }

        if (addressChangeForm.getOldAddress() == null) {
            errors.rejectValue("oldAddress", "address.change.oldAddress.invalid");
        }

        if (addressChangeForm.getNewAddress() == null) {
            errors.rejectValue("newAddress", "address.change.newAddress.invalid");
        }
    }
}
