/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import com.distrelec.webservice.sap.v1.ReadCustomerResponse;
import com.distrelec.webservice.sap.v1.SearchDunsResponse;
import com.namics.distrelec.b2b.core.inout.erp.data.FindContactRequestData;
import com.namics.distrelec.b2b.core.inout.erp.data.FindContactResponseData;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 * Service to provide customer options.
 * 
 * @author rmeier, Namics AG
 * @since Distrelec 1.0
 */
public interface CustomerService {

    /**
     * Triggered by an existing B2C customer that already has a customer number but is not yet a webshop user. No webshop user must exist
     * for this customer number.
     * 
     * @param salesOrganization
     * @param customerId
     * @param lastName
     * @return true if customer has been found
     */
    boolean lookupPrivateCustomer(final String salesOrganization, final String customerId, final String lastName) throws ErpCommunicationException;

    /**
     * Triggered by an existing B2B customer that already has a customer number but is not yet a webshop user. No webshop user must exist
     * for this customer number.
     * 
     * @param salesOrganization
     * @param customerId
     * @param companyName
     * @return true if customer has been found
     */
    boolean lookupBusinessCustomer(final String salesOrganization, final String customerId, final String companyName) throws ErpCommunicationException;

    /**
     * Creates a new customer in the ERP.
     * 
     * @param customer
     *            the current hybris customer
     * @param contact
     *            the current hybris contact
     * @return an id representing this customer in the ERP. (Just available in newSAP)
     */
    String createCustomer(final B2BUnitModel customer, final B2BCustomerModel contact) throws ErpCommunicationException;

    /**
     * Reads the customer data from the ERP and updates the hybris customer. (this is just a shortcut for
     * {@link #readCustomer(B2BUnitModel, boolean)} with boolean flag set to {@code false}, i.e., {@code readCustomer(customer, false)}).
     * 
     * @param customer
     *            the hybris customer that has to be updated by the ERP data.
     * @return the updated Hybris customer.
     * @see #readCustomer(B2BUnitModel, boolean)
     */
    B2BUnitModel readCustomer(final B2BUnitModel customer) throws ErpCommunicationException;
    
    /**
     * Reads the customer data from the ERP 
     * 
     * @param customerId
     *            CustomerId in ERP.
     *            
     * @param salesOrgId
     *             Sales org id
     * @return  customer.
     * @see #readCustomer(B2BUnitModel, boolean)
     */
    ReadCustomerResponse readCustomer(final String salesOrgId, final String customerId) throws ErpCommunicationException;
    

    /**
     * <p>
     * Reads the customer data from the ERP and updates the Hybris customer. If the flag {@code updateCurrentUserOnly} is set to
     * {@code true}, then only the currently logged in contact will be updated, otherwise, all contacts will be updated.
     * </p>
     * <b>Note:</b> the flag {@code updateCurrentUserOnly} should be used with value {@code true} ONLY if we are sure that one of the
     * customer contacts is the owner of the current session.
     * 
     * @param customer
     *            the Hybris customer that has to be updated by the ERP data.
     * @param updateCurrentUserOnly
     *            a flag indicating whether we update all contacts or only the current user.
     * @return the updated Hybris customer.
     * @throws ErpCommunicationException
     */
    B2BUnitModel readCustomer(final B2BUnitModel customer, final boolean updateCurrentUserOnly) throws ErpCommunicationException;

    /**
     * Updates a customer in the ERP.
     * 
     * @param unit
     *            the current hybris customer
     */
    //void updateCustomer(final B2BUnitModel unit) throws ErpCommunicationException;

    /**
     * Creates a new address in the ERP.
     * 
     * @param address
     *            the address of the customer
     * @return an id representing this address in the ERP. (Just available in newSAP)
     */
    String createAddress(final B2BUnitModel customer, final AddressModel address) throws ErpCommunicationException;

    /**
     * Updates the given address in the ERP.
     * 
     * @param address
     *            the address that have to be update in the ERP
     */
    void updateAddress(final B2BUnitModel customer, final AddressModel address) throws ErpCommunicationException;

    /**
     * Deletes the address in the ERP. The "main" address can not be deleted.
     * 
     * @param address
     *            the address that has to be delete in the ERP
     */
    void deleteAddress(final B2BUnitModel customer, final AddressModel address) throws ErpCommunicationException;

    /**
     * Creates a new contact in the ERP.
     * 
     * @param contact
     *            the current hybris contact
     * @return an id representing this contact in the ERP. (Just available in newSAP)
     */
    String createContact(final B2BCustomerModel contact) throws ErpCommunicationException;

    /**
     * Reads the contact data from the ERP and updates the hybris contact.
     * 
     * @param contact
     *            the hybris contact that has to be updated by the ERP data.
     */
    B2BCustomerModel readContact(final B2BCustomerModel contact) throws ErpCommunicationException;

    /**
     * Updates a contact in the ERP.
     * 
     * @param contact
     *            the current hybris contact
     */
    boolean updateContact(final B2BCustomerModel contact) throws ErpCommunicationException;

    /**
     * Deletes the contact in the ERP. The "main" contact can not be deleted.
     * 
     * @param contact
     *            the contact that has to be delete in the ERP
     */
    boolean deleteContact(final B2BCustomerModel contact) throws ErpCommunicationException;

    /**
     * Check the correctness of login information provided by the customer in SAP Elfa system.
     * 
     * @param username
     *            the username of the customer in Elfa SAP
     * 
     * @param password
     *            the password of the customer in Elfa SAP
     * 
     * @return ReturnCode that indicates:<br />
     *         01 - The username and the password provided by the user are correct.<br />
     *         02 - The username provided by the user is not known in SAP system.<br />
     *         03 - The password provided by the user is wrong in SAP system.
     * 
     * @throws ErpCommunicationException
     */
    public boolean checkElfaAuthentication(String username, String password) throws ErpCommunicationException;

    /**
     * Find the contact and customer in SAP
     * 
     * @param findContact
     * @return FindContactResponse
     */
    public FindContactResponseData findContact(FindContactRequestData findContact);

	boolean updateCustomer(CustomerModel currentCustomer) throws ErpCommunicationException;
	
	SearchDunsResponse searchDuns(String dunsNumber);

}
