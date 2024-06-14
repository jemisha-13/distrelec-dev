/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static de.hybris.platform.commerceservices.enums.CustomerType.B2B_KEY_ACCOUNT;
import static java.util.stream.Collectors.toMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.ws.WebServiceException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.webservice.sap.v1.*;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.inout.erp.CustomerService;
import com.namics.distrelec.b2b.core.inout.erp.dao.SapCustomerDao;
import com.namics.distrelec.b2b.core.inout.erp.data.FindContactRequestData;
import com.namics.distrelec.b2b.core.inout.erp.data.FindContactResponseData;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.user.daos.DistTitleDao;
import com.namics.distrelec.b2b.core.util.DistLogUtils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.daos.CountryDao;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.i18n.daos.RegionDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.AddressService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

public class SapCustomerService implements CustomerService {

    private static final Logger LOG = LogManager.getLogger(SapCustomerService.class);

    private static final String NOT_NULL_CONTACT_MSG = "Contact must not be null!";

    private static final String NOT_NULL_CUSTOMER_MSG = "Customer must not be null!";

    private static final String ITALY_ISO_CODE = "IT";

    private static final String ITALY_SALESORG_ID = "7330";

    private ModelService modelService;

    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;

    private SIHybrisV1Out webServiceClient;

    private SapCustomerDao sapCustomerDao;

    private DistTitleDao titleDao;

    private CountryDao countryDao;

    private RegionDao regionDao;

    private CurrencyDao currencyDao;

    private DistrelecCodelistService distCodelistService;

    private AddressService addressService;

    private UserService userService;

    private CommonI18NService commonI18NService;

    // The SOAP Requests object factory
    @Autowired
    private ObjectFactory sapObjectFactory;

    private CustomerNameStrategy customerNameStrategy;

    private SessionService sessionService;

    @Override
    public B2BUnitModel readCustomer(final B2BUnitModel customer) {
        return readCustomer(customer, false);
    }

    @Override
    public B2BUnitModel readCustomer(final B2BUnitModel customer, final boolean updateCurrentUserOnly) throws ErpCommunicationException {
        ServicesUtil.validateParameterNotNull(customer, NOT_NULL_CUSTOMER_MSG);

        final ReadCustomerRequest request = buildReadCustomerRequest(customer);
        final ReadCustomerResponse response = executeSOAPCustomer(request, "if08ReadCustomer");

        updateCustomerFromReadCustomerResponse(response, customer, updateCurrentUserOnly);

        return customer;
    }

    @Override
    public ReadCustomerResponse readCustomer(final String salesOrgId, final String customerId) throws ErpCommunicationException {
        ServicesUtil.validateParameterNotNull(customerId, NOT_NULL_CUSTOMER_MSG);

        final ReadCustomerRequest request = sapObjectFactory.createReadCustomerRequest();
        request.setCustomerId(customerId);
        request.setSalesOrganization(salesOrgId);
        return executeSOAPCustomer(request, "if08ReadCustomer");
    }

    @Override
    public boolean updateCustomer(final CustomerModel customer) throws ErpCommunicationException {
        ServicesUtil.validateParameterNotNull(customer, NOT_NULL_CONTACT_MSG);

        final UpdateCustomerRequest request = buildUpdateCustomerRequest(customer);

        final UpdateCustomerResponse response = executeSOAPCustomer(request, "if08UpdateCustomer");
        checkNullResponse(response);
        // The line above throws an ErpCommunicationException the response is null.
        return response.isSuccessful();
    }

    @Override
    public String createCustomer(final B2BUnitModel customer, final B2BCustomerModel contact) {
        ServicesUtil.validateParameterNotNull(customer, NOT_NULL_CUSTOMER_MSG);
        CreateCustomerRequest request;
        if (CollectionUtils.isNotEmpty(customer.getAddresses())) {
            request = buildCreateCustomerRequest(customer);
        } else {
            request = buildCreateCustomerRequest(contact, customer);
        }

        request.setVat4(contact.getVat4());
        request.setLegalEmail(contact.getLegalEmail());
        request.setInvoiceEmail(customer.getInvoiceEmail());

        if (request.getCountry().equalsIgnoreCase("XI")) {
            request.setCountry("GB");
            request.setRegion("XI");
        }
        final CreateCustomerResponse response = executeSOAPCustomer(request, "if08CreateCustomer");

        checkNullResponse(response);

        // update the B2BUnit in hybris adding the sapID
        customer.setErpCustomerID(response.getCustomerId());
        // update the B2BCustomer in hybris adding the sapID
        contact.setErpContactID(response.getContactId());

        /*
         * Update the B2BUnit address adding the sapID. When the SAP Company is created the address is created always with the same sapID of
         * the Company.
         */
        final AddressModel customerAddress = customer.getBillingAddress();
        if (customerAddress != null) {
            customerAddress.setErpAddressID(response.getCustomerId());
            modelService.saveAll(customer, contact, customerAddress);
        } else {
            modelService.saveAll(customer, contact);
        }

        return response.getCustomerId();
    }

    @Override
    public String createAddress(final B2BUnitModel customer, final AddressModel address) {
        ServicesUtil.validateParameterNotNull(customer, NOT_NULL_CUSTOMER_MSG);

        final CreateAddressRequest request = buildCreateAddressRequest(customer, address);
        final CreateAddressResponse response = executeSOAPCustomer(request, "if08CreateAddress");
        checkNullResponse(response);
        address.setErpAddressID(response.getAddressId());
        modelService.save(address);

        return response.getAddressId();
    }

    @Override
    public void deleteAddress(final B2BUnitModel customer, final AddressModel address) {
        ServicesUtil.validateParameterNotNull(customer, NOT_NULL_CUSTOMER_MSG);
        ServicesUtil.validateParameterNotNull(address, "Address must not be null!");

        final DeleteAddressRequest request = buildDeleteAddressRequest(customer, address);
        final DeleteAddressResponse response = executeSOAPCustomer(request, "if08DeleteAddress");

        checkNullResponse(response);

    }

