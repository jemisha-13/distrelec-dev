/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import com.namics.distrelec.b2b.core.service.customer.dao.DistCustomerAccountDao;

import de.hybris.platform.b2b.company.impl.DefaultB2BCommerceUnitService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.dao.B2BUnitDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

/**
 * Default implementation of DistB2BCommerceUnitService.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistB2BCommerceUnitService extends DefaultB2BCommerceUnitService implements DistB2BCommerceUnitService {

    private static final Logger LOG = Logger.getLogger(DefaultDistB2BCommerceUnitService.class);

    @Autowired
    private B2BUnitDao b2bUnitDao;

    @Autowired
    @Qualifier("defaultDistCustomerAccountDao")
    private DistCustomerAccountDao customerAccountDao;

    @Override
    public void setDefaultAddressEntry(final B2BUnitModel company, final AddressModel addressModel) {
        validateParameterNotNull(company, "Company model cannot be null");
        validateParameterNotNull(addressModel, "Address model cannot be null");
        if (company.getAddresses().contains(addressModel)) {
            if (Boolean.TRUE.equals(addressModel.getBillingAddress())) {
                company.setBillingAddress(addressModel);
            }
            if (Boolean.TRUE.equals(addressModel.getShippingAddress())) {
                company.setShippingAddress(addressModel);
            }
        } else {
            final AddressModel clone = getModelService().clone(addressModel);
            clone.setOwner(company);
            getModelService().save(clone);
            final List<AddressModel> customerAddresses = new ArrayList<>();
            customerAddresses.addAll(company.getAddresses());
            customerAddresses.add(clone);
            company.setAddresses(customerAddresses);
            if (Boolean.TRUE.equals(addressModel.getBillingAddress())) {
                company.setBillingAddress(addressModel);
            }
            if (Boolean.TRUE.equals(addressModel.getShippingAddress())) {
                company.setShippingAddress(addressModel);
            }
        }
        getModelService().save(company);
        getModelService().refresh(company);
    }

    @Override
    public void clearDefaultAddressEntry(final B2BUnitModel company) {
        validateParameterNotNull(company, "Company model cannot be null");
        company.setBillingAddress(null);
        company.setShippingAddress(null);
        getModelService().save(company);
        getModelService().refresh(company);
    }

    @Override
    public B2BUnitModel getB2BUnitByErpCustomerId(final DistSalesOrgModel salesOrg, final String erpCustomerId) {
        validateParameterNotNull(salesOrg, "Sales organisation cannot be null");
        validateParameterNotNull(erpCustomerId, "ERP customer ID cannot be null");

        final Map<String, Object> params = new HashMap<>();
        params.put(B2BUnitModel.SALESORG, salesOrg);
        params.put(B2BUnitModel.ERPCUSTOMERID, erpCustomerId);
        final List<B2BUnitModel> results = getB2bUnitDao().find(params);
        if (CollectionUtils.isNotEmpty(results) && results.size() > 1) {
            throw new AmbiguousIdentifierException(
                    "More than one unit found for erpCustomerId " + erpCustomerId + " in sales organisation " + salesOrg.getCode());
        } else if (CollectionUtils.isNotEmpty(results)) {
            return results.get(0);
        } else {
            LOG.warn("Could not resolve B2BUnit for customer ID " + erpCustomerId + " and sales organisation " + salesOrg.getCode());
        }

        return null;
    }

    @Override
    public B2BCustomerModel getB2BCustomerByErpContactId(final B2BUnitModel unit, final String erpContactId) {
        validateParameterNotNull(unit, "Unit cannot be null");
        validateParameterNotNull(erpContactId, "ERP contact ID cannot be null");
        try {
            return getCustomerAccountDao().findCustomerForErpContactId(unit, erpContactId);
        } catch (final ModelNotFoundException e) {
            LOG.error("Can not resolve B2BCustomer for ERP contactId ID " + erpContactId + " in unit " + unit.getUid());
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService#getB2BCustomerByErpContactId(java.lang.String)
     */
    @Override
    public List<B2BCustomerModel> getB2BCustomerByErpContactId(final String erpContactId) {
        validateParameterNotNull(erpContactId, "ERP contact ID cannot be null");
        return getCustomerAccountDao().findCustomerForErpContactId(erpContactId);
    }

    @Override
    public <T extends B2BCustomerModel> T getCustomerForUid(final String uid) {
        return (T) getB2bCustomerService().getUserForUID(uid);
    }

    @Override
    public B2BCustomerModel addApproverToUnit(final String unitId, final String approverId) {
        final B2BUnitModel unit = getUnitForUid(unitId);
        final B2BCustomerModel approver = getCustomerForUid(approverId);
        return (addApproverToUnit(unit, approver));
    }

    @Override
    public B2BCustomerModel addApproverToUnit(final B2BUnitModel unit, final B2BCustomerModel approver) {
        final Set<B2BCustomerModel> approvers = new HashSet<>(unit.getApprovers());
        final Set<PrincipalGroupModel> groups = new HashSet<>(approver.getGroups());
        groups.add(getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP));
        approver.setGroups(groups);
        approvers.add(approver);
        unit.setApprovers(approvers);
        getModelService().saveAll(approver, unit);
        return approver;
    }

    public B2BUnitDao getB2bUnitDao() {
        return b2bUnitDao;
    }

    public void setB2bUnitDao(final B2BUnitDao b2bUnitDao) {
        this.b2bUnitDao = b2bUnitDao;
    }

    public DistCustomerAccountDao getCustomerAccountDao() {
        return customerAccountDao;
    }

    public void setCustomerAccountDao(final DistCustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

}
