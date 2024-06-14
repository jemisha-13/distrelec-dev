package com.namics.distrelec.b2b.facades.customer;

import java.util.List;

import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.facades.customer.exceptions.ExistingCustomerRegistrationException;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistSubUserData;

import com.namics.distrelec.occ.core.v2.forms.SetInitialPasswordForm;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;

public interface DistCustomerFacade extends CustomerFacade {

    void registerNewCustomer(final DistRegisterData registerData, final CustomerType customerType) throws DuplicateUidException;

    void registerExistingB2B(final DistExistingCustomerRegisterData registerData) throws DuplicateUidException, ExistingCustomerRegistrationException;

    void createGuestUserForAnonymousCheckout(String name, DistRegisterData registerData) throws DuplicateUidException;

    void createB2BEmployee(final DistSubUserData data) throws DuplicateUidException;

    void createB2BEmployee(DistSubUserData registerData, String approverCustomerUid) throws DuplicateUidException;

    void createB2BEmployee(DistSubUserData registerData, B2BCustomerModel approverCustomer) throws DuplicateUidException;

    void updateB2BEmployee(final DistSubUserData data, final B2BCustomerModel customer) throws DuplicateUidException;

    List<CustomerData> searchEmployees(final String name, final String stateCode, final String sortCode);

    List<CustomerData> searchEmployees(final String name, final String stateCode, final String sortCode, final boolean includeCurrentUser);

    void resendSetInitialPasswordEmail(String uid) throws DuplicateUidException;

    CustomerData getCustomerDataForUid(final String uid);

    void forgottenMigratedUsersPassword(final String login, final String email);

    void storeIPAddress();

    void confirmDoubleOptforResetPwd(final String token);

    List<CountryData> getCountriesForRegistration(SiteChannel channel);

    boolean updateShowProductBox(final boolean newValue);

    void updateSelectedProfileInformation(CustomerData customerData) throws DuplicateUidException;

    boolean canEditCompanyName(final AddressData addressData);

    void registerExistingCustomer(final DistExistingCustomerRegisterData registerData, final boolean updateCurrentUserOnly,
                                  final CustomerType customerType) throws DuplicateUidException, ExistingCustomerRegistrationException;

    boolean validateResetPasswordToken(final String token);

    boolean validateInitialPasswordToken(String token, boolean migration);

    boolean deleteSubUser(final String uid);

    boolean deleteSubUser(final String uid, final boolean force);

    void removeForgotPasswordToken();

    boolean searchCustomer(final String customerId, final String customerName);

    void checkAndUpdateCustomer();

    void checkoutForgottenPassword(String email);

    void updateMarketingCookieConsent(boolean isMarketingCookieEnabled);

    List<DistCategoryIndexData> getTopCategoriesIgnoreObsolete();

    List<DistCategoryIndexData> getTopCategories();

    boolean doesCustomerExistForUid(String uid);

    void updateLogin(String newLogin);

    List<String> formatCompanyName(String companyName);

    void updateVatIdForGuest(String codiceFiscale);

    String getCodiceFiscaleForGuest();

    void convertGuestToB2CAndRegisterInSMC(final String pwd, final String orderGUID) throws DuplicateUidException;

    void convertGuestToB2CAndRegisterInBloomreach(final String pwd, final String orderGUID) throws DuplicateUidException;

    boolean isJobRoleShown(RegistrationType registrationType, CustomerType customerType);

    void activateCustomer(final DistExistingCustomerRegisterData registerData, final CustomerType customerType)
            throws DuplicateUidException, ExistingCustomerRegistrationException;

    boolean createUserInBloomreach(final DistConsentData distConsentData);

    String generateRsActivationLink(String email);

    void setInitialPasswordAndActivateCustomer(SetInitialPasswordForm setInitialPasswordForm) throws TokenInvalidatedException;

}
