/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.user;

import java.util.List;

import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentData;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionData;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * DistUserFacade extends UserFacade.
 *
 * @author rmeier, Namics AG
 * @author daehusir, Distrelec
 *
 */
public interface DistUserFacade extends UserFacade {

    /**
     * Returns all delivery addresses of the current users address book.
     *
     * @return all delivery addresses of the current users address book.
     */
    List<AddressData> getAddressBookDeliveryEntries();

    /**
     * Returns all delivery addresses of the current users address book ordered.
     *
     * @return all delivery addresses of the current users address book ordered.
     */
    List<AddressData> getAddressBookDeliveryEntries(String orderBy, String orderType);

    /**
     * Returns all payment addresses of the current users address book.
     *
     * @return all payment addresses of the current users address book.
     */
    List<AddressData> getAddressBookPaymentEntries();

    /**
     * Checks whether the address given by its {@code PK} is an existing address and belongs the current customer.
     *
     * @param PK
     *            the address {@code PK}
     * @return {@code true} if an address with the specified {@code PK} exists and belongs to the current customer.
     */
    boolean isValidUserAddress(final String PK);

    /**
     * Checks if a user already exists.
     *
     * @param uid
     *            UID of the user
     * @return true if a user already exists
     */
    boolean isExistingUser(final String uid);

    /**
     * Return all delivery modes supported by the current user.
     *
     * @return all delivery modes supported by the current user.
     */
    List<DeliveryModeData> getSupportedDeliveryModesForUser();

    /**
     * Return all payment modes supported by the current user.
     *
     * @return all payment modes supported by the current user.
     */
    List<DistPaymentModeData> getSupportedPaymentModesForUser();

    /**
     * Returns true if a b2b customer does not have the invoice payment mode and if it can request to get it.
     * <p>
     * Also return true if the b2b customer already requested the invoice payment mode.
     * </p>
     *
     * @return true if a b2b customer can request the invoice payment mode
     */
    boolean canRequestInvoicePaymentMode();

    /**
     * Returns true if a b2b customer requested the invoice payment mode and it is still not approved.
     */
    boolean isInvoicePaymentModeRequested();

    /**
     * Sends an email to a finance deparment to enable invoice payment mode for a customer.
     */
    void requestInvoicePaymentMode();

    /**
     * Set a delivery mode for the current user.
     *
     * @param deliveryId
     *            of the delivery mode
     *
     * @return true if the set is possible
     */
    boolean setDefaultDeliveryMode(final String deliveryId);

    /**
     * Set a payment mode for the current user.
     *
     * @param paymentId
     *            of the payment mode
     *
     * @return true if the set is possible
     */
    boolean setDefaultPaymentMode(final String paymentId);

    /**
     * Set a payment info mode for the current user.
     *
     * @param paymentId
     *            of the payment info
     *
     * @return true if the set is possible
     */
    boolean setDefaultPaymentInfo(final String paymentId);

    /**
     * Provide all localized functions.
     *
     * @return List of {@link DistFunctionData} objects
     */
    List<DistFunctionData> getFunctions();

    /**
     * Provide all localized departments.
     *
     * @return List of {@link DistDepartmentData} objects
     */
    List<DistDepartmentData> getDepartments();

    /**
     * Add the specified address and if the flag {@code updateErp} is set to {@code true} then it pushes also creates it in the ERP system.
     *
     * @param addressData
     *            the new address to add.
     * @param updateErp
     */
    AddressData addAddress(final AddressData addressData, final boolean updateErp);

    /**
     * Update the specified address and if the flag {@code updateErp} is set to {@code true} then it pushes also the update to the ERP
     * system.
     *
     * @param addressData
     *            the address to update
     * @param updateErp
     */
    AddressData editAddress(final AddressData addressData, final boolean updateErp);

    /**
     * Check whether or not the current customer is allowed to edit his company name.
     *
     * @param addressModel
     *            the target address.
     * @return {@code true} if the current customer is allowed to edit his company name on the specified address, {@code false} otherwise.
     */
    boolean canEditCompanyName(final AddressModel addressModel);

    void setDefaultShippingAddress(final AddressData address);

    void setDefaultBillingAddress(final AddressData address);

    void setDefaultShippingAddressIfNotSet(final AddressData address);

    void setDefaultBillingAddressIfNotSet(final AddressData address);

    List<String> getMemberCustomersForB2BUnit(final String b2bUnitId);

    void updateConsentConditionsRequiredFlag(final B2BCustomerModel customer, final boolean consentConditionRequired);

}
