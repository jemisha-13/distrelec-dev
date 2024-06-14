package com.namics.distrelec.b2b.core.service.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.stream.Collectors.toList;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.event.*;
import com.namics.distrelec.b2b.core.inout.erp.CustomerService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.core.model.customer.PasswordChangeEntryModel;
import com.namics.distrelec.b2b.core.model.process.SetInitialPasswordProcessModel;
import com.namics.distrelec.b2b.core.security.IpAddressService;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.core.service.customer.dao.DistCustomerAccountDao;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorservices.customer.impl.DefaultB2BCustomerAccountService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

public class DefaultDistCustomerAccountService extends DefaultB2BCustomerAccountService implements DistCustomerAccountService {
    private static final Logger LOG = LogManager.getLogger(DefaultDistCustomerAccountService.class);

    private long migrationTokenValiditySeconds;

    private long resetPasswordTokenValiditySeconds;

    @Autowired
    @Qualifier("erp.customerService")
    private CustomerService erpCustomerService;

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Autowired
    private DistrelecCodelistService distCodelistService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private ShippingOptionService shippingOptionService;

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Autowired
    private UserService userService;

    @Autowired
    private IpAddressService ipAddressService;

    @Override
    public AddressModel getAddressForCode(final CustomerModel customerModel, final String code) {
        validateParameterNotNull(customerModel, "Customer model cannot be null");

        return getAddressBookEntries(customerModel).stream().filter(addressModel -> addressModel.getPk().getLongValueAsString().equals(code)).findFirst()
                                                   .orElse(null);
    }

    @Override
    public List<B2BCustomerModel> getCustomersByEmail(final String email) {
        return getCustomerAccountDao().findCustomersByEmail(email);
    }

    @Override
    public void register(final CustomerModel customerModel, final String password) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("customerModel", customerModel);

        generateCustomerId(customerModel);
        if (password != null) {
            getUserService().setPassword(customerModel, password, getPasswordEncoding());
        }

        final String token = generateToken(customerModel);
        customerModel.setToken(token);
        internalSaveCustomer(customerModel);
        LOG.debug("NEW REGISTER WITH TOKEN: {}", token);