    @Override
    public void updateAddress(final B2BUnitModel customer, final AddressModel address) {
        ServicesUtil.validateParameterNotNull(customer, NOT_NULL_CUSTOMER_MSG);
        ServicesUtil.validateParameterNotNull(address, "Address must not be null!");

        final UpdateAddressRequest request = buildUpdateAddressRequest(customer, address);
        final UpdateAddressResponse response = executeSOAPCustomer(request, "if08UpdateAddress");
        checkNullResponse(response);
        if (!response.isSuccessful()) {
            LOG.error("{} {} Problems updating the address in SAP.", ErrorLogCode.ADDRESS_ERROR, ErrorSource.SAP_FAULT);
        }
    }

    @Override
    public boolean lookupPrivateCustomer(final String salesOrganization, final String customerId, final String lastName) {
        ServicesUtil.validateParameterNotNull(salesOrganization, "Sales Organization must not be null!");
        ServicesUtil.validateParameterNotNull(customerId, "Customer id must not be null!");
        ServicesUtil.validateParameterNotNull(lastName, "Last Name must not be null!");

        final CheckPrivateCustomerRequest request = buildCheckPrivateCustomer(salesOrganization, customerId, lastName);
        final CheckPrivateCustomerResponse response = executeSOAPCustomer(request, "if08CheckPrivateCustomer");
        checkNullResponse(response);
        return response.isResult();

    }

    @Override
    public boolean lookupBusinessCustomer(final String salesOrganization, final String customerId, final String vatId) {
        ServicesUtil.validateParameterNotNull(salesOrganization, "Sales Organization must not be null!");
        ServicesUtil.validateParameterNotNull(customerId, "Customer id must not be null!");
        ServicesUtil.validateParameterNotNull(vatId, "VAT Id must not be null!");

        final CheckBusinessCustomerRequest request = buildCheckBusinessCustomer(salesOrganization, customerId, vatId);
        final CheckBusinessCustomerResponse response = executeSOAPCustomer(request, "if08CheckBusinessCustomer");
        checkNullResponse(response);
        return response.isResult();
    }

    @Override
    public String createContact(final B2BCustomerModel contact) {
        ServicesUtil.validateParameterNotNull(contact, NOT_NULL_CONTACT_MSG);

        final CreateContactRequest request = buildCreateContactRequest(contact);
        final CreateContactResponse response = executeSOAPCustomer(request, "if08CreateContact");
        checkNullResponse(response);

        contact.setErpContactID(response.getContactId());

        modelService.save(contact);
        return response.getContactId();
    }

    @Override
    public B2BCustomerModel readContact(final B2BCustomerModel contact) {
        ServicesUtil.validateParameterNotNull(contact, NOT_NULL_CONTACT_MSG);

        final B2BUnitModel customer = readCustomer(contact.getDefaultB2BUnit());

        return (B2BCustomerModel) customer.getContact();
    }

    @Override
    public boolean updateContact(final B2BCustomerModel contact) {
        ServicesUtil.validateParameterNotNull(contact, NOT_NULL_CONTACT_MSG);

        final UpdateContactRequest request = buildUpdateContactRequest(contact);

        final UpdateContactResponse response = executeSOAPCustomer(request, "if08UpdateContact");
        checkNullResponse(response);
        return response.isSuccessful();
    }

    @Override
    public boolean deleteContact(final B2BCustomerModel contact) {

        final DeleteContactRequest request = buildDeleteContactRequest(contact);
        final DeleteContactResponse response = executeSOAPCustomer(request, "if08DeleteContact");
        checkNullResponse(response);
        return response.isSuccessful();
    }

    @Override
    public boolean checkElfaAuthentication(final String username, final String password) {
        final CheckUserAuthenticationRequest request = sapObjectFactory.createCheckUserAuthenticationRequest();
        request.setUser(username);
        request.setPassword(password);

        final CheckUserAuthenticationResponse response = executeSOAPCustomer(request, "if08CheckUserAuthentication");
        checkNullResponse(response);

        return response.isUserCheckResponse();

    }

    @Override
    public FindContactResponseData findContact(final FindContactRequestData findContact) {

        // create the request
        final FindContactRequest request = sapObjectFactory.createFindContactRequest();
        request.setCustomerId(findContact.getErpCustomerId());
        request.setEmail(findContact.getEmail());
        request.setFirstName(findContact.getFirstName());
        request.setLastName(findContact.getLastName());
        request.setSalesOrganization(findContact.getSalesOrganization());
        request.setOrganizationNumber(findContact.getOrganizationalNumber());
        request.setVatNumber(findContact.getVatNumber());

        // execute soap call
        final FindContactResponse response = executeSOAPCustomer(request, "if08FindContact");
        checkNullResponse(response);

        // map the response
        final FindContactResponseData responseData = new FindContactResponseData();
        responseData.setCustomerFound(CustomerStatus.CUSTOMER_FOUND.equals(response.getCustomerStatus()));
        responseData.setErpCustomerId(response.getCustomerId());
        responseData.setContactFound(ContactStatus.CONTACT_FOUND.equals(response.getContactStatus()));
        responseData.setContactUnique(!ContactStatus.CONTACT_NOT_UNIQUE.equals(response.getContactStatus()));
        responseData.setErpContactId(response.getContactId());

        return responseData;
    }

    @Override
    public SearchDunsResponse searchDuns(String dunsNumber) {
        final SearchDunsRequest request = sapObjectFactory.createSearchDunsRequest();
        request.setDuns(dunsNumber);

        final SearchDunsResponse response = executeSOAPCustomer(request, "if08SearchDuns");
        checkNullResponse(response);

        return response;
    }

