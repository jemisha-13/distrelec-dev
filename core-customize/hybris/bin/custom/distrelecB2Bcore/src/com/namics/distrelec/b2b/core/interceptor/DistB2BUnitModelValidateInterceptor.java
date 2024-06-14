/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * ValidateInterceptor for B2BUnitModel.
 * 
 * @author rmeier, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class DistB2BUnitModelValidateInterceptor implements ValidateInterceptor {

    private static final Logger LOG = LogManager.getLogger(DistB2BUnitModelValidateInterceptor.class);

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Autowired
    private L10NService l10NService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistB2BCommerceUnitService b2bCommerceUnitService;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.interceptor.ValidateInterceptor#onValidate(java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof B2BUnitModel)) {
            return;
        }

        final B2BUnitModel unit = (B2BUnitModel) model;
        LOG.info("Validating B2BUnitModel " + unit.getErpCustomerID());

        // Check if ERP Customer ID already exists
        if (unit.getErpCustomerID() != null && unit.getSalesOrg() != null) {
            validateCustomerId(unit);
        }

        final CustomerType unitCT = unit.getCustomerType();
        if (unitCT == null) {
            throw new IllegalStateException(l10NService.getLocalizedString("error.b2bunit.customertype.notset"));
        }

        // Check if all customers of this unit have the same customer type
        AtomicBoolean isChanged = new AtomicBoolean(false);
        final Set<PrincipalModel> members = unit.getMembers();
        if (CollectionUtils.isNotEmpty(members)) {
            final List<B2BCustomerModel> filteredMembers = members.stream() //
                    .filter(m -> (m instanceof B2BCustomerModel) && !unitCT.equals(((B2BCustomerModel) m).getCustomerType())) //
                    .map(m -> (B2BCustomerModel) m) //
                    .collect(Collectors.toList());

            filteredMembers.forEach(m -> {
                        CustomerType customerType = m.getCustomerType();
                        LOG.warn(
                                "Customer type of contact with id {} is set to {} which is not the same as the one of its Company with id {} with the customer type {}! The customer type of the contact with id {} has been set to {}.",
                                m.getUid(), customerType, unit.getUid(), unitCT.getCode(), m.getUid(), unitCT.getCode());

                        final String customerTypeCode = customerType != null ? customerType.getCode() : null;
                        final String unitCTCode = unitCT.getCode();
                        if (!unitCTCode.equalsIgnoreCase(customerTypeCode)) {
                            m.setCustomerType(unitCT);
                            isChanged.set(true);
                        }
                    });

            if(isChanged.get()) {
                modelService.saveAll(filteredMembers);
                //reset isChanged.
                isChanged.set(false);
            }
        }

        // Check that the unit has no parent unit. Hierarchical structures are not allowed at the moment
        if (unit.getGroups() != null) {
            if (unit.getGroups().stream().anyMatch(u -> u instanceof B2BUnitModel)) {
                throw new InterceptorException(l10NService.getLocalizedString("error.b2bunit.b2bunit.multiple"));
            }
        }

        final B2BUnitModel parentUnit = b2bUnitService.getParent(unit);
        if (parentUnit != null && BooleanUtils.isTrue(unit.getActive()) && !BooleanUtils.isTrue(parentUnit.getActive())) {
            throw new InterceptorException(l10NService.getLocalizedString("error.b2bunit.enable.b2bunit.disabled"));
        }

        if (BooleanUtils.isTrue(unit.getActive())) {
            return;
        }

        final Set<B2BUnitModel> childUnits = b2bUnitService.getB2BUnits(unit);
        final List<B2BUnitModel> updatedChildUnits = new ArrayList<>();
        childUnits.stream().filter(child -> BooleanUtils.isTrue(child.getActive())).forEach(child -> {
            isChanged.set(true);
            child.setActive(Boolean.FALSE);
            updatedChildUnits.add(child);
        });

        if(!updatedChildUnits.isEmpty()){
            if(isChanged.get()){
                modelService.saveAll(updatedChildUnits);
                //reset ischanged
                isChanged.set(false);
            }
        }
    }

    protected boolean validateCustomerId(final B2BUnitModel unitModel) throws InterceptorException {
        final B2BUnitModel other = b2bCommerceUnitService.getB2BUnitByErpCustomerId(unitModel.getSalesOrg(), unitModel.getErpCustomerID());
        // Don't throw an exception if the result is same company.
        if (other != null && other.getPk() != null && !other.getPk().equals(unitModel.getPk())) {
            throw new InterceptorException("Duplicate ERP Customer ID. Not allowed");
        }

        return true;
    }

    public DistB2BCommerceUnitService getB2bCommerceUnitService() {
        return b2bCommerceUnitService;
    }

    public void setB2bCommerceUnitService(final DistB2BCommerceUnitService b2bCommerceUnitService) {
        this.b2bCommerceUnitService = b2bCommerceUnitService;
    }
}
