/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.interceptor;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;
import com.namics.distrelec.b2b.core.service.newsletter.service.DistSavedValuesService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.interceptor.B2BCustomerModelValidateInterceptor;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.user.UserService;

public class DistB2BCustomerModelValidateInterceptor extends B2BCustomerModelValidateInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(DistB2BCustomerModelValidateInterceptor.class);

    @Autowired
    private DistB2BCommerceBudgetService distB2BCommerceBudgetService;

    @Autowired
    private DistB2BCommerceUnitService b2bCommerceUnitService;

    @Autowired
    private DistSavedValuesService distSavedValuesService;

    @Autowired
    private UserService userService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        super.onValidate(model, ctx);

        if (!(model instanceof B2BCustomerModel)) {
            return;
        }

        final B2BCustomerModel customer = (B2BCustomerModel) model;
        LOG.debug("Validating customer {}", customer.getUid());
        // validate only if the ERP Contact Id and B2BUnit is not null
        if (customer.getErpContactID() != null && customer.getDefaultB2BUnit() != null) {
            validateErpContactID(customer);
        }
        if (ctx.isModified(customer, B2BCustomerModel.BUDGET) && customer.getBudget() != null) {
            validateBudget(customer, customer.getBudget());
        }

        final String field = B2BCustomerModel.NEWSLETTER;

        final boolean newValue = customer.isNewsletter();
        final boolean oldValue = !newValue;
        if (ctx.isModified(customer, field)) {
            LOG.info("Value for field {} for customer {} changed", field, customer.getUid());
            getDistSavedValuesService().logModifiedField(customer, field, newValue, oldValue);
        }

        final String phoneMarketing = B2BCustomerModel.PHONEMARKETINGCONSENT;
        final boolean phoneMarketingNewValue = customer.isPhoneMarketingConsent();
        final boolean phoneMarketingOldValue = !phoneMarketingNewValue;
        if (ctx.isModified(customer, phoneMarketing)) {
            LOG.info("Value for field {} for customer {} changed", phoneMarketing, customer.getUid());
            getDistSavedValuesService().logModifiedField(customer, phoneMarketing, phoneMarketingNewValue, phoneMarketingOldValue);
        }

    }

    protected void validateBudget(final B2BCustomerModel customer, final DistB2BBudgetModel budget) throws InterceptorException {
        final UserModel user = distB2BCommerceBudgetService.getUserForBudget(budget);
        if (user != null && !user.equals(customer)) {
            throw new InterceptorException(getL10NService().getLocalizedString("validations.distb2bbudget.user"));
        }

        if (!getUserService().isAdmin(getUserService().getCurrentUser())) {
            final B2BCustomerModel currUser = (B2BCustomerModel) getUserService().getCurrentUser();
            final PrincipalGroupModel group = getUserService().getUserGroupForUID(DistConstants.User.B2BADMINGROUP_UID);
            if (CollectionUtils.isNotEmpty(currUser.getGroups())) {
                if (!currUser.getGroups().contains(group)) {
                    throw new InterceptorException(getL10NService().getLocalizedString("validations.distb2bbudget.admin"));
                }
            } else {
                throw new InterceptorException(getL10NService().getLocalizedString("validations.distb2bbudget.admin"));
            }

            final B2BUnitModel unit = getB2bUnitService().getParent(customer);
            if (unit == null || !unit.equals(getB2bUnitService().getParent(currUser))) {
                throw new InterceptorException(getL10NService().getLocalizedString("validations.distb2bbudget.unit"));
            }
        }

        if (!CustomerType.B2B.equals(customer.getCustomerType()) && !CustomerType.B2B_KEY_ACCOUNT.equals(customer.getCustomerType())) {
            throw new InterceptorException(getL10NService().getLocalizedString("validations.distb2bbudget.b2buser"));
        }
        final PrincipalGroupModel b2bGroup = getUserService().getUserGroupForUID(DistConstants.User.B2BCUSTOMERGROUP_UID);
        if (CollectionUtils.isNotEmpty(customer.getGroups())) {
            if (!customer.getGroups().contains(b2bGroup)) {
                throw new InterceptorException(getL10NService().getLocalizedString("validations.distb2bbudget.b2buser"));
            }
        } else {
            throw new InterceptorException(getL10NService().getLocalizedString("validations.distb2bbudget.b2buser"));
        }
    }

    private boolean validateErpContactID(final B2BCustomerModel customer) throws InterceptorException {
        final List<B2BCustomerModel> others = b2bCommerceUnitService.getB2BCustomerByErpContactId(customer.getErpContactID());
        if (CollectionUtils.isEmpty(others)) {
            return true;
        }

        final B2BCustomerModel other = others.get(0);

        // An exception is thrown:
        // 1) if the size of the collection is bigger than 1.
        // 2) the unique result found is not the same as the target contact
        if (others.size() > 1 || (other != null && other.getPk() != null && !other.getPk().equals(customer.getPk()))) {
            throw new InterceptorException("The ERP contact ID: " + customer.getErpContactID() + " already exists on " + other.getUid() + ". Validation of "
                                           + customer.getUid() + " failed");
        }
        return true;
    }

    public DistB2BCommerceBudgetService getDistB2BCommerceBudgetService() {
        return distB2BCommerceBudgetService;
    }

    public void setDistB2BCommerceBudgetService(final DistB2BCommerceBudgetService distB2BCommerceBudgetService) {
        this.distB2BCommerceBudgetService = distB2BCommerceBudgetService;
    }

    public DistB2BCommerceUnitService getB2bCommerceUnitService() {
        return b2bCommerceUnitService;
    }

    public void setB2bCommerceUnitService(final DistB2BCommerceUnitService b2bCommerceUnitService) {
        this.b2bCommerceUnitService = b2bCommerceUnitService;
    }

    public DistSavedValuesService getDistSavedValuesService() {
        return distSavedValuesService;
    }

    public void setDistSavedValuesService(final DistSavedValuesService distSavedValuesService) {
        this.distSavedValuesService = distSavedValuesService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
