/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.company;

import java.util.List;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * DistB2BCommerceUnitService extends B2BCommerceUnitService
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public interface DistB2BCommerceUnitService extends B2BCommerceUnitService {

    /**
     * Sets the default address entry. Because company addresses are not sorted it sets given address as default company payment address. If
     * the address is not saved it will be created and assigned to the given user. If given address belongs to other user it will be cloned
     * and assigned to the given company and the original address will be left as it is.
     * 
     * @param company
     *            the unit model to set default payment address for
     * @param addressModel
     *            the address model to be set as default one
     * @throws IllegalArgumentException
     *             the illegal argument exception if any argument is null
     */
    void setDefaultAddressEntry(B2BUnitModel company, AddressModel addressModel);

    /**
     * Clears default address on the given company
     * 
     * @param company
     *            the customer
     */
    void clearDefaultAddressEntry(B2BUnitModel company);

    /**
     * Get the B2BUnit on the base of the ERP customer Id and sales organisation
     * 
     * @param salesOrg
     *            the sales organisation the customer belongs to
     * @param erpCustomerId
     *            the ERP customer ID
     * @return the B2BUnitModel matching the given ERP customer ID
     */
    B2BUnitModel getB2BUnitByErpCustomerId(final DistSalesOrgModel salesOrg, final String erpCustomerId);

    /**
     * Get the B2BCustomer on the base of the ERP contact Id and sales organisation
     * 
     * @param unit
     *            the unit the customer belongs to
     * @param erpContactId
     *            the ERP contact ID
     * @return the B2BCustomerModel matching the given ERP contact ID
     */
    B2BCustomerModel getB2BCustomerByErpContactId(final B2BUnitModel unit, final String erpContactId);

    /**
     * Get the B2BCustomer on the base of the ERP contact Id and sales organisation
     * 
     * @param erpContactId
     *            the ERP contact ID
     * @return the B2BCustomerModel matching the given ERP contact ID
     */
    List<B2BCustomerModel> getB2BCustomerByErpContactId(final String erpContactId);

    /**
     * Adds an approver to a company ({@code B2BUnit}).
     * 
     * @param unitId
     *            the company UID
     * @param approverId
     *            the approver UID
     * @return the company
     */
    B2BCustomerModel addApproverToUnit(final String unitId, final String approverId);

    /**
     * 
     * @param unit
     *            the company
     * @param approver
     *            the approver
     * @return the company
     */
    B2BCustomerModel addApproverToUnit(final B2BUnitModel unit, final B2BCustomerModel approver);

    <T extends B2BCustomerModel> T getCustomerForUid(final String uid);
}
