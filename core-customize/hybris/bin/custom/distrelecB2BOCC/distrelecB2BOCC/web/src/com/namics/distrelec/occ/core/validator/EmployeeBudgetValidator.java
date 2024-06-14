package com.namics.distrelec.occ.core.validator;

import com.namics.distrelec.b2b.facades.user.ws.dto.RegisterB2BEmployeeWsDTO;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

public class EmployeeBudgetValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterB2BEmployeeWsDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final RegisterB2BEmployeeWsDTO registerB2BEmployeeWsDTO = (RegisterB2BEmployeeWsDTO) target;
        Assert.notNull(errors, "Errors object must not be null");

        if (!registerB2BEmployeeWsDTO.isBudgetWithoutLimit()) {
            final BigDecimal perOrder = registerB2BEmployeeWsDTO.getBudgetPerOrder();
            final BigDecimal yearly = registerB2BEmployeeWsDTO.getYearlyBudget();
            if (perOrder == null && yearly == null) {
                errors.rejectValue("budgetPerOrder", "registration.error.budget.null");
            }
        }
    }
}
