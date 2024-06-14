package com.namics.distrelec.occ.core.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CustomerReferenceValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String customerReference = (String) target;
        if (StringUtils.length(customerReference) > 35) {
            errors.reject("import-tool.file.customerReferenceToLong");
        }
    }
}
