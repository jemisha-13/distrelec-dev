/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * DistB2BBudget Validator.
 * 
 * @author dsivakumaran, Namics AG
 * 
 */
public class DistB2BBudgetModelValidateInterceptor implements ValidateInterceptor {

    private static final Logger LOG = LogManager.getLogger(DistB2BBudgetModelValidateInterceptor.class);

    @Autowired
    private UserService userService;

    @Autowired
    private L10NService l10NService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof DistB2BBudgetModel)) {
            if (model instanceof B2BBudgetModel) {
                LOG.warn(getL10NService().getLocalizedString("validations.distb2bbudget.deprecated"));
            }
        } else {
            final DistB2BBudgetModel budget = (DistB2BBudgetModel) model;
            if (budget.getOrderBudget() == null && budget.getYearlyBudget() == null) {
                throw new InterceptorException(getL10NService().getLocalizedString("validations.distb2bbudget.null"));
            }

            if (!ctx.isNew(model) && ctx.isModified(model, DistB2BBudgetModel.YEARLYBUDGET)) {
                return;
            } else {
                final UserModel user = userService.getCurrentUser();
                if (!(user instanceof EmployeeModel) && !userService.isMemberOfGroup(user, userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP))) {
                    throw new InterceptorException(getL10NService().getLocalizedString("validations.distb2bbudget.admin"));
                }
            }
        }
    }

    public L10NService getL10NService() {
        return l10NService;
    }

    public void setL10NService(final L10NService l10NService) {
        this.l10NService = l10NService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

}