    /**
     * Update the customer informations from the SAP response. To avoid calling several times the method {@link ModelService#save(Object)},
     * we add all items to a list and then we call only once the method {@link ModelService#saveAll(java.util.Collection)}.
     *
     * @param response
     *            The SAP response
     * @param customer
     *            the customer to update.
     * @param updateCurrentUserOnly
     */
    private void updateCustomerFromReadCustomerResponse(final ReadCustomerResponse response, final B2BUnitModel customer, final boolean updateCurrentUserOnly) {

        final List<ItemModel> items = new ArrayList<>();
        items.add(customer);
        // update customer info
        updateCustomerDetails(items, response, customer);
        // update the contacts for this company
        updateContactDetails(response, customer, items, updateCurrentUserOnly);
        // update addresses on the B2BUnit level
        updateAddresses(response, customer, items);
        // update currency in the existing cart
        updateCart(items, response);

        // Save all items
        try {
            modelService.saveAll(items);
        } catch (final Exception exp) {
            DistLogUtils.logError(LOG, "An error occur while saving the items", exp, ErrorLogCode.UPDATE_USER_ERROR, ErrorSource.MODEL_SAVING_EXCEPTION_HYBRIS,
                                  exp);
        }
    }

    private void updateCart(final List<ItemModel> items, final ReadCustomerResponse response) {
        final CurrencyModel sapresponsecurrency = getCurrencyFromReadCustomerResponse(response);
        final UserModel currentUser = getUserService().getCurrentUser();
        if (sapresponsecurrency != null && !isGuestUser(currentUser)) {
            currentUser.getCarts().stream().filter(cartModel -> cartCurrencyisDifferentFromSapResponse(sapresponsecurrency, cartModel)).forEach(cart -> {
                cart.setCurrency(sapresponsecurrency);
                items.add(cart);
            });
        }
    }

    protected boolean isGuestUser(final UserModel currentUser) {
        return getUserService().isAnonymousUser(currentUser) || isMemberOfGroup(DistConstants.User.EPROCUREMENTGROUP_UID)
                || isMemberOfGroup(DistConstants.User.B2BEESHOPGROUP_UID) || isMemberOfGroup(DistConstants.User.ARIBACUSTOMERGROUP_UID)
                || isMemberOfGroup(DistConstants.User.CXMLCUSTOMERGROUP_UID);
    }

    protected boolean isMemberOfGroup(final String groupName) {
        return getUserService().isMemberOfGroup(getUserService().getCurrentUser(), getUserService().getUserGroupForUID(groupName));
    }

    private boolean cartCurrencyisDifferentFromSapResponse(final CurrencyModel sapresponsecurrency, final CartModel cartModel) {
        return !sapresponsecurrency.equals(cartModel.getCurrency());
    }

    private void updateContactDetails(final ReadCustomerResponse response, final B2BUnitModel customer, final List<ItemModel> items,
                                      final boolean updateCurrentUserOnly) {

        if (updateCurrentUserOnly) {
            LOG.debug("Updating only the current user!");

            final B2BCustomerModel currentUser = b2bCustomerService.getCurrentB2BCustomer();

            if (currentUser == null) {
                return;
            }
            final ContactWithIdResponse soapContact = response.getContacts().stream()
                                                              .filter(contact -> StringUtils.equals(contact.getContactId(), currentUser.getErpContactID()))
                                                              .findFirst()
                                                              .orElse(null);

            // Adding the contact to list of items to be persisted
            if (items.contains(currentUser)) {
                items.set(items.indexOf(currentUser), currentUser);
            } else {
                items.add(currentUser);
            }

            if (soapContact == null || !response.isActive()) {
                currentUser.setActive(Boolean.FALSE);
                return;
            }
            currentUser.setRsCustomer(soapContact.isRsContact());
            updateAddresses(response, items, currentUser, soapContact);
        } else {
            LOG.debug("Updating all contacts of the customer {}", customer.getErpCustomerID());

            final Map<String, ContactWithIdResponse> sapContactMap = response.getContacts().stream()
                                                                             .collect(toMap(ContactWithIdResponse::getContactId, Function.identity()));

            customer.getMembers().stream() //
                    .filter(B2BCustomerModel.class::isInstance) //
                    .map(B2BCustomerModel.class::cast)
                    .forEach(b2bCustomer -> {
                        // Adding the contact to list of items to be persisted
                        items.add(b2bCustomer);
                        // Set the title of b2bCustomer according to sapContact
                        final ContactWithIdResponse sapContact = sapContactMap.get(b2bCustomer.getErpContactID());
                        if (sapContact == null) {
                            b2bCustomer.setActive(Boolean.FALSE);
                        } else {
                            updateAddresses(response, items, b2bCustomer, sapContact);
                            sapContactMap.remove(b2bCustomer.getErpContactID());
                        }
                    });
        }
    }

    private void updateAddresses(final ReadCustomerResponse response, final List<ItemModel> items, final B2BCustomerModel currentUser,
                                 final ContactWithIdResponse soapContact) {
        if (soapContact.getTitle() != null && StringUtils.isNotBlank(soapContact.getTitle().value())) {
            currentUser.setTitle(titleDao.findTitleBySapCode(soapContact.getTitle().value()));
        }
        // DISTRELEC-4650: SAP send the current currency for the company, then set the session currency for all the contacts
        currentUser.setSessionCurrency(getCurrencyFromReadCustomerResponse(response));
        final AddressModel contactAddress = updateContactLocal(soapContact, currentUser);
        // Adding the contact address to list of items to be persisted
        items.add(contactAddress);
    }

