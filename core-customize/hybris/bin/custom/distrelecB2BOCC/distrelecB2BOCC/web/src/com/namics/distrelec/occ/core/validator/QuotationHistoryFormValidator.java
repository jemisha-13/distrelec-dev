package com.namics.distrelec.occ.core.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.namics.distrelec.occ.core.v2.forms.QuotationHistoryForm;

public class QuotationHistoryFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return QuotationHistoryForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        QuotationHistoryForm quotationHistoryForm = (QuotationHistoryForm) target;
        if (quotationHistoryForm.getFromDate() != null && quotationHistoryForm.getToDate() != null) {
            if (quotationHistoryForm.getFromDate().isAfter(quotationHistoryForm.getToDate())) {
                errors.rejectValue("fromDate", "validate.error.date.before");
            }
        }
    }
}
