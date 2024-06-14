/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer;

import java.util.Collection;
import java.util.List;

import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.core.model.process.SetInitialPasswordProcessModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.customer.B2BCustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

public interface DistCustomerAccountService extends B2BCustomerAccountService {

    List<B2BCustomerModel> getCustomersByEmail(final String email);

    List<AddressModel> getAddressBookPaymentEntries(final CustomerModel customerModel);

    List<AddressModel> getAddressBookDeliveryEntries(final CustomerModel customerModel, final String orderBy, final String orderType);

    void setContactDefaultAddresses(final CustomerModel customer, final AddressModel address, final boolean billing, final boolean shipping);

    void registerExisting(final CustomerModel customerModel, final boolean contactFoundInERP, final String passwor) throws DuplicateUidException;

    void registerExisting(final CustomerModel customerModel, final boolean contactFoundInERP, final String password,
                          final boolean updateCurrentUserOnly) throws DuplicateUidException;

    void registerGuest(final CustomerModel customerModel, final String password) throws DuplicateUidException;

    void requestUpdatetUserInformation(final B2BCustomerModel customer);

    void resendAccountActivationToken(final String uid);

    void resendSetInitialPasswordEmail(final CustomerModel customer) throws DuplicateUidException;

    CustomerModel activateCustomer(final String token) throws TokenInvalidatedException;

    boolean deactivateCustomer(final String uid);

    boolean deleteSubUser(final String uid, final boolean force);

    void setInitialPasswordAndActivateCustomer(final String token, final String newPassword, boolean migration) throws TokenInvalidatedException;

    void registerB2BEmployee(final CustomerModel customerModel) throws DuplicateUidException;

    void clearDefaultPaymentInfo(final CustomerModel customerModel);

    boolean isInitialPasswordTokenValid(final String token, boolean migration);

    boolean isResetPasswordTokenValid(final String token);

    Collection<DistFunctionModel> getFunctions();

    Collection<DistDepartmentModel> getDepartments();

    boolean updateSessionCurrency(final B2BCustomerModel customer);

    void raiseRegistrationEvent(final B2BCustomerModel customer, final RegistrationType registrationType);

    void raiseCheckNewCustomerEvent(final B2BCustomerModel customer, final RegistrationType registrationType);

    void confirmDoubleOptforResetPwd(final String token);

    boolean updateShowProductBox(final UserModel user, final boolean newValue);

    ForgottenPasswordProcessModel getForgottenPasswordProcessForToken(String token);

    SetInitialPasswordProcessModel getInitialPasswordProcessForToken(String token);

    void removeForgotPasswordToken(CustomerModel customer, boolean deleteCustomerToken);

    void removeInitialPasswordToken(CustomerModel customer, boolean deleteCustomerToken);

    void checkoutForgottenPassword(CustomerModel customerModel, boolean isStorefrontRequest);

    void updateMarketingCookieConsent(boolean isMarketingCookieEnabled);

    void setDefaultShippingAddress(final CustomerModel customer, final AddressModel address, final boolean shipping);

    void setDefaultBillingAddress(final CustomerModel customer, final AddressModel address, final boolean billing);

    void forgottenPassword(CustomerModel customerModel, boolean isStorefrontRequest);

    void raiseNewCustomerActivationEvent(final B2BCustomerModel customer);

    String generateToken(final CustomerModel customerModel);

    String generateTokenForRsCustomer(String uid);

    void updateConsentConditionRequired(final B2BCustomerModel customer, final boolean consentConditionRequired);

}