        // Process 601 Registration
        try {
            if (StringUtils.isEmpty(((B2BCustomerModel) customerModel).getDefaultB2BUnit().getErpCustomerID())) {
                // if the erpCustomerCode is empty it means that the IF-08CreateCustomer has not yet been executed
                if (DistErpSystem.SAP.equals(distSalesOrgService.getCurrentSalesOrg().getErpSystem())) {
                    // Skip customer creation in Movex case. Movex customer will be created by entering a new erpCustomerId in hMC.
                    erpCustomerService.createCustomer(b2bUnitService.getParent((B2BCustomerModel) customerModel), (B2BCustomerModel) customerModel);
                }
            } else {
                // erpCustomerID already present. Create a new contact then.
                erpCustomerService.createContact((B2BCustomerModel) customerModel);
            }
            shippingOptionService.setErpDefaultShippingOptionForUser((B2BCustomerModel) customerModel);
        } catch (final UnsupportedOperationException uoex) {
            LOG.debug(uoex.getMessage());
        }
    }

    @Override
    public void registerExisting(final CustomerModel customerModel, final boolean contactFoundInERP, final String password) throws DuplicateUidException {
        registerExisting(customerModel, contactFoundInERP, password, false);
    }

    @Override
    public void registerExisting(final CustomerModel customerModel, final boolean contactFoundInERP, final String password,
                                 final boolean updateCurrentUserOnly) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("The customer data is missing", customerModel);
        validateParameterNotNullStandardMessage("The ERP customer ID is missing", ((B2BCustomerModel) customerModel).getDefaultB2BUnit().getErpCustomerID());

        generateCustomerId(customerModel);

        final String token = generateToken(customerModel);
        customerModel.setToken(token);
        if (password != null) {
            getUserService().setPassword(customerModel, password, getPasswordEncoding());
        }
        internalSaveCustomer(customerModel);
        LOG.debug("NEW REGISTER FOR EXISTING CUSTOMER WITH TOKEN: {}", token);

        if (contactFoundInERP) {
            // Update the contact in the ERP with the new data given by the registration
            erpCustomerService.updateContact((B2BCustomerModel) customerModel);
        } else {
            // The customer was found in the ERP but not the contact, so create it in the ERP!
            erpCustomerService.createContact((B2BCustomerModel) customerModel);
        }

        // Update the newly create unit and customer with the data from the ERP
        erpCustomerService.readCustomer(((B2BCustomerModel) customerModel).getDefaultB2BUnit(), updateCurrentUserOnly);
        shippingOptionService.setErpDefaultShippingOptionForUser((B2BCustomerModel) customerModel);
    }

    @Override
    public void raiseRegistrationEvent(final B2BCustomerModel customer, final RegistrationType registrationType) {
        validateParameterNotNull(customer, "Customer must not be null");
        Assert.hasText(customer.getToken(), "The field [token] in customer model cannot be empty");
        getEventService().publishEvent(initializeEvent(new DistRegisterEvent(customer.getToken(), registrationType), customer));
    }

    @Override
    public void raiseCheckNewCustomerEvent(final B2BCustomerModel customer, final RegistrationType registrationType) {
        validateParameterNotNull(customer, "Customer must not be null");
        Assert.hasText(customer.getToken(), "The field [token] in customer model cannot be empty");
        Boolean isStorefrontRequest = getSessionService().getAttribute(DistConstants.Session.IS_STOREFRONT_REQUEST);
        getEventService().publishEvent(initializeEvent(new DistCheckNewCustomerRegistrationEvent(registrationType,
                                                                                                 Objects.requireNonNullElse(isStorefrontRequest, false)),
                                                       customer));
    }

    @Override
    public void registerB2BEmployee(final CustomerModel customerModel) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("customerModel", customerModel);

        generateCustomerId(customerModel);

        final String token = generateToken(customerModel);
        customerModel.setToken(token);
        internalSaveCustomer(customerModel);

        erpCustomerService.createContact((B2BCustomerModel) customerModel);
        shippingOptionService.setErpDefaultShippingOptionForUser((B2BCustomerModel) customerModel);

        LOG.debug("NEW B2B EMPLOYEE REGISTRATION WITH TOKEN: {}", token);
        Boolean isStorefrontRequest = getSessionService().getAttribute(DistConstants.Session.IS_STOREFRONT_REQUEST);
        getEventService().publishEvent(initializeEvent(new SetInitialPwdEvent(token, Objects.requireNonNullElse(isStorefrontRequest, false)), customerModel));
    }

    @Override
    public void resendSetInitialPasswordEmail(final CustomerModel customer) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("customerModel", customer);

        final String token = generateToken(customer);
        customer.setToken(token);
        internalSaveCustomer(customer);
        LOG.debug("NEW B2B EMPLOYEE REGISTRATION WITH TOKEN: {}", token);
        Boolean isStorefrontRequest = getSessionService().getAttribute(DistConstants.Session.IS_STOREFRONT_REQUEST);
        getEventService().publishEvent(initializeEvent(new SetInitialPwdEvent(token, Objects.requireNonNullElse(isStorefrontRequest, false)), customer));
    }

    @Override
    public void setInitialPasswordAndActivateCustomer(final String token, final String newPassword,
                                                      boolean migration) throws TokenInvalidatedException {
        Assert.hasText(token, "The field [token] cannot be empty");
        Assert.hasText(newPassword, "The field [newPassword] cannot be empty");

        SecureToken data = getSecureTokenService().decryptData(token);
        CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);

        checkIsTokenExpired(getTokenValidityLength(migration), data);
        checkIsTokenValid(token, customer);

        if (customer instanceof B2BCustomerModel b2bCustomer) {
            b2bCustomer.setActive(Boolean.TRUE);
            b2bCustomer.setToken(null);
            b2bCustomer.setDoubleOptInActivated(true);
            getModelService().save(b2bCustomer);
        }
        getUserService().setPassword(data.getData(), newPassword, getPasswordEncoding());
    }

    @Override
    public void registerGuest(final CustomerModel customerModel, final String password) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("customerModel", customerModel);

        generateCustomerId(customerModel);
        if (password != null) {
            getUserService().setPassword(customerModel, password, getPasswordEncoding());
        }

        internalSaveCustomer(customerModel);

        LOG.debug("NEW GUEST REGISTER WITH TOKEN");
    }

    @Override
    public void resendAccountActivationToken(final String uid) {
        Assert.hasText(uid, "The field [uid] cannot be empty");
        final CustomerModel customerModel = getUserService().getUserForUID(uid, CustomerModel.class);
        validateParameterNotNullStandardMessage("customerModel", customerModel);

        final String token = generateToken(customerModel);
        customerModel.setToken(token);
        getModelService().save(customerModel);

        getEventService().publishEvent(initializeEvent(new DistRegisterEvent(token), customerModel));
    }

    @Override
    public String generateToken(final CustomerModel customerModel) {
        long tokenValidityLength = getTokenValiditySeconds();
        long timeStamp = tokenValidityLength > 0L ? new Date().getTime() : 0L;
        SecureToken data = new SecureToken(customerModel.getUid(), timeStamp);
        return getSecureTokenService().encryptData(data);
    }

    private long getTokenValidityLength(boolean migration) {
        return migration ? migrationTokenValiditySeconds : getTokenValiditySeconds();
    }

    @Override
    public String generateTokenForRsCustomer(String uid) {
        B2BCustomerModel customer = (B2BCustomerModel) getUserService().getUserForUID(uid);
        validateParameterNotNullStandardMessage("customerModel", customer);

        String token = generateToken(customer);
        customer.setToken(token);
        customer.setRsInitEmailSent(true);
        getEventService().publishEvent(initializeEvent(new SetInitialPwdEvent(token, false, true), customer));
        getModelService().save(customer);
        return token;
    }

    @Override
    public CustomerModel activateCustomer(final String token) throws TokenInvalidatedException {
        Assert.hasText(token, "The field [token] cannot be empty");
        final SecureToken data = getSecureTokenService().decryptData(token);
        if (getTokenValiditySeconds() > 0L) {
            final long delta = new Date().getTime() - data.getTimeStamp();
            if (delta / 1000 > getTokenValiditySeconds()) {
                throw new IllegalArgumentException("token expired");
            }
        }
        final CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);
        if (customer == null) {
            throw new IllegalArgumentException("user for token not found");
        }
        if (!token.equals(customer.getToken())) {
            throw new TokenInvalidatedException();
        }
        customer.setToken(null);
        customer.setDoubleOptInActivated(true);
        if (customer instanceof B2BCustomerModel) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;
            b2bCustomer.setActive(Boolean.TRUE);
        }
        getModelService().save(customer);
        return customer;
    }

    private void checkIsTokenExpired(long tokenValiditySeconds, SecureToken data) throws TokenInvalidatedException {
        if (tokenValiditySeconds > 0L) {
            final long delta = new Date().getTime() - data.getTimeStamp();
            if (delta / 1000 > tokenValiditySeconds) {
                throw new TokenInvalidatedException();
            }
        }
    }

    @Override
    public boolean deactivateCustomer(final String uid) {
        final B2BCustomerModel customerToDeactivate = (B2BCustomerModel) getUserService().getUserForUID(uid);
        final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
        if (b2bUnitService.getParent(currentUser).getMembers().contains(customerToDeactivate)) {
            customerToDeactivate.setDoubleOptInActivated(false);
            getModelService().save(customerToDeactivate);
            getEventService().publishEvent(initializeEvent(new DeactivateUserEvent(customerToDeactivate, currentUser), customerToDeactivate));
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSubUser(final String uid, final boolean force) {
        final B2BCustomerModel subUser = (B2BCustomerModel) getUserService().getUserForUID(uid);
        final B2BCustomerModel currentMasterUser = (B2BCustomerModel) getUserService().getCurrentUser();
        final B2BUnitModel masterB2BUnit = b2bUnitService.getParent(currentMasterUser);
        if (masterB2BUnit.getMembers().contains(subUser)) {
            if (force && CollectionUtils.isEmpty(subUser.getOrders())) {
                try {
                    LOG.info("Removing user: {}", uid);
                    getModelService().remove(subUser);
                } catch (final ModelRemovalException exp) {
                    LOG.error(exp.getMessage(), exp);
                    return false;
                }
            } else {
                LOG.info("Deactivating user: {} (No event publish)", uid);
                subUser.setActive(Boolean.FALSE);
                subUser.setDoubleOptInActivated(false);
                getModelService().save(subUser);
            }

            return true;
        }
        LOG.warn("b2bUnit: {} of master user: {} does not contain: {}", masterB2BUnit.getUid(), currentMasterUser.getUid(), subUser.getUid());
        return false;
    }

    @Override
    public List<AddressModel> getAddressBookEntries(final CustomerModel customerModel) {
        validateParameterNotNull(customerModel, "Customer model cannot be null");

        B2BUnitModel company = b2bUnitService.getParent((B2BCustomerModel) customerModel);
        if (company != null && company.getAddresses() != null) {
            return company.getAddresses().stream()
                          .filter(address -> BooleanUtils.isTrue(address.getBillingAddress()) || BooleanUtils.isTrue(address.getShippingAddress()))
                          .collect(toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<AddressModel> getAddressBookPaymentEntries(final CustomerModel customerModel) {
        validateParameterNotNull(customerModel, "Customer model cannot be null");
        return getAddressBookEntries(customerModel).stream().filter(address -> BooleanUtils.isTrue(address.getBillingAddress())).collect(toList());
    }

    @Override
    public List<AddressModel> getAddressBookDeliveryEntries(final CustomerModel customer) {
        validateParameterNotNull(customer, "Customer model cannot be null");
        return getAddressBookEntries(customer).stream().filter(address -> BooleanUtils.isTrue(address.getShippingAddress())).collect(toList());
    }

    @Override
    public void deleteAddressEntry(final CustomerModel customerModel, final AddressModel addressModel) {
        validateParameterNotNull(customerModel, "Customer model cannot be null");
        validateParameterNotNull(addressModel, "Address model cannot be null");

        if (b2bUnitService.getParent((B2BCustomerModel) customerModel).getShippingAddresses().contains(addressModel)) {

            if (!checkoutCustomerStrategy.isAnonymousCheckout()) {
                erpCustomerService.deleteAddress(b2bUnitService.getParent((B2BCustomerModel) customerModel), addressModel);
            }

            getModelService().remove(addressModel);
            getModelService().refresh(customerModel);

        } else {
            throw new IllegalArgumentException("Address " + addressModel + " does not belong to the customer " + customerModel + " and will not be removed.");
        }
    }

    @Override
    public void setContactDefaultAddresses(final CustomerModel customer, final AddressModel address, final boolean billing, final boolean shipping) {
        validateParameterNotNull(customer, "Customer model cannot be null");
        validateParameterNotNull(address, "Address model cannot be null");
        validateParameterNotNull(address.getErpAddressID(), "The address must be known by the ERP");
        boolean updated = false;
        if (billing && BooleanUtils.isTrue(address.getBillingAddress())) {
            customer.setDefaultPaymentAddress(address);
            updated = true;
        }

        if (shipping && BooleanUtils.isTrue(address.getShippingAddress())) {
            customer.setDefaultShipmentAddress(address);
            updated = true;
        }

        if (updated) {
            getModelService().save(customer);
        }
    }

    @Override
    public void setDefaultAddressEntry(final CustomerModel customerModel, final AddressModel addressModel) {
        validateParameterNotNull(customerModel, "Customer model cannot be null");
        validateParameterNotNull(addressModel, "Address model cannot be null");
        if (customerModel.getAddresses().contains(addressModel)) {
            if (Boolean.TRUE.equals(addressModel.getBillingAddress())) {
                customerModel.setDefaultPaymentAddress(addressModel);
            }
            if (Boolean.TRUE.equals(addressModel.getShippingAddress())) {
                customerModel.setDefaultShipmentAddress(addressModel);
            }
        } else {
            final AddressModel clone = getModelService().clone(addressModel);
            clone.setOwner(customerModel);
            getModelService().save(clone);
            final List<AddressModel> customerAddresses = new ArrayList<>();
            customerAddresses.addAll(customerModel.getAddresses());
            customerAddresses.add(clone);
            customerModel.setAddresses(customerAddresses);
            if (Boolean.TRUE.equals(addressModel.getBillingAddress())) {
                customerModel.setDefaultPaymentAddress(clone);
            }
            if (Boolean.TRUE.equals(addressModel.getShippingAddress())) {
                customerModel.setDefaultShipmentAddress(clone);
            }
        }
        getModelService().save(customerModel);
        getModelService().refresh(customerModel);
    }

    @Override
    public void convertGuestToCustomer(String pwd, String orderGUID) throws DuplicateUidException {
        final OrderModel orderModel = getOrderDetailsForGUID(orderGUID, getBaseStoreService().getCurrentBaseStore());
        if (orderModel == null) {
            throw new UnknownIdentifierException("Order with guid " + orderGUID + " not found in current BaseStore");
        }

        final B2BCustomerModel customer = (B2BCustomerModel) orderModel.getUser();
        if (!CustomerType.GUEST.equals(customer.getCustomerType())) {
            throw new IllegalArgumentException("Order with guid " + orderGUID + " does not belong to guest user");
        }

        final B2BUnitModel newUnit = createB2BUnitFromGuest(orderModel, customer);
        final B2BCustomerModel newCustomer = createB2CCustomerFromGuest(orderModel, customer, newUnit);
        cloneAndPopulateAddressInformation(customer.getDefaultPaymentAddress(), newUnit, newCustomer);

        register(newCustomer, pwd);
        getUserService().setCurrentUser(newCustomer);
    }

    private void cloneAndPopulateAddressInformation(AddressModel address, B2BUnitModel unit, B2BCustomerModel b2BCustomer) {
        final AddressModel clonedB2BUnitAddress = cloneAddress(address, unit);
        final AddressModel clonedB2BCustomerAddress = cloneAddress(address, b2BCustomer);

        setB2BCustomerAddress(b2BCustomer, clonedB2BCustomerAddress);
        setB2BUnitAddress(unit, b2BCustomer, clonedB2BUnitAddress);

        getModelService().saveAll(unit, b2BCustomer);
    }

    private void setB2BCustomerAddress(B2BCustomerModel b2BCustomer, AddressModel clonedB2BCustomerAddress) {
        b2BCustomer.setContactAddress(clonedB2BCustomerAddress);
        b2BCustomer.setAddresses(Collections.singletonList(clonedB2BCustomerAddress));
    }

    private void setB2BUnitAddress(B2BUnitModel unit, B2BCustomerModel b2BCustomer, AddressModel clonedB2BUnitAddress) {
        unit.setBillingAddress(clonedB2BUnitAddress);
        unit.setShippingAddress(clonedB2BUnitAddress);
        b2BCustomer.setDefaultPaymentAddress(clonedB2BUnitAddress);
        b2BCustomer.setDefaultShipmentAddress(clonedB2BUnitAddress);
        unit.setAddresses(Collections.singletonList(clonedB2BUnitAddress));
    }

    private AddressModel cloneAddress(AddressModel address, ItemModel owner) {
        final AddressModel addressModel = getModelService().clone(address);
        addressModel.setOwner(owner);
        getModelService().save(addressModel);
        return addressModel;
    }

    private B2BUnitModel createB2BUnitFromGuest(OrderModel orderModel, B2BCustomerModel customer) {
        final B2BUnitModel newUnit = getModelService().create(B2BUnitModel.class);
        newUnit.setUid(UUID.randomUUID().toString());
        newUnit.setCustomerType(CustomerType.B2C);
        newUnit.setName(combineFirstAndLastNameFromAddress(customer));
        newUnit.setLocName(combineFirstAndLastNameFromAddress(customer));
        newUnit.setSalesOrg(customer.getDefaultB2BUnit().getSalesOrg());
        newUnit.setCurrency(orderModel.getCurrency());
        newUnit.setCountry(customer.getDefaultPaymentAddress().getCountry());
        newUnit.setVatID(customer.getDefaultB2BUnit().getVatID());
        return newUnit;
    }

    private B2BCustomerModel createB2CCustomerFromGuest(OrderModel orderModel, B2BCustomerModel customer, B2BUnitModel newUnit) {
        final B2BCustomerModel newCustomer = getModelService().create(B2BCustomerModel.class);

        newCustomer.setName(combineFirstAndLastNameFromAddress(customer));
        newCustomer.setTitle(customer.getDefaultPaymentAddress().getTitle());
        newCustomer.setUid(StringUtils.substringAfter(customer.getUid(), "|"));
        newCustomer.setOriginalUid(customer.getEmail());
        newCustomer.setCustomerType(CustomerType.B2C);
        newCustomer.setSessionLanguage(customer.getSessionLanguage());
        newCustomer.setSessionCurrency(orderModel.getCurrency());
        newCustomer.setEmail(customer.getEmail());
        newCustomer.setEligibleForReevoo(Boolean.TRUE);
        newCustomer.setGroups(Collections.singleton(userService.getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID)));
        newCustomer.setRegisteredAsGuest(Boolean.TRUE);
        newCustomer.setConsentConditionRequired(Boolean.TRUE);
        newCustomer.setDefaultB2BUnit(newUnit);
        getModelService().save(newCustomer);
        b2bUnitService.addMember(newUnit, newCustomer);
        getModelService().save(newUnit);

        return newCustomer;
    }

    private String combineFirstAndLastNameFromAddress(B2BCustomerModel customer) {
        return getCustomerNameStrategy().getName(customer.getDefaultPaymentAddress().getFirstname(), customer.getDefaultPaymentAddress().getLastname());
    }

    @Override
    public void requestUpdatetUserInformation(final B2BCustomerModel customer) {

        // Process 602 Login and Password Recovery
        try {
            final B2BUnitModel b2bunit = b2bUnitService.getParent(customer);
            if (b2bunit != null && b2bunit.getSalesOrg() != null) {
                if (b2bunit.getErpCustomerID() != null) {
                    erpCustomerService.readCustomer(b2bunit, true);
                } else if (b2bunit.getSalesOrg().getErpSystem() == DistErpSystem.SAP) {
                    // DISTRELEC-9172: the customer should be deactivated if the ERP Customer ID is empty and the ERP System is SAP.
                    customer.setActive(Boolean.FALSE);
                    getModelService().save(customer);
                }
            }
        } catch (final UnsupportedOperationException uoex) {
            LOG.debug(uoex.getMessage());
        }
    }

    @Override
    public Collection<DistFunctionModel> getFunctions() {
        return distCodelistService.getAllDistFunctions();
    }

    @Override
    public Collection<DistDepartmentModel> getDepartments() {
        return distCodelistService.getAllDistDepartments();
    }

    @Override
    public void clearDefaultPaymentInfo(final CustomerModel customerModel) {
        customerModel.setDefaultPaymentInfo(null);
        getModelService().save(customerModel);
    }

    @Override
    public boolean isInitialPasswordTokenValid(final String token, boolean migration) {
        Assert.hasText(token, "The field [token] cannot be empty");

        SecureToken data = getSecureTokenService().decryptData(token);
        CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);

        try {
            checkIsTokenExpired(getTokenValidityLength(migration), data);
            checkIsTokenValid(token, customer);
            return true;
        } catch (TokenInvalidatedException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean isResetPasswordTokenValid(String token) {
        Assert.hasText(token, "The field [token] cannot be empty");

        SecureToken data = getSecureTokenService().decryptData(token);
        CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);

        try {
            checkIsTokenExpired(resetPasswordTokenValiditySeconds, data);
            checkIsTokenValid(token, customer);
            return true;
        } catch (TokenInvalidatedException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public List<AddressModel> getAddressBookDeliveryEntries(final CustomerModel customerModel, final String orderBy, String orderType) {
        validateParameterNotNull(customerModel, "Customer must not be null");
        final B2BUnitModel b2bunit = b2bUnitService.getParent((B2BCustomerModel) customerModel);

        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("customer", b2bunit);

        // order type
        if (!"desc".equalsIgnoreCase(orderType)) {
            orderType = "asc";
        }

        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(ItemModel.PK).append("} ");
        query.append("FROM {").append(AddressModel._TYPECODE).append("} ");
        query.append("WHERE {").append(AddressModel.BILLINGADDRESS).append("} = 0 AND {").append(AddressModel.SHIPPINGADDRESS).append("} = 1 AND {")
             .append(ItemModel.OWNER).append("} = ?customer ");
        // order by part
        if ("byName".equalsIgnoreCase(orderBy)) {
            query.append("ORDER BY {").append(AddressModel.LASTNAME).append("} ").append(orderType).append(", ");
            query.append("{" + AddressModel.FIRSTNAME).append("} ").append(orderType);
        } else if ("byCompany".equalsIgnoreCase(orderBy) && !"B2C".equalsIgnoreCase(b2bunit.getCustomerType().getType())) { // only b2b
            query.append("ORDER BY {").append(AddressModel.COMPANY).append("} ").append(orderType);
        } else {// default : byCity
            query.append("ORDER BY {").append(AddressModel.TOWN).append("} ").append(orderType);
        }

        final SearchResult<AddressModel> result = getFlexibleSearchService().search(query.toString(), queryParams);
        return result.getCount() > 0 ? result.getResult() : null;
    }

    @Override
    public boolean updateSessionCurrency(final B2BCustomerModel customer) {
        validateParameterNotNull(customer, "Customer must not be null");
        final B2BUnitModel b2bUnit = b2bUnitService.getParent(customer);
        final Boolean isMultiCountryAribaCustomer = getSessionService().getAttribute(DistConstants.Ariba.Session.ARIBA_MULTI_COUNTRY_CUSTOMER);
        if (null != isMultiCountryAribaCustomer && isMultiCountryAribaCustomer.booleanValue()) {
            return true;
        } else if (b2bUnit.getCurrency() != null && !b2bUnit.getCurrency().equals(getCommonI18NService().getCurrentCurrency())) {
            // Change the session currency
            getCommonI18NService().setCurrentCurrency(b2bUnit.getCurrency());
            return true;
        }

        return false;
    }

    @Override
    protected AbstractCommerceUserEvent initializeEvent(final AbstractCommerceUserEvent event, final CustomerModel customerModel) {
        event.setBaseStore(getBaseStore(customerModel));
        event.setSite(getBaseSite(customerModel));
        event.setCustomer(customerModel);
        return event;
    }

    private BaseStoreModel getBaseStore(final CustomerModel customerModel) {
        BaseStoreModel baseStore = getBaseSiteService().getCurrentBaseSite() != null ? getBaseStoreService().getCurrentBaseStore() : null;
        if (baseStore == null && customerModel instanceof B2BCustomerModel) {
            baseStore = ((B2BCustomerModel) customerModel).getCustomersBaseStore();
        }

        return baseStore;
    }

    private BaseSiteModel getBaseSite(final CustomerModel customerModel) {
        BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();
        if (baseSite == null && customerModel instanceof B2BCustomerModel) {
            baseSite = ((B2BCustomerModel) customerModel).getCustomersBaseSite();
        }

        return baseSite;
    }

    @Override
    public void confirmDoubleOptforResetPwd(final String token) {
        final SecureToken data = getSecureTokenService().decryptData(token);
        final CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);
        customer.setDoubleOptInActivated(true);
        getModelService().save(customer);
    }

    @Override
    protected DistCustomerAccountDao getCustomerAccountDao() {
        return (DistCustomerAccountDao) super.getCustomerAccountDao();
    }

    @Override
    public boolean updateShowProductBox(final UserModel customer, final boolean newValue) {
        if (!getUserService().isAnonymousUser(customer) && customer instanceof B2BCustomerModel) {
            try {
                ((B2BCustomerModel) customer).setShowProductBox(newValue);
                getModelService().save(customer);
            } catch (final Exception exp) {
                LOG.error("Error occurs while updating the customer: {}", exp.getMessage(), exp);
                return false;
            }
        }
        return true;
    }

    @Override
    public ForgottenPasswordProcessModel getForgottenPasswordProcessForToken(String token) {
        return getCustomerAccountDao().findForgottenPasswordProcessByToken(token);
    }

    @Override
    public SetInitialPasswordProcessModel getInitialPasswordProcessForToken(String token) {
        return getCustomerAccountDao().findSetInitialPasswordProcessByToken(token);
    }

    @Override
    public void removeForgotPasswordToken(CustomerModel customer, boolean deleteCustomerToken) {
        List<ForgottenPasswordProcessModel> forgotPassword = getCustomerAccountDao().findForgottenPasswordProcessByCustomer(customer);
        getModelService().removeAll(forgotPassword);
        if (deleteCustomerToken) {
            customer.setToken(null);
            getModelService().save(customer);
        }
    }

    @Override
    public void removeInitialPasswordToken(CustomerModel customer, boolean deleteCustomerToken) {
        List<SetInitialPasswordProcessModel> initialPasswordProcesses = getCustomerAccountDao().findSetInitialPasswordProcessByCustomer(customer);
        getModelService().removeAll(initialPasswordProcesses);
        if (deleteCustomerToken) {
            customer.setToken(null);
            getModelService().save(customer);
        }
    }

    @Override
    public void checkoutForgottenPassword(CustomerModel customerModel, boolean isStorefrontRequest) {
        validateParameterNotNullStandardMessage("customerModel", customerModel);
        final long timeStamp = getTokenValiditySeconds() > 0L ? new Date().getTime() : 0L;
        final SecureToken data = new SecureToken(customerModel.getUid(), timeStamp);
        final String token = getSecureTokenService().encryptData(data);
        customerModel.setToken(token);
        getModelService().save(customerModel);
        getEventService().publishEvent(initializeEvent(new DistCheckoutForgottenPwdEvent(token, isStorefrontRequest), customerModel));
    }

    @Override
    public void updateMarketingCookieConsent(boolean isMarketingCookieEnabled) {
        B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
        if (customer.isMarketingCookieConsent() != isMarketingCookieEnabled) {
            customer.setMarketingCookieConsent(isMarketingCookieEnabled);
            getModelService().save(customer);
        }
    }

    @Override
    public void setDefaultShippingAddress(CustomerModel customer, AddressModel address, boolean shipping) {
        validateParameterNotNull(customer, "Customer model cannot be null");
        validateParameterNotNull(address, "Address model cannot be null");
        validateParameterNotNull(address.getErpAddressID(), "The address must be known by the ERP");
        boolean updated = false;

        if (shipping && BooleanUtils.isTrue(address.getShippingAddress())) {
            customer.setDefaultShipmentAddress(address);
            updated = true;
        }

        if (updated) {
            getModelService().save(customer);
        }
    }

    @Override
    public void setDefaultBillingAddress(CustomerModel customer, AddressModel address, boolean billing) {
        validateParameterNotNull(customer, "Customer model cannot be null");
        validateParameterNotNull(address, "Address model cannot be null");
        validateParameterNotNull(address.getErpAddressID(), "The address must be known by the ERP");
        boolean updated = false;
        if (billing && BooleanUtils.isTrue(address.getBillingAddress())) {
            customer.setDefaultPaymentAddress(address);
            updated = true;
        }

        if (updated) {
            getModelService().save(customer);
        }
    }

    @Override
    public void forgottenPassword(CustomerModel customerModel, boolean isStorefrontRequest) {
        validateParameterNotNullStandardMessage("customerModel", customerModel);
        final long timeStamp = getTokenValiditySeconds() > 0L ? new Date().getTime() : 0L;
        final SecureToken data = new SecureToken(customerModel.getUid(), timeStamp);
        final String token = getSecureTokenService().encryptData(data);
        customerModel.setToken(token);
        getModelService().save(customerModel);
        getEventService().publishEvent(initializeEvent(new DistForgottenPwdEvent(token, isStorefrontRequest), customerModel));
    }

    @Override
    public void raiseNewCustomerActivationEvent(B2BCustomerModel customer) {
        validateParameterNotNull(customer, "Customer must not be null");
        Assert.hasText(customer.getToken(), "The field [token] in customer model cannot be empty");
        getEventService().publishEvent(initializeEvent(new DistNewCustomerActivationEvent(customer.getToken()), customer));
    }

    @Override
    public void changePassword(final UserModel userModel, final String oldPassword, final String newPassword)
                                                                                                              throws PasswordMismatchException {
        validateParameterNotNullStandardMessage("customerModel", userModel);
        if (!getUserService().isAnonymousUser(userModel)) {
            if (getPasswordEncoderService().isValid(userModel, oldPassword)) {
                getUserService().setPassword(userModel, newPassword, userModel.getPasswordEncoding());
                getModelService().save(userModel);
                savePasswordChangeEntry(userModel.getUid());
            } else {
                throw new PasswordMismatchException(userModel.getUid());
            }
        }
    }

    @Override
    public void updatePassword(final String token, final String newPassword) throws TokenInvalidatedException {
        Assert.hasText(token, "The field [token] cannot be empty");
        Assert.hasText(newPassword, "The field [newPassword] cannot be empty");

        SecureToken data = getSecureTokenService().decryptData(token);
        CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);

        checkIsTokenExpired(resetPasswordTokenValiditySeconds, data);
        checkIsTokenValid(token, customer);

        customer.setToken(null);
        customer.setLoginDisabled(false);
        getModelService().save(customer);

        getUserService().setPassword(data.getData(), newPassword, getPasswordEncoding());
        savePasswordChangeEntry(data.getData());
    }

    private void checkIsTokenValid(String token, CustomerModel customer) throws TokenInvalidatedException {
        if (customer == null) {
            throw new IllegalArgumentException("user for token not found");
        }
        if (!token.equals(customer.getToken())) {
            throw new TokenInvalidatedException();
        }
    }

    private void savePasswordChangeEntry(String uid) {
        final B2BCustomerModel user = userService.getUserForUID(uid, B2BCustomerModel.class);
        PasswordChangeEntryModel passwordChangeEntryModel = getModelService().create(PasswordChangeEntryModel.class);
        passwordChangeEntryModel.setCreationtime(new Date());
        passwordChangeEntryModel.setUser(user);
        passwordChangeEntryModel.setIpAddress(ipAddressService.getClientIpAddress());
        getModelService().save(passwordChangeEntryModel);
    }

    @Override
    public void updateConsentConditionRequired(final B2BCustomerModel customer, final boolean consentConditionRequired) {
        customer.setConsentConditionRequired(consentConditionRequired);
        getModelService().save(customer);
    }

    @Required
    public void setMigrationTokenValiditySeconds(long migrationTokenValiditySeconds) {
        if (migrationTokenValiditySeconds < 0) {
            throw new IllegalArgumentException("migrationTokenValiditySeconds has to be >= 0");
        }
        this.migrationTokenValiditySeconds = migrationTokenValiditySeconds;
    }

    @Required
    public void setResetPasswordTokenValiditySeconds(long resetPasswordTokenValiditySeconds) {
        if (resetPasswordTokenValiditySeconds < 0) {
            throw new IllegalArgumentException("resetPasswordTokenValiditySeconds has to be >= 0");
        }
        this.resetPasswordTokenValiditySeconds = resetPasswordTokenValiditySeconds;
    }

    public void setErpCustomerService(final CustomerService erpCustomerService) {
        this.erpCustomerService = erpCustomerService;
    }
}