    /**
     * Update the company details from ERP
     *
     * @param items
     * @param response
     *            the SOAP response
     * @param customer
     *            the company to be updated
     */
    private void updateCustomerDetails(List<ItemModel> items, final ReadCustomerResponse response, final B2BUnitModel customer) {
        customer.setActive(response.isActive());
        if (DistConstants.Login.Z100.equals(response.getCustomerType()) || DistConstants.Login.COMPANY.equals(response.getCustomerType())) {
            if (response.isKeyAccountBusinessCustomer()) {
                customer.setCustomerType(B2B_KEY_ACCOUNT);
            } else {
                customer.setCustomerType(de.hybris.platform.commerceservices.enums.CustomerType.B2B);
            }
        } else if (DistConstants.Login.Z200.equals(response.getCustomerType()) || DistConstants.Login.PRIVATE.equals(response.getCustomerType())) {
            customer.setCustomerType(de.hybris.platform.commerceservices.enums.CustomerType.B2C);
        }

        customer.setVatID(StringUtils.isEmpty(response.getVatId()) ? null : response.getVatId());
        customer.setDunsID(StringUtils.isEmpty(response.getDuns()) ? null : response.getDuns());
        customer.setCurrency(getCurrencyFromReadCustomerResponse(response));
        customer.setOnlinePriceCalculation(response.isOnlinePriceCalculation());
        final BigInteger openOrderMaxKeepOpenDays = response.getOpenOrderMaxKeepOpenDays();
        customer.setOpenOrderMaxKeepOpenDays(openOrderMaxKeepOpenDays == null ? 0 : openOrderMaxKeepOpenDays.intValue());
        final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
        currentCustomer.setVat4(response.getVat4());
        currentCustomer.setLegalEmail(response.getLegalEmail());
        currentCustomer.setErpSelectedCustomer(BooleanUtils.isTrue(response.isRsWebOrderOverride()));
        items.add(currentCustomer);
    }

    private CurrencyModel getCurrencyFromReadCustomerResponse(final ReadCustomerResponse response) {
        final CurrencyCode currencyCode = response.getCurrency();
        return currencyCode == null || StringUtils.isEmpty(currencyCode.value()) ? null : getCurrencyFromSAPCode(currencyCode.value().toUpperCase());
    }

