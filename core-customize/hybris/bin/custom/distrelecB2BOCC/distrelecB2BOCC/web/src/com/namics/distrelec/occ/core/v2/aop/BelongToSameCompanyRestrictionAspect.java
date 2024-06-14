package com.namics.distrelec.occ.core.v2.aop;

import java.util.Arrays;

import javax.ws.rs.ForbiddenException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

@Aspect
@Component
public class BelongToSameCompanyRestrictionAspect {

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("b2bUnitService")
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    @Autowired
    private CustomerService customerService;

    @Before("@annotation(com.namics.distrelec.occ.core.v2.annotations.BelongToSameCompanyRestriction)")
    public void checkUser(JoinPoint jp) {
        boolean isBelongingToTheSameCompany = Arrays.stream(jp.getArgs())
                                                    .map(String.class::cast)
                                                    .anyMatch(customerId -> {
                                                        try {
                                                            B2BUnitModel currentUserB2bUnit = b2BUnitService.getParent((B2BCustomerModel) userService.getCurrentUser());
                                                            B2BUnitModel b2bUnitForRequestedCustomer = b2BUnitService.getParent((B2BCustomerModel) customerService.getCustomerByCustomerId(customerId));
                                                            return currentUserB2bUnit.getPk().equals(b2bUnitForRequestedCustomer.getPk());
                                                        } catch (UnknownIdentifierException | ClassCastException exceptions) {
                                                            return Boolean.FALSE;
                                                        }
                                                    });
        if (!isBelongingToTheSameCompany) {
            throw new ForbiddenException("User is not allowed to access");
        }
    }

}
