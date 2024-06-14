package com.namics.distrelec.occ.core.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.namics.distrelec.occ.core.v2.dto.invoice.InvoiceHistorySearchInputWsDTO;

public class InvoiceInputValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return InvoiceHistorySearchInputWsDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final InvoiceHistorySearchInputWsDTO invoiceHistorySearchInputWsDTO = (InvoiceHistorySearchInputWsDTO) target;
        Assert.notNull(errors, "Errors object must not be null");

        if (invoiceHistorySearchInputWsDTO.getFromDate() != null && invoiceHistorySearchInputWsDTO.getToDate() != null) {
            if (invoiceHistorySearchInputWsDTO.getFromDate().isAfter(invoiceHistorySearchInputWsDTO.getToDate())) {
                errors.rejectValue("fromDate", "validate.error.date.before");
            }
        }

        if (invoiceHistorySearchInputWsDTO.getFromDueDate() != null && invoiceHistorySearchInputWsDTO.getToDueDate() != null) {
            if (invoiceHistorySearchInputWsDTO.getFromDueDate().isAfter(invoiceHistorySearchInputWsDTO.getToDueDate())) {
                errors.rejectValue("fromDueDate", "validate.error.date.before");
            }
        }

        if (invoiceHistorySearchInputWsDTO.getOrderNumber() != null) {
            final Pattern pattern = Pattern.compile("[A-Za-z0-9\\*]*");
            final Matcher matcher = pattern.matcher(invoiceHistorySearchInputWsDTO.getOrderNumber());
            if (!matcher.matches()) {
                errors.rejectValue("orderNumber", "validate.error.alphanumeric");
            }
        }

    }
}