    /**
     * Update the company addresses from the ERP.
     *
     * @param response
     *            the SOAP response
     * @param customer
     *            the company to be updated
     * @param items
     *            a list of items to be persisted at the end of the update operations.
     */
    private void updateAddresses(final ReadCustomerResponse response, final B2BUnitModel customer, final List<ItemModel> items) {

        final List<AddressResponse> addresses = CollectionUtils.isEmpty(response.getAddresses()) ? new ArrayList<>() : response.getAddresses();
        final List<AddressModel> erpAddresses = new ArrayList<>();

        for (final AddressResponse sapAddress : addresses) {
            boolean isNew = false;
            AddressModel address = sapCustomerDao.getCustomerAddressForSapId(customer, sapAddress.getAddressId());
            if (address == null) {
                LOG.debug("{} {} Address not found, then create it for Unit: {}, sapAddressId: {} ", ErrorLogCode.ADDRESS_ERROR, ErrorSource.SAP_FAULT,
                          customer.getUid(), sapAddress.getAddressId());

                address = addressService.createAddressForOwner(customer);
                address.setErpAddressID(sapAddress.getAddressId());
                isNew = true;
            }

            address.setBillingAddress(sapAddress.isBillingAddress());
            address.setShippingAddress(sapAddress.isShippingAddress());
            final TitleModel title = sapAddress.getTitle() == null ? null : titleDao.findTitleBySapCode(sapAddress.getTitle().value());
            if (title == null) {
                LOG.debug("{} Title in SAP Response not found: Unit: {}, sapAddressId: {} ", sapAddress.getTitle(), customer.getErpCustomerID(),
                          sapAddress.getAddressId());
            } else {
                address.setTitle(title);
            }
            address.setCompany(sapAddress.getCompanyName1());
            address.setCompanyName2(sapAddress.getCompanyName2());
            address.setCompanyName3(sapAddress.getCompanyName3());
            address.setFirstname(sapAddress.getFirstName());
            address.setLastname(sapAddress.getLastName());
            address.setStreetname(sapAddress.getStreetName());
            address.setStreetnumber(sapAddress.getStreetNumber());
            address.setPostalcode(sapAddress.getPostalCode());
            address.setTown(sapAddress.getTown());
            address.setPhone1(sapAddress.getPhoneNumber());

            String addressCountry = sapAddress.getCountry();
            if (StringUtils.equals(sapAddress.getCountry(), "GB") && StringUtils.equals(sapAddress.getRegion(), "XI")) {
                addressCountry = "XI";
            }

            final List<CountryModel> countries = getCountryDao().findCountriesByCode(addressCountry);
            if (countries == null || countries.isEmpty()) {
                LOG.debug("{} {} Country in SAP Response not found: CountryCode: {}, Unit:  {}, sapAddressId: {}.", ErrorLogCode.ADDRESS_ERROR,
                          ErrorSource.SAP_FAULT, sapAddress.getCountry(), customer.getErpCustomerID(), sapAddress.getAddressId());
            } else {
                final CountryModel country = countries.get(0);
                address.setCountry(country);

                if (StringUtils.isNotEmpty(sapAddress.getRegion())) {
                    final List<RegionModel> regions = getRegionDao().findRegionsByCountryAndCode(country, sapAddress.getRegion());
                    if (regions == null || regions.isEmpty()) {
                        LOG.debug("{} {} Region in SAP Response not found: RegionCode: {}, Unit: {} sapAddressId: {}", ErrorLogCode.ADDRESS_ERROR,
                                  ErrorSource.SAP_FAULT, sapAddress.getRegion(), customer.getErpCustomerID(), sapAddress.getAddressId());
                    } else {
                        address.setRegion(regions.get(0));
                    }
                }
            }

            if (isNew) {
                getModelService().save(address);
            }

            // DISTRELEC-8333 :- default billing & shipping address
            if (sapAddress.isShippingAddress()) {
                customer.setShippingAddress(address);
            }
            if (sapAddress.isBillingAddress()) {
                customer.setBillingAddress(address);
            }

            // Adding the address to the list of ERP addresses
            erpAddresses.add(address);
            items.add(address);
        }

        // Removing addresses which are not in the ERP addresses
        final List<AddressModel> addressesForRemoval = customer.getAddresses().stream().filter(addr -> !erpAddresses.contains(addr))
                                                               .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(addressesForRemoval)) {
            getModelService().removeAll(addressesForRemoval);
        }
    }

    private CurrencyModel getCurrencyFromSAPCode(final String sapCurrencyCode) {
        final List<CurrencyModel> currenciesFound = getCurrencyDao().findCurrenciesByCode(sapCurrencyCode);
        return CollectionUtils.isEmpty(currenciesFound) ? null : currenciesFound.get(0);
    }

    /**
     * Update the contact data and its contact address
     *
     * @param soapContact
     *            the contact data coming from the ERP system
     * @param contact
     *            the Hybris Contact to update
     * @return the contact address.
     */
    private AddressModel updateContactLocal(final ContactWithIdResponse soapContact, final B2BCustomerModel contact) {

        boolean isNew = false;
        AddressModel contactAddress = contact.getContactAddress();
        if (contactAddress == null) {
            contactAddress = addressService.createAddressForUser(contact);
            contactAddress.setContactAddress(Boolean.TRUE);
            contact.setContactAddress(contactAddress);
            isNew = true;
        }

        if (StringUtils.isNotEmpty(soapContact.getFunction())) {
            final DistFunctionModel function = getDistCodelistService().getDistFunction(soapContact.getFunction());
            if (function != null) {
                contact.setDistFunction(function);
            }
        }

        // DISTRELEC-6659: 9999 department code means that the contact is deactivated.
        if (StringUtils.isNotEmpty(soapContact.getDepartment())) {
            if ("9999".equals(soapContact.getDepartment())) {
                contact.setActive(Boolean.FALSE);
            } else {
                if (!BooleanUtils.isTrue(contact.getActive())) {
                    contact.setActive(Boolean.TRUE);
                    contact.setDoubleOptInActivated(false);
                }
                final DistDepartmentModel department = getDistCodelistService().getDistDepartment(soapContact.getDepartment());
                if (department != null) {
                    contactAddress.setDistDepartment(department);
                }
            }
        } else if (!BooleanUtils.isTrue(contact.getActive())) {
            // If the department is empty and the contact is not active, then activate it.
            contact.setActive(Boolean.TRUE);
            contact.setDoubleOptInActivated(false);
        }

        if (StringUtils.isNotEmpty(soapContact.getEmail())) {
            contactAddress.setEmail(soapContact.getEmail());
        }
        if (StringUtils.isNotEmpty(soapContact.getFaxNumber())) {
            contactAddress.setFax(soapContact.getFaxNumber());
        }
        if (StringUtils.isNotEmpty(soapContact.getFirstName())) {
            contactAddress.setFirstname(soapContact.getFirstName());
        }
        if (StringUtils.isNotEmpty(soapContact.getLastName())) {
            contactAddress.setLastname(soapContact.getLastName());
        }
        if (StringUtils.isNotEmpty(soapContact.getPhoneNumber())) {
            contactAddress.setPhone1(soapContact.getPhoneNumber());
        }
        if (StringUtils.isNotEmpty(soapContact.getMobileNumber())) {
            contactAddress.setCellphone(soapContact.getMobileNumber());
        }
        if (StringUtils.isNotEmpty(soapContact.getFaxNumber())) {
            contactAddress.setFax(soapContact.getFaxNumber());
        }

        if (soapContact.getTitle() != null && StringUtils.isNotBlank(soapContact.getTitle().value())) {
            contactAddress.setTitle(titleDao.findTitleBySapCode(soapContact.getTitle().value()));
        }

        if (isNew) {
            getModelService().save(contactAddress);
        }

        return contactAddress;
    }

    private CreateAddressRequest buildCreateAddressRequest(final B2BUnitModel customer, final AddressModel address) {
        final CreateAddressRequest request = sapObjectFactory.createCreateAddressRequest();

        request.setCustomerId(customer.getErpCustomerID());
        request.setSalesOrganization(customer.getSalesOrg().getCode());

        final Address soapAddress = sapObjectFactory.createAddress();
        soapAddress.setTitle(getTitle(address.getTitle()));
        soapAddress.setFirstName(address.getFirstname());
        soapAddress.setLastName(address.getLastname());
        soapAddress.setStreetName(address.getStreetname());
        soapAddress.setStreetNumber(address.getStreetnumber());
        soapAddress.setPostalCode(address.getPostalcode());

        String country = address.getCountry() == null ? null : address.getCountry().getIsocode();
        if (StringUtils.equals(country, "XI")) {
            soapAddress.setCountry("GB");
            soapAddress.setRegion("XI");
        } else {
            soapAddress.setCountry(country);
            soapAddress.setRegion(address.getRegion() == null ? null : address.getRegion().getIsocode());
        }

        soapAddress.setTown(address.getTown());
        soapAddress.setBillingAddress(BooleanUtils.toBoolean(address.getBillingAddress()));
        soapAddress.setShippingAddress(BooleanUtils.toBoolean(address.getShippingAddress()));
        soapAddress.setCompanyName1(address.getCompany());
        soapAddress.setCompanyName2(address.getCompanyName2());
        soapAddress.setCompanyName3(address.getCompanyName3());

        soapAddress.setPhoneNumber(address.getPhone1());
        soapAddress.setPobox(address.getPobox());
        request.setAddress(soapAddress);
        return request;
    }

    private UpdateAddressRequest buildUpdateAddressRequest(final B2BUnitModel customer, final AddressModel address) {
        final UpdateAddressRequest request = sapObjectFactory.createUpdateAddressRequest();
        request.setCustomerId(customer.getErpCustomerID());
        request.setSalesOrganization(customer.getSalesOrg().getCode());

        final AddressWithIdExtended soapAddress = sapObjectFactory.createAddressWithIdExtended();
        soapAddress.setAddressId(address.getErpAddressID());
        soapAddress.setTitle(getTitle(address.getTitle()));
        soapAddress.setFirstName(address.getFirstname());
        soapAddress.setLastName(address.getLastname());
        soapAddress.setStreetName(address.getStreetname());
        soapAddress.setStreetNumber(address.getStreetnumber());
        soapAddress.setPostalCode(address.getPostalcode());

        String country = address.getCountry() == null ? null : address.getCountry().getIsocode();
        if (StringUtils.equals(country, "XI")) {
            soapAddress.setCountry("GB");
            soapAddress.setRegion("XI");
        } else {
            soapAddress.setCountry(country);
            soapAddress.setRegion(address.getRegion() == null ? null : address.getRegion().getIsocode());
        }

        soapAddress.setTown(address.getTown());
        soapAddress.setCompanyName1(address.getCompany());
        soapAddress.setCompanyName2(address.getCompanyName2());
        soapAddress.setCompanyName3(address.getCompanyName3());

        soapAddress.setPhoneNumber(StringUtils.isNotEmpty(address.getPhone1()) ? address.getPhone1() : address.getCellphone());
        soapAddress.setPobox(address.getPobox());
        request.setAddress(soapAddress);

        return request;
    }

    private DeleteAddressRequest buildDeleteAddressRequest(final B2BUnitModel customer, final AddressModel address) {
        final DeleteAddressRequest request = sapObjectFactory.createDeleteAddressRequest();
        request.setCustomerId(customer.getErpCustomerID());
        request.setSalesOrganization(customer.getSalesOrg().getCode());
        request.setAddressId(address.getErpAddressID());
        return request;
    }

    private ReadCustomerRequest buildReadCustomerRequest(final B2BUnitModel customer) {
        final ReadCustomerRequest request = sapObjectFactory.createReadCustomerRequest();
        request.setCustomerId(customer.getErpCustomerID());
        request.setSalesOrganization(customer.getSalesOrg().getCode());

        return request;
    }

    private CreateCustomerRequest buildCreateCustomerRequest(final B2BCustomerModel customer, B2BUnitModel unit) {
        final CreateCustomerRequest request = sapObjectFactory.createCreateCustomerRequest();
        request.setSalesOrganization(unit.getSalesOrg().getCode());
        if (isB2BCustomer(customer)) {
            request.setCustomerType(CustomerType.COMPANY);
            request.setOrganizationNumber(unit.getOrganizationalNumber());
        } else {
            request.setCustomerType(CustomerType.PRIVATE);
        }
        request.setKeyAccountBusinessCustomer(B2B_KEY_ACCOUNT.equals(customer.getCustomerType()));
        request.setVatId(unit.getVatID());
        request.setTitle(getTitle(customer.getTitle()));
        final AddressModel contactAddress;
        if (customer.getContactAddresses().iterator().hasNext()) {
            contactAddress = customer.getContactAddresses().iterator().next();
            request.setFirstName(contactAddress.getFirstname());
            request.setLastName(contactAddress.getLastname());
            request.setPhoneNumber(contactAddress.getPhone1());
            request.setMobileNumber(contactAddress.getCellphone());
            request.setFaxNumber(contactAddress.getFax());
        }

        request.setCompanyName1(unit.getName());
        request.setCompanyName2(unit.getCompanyName2());
        request.setCompanyName3(unit.getCompanyName3());
        request.setDuns(unit.getDunsID());
        request.setCommunicationLanguage(getLocaleFromSession());
        request.setPostalCode(StringUtils.EMPTY);
        request.setStreetName(StringUtils.EMPTY);
        request.setStreetNumber(StringUtils.EMPTY);
        request.setTown(StringUtils.EMPTY);
        request.setCountry(unit.getCountry().getIsocode());
        request.setEmail(customer.getEmail());
        setFunctionAndDepartment(unit, request);

        return request;
    }

    private Title getTitle(TitleModel title) {
        return title == null ? null : Title.fromValue(title.getSapCode());
    }

    private String getDepartmentIfExists(B2BCustomerModel custContact) {
        return custContact.getContactAddress() != null && custContact.getContactAddress().getDistDepartment() != null
                                                                                                                      ? custContact.getContactAddress()
                                                                                                                                   .getDistDepartment()
                                                                                                                                   .getCode()
                                                                                                                      : null;
    }

    private CreateCustomerRequest buildCreateCustomerRequest(final B2BUnitModel customer) {

        final CreateCustomerRequest request = sapObjectFactory.createCreateCustomerRequest();

        final AddressModel contactAddress = customer.getAddresses().iterator().next();

        request.setSalesOrganization(customer.getSalesOrg().getCode());
        if (isB2BCustomer(customer)) {
            request.setCustomerType(CustomerType.valueOf("COMPANY"));
            request.setOrganizationNumber(customer.getOrganizationalNumber());
        } else {
            request.setCustomerType(CustomerType.valueOf("PRIVATE"));
        }
        request.setKeyAccountBusinessCustomer(B2B_KEY_ACCOUNT.equals(customer.getCustomerType()));
        if (null != customer.getVatID() && customer.getSalesOrg().getCode().equals(ITALY_SALESORG_ID)) {
            request.setVatId(customer.getVatID().replace(ITALY_ISO_CODE, StringUtils.EMPTY));
        } else {
            request.setVatId(customer.getVatID());
        }
        request.setTitle(getTitle(contactAddress.getTitle()));
        request.setFirstName(contactAddress.getFirstname());
        request.setLastName(contactAddress.getLastname());
        request.setCompanyName1(contactAddress.getCompany());
        request.setCompanyName2(contactAddress.getCompanyName2());
        request.setCompanyName3(contactAddress.getCompanyName3());
        request.setDuns(customer.getDunsID());
        request.setAdditionalName(contactAddress.getAdditionalAddressCompany());

        request.setCommunicationLanguage(getLocaleFromSession());

        request.setPostalCode(contactAddress.getPostalcode());
        request.setStreetName(contactAddress.getStreetname());
        request.setStreetNumber(contactAddress.getStreetnumber());
        request.setTown(contactAddress.getTown());
        request.setCountry(contactAddress.getCountry() == null ? null : contactAddress.getCountry().getIsocode());
        request.setRegion(contactAddress.getRegion() == null ? null : contactAddress.getRegion().getIsocode());
        request.setEmail(contactAddress.getEmail());
        request.setPhoneNumber(contactAddress.getPhone1());
        request.setMobileNumber(contactAddress.getCellphone());
        request.setFaxNumber(contactAddress.getFax());
        request.setPobox(contactAddress.getPobox());
        setFunctionAndDepartment(customer, request);

        return request;
    }

    private void setFunctionAndDepartment(B2BUnitModel customer, CreateCustomerRequest request) {
        if (CollectionUtils.isNotEmpty(customer.getMembers())) {
            customer.getMembers()
                    .stream()
                    .filter(B2BCustomerModel.class::isInstance)
                    .findFirst()
                    .ifPresent(o -> {
                        // Since we are creating a new customer, then we have only one contact
                        final B2BCustomerModel contact = (B2BCustomerModel) o;
                        request.setFunction(contact.getDistFunction() == null ? null : contact.getDistFunction().getCode());
                        request.setDepartment(getDepartmentIfExists(contact));
                    });
        }
    }

    private boolean isB2BCustomer(final B2BUnitModel customer) {

        if (customer.getCustomerType() != null && customer.getCustomerType().equals(de.hybris.platform.commerceservices.enums.CustomerType.B2C)) {
            return false;
        }

        return true;
    }

    private boolean isB2BCustomer(final B2BCustomerModel customer) {

        if (customer.getCustomerType() != null && customer.getCustomerType().equals(de.hybris.platform.commerceservices.enums.CustomerType.B2C)) {
            return false;
        }

        return true;
    }

    private CheckPrivateCustomerRequest buildCheckPrivateCustomer(final String salesOrganization, final String customerId, final String lastName) {
        final CheckPrivateCustomerRequest request = sapObjectFactory.createCheckPrivateCustomerRequest();
        request.setCustomerId(customerId);
        request.setSalesOrganization(salesOrganization);
        request.setLastName(lastName);
        return request;
    }

    private CheckBusinessCustomerRequest buildCheckBusinessCustomer(final String salesOrganization, final String customerId, final String vatNumber) {
        final CheckBusinessCustomerRequest request = sapObjectFactory.createCheckBusinessCustomerRequest();
        request.setCustomerId(customerId);
        request.setSalesOrganization(salesOrganization);
        request.setVatNumber(vatNumber);

        return request;
    }

    private CreateContactRequest buildCreateContactRequest(final B2BCustomerModel contact) {

        final B2BUnitModel customer = contact.getDefaultB2BUnit();
        final AddressModel contactAddress = contact.getContactAddress();
        final CreateContactRequest request = sapObjectFactory.createCreateContactRequest();
        final Contact soapContact = sapObjectFactory.createContact();
        soapContact.setFirstName(contactAddress.getFirstname());
        soapContact.setLastName(contactAddress.getLastname());
        soapContact.setTitle(getTitle(contactAddress.getTitle()));
        soapContact.setEmail(contact.getEmail());
        soapContact.setPhoneNumber(contactAddress.getPhone1());
        soapContact.setMobileNumber(contactAddress.getCellphone());
        soapContact.setFaxNumber(contactAddress.getFax());
        soapContact.setFunction(contact.getDistFunction() == null ? null : contact.getDistFunction().getCode());
        soapContact.setDepartment(contactAddress.getDistDepartment() == null ? null : contactAddress.getDistDepartment().getCode());

        request.setContact(soapContact);
        request.setCustomerId(customer.getErpCustomerID());
        request.setSalesOrganization(customer.getSalesOrg().getCode());

        return request;
    }

    private UpdateContactRequest buildUpdateContactRequest(final B2BCustomerModel contact) {

        final B2BUnitModel company = contact.getDefaultB2BUnit();
        final AddressModel contactAddress = contact.getContactAddress();
        final UpdateContactRequest request = sapObjectFactory.createUpdateContactRequest();

        request.setCustomerId(company.getErpCustomerID());
        request.setSalesOrganization(company.getSalesOrg().getCode());

        final ContactWithId soapContact = sapObjectFactory.createContactWithId();
        soapContact.setContactId(contact.getErpContactID());
        soapContact.setFirstName(contactAddress.getFirstname());
        soapContact.setLastName(contactAddress.getLastname());
        soapContact.setTitle(getTitle(contactAddress.getTitle()));
        soapContact.setEmail(contact.getEmail());
        soapContact.setPhoneNumber(contactAddress.getPhone1());
        soapContact.setMobileNumber(contactAddress.getCellphone());
        soapContact.setFaxNumber(contactAddress.getFax());

        if (contact.getDefaultB2BUnit().getSalesOrg().getErpSystem() == DistErpSystem.SAP) {
            soapContact.setFunction(contact.getDistFunction() == null ? null : contact.getDistFunction().getCode());
            soapContact.setDepartment(getDepartmentIfExists(contact));
        }

        request.setContact(soapContact);

        return request;
    }

    private UpdateCustomerRequest buildUpdateCustomerRequest(final CustomerModel customer) {

        final B2BUnitModel company = ((B2BCustomerModel) customer).getDefaultB2BUnit();
        final UpdateCustomerRequest request = sapObjectFactory.createUpdateCustomerRequest();

        request.setCustomerId(company.getErpCustomerID());
        request.setSalesOrganization(company.getSalesOrg().getCode());
        request.setVat4(customer.getVat4());
        request.setLegalEmail(customer.getLegalEmail());

        return request;
    }

    private DeleteContactRequest buildDeleteContactRequest(final B2BCustomerModel contact) {

        final B2BUnitModel company = contact.getDefaultB2BUnit();

        final DeleteContactRequest request = sapObjectFactory.createDeleteContactRequest();
        request.setCustomerId(company.getErpCustomerID());
        request.setContactId(contact.getErpContactID());
        request.setSalesOrganization(company.getSalesOrg().getCode());

        return request;
    }

    // generic method for IF-08
    private <R, T> R executeSOAPCustomer(final T request, final String methodName) {
        R response = null;
        final long startTime = System.currentTimeMillis();

        try {
            if (request instanceof ReadCustomerRequest) {
                response = (R) webServiceClient.if08ReadCustomer((ReadCustomerRequest) request);
            } else if (request instanceof CreateCustomerRequest) {
                response = (R) webServiceClient.if08CreateCustomer((CreateCustomerRequest) request);
            } else if (request instanceof CheckBusinessCustomerRequest) {
                response = (R) webServiceClient.if08CheckBusinessCustomer((CheckBusinessCustomerRequest) request);
            } else if (request instanceof CheckPrivateCustomerRequest) {
                response = (R) webServiceClient.if08CheckPrivateCustomer((CheckPrivateCustomerRequest) request);
            } else if (request instanceof CheckUserAuthenticationRequest) {
                response = (R) webServiceClient.if08CheckUserAuthentication((CheckUserAuthenticationRequest) request);
            } else if (request instanceof CreateAddressRequest) {
                response = (R) webServiceClient.if08CreateAddress((CreateAddressRequest) request);
            } else if (request instanceof CreateContactRequest) {
                response = (R) webServiceClient.if08CreateContact((CreateContactRequest) request);
            } else if (request instanceof DeleteAddressRequest) {
                response = (R) webServiceClient.if08DeleteAddress((DeleteAddressRequest) request);
            } else if (request instanceof DeleteContactRequest) {
                response = (R) webServiceClient.if08DeleteContact((DeleteContactRequest) request);
            } else if (request instanceof UpdateAddressRequest) {
                response = (R) webServiceClient.if08UpdateAddress((UpdateAddressRequest) request);
            } else if (request instanceof UpdateContactRequest) {
                response = (R) webServiceClient.if08UpdateContact((UpdateContactRequest) request);
            } else if (request instanceof UpdateCustomerRequest) {
                response = (R) webServiceClient.if08UpdateCustomer((UpdateCustomerRequest) request);
            } else if (request instanceof FindContactRequest) {
                response = (R) webServiceClient.if08FindContact((FindContactRequest) request);
            } else if (request instanceof SearchDunsRequest) {
                response = (R) webServiceClient.if08SearchDuns((SearchDunsRequest) request);
            }
        } catch (final P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, methodName, faultMessage);
            throw new ErpCommunicationException(
                                                ErrorSource.SAP_FAULT + "Error during SAP method: " + methodName + ": "
                                                + faultMessage.getFaultInfo().getFaultText(), faultMessage);
        } catch (final WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, methodName, webServiceException);
            throw new ErpCommunicationException(ErrorSource.WEBSERVICE_EXCEPTION + "Error during SAP method: " + methodName, webServiceException);
        }
        LOG.debug("Call to SAP PI {} took {} ms", methodName, (System.currentTimeMillis() - startTime));
        return response;
    }

    /*
     * Will check the soap response if it's null.
     * @param response
     * @throws ErpCommunicationException
     */
    private <T> void checkNullResponse(final T response) {
        if (response == null) {
            throw new ErpCommunicationException(ErrorSource.SAP_FAULT + "The response was null");
        }
    }

    protected String getLocaleFromSession() {
        LanguageModel currentLanguage = getCommonI18NService().getCurrentLanguage();
        if (currentLanguage != null) {
            String currentLanguageIsocode = currentLanguage.getIsocode().toUpperCase();
            if (currentLanguageIsocode.contains("_")) {
                currentLanguageIsocode = currentLanguageIsocode.substring(0, currentLanguageIsocode.indexOf("_"));
            }
            return currentLanguageIsocode;
        } else {
            return "EN";
        }
    }

    // spring
    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public SIHybrisV1Out getWebServiceClient() {
        return webServiceClient;
    }

    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public SapCustomerDao getSapCustomerDao() {
        return sapCustomerDao;
    }

    public void setSapCustomerDao(final SapCustomerDao sapCustomerDao) {
        this.sapCustomerDao = sapCustomerDao;
    }

    public DistTitleDao getTitleDao() {
        return titleDao;
    }

    public void setTitleDao(final DistTitleDao titleDao) {
        this.titleDao = titleDao;
    }

    public CountryDao getCountryDao() {
        return countryDao;
    }

    public void setCountryDao(final CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    public RegionDao getRegionDao() {
        return regionDao;
    }

    public void setRegionDao(final RegionDao regionDao) {
        this.regionDao = regionDao;
    }

    public CurrencyDao getCurrencyDao() {
        return currencyDao;
    }

    public void setCurrencyDao(final CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public DistrelecCodelistService getDistCodelistService() {
        return distCodelistService;
    }

    public void setDistCodelistService(final DistrelecCodelistService distCodelistService) {
        this.distCodelistService = distCodelistService;
    }

    public AddressService getAddressService() {
        return addressService;
    }

    public void setAddressService(final AddressService addressService) {
        this.addressService = addressService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public B2BCustomerService getB2bCustomerService() {
        return b2bCustomerService;
    }

    public void setB2bCustomerService(B2BCustomerService b2bCustomerService) {
        this.b2bCustomerService = b2bCustomerService;
    }

    protected CustomerNameStrategy getCustomerNameStrategy() {
        return customerNameStrategy;
    }

    public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy) {
        this.customerNameStrategy = customerNameStrategy;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

}
