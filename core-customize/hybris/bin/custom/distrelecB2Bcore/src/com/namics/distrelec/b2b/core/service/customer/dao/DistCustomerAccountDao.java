/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.dao;

import java.util.List;

import com.namics.distrelec.b2b.core.model.process.SetInitialPasswordProcessModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 * NamicsCustomerAccountDao.
 * 
 * @author rhusi, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public interface DistCustomerAccountDao extends CustomerAccountDao {

    /**
     * Get customer for a given ERP contact ID.
     * 
     * @param unit
     *            the unit the customer belongs to
     * @param erpContactId
     *            the contact id of the user in ERP
     * @return the customer for the given erp contact ID
     */
    B2BCustomerModel findCustomerForErpContactId(final B2BUnitModel unit, final String erpContactId);

    /**
     * Get customer for a given ERP contact ID.
     * 
     * @param erpContactId
     *            the contact id of the user in ERP
     * @return the contacts for the given ERP contact ID
     */
    List<B2BCustomerModel> findCustomerForErpContactId(final String erpContactId);

    /**
     * Retrieve all customers having the specified email address.
     * 
     * @param email
     *            the customer email address
     * @return a list of {@code B2BCustomerModel}
     */
    List<B2BCustomerModel> findCustomersByEmail(final String email);

    ForgottenPasswordProcessModel findForgottenPasswordProcessByToken(String token);

    List<ForgottenPasswordProcessModel> findForgottenPasswordProcessByCustomer(CustomerModel customer);

    SetInitialPasswordProcessModel findSetInitialPasswordProcessByToken(String token);

    List<SetInitialPasswordProcessModel> findSetInitialPasswordProcessByCustomer(CustomerModel customer);

}
