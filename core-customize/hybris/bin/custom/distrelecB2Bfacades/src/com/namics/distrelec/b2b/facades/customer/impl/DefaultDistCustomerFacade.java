package com.namics.distrelec.b2b.facades.customer.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.CountryIsoCode.*;
import static com.namics.distrelec.b2b.core.constants.DistConstants.UTMParams.*;
import static de.hybris.platform.core.model.security.PrincipalModel.UID;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.distrelec.webservice.sap.v1.AddressResponse;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadCustomerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.namics.distrelec.b2b.core.annotations.CxTransaction;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.inout.erp.CustomerService;
import com.namics.distrelec.b2b.core.inout.erp.data.FindContactRequestData;
import com.namics.distrelec.b2b.core.inout.erp.data.FindContactResponseData;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.model.*;
import com.namics.distrelec.b2b.core.model.process.SetInitialPasswordProcessModel;
import com.namics.distrelec.b2b.core.security.IpAddressService;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerRegistrationService;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.facades.bloomreach.DistBloomreachFacade;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.customer.exceptions.DuplicateEmailException;
import com.namics.distrelec.b2b.facades.customer.exceptions.ExistingCustomerRegistrationException;
import com.namics.distrelec.b2b.facades.customer.exceptions.ExistingCustomerRegistrationException.Reason;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ChannelData;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistSubUserData;
import com.namics.distrelec.occ.core.v2.forms.SetInitialPasswordForm;

import de.hybris.platform.b2b.company.B2BCommercePermissionService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BBudgetExceededPermissionModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bapprovalprocessfacades.company.B2BApproverFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.interceptor.impl.UniqueAttributesInterceptor.AmbiguousUniqueKeysException;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.StandardDateRange;

public class DefaultDistCustomerFacade extends DefaultCustomerFacade implements DistCustomerFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistCustomerFacade.class);

    private static final String CUSTOMER_ALREADY_EXISTS_IN_HYBRIS = "Registration for an existing customer with customer ID %s and contact ID %s failed, because the customer and the contact is already existing in hybris!";

    private static final String FIRST_NAME_CANNOT_BE_EMPTY = "The field [FirstName] cannot be empty";

    private static final String LAST_NAME_CANNOT_BE_EMPTY = "The field [LastName] cannot be empty";

    private static final int NUMBER_OF_YEARS = 100;

    private static final int MAX_FIELD_LENGTH_ERP = 35;

    private static final int NUMBER_OF_COMPANY_NAME_FIELDS = 3;

    private static final int SEPARATOR_LENGTH = 1;

    private static final String INITIAL_PASSWORD_URL = "/de/account/password/setinitialpw";

    @Resource
    private CategoryService categoryService;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private B2BApproverFacade b2bApproverFacade;

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Autowired
    private B2BCommercePermissionService b2bCommercePermissionService;

    @Autowired
    private DistB2BCommerceUnitService b2bCommerceUnitService;

    @Autowired
    private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private DistrelecCodelistService codelistService;

    @Autowired
    @Qualifier("erp.customerService")
    private CustomerService customerService;

    @Autowired
    private DistCustomerRegistrationService distCustomerRegistrationService;

    @Autowired
    private Converter<CountryModel, CountryData> countryConverter;

    @Autowired
    @Qualifier("erp.customerService")
    private CustomerService erpCustomerService;

    @Autowired
    @Qualifier("erp.orderHistoryFacade")
    private OrderHistoryFacade orderHistoryFacade;

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Autowired
    private DistBloomreachFacade distBloomreachFacade;

    @Autowired
    private IpAddressService ipAddressService;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    private static final String QUERY_FOR_ORPHAN_B2BUNIT2APPROVERS = "select {b2a.pk} "
                                                                     + "from {B2BUnit2Approvers as b2a} "
                                                                     + "where {b2a.target} NOT IN ({{ "
                                                                     + "    select {c.pk} from {b2bcustomer as c}"
                                                                     + "}})";

    private static final String B2BUNIT_PARAMETER_NAME_IN_QUERY_FOR_ORPHAN_B2BUNIT2APPROVERS = "b2bUnit";

    /**
     * Retrieves all the approver links between given {@code b2bUnitOptional} where the target {@code B2BCustomer is null}
     *
     * @param b2bUnitOptional
     *            if is empty, do not filter on B2BUnit, if is present filter only on given B2BUnit
     * @return a list of links where the target {@code B2BCustomer is null}
     */
    protected List<LinkModel> getOrphanB2BUnit2Approvers(final Optional<B2BUnitModel> b2bUnitOptional) {
        String queryForOrphanB2bunit2approvers = QUERY_FOR_ORPHAN_B2BUNIT2APPROVERS;
        if (b2bUnitOptional.isPresent()) {
            queryForOrphanB2bunit2approvers = queryForOrphanB2bunit2approvers + " and {b2a.source} = ?"
                                              + B2BUNIT_PARAMETER_NAME_IN_QUERY_FOR_ORPHAN_B2BUNIT2APPROVERS;
        }
        final FlexibleSearchQuery flexibleSearch = new FlexibleSearchQuery(queryForOrphanB2bunit2approvers);
        b2bUnitOptional.ifPresent(b2BUnitModel -> flexibleSearch.addQueryParameter(B2BUNIT_PARAMETER_NAME_IN_QUERY_FOR_ORPHAN_B2BUNIT2APPROVERS, b2BUnitModel));
        final SearchResult<LinkModel> searchResult = flexibleSearchService.search(flexibleSearch);
        return searchResult.getResult();
    }

    /**
     * Deletes the links returned by {@link #getOrphanB2BUnit2Approvers(Optional)}
     *
     * @param b2bUnitOptional
     * @return the count of removed elements
     */
    protected int removeOrphanB2BUnit2Approvers(final Optional<B2BUnitModel> b2bUnitOptional) {
        final List<LinkModel> orphans = getOrphanB2BUnit2Approvers(b2bUnitOptional);
        if (CollectionUtils.isNotEmpty(orphans)) {
            LOG.warn("Removing LinkModels: {}", Arrays.toString(orphans.stream().map(AbstractItemModel::getPk).toArray()));
            getModelService().removeAll(orphans);
        }
        return orphans.size();
    }

    @Override
    public boolean deleteSubUser(final String uid) {
        final DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData(0, 1, null, null);
        final boolean force = CollectionUtils.isEmpty(orderHistoryFacade.getOrderHistory(uid, pageableData, null).getResults());
        return deleteSubUser(uid, force);
    }

    @Override
    public boolean deleteSubUser(final String uid, final boolean force) {
        return distCustomerAccountService.deleteSubUser(uid, force);
    }

    @Override
    public void registerNewCustomer(final DistRegisterData registerData, final CustomerType customerType) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("registerData", registerData);
        final boolean isB2BCustomer = CustomerType.B2B.equals(customerType);
        // begin registration process starting the transaction
        boolean success = false;
        final Transaction tx = Transaction.current();
        tx.begin();

        try {
            final String name = getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName());

            // STEP 1: Create new customer
            final B2BCustomerModel newCustomer = createCustomer(name, registerData, customerType);

            // STEP 2: Create new unit
            final B2BUnitModel unit = createUnit(generateGUID(), customerType, registerData);
            b2bUnitService.addMember(unit, newCustomer);
            newCustomer.setDefaultB2BUnit(unit);

            saveObsolescenceCategories(newCustomer, registerData.isNewsletterOption());

            if (isB2BCustomer) {
                newCustomer.setShowAllOrderhistory(getDefaultShowAllOrderHistory());
            }

            getModelService().save(newCustomer);

            // STEP 3: Create a new contact address for the new contact
            final AddressModel contactAddress = createContactAddress(registerData, newCustomer);
            getModelService().save(contactAddress);
            newCustomer.setContactAddress(contactAddress);
            getModelService().save(newCustomer);

            // STEP 4: Create new shipping/billing address for the new customer
            final AddressModel unitAddress = createUnitAddress(registerData, unit);
            getModelService().save(unitAddress);
            unit.setBillingAddress(unitAddress);
            unit.setShippingAddress(unitAddress);
            getModelService().save(unit);

            // STEP 5: Register the new contact and customer in hybris and the ERP if necessary
            distCustomerAccountService.register(newCustomer, registerData.getPassword());

            if (isB2BCustomer) {
                // STEP 6: Add the new contact as an approver to the new unit
                b2bCommerceUnitService.addApproverToUnit(unit, newCustomer);

                // STEP 7: Create permissions for the new contact
                createPermissionsForCustomer(newCustomer, unit);
            }

            // STEP 8: Fire registration event, to send out the double opt-in mail
            if (registerData.isMarketingCookieEnabled()) {
                distCustomerAccountService.raiseRegistrationEvent(newCustomer, registerData.getRegistrationType());
            }
            registerData.setErpContactId(newCustomer.getErpContactID());
            success = true;
        } catch (final ModelSavingException ex) {
            if (ex.getCause() instanceof AmbiguousUniqueKeysException) {
                throw new DuplicateUidException(ex);
            } else {
                throw new ModelSavingException("Cannot register new customer!", ex);
            }
        } finally {
            if (success) {
                tx.commit();
            } else {
                tx.rollback();
            }
        }
    }

    private boolean getDefaultShowAllOrderHistory() {
        final BaseSiteModel currentSite = getBaseSiteService().getCurrentBaseSite();
        final String configurationKey = "registration.b2b.defaultShowAllOrderHistory." + currentSite.getUid();
        return configurationService.getConfiguration().getBoolean(configurationKey, false);
    }

    private void createPermissionsForCustomer(final B2BCustomerModel customer, final B2BUnitModel unit) {
        final B2BBudgetExceededPermissionModel b2BBudgetExceededPermission = createBudgetExceededPermission(customer, unit);
        b2bCommercePermissionService.addPermissionToCustomer(customer.getUid(), b2BBudgetExceededPermission.getCode());

        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = createRequestQuotationPermission(customer);
        b2bCommercePermissionService.addPermissionToCustomer(customer.getUid(), requestQuotationPermission.getCode());
    }

    @Override
    public void registerExistingB2B(final DistExistingCustomerRegisterData registerData) throws DuplicateUidException, ExistingCustomerRegistrationException {
        registerExistingCustomer(registerData, false, CustomerType.B2B);
    }

    @Override
    public void registerExistingCustomer(final DistExistingCustomerRegisterData registerData, final boolean updateCurrentUserOnly,
                                         final CustomerType customerType) throws DuplicateUidException, ExistingCustomerRegistrationException {
        validateParameterNotNullStandardMessage("registerData", registerData);
        final boolean isB2BCustomer = CustomerType.B2B.equals(customerType);
        // STEP 1: Try to find the customer/contact and validate the response. If the
        // customer or contact was not found an
        // ExistingCustomerRegistrationException is thrown, which has to be handled in
        // the controller to display corresponding error
        // messages in the frontend
        final FindContactResponseData findResult = findExistingCustomerAndContactInERP(registerData);
        final DistSalesOrgModel currentSalesOrg = distSalesOrgService.getCurrentSalesOrg();
        validateFindContactResult(findResult, currentSalesOrg);

        if (isB2BCustomer) {
            validateCompanyName(registerData, currentSalesOrg);
        }

        // STEP 2: The customer was successfully found, so update the registration data
        // based on the found information the ERP and go
        // ahead with the registration
        updateRegistrationData(registerData, findResult);

        boolean success = false;
        final Transaction tx = Transaction.current();
        tx.begin();

        try {
            // STEP 3: Check if a unit is already existing for the given erpCustomerId
            // If yes the contact gets attached to the existing unit, otherwise the unit
            // gets created and the user gets attached to this one
            B2BUnitModel existingUnit = getOrCreateB2BUnit(currentSalesOrg, registerData, customerType);

            // STEP 4: Check if a customer is already existing for the given erpContactId.
            // If the erpContactId is not present than we create a new one
            B2BCustomerModel existingCustomer = getExistingCustomer(registerData, findResult, existingUnit);
            if (existingCustomer != null) {
                // The contact is already existing in hybris. So the customer has probably
                // forgotten his login/password. We should redirect
                // the customer to the corresponding form
                throw new DuplicateUidException(String.format(CUSTOMER_ALREADY_EXISTS_IN_HYBRIS, registerData.getCustomerId(), registerData.getContactId()));
            } else {
                // The contact is NOT existing in hybris, so create it!
                final String name = getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName());
                existingCustomer = createCustomerForExistingCustomer(name, registerData, customerType);
                b2bUnitService.addMember(existingUnit, existingCustomer);
                existingCustomer.setDefaultB2BUnit(existingUnit);

                if (isB2BCustomer) {
                    existingCustomer.setShowAllOrderhistory(getDefaultShowAllOrderHistory());
                }

                saveObsolescenceCategories(existingCustomer, registerData.isNewsletterOption());
                getModelService().save(existingCustomer);

                // STEP 5: Create a new contact address for the new contact
                final AddressModel contactAddress = createContactAddressForExistingCustomer(registerData, existingCustomer);
                getModelService().save(contactAddress);
                existingCustomer.setContactAddress(contactAddress);
                getModelService().save(existingCustomer);

                // STEP 6: Register the new contact and customer in hybris and the ERP if necessary
                distCustomerAccountService.registerExisting(existingCustomer, findResult.isContactFound(), registerData.getPassword(),
                                                            updateCurrentUserOnly);

                if (isB2BCustomer) {
                    // STEP 7: Add the new contact as an approver to the new unit
                    b2bCommerceUnitService.addApproverToUnit(existingUnit, existingCustomer);

                    // STEP 8: Create permissions for the new contact
                    createPermissionsForCustomer(existingCustomer, existingUnit);

                    getUserService().setPassword(existingCustomer, registerData.getPassword());

                    // STEP 9: Fire registration event, to send out the double opt-in mail or
                    // inform the customer service to check the newly
                    // created contact attached to an existing customer
                    distCustomerAccountService.raiseCheckNewCustomerEvent(existingCustomer, registerData.getRegistrationType());
                    distCustomerAccountService.raiseRegistrationEvent(existingCustomer, registerData.getRegistrationType());
                } else {
                    changeCurrentCartUser(existingCustomer.getUid());

                    // STEP 7: Inform the customer service to check the newly
                    // created contact attached to an existing customer
                    distCustomerAccountService.raiseCheckNewCustomerEvent(existingCustomer, getRegistrationTypeForExistingB2CCustomer());
                }

                registerData.setErpContactId(existingCustomer.getErpContactID());
                success = true;
            }
        } catch (final ModelSavingException ex) {
            if (ex.getCause() instanceof AmbiguousUniqueKeysException) {
                throw new DuplicateUidException(ex);
            } else {
                throw new ModelSavingException("Cannot register existing customer!", ex);
            }
        } finally {
            if (success) {
                tx.commit();
            } else {
                tx.rollback();
            }
        }
    }

    private void validateCompanyName(DistExistingCustomerRegisterData registerData, DistSalesOrgModel salesOrg) throws ExistingCustomerRegistrationException {
        ReadCustomerResponse readCustomerResponse = customerService.readCustomer(salesOrg.getCode(), registerData.getCustomerId());
        if (nonNull(readCustomerResponse) && CollectionUtils.isNotEmpty(readCustomerResponse.getAddresses())) {
            final String companyName = getCompanyName(readCustomerResponse);
            if (isBlank(registerData.getCompanyName()) || !StringUtils.equalsIgnoreCase(registerData.getCompanyName(), companyName)) {
                throw new ExistingCustomerRegistrationException(Reason.CUSTOMER_NOT_FOUND.getValue(), Reason.CUSTOMER_NOT_FOUND);
            }
        }
    }

    private B2BUnitModel getOrCreateB2BUnit(final DistSalesOrgModel currentSalesOrg, final DistExistingCustomerRegisterData registerData,
                                            final CustomerType customerType) {
        B2BUnitModel existingUnit = b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, registerData.getCustomerId());
        if (existingUnit == null) {
            existingUnit = createUnitForExistingCustomer(generateGUID(), customerType, registerData);
        } else if (CustomerType.B2B.equals(customerType)) {
            removeOrphanB2BUnit2Approvers(Optional.of(existingUnit));
        }
        return existingUnit;
    }

    private RegistrationType getRegistrationTypeForExistingB2CCustomer() {
        return getCartService().hasSessionCart() ? RegistrationType.CHECKOUT_EXISTING : RegistrationType.STANDALONE_EXISTING;
    }

    private void changeCurrentCartUser(final String userId) {
        if (getCartService().hasSessionCart()) {
            getCartService().changeCurrentCartUser(getUserService().getUserForUID(userId));
        }
    }

    @Override
    public void createB2BEmployee(final DistSubUserData registerData) throws DuplicateUidException {
        final B2BCustomerModel approver = (B2BCustomerModel) getUserService().getCurrentUser();
        createB2BEmployee(registerData, approver);
    }

    @Override
    public void createB2BEmployee(final DistSubUserData registerData, final String approverCustomerUid) throws DuplicateUidException {
        final B2BCustomerModel approverCustomer = getUserService().getUserForUID(approverCustomerUid, B2BCustomerModel.class);
        createB2BEmployee(registerData, approverCustomer);
    }

    @Override
    public void createB2BEmployee(final DistSubUserData registerData, final B2BCustomerModel approverCustomer) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("registerData", registerData);
        Assert.hasText(registerData.getFirstName(), FIRST_NAME_CANNOT_BE_EMPTY);
        Assert.hasText(registerData.getLastName(), LAST_NAME_CANNOT_BE_EMPTY);
        Assert.hasText(registerData.getLogin(), "The field [Login] cannot be empty");
        Assert.notNull(approverCustomer, "Approver is mandatory");

        // begin registration process starting the transaction
        final Transaction tx = Transaction.current();
        tx.begin();
        boolean success = false;
        try {
            final String name = getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName());
            final B2BUnitModel unit = approverCustomer.getDefaultB2BUnit();
            final B2BCustomerModel newCustomer = createEmployee(name, registerData, unit);
            b2bUnitService.addMember(unit, newCustomer);

            saveObsolescenceCategories(newCustomer, true);
            newCustomer.setDefaultB2BUnit(unit);
            saveEmployee(newCustomer);

            final AddressModel address = createContactAddressForSubUser(registerData, newCustomer);
            getModelService().save(address);
            newCustomer.setContactAddress(address);
            getModelService().save(newCustomer);

            distCustomerAccountService.registerB2BEmployee(newCustomer);

            b2bApproverFacade.addApproverForCustomer(newCustomer.getUid(), approverCustomer.getUid());
            distCustomerAccountService.updateConsentConditionRequired(newCustomer, true);

            if (!registerData.isBudgetWithoutLimit()) {
                final DistB2BBudgetModel budget = createBudgetForEmployee(newCustomer, registerData);
                getModelService().save(budget);
                newCustomer.setBudget(budget);
                getModelService().save(newCustomer);
            }

            if (registerData.isRequestQuotationPermission()) {
                boolean requestQuotationPermissionExists = requestQuotationPermissionExists(newCustomer.getPermissions());
                if (!requestQuotationPermissionExists) {
                    final DistB2BRequestQuotationPermissionModel requestQuotationPermission = createRequestQuotationPermission(newCustomer);
                    b2bCommercePermissionService.addPermissionToCustomer(newCustomer.getUid(), requestQuotationPermission.getCode());
                }
            }

            final DistConsentData distConsentData = createConsentData(registerData, newCustomer, unit, true);
            distConsentData.setUid(registerData.getLogin());
            createUserInBloomreach(distConsentData);
            success = true;
        } finally {
            if (success) {
                tx.commit();
            } else {
                tx.rollback();
            }
        }
    }

    private void saveEmployee(final B2BCustomerModel customer) throws DuplicateUidException {
        try {
            getModelService().save(customer);
        } catch (final ModelSavingException ex) {
            if (ex.getCause() instanceof AmbiguousUniqueKeysException) {
                throw new DuplicateUidException(ex);
            } else {
                throw new ModelSavingException("Cannot register B2B employee!", ex);
            }
        }
    }

    private DistB2BBudgetModel createBudgetForEmployee(final B2BCustomerModel customer, final DistSubUserData registerData) {
        final DistB2BBudgetModel budget = getModelService().create(DistB2BBudgetModel.class);
        budget.setCode("Budget_" + customer.getCustomerID());
        budget.setName("Budget for " + customer.getCustomerID());
        budget.setOrderBudget(registerData.getBudgetPerOrder());
        budget.setOriginalYearlyBudget(registerData.getYearlyBudget());
        budget.setYearlyBudget(registerData.getYearlyBudget());
        budget.setActive(Boolean.TRUE);
        budget.setDateRange(createInitialDateRangeForBudget());
        budget.setCurrency(customer.getSessionCurrency());
        return budget;
    }

    private boolean requestQuotationPermissionExists(final Set<B2BPermissionModel> permissions) {
        return CollectionUtils.emptyIfNull(permissions).stream()
                              .anyMatch(DistB2BRequestQuotationPermissionModel.class::isInstance);
    }

    @Override
    public boolean createUserInBloomreach(final DistConsentData distConsentData) {
        try {
            final String registrationRequest = distBloomreachFacade.createBloomreachRegistrationRequest(distConsentData);
            distBloomreachFacade.sendBatchRequestToBloomreach(registrationRequest);
            return true;
        } catch (IOException | DistBloomreachBatchException e) {
            LOG.error("Error occurred in Bloomreach during registration of customer: {}", distConsentData.getUid(), e);
            return false;
        }
    }

    @Override
    public void resendSetInitialPasswordEmail(final String uid) throws DuplicateUidException {
        distCustomerAccountService.resendSetInitialPasswordEmail((CustomerModel) getUserService().getUserForUID(uid));
    }

    @Override
    public void updateB2BEmployee(final DistSubUserData subUserData, final B2BCustomerModel customer) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("registerData", subUserData);
        Assert.hasText(subUserData.getFirstName(), FIRST_NAME_CANNOT_BE_EMPTY);
        Assert.hasText(subUserData.getLastName(), LAST_NAME_CANNOT_BE_EMPTY);

        // begin registration process starting the transaction
        final Transaction tx = Transaction.current();
        tx.begin();
        boolean success = false;
        try {

            final TitleModel title = getUserService().getTitleForCode(subUserData.getTitleCode());
            customer.setTitle(title);
            customer.setName(getCustomerNameStrategy().getName(subUserData.getFirstName(), subUserData.getLastName()));
            setCustomerFunction(customer, subUserData.getFunctionCode());

            final AddressModel contactAddress = customer.getContactAddress();
            if (contactAddress != null) {
                updateContactAddress(contactAddress, title, subUserData);
                customer.setEmail(subUserData.getEmail());
                customer.setUid(subUserData.getEmail());
                getModelService().save(customer);
                getModelService().save(contactAddress);
            }

            final DistB2BBudgetModel budget = customer.getBudget();
            if (subUserData.isBudgetWithoutLimit()) {
                removeExistingBudget(budget);
            } else {
                createOrUpdateBudgetForEmployee(budget, customer, subUserData);
            }

            if (subUserData.isRequestQuotationPermission()) {
                boolean requestQuotationPermissionExists = requestQuotationPermissionExists(customer.getPermissions());
                if (requestQuotationPermissionExists) {
                    enableQuotationPermissions(customer.getPermissions());
                } else {
                    final DistB2BRequestQuotationPermissionModel requestQuotationPermission = createRequestQuotationPermission(customer);
                    b2bCommercePermissionService.addPermissionToCustomer(customer.getUid(), requestQuotationPermission.getCode());
                }
            } else {
                disableQuotationPermissions(customer.getPermissions());
            }

            saveObsolescenceCategories(customer, true);
            getModelService().save(customer);
            customerService.updateContact(customer);

            final DistConsentData consentData = createConsentData(subUserData, customer, customer.getDefaultB2BUnit(), false);
            consentData.setUid(subUserData.getEmail());
            updateUserInBloomreach(consentData);
            success = true;
        } catch (final ModelSavingException ex) {
            if (ex.getCause() instanceof AmbiguousUniqueKeysException) {
                throw new DuplicateUidException(ex);
            } else {
                throw new ModelSavingException("Cannot update B2B employee!", ex);
            }
        } finally {
            if (success) {
                tx.commit();
            } else {
                tx.rollback();
            }
        }
    }

    private void updateContactAddress(final AddressModel contactAddress, final TitleModel title, final DistSubUserData subUserData) {
        contactAddress.setTitle(title);
        contactAddress.setFirstname(subUserData.getFirstName());
        contactAddress.setLastname(subUserData.getLastName());
        contactAddress.setEmail(subUserData.getEmail());
        contactAddress.setPhone1(subUserData.getPhoneNumber());
        contactAddress.setCellphone(subUserData.getMobileNumber());
        contactAddress.setFax(subUserData.getFaxNumber());
    }

    private void removeExistingBudget(final DistB2BBudgetModel budget) {
        if (budget != null) {
            getModelService().remove(budget);
        }
    }

    private void createOrUpdateBudgetForEmployee(final DistB2BBudgetModel budget, final B2BCustomerModel customer, final DistSubUserData subUserData) {
        if (budget == null) {
            final DistB2BBudgetModel newBudget = createBudgetForEmployee(customer, subUserData);
            getModelService().save(newBudget);
            customer.setBudget(newBudget);
        } else {
            updateBudgetForEmployee(budget, subUserData);
            getModelService().save(budget);
        }
    }

    private void updateBudgetForEmployee(final DistB2BBudgetModel budget, final DistSubUserData subUserData) {
        final BigDecimal orderBudget = subUserData.getBudgetPerOrder();
        budget.setOrderBudget(orderBudget);
        final BigDecimal yearlyBudget = subUserData.getYearlyBudget();
        if (yearlyBudget != null && !yearlyBudget.equals(budget.getOriginalYearlyBudget())) {
            budget.setOriginalYearlyBudget(yearlyBudget);
            budget.setYearlyBudget(yearlyBudget);
        }
        budget.setActive(Boolean.TRUE);
    }

    private void enableQuotationPermissions(final Set<B2BPermissionModel> permissions) {
        CollectionUtils.emptyIfNull(permissions).stream()
                       .filter(permission -> permission instanceof DistB2BRequestQuotationPermissionModel && BooleanUtils.isFalse(permission.getActive()))
                       .forEach(permission -> updateQuotationPermission(permission, Boolean.TRUE));
    }

    private void disableQuotationPermissions(final Set<B2BPermissionModel> permissions) {
        CollectionUtils.emptyIfNull(permissions).stream()
                       .filter(permission -> permission instanceof DistB2BRequestQuotationPermissionModel && BooleanUtils.isTrue(permission.getActive()))
                       .forEach(permission -> updateQuotationPermission(permission, Boolean.FALSE));
    }

    private void updateQuotationPermission(final B2BPermissionModel permission, final boolean isActive) {
        permission.setActive(isActive);
        getModelService().save(permission);
    }

    private DistConsentData createConsentData(final DistSubUserData subUserData, final B2BCustomerModel customer, final B2BUnitModel unit,
                                              final boolean isRegistration) {
        final DistConsentData consentData = new DistConsentData();
        consentData.setFirstName(subUserData.getFirstName());
        consentData.setLastName(subUserData.getLastName());
        consentData.setTitleCode(subUserData.getTitleCode());
        consentData.setErpContactId(customer.getErpContactID());
        consentData.setPhoneNumber(subUserData.getPhoneNumber());
        consentData.setMobileNumber(subUserData.getMobileNumber());
        if (unit != null && unit.getCountry() != null) {
            consentData.setCountryCode(unit.getCountry().getIsocode());
        }
        if (isRegistration) {
            consentData.setRole(subUserData.getFunctionCode());
            consentData.setIsRegistration(true);
            consentData.setActiveSubscription(Boolean.FALSE);
            consentData.setNpsSubscription(Boolean.FALSE);
            consentData.setIsAnonymousUser(Boolean.FALSE);
        }
        return consentData;
    }

    private void updateUserInBloomreach(final DistConsentData consentData) {
        try {
            distBloomreachFacade.updateCustomerInBloomreach(consentData);
        } catch (DistBloomreachBatchException | JsonProcessingException e) {
            LOG.error("Error occurred during updating customer in Bloomreach.", e);
        }
    }

    @Override
    @CxTransaction
    public void createGuestUserForAnonymousCheckout(final String name, final DistRegisterData registerData) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("email", registerData.getEmail());
        final String guid = generateGUID();
        registerData.setLogin(guid + DistConstants.Punctuation.PIPE + registerData.getEmail());
        final B2BCustomerModel guestCustomer = createCustomer(name, registerData, CustomerType.GUEST);
        final B2BUnitModel unit = createUnit(generateGUID(), CustomerType.GUEST, registerData);
        b2bUnitService.addMember(unit, guestCustomer);
        guestCustomer.setDefaultB2BUnit(unit);
        guestCustomer.setType(CustomerType.GUEST);
        distCustomerAccountService.registerGuest(guestCustomer, guid);
        getStoreSessionFacade().setCurrentChannel(SiteChannel.B2C.getCode());
        final CustomerData customer = b2BCustomerConverter.convert(guestCustomer);
        updateCartWithGuestForAnonymousCheckout(customer);
    }

    @Override
    public void updateCartWithGuestForAnonymousCheckout(final CustomerData guestCustomerData) {
        if (getCartService().hasSessionCart()) {
            resetSessionCart();
            getCartService().changeCurrentCartUser(getUserService().getUserForUID(guestCustomerData.getUid()));
        }

        if (!updateSessionCurrency(guestCustomerData.getCurrency(), getStoreSessionFacade().getDefaultCurrency())) {
            getUserFacade().syncSessionCurrency();
        }

        if (!updateSessionLanguage(guestCustomerData.getLanguage(), getStoreSessionFacade().getDefaultLanguage())) {
            getUserFacade().syncSessionLanguage();
        }
    }

    private void resetSessionCart() {
        CartModel sessionCart = getCartService().getSessionCart();
        sessionCart.setDeliveryAddress(null);
        sessionCart.setDeliveryMode(null);
        sessionCart.setPaymentInfo(null);
        getCartService().saveOrder(sessionCart);
    }

    @Override
    public void forgottenPassword(final String uid) {
        try {
            Assert.hasText(uid, "The field [id] cannot be empty");
            final Map<String, Object> processMap = Map.of(UID, uid.toLowerCase(Locale.ENGLISH));
            ForgottenPasswordProcessModel forgottenPasswordProcessModel = getForgottenPasswordProcessModel(uid, processMap);
            getBusinessProcessService().startProcess(forgottenPasswordProcessModel);
        } catch (final UnknownIdentifierException exp) {
            final List<B2BCustomerModel> customers = distCustomerAccountService.getCustomersByEmail(uid);
            if (customers.isEmpty()) {
                throw exp;
            } else if (customers.size() == 1) {
                super.forgottenPassword(customers.get(0).getUid());
            } else {
                throw new DuplicateEmailException("Multiple accounts are using the same e-mail address.");
            }
        }
    }

    private ForgottenPasswordProcessModel getForgottenPasswordProcessModel(String uid, Map<String, Object> processMap) {
        ForgottenPasswordProcessModel forgottenPasswordProcessModel = getBusinessProcessService()
                                                                                                 .createProcess("forgottenPassword-" + uid + "-"
                                                                                                                + System.currentTimeMillis(),
                                                                                                                "forgottenPasswordProcess", processMap);
        forgottenPasswordProcessModel.setSite(getBaseSiteService().getCurrentBaseSite());
        Boolean isStorefrontRequest = getSessionService().getAttribute(DistConstants.Session.IS_STOREFRONT_REQUEST);
        forgottenPasswordProcessModel.setStorefrontRequest(Objects.requireNonNullElse(isStorefrontRequest, false));
        return forgottenPasswordProcessModel;
    }

    /**
     * Generates a customer ID during registration
     */
    @Override
    public String generateGUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void loginSuccess() {
        final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();

        if (!BooleanUtils.isTrue(currentUser.getActive())) {
            LOG.warn("Non-active user login trial: {}", currentUser.getUid());
            return;
        }

        // Set current country to users billing address country
        CountryModel countryModel = null;
        for (final AddressModel addressModel : currentUser.getAddresses()) {
            if (BooleanUtils.isTrue(addressModel.getBillingAddress()) && addressModel.getCountry() != null) {
                countryModel = addressModel.getCountry();
            }
        }
        if (countryModel == null) {
            for (final AddressModel addressModel : currentUser.getDefaultB2BUnit().getAddresses()) {
                if (BooleanUtils.isTrue(addressModel.getBillingAddress()) && addressModel.getCountry() != null) {
                    countryModel = addressModel.getCountry();
                }
            }
        }
        if (countryModel != null) {
            getStoreSessionFacade().setCurrentCountry(countryModel.getIsocode());
        }

        // Touch last login timestamp
        currentUser.setLastLogin(new Date());
        getModelService().save(currentUser);

        // Update the session Channel
        updateSessionChannel(currentUser.getCustomerType(), getStoreSessionFacade().getCurrentChannel());

        final CustomerData customerData = getCurrentCustomer();

        // Update session currency on user using the currency set on company
        distCustomerAccountService.updateSessionCurrency(currentUser);

        // First thing to do is to try to change the user on the session cart
        if (getCartService().hasSessionCart()) {
            getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
        }

        if (!updateSessionLanguage(customerData.getLanguage(), getStoreSessionFacade().getDefaultLanguage())) {
            // Update the user
            getUserFacade().syncSessionLanguage();
        }

        // Calculate the cart after setting everything up
        if (getCartService().hasSessionCart()) {
            try {
                // // THIS LINE OF CODE HAS BEEN DISABLED BECAUSE OF THIS TICKET: DISTRELEC-6523
                // set correct currency on each cart from the user, in case it has been modified
                // by the ERP
                // ((DistCommerceCartService)
                // getCommerceCartService()).updateCartCurrencies(currentUser);

                final CommerceCartParameter parameter = new CommerceCartParameter();
                parameter.setEnableHooks(true);
                parameter.setCart(getCartService().getSessionCart());
                getCommerceCartService().calculateCart(parameter);
            } catch (final IllegalStateException e) {
                LOG.error("{} Error occured during after login process! {}", ErrorLogCode.CART_CALCULATION_ERROR.getCode(), e.getMessage());
            }
        }

    }

    @Override
    public void checkAndUpdateCustomer() {
        final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
        // Update current User User Data
        try {
            distCustomerAccountService.requestUpdatetUserInformation(currentUser);
        } catch (final ErpCommunicationException e) {
            LOG.error("{} {} Error occured during updating customer: {} data! {} ", ErrorLogCode.UPDATE_USER_ERROR, ErrorSource.SAP_FAULT, currentUser.getUid(),
                      e.getMessage());
            if (e.getCause() instanceof P1FaultMessage) {
                currentUser.setActive(Boolean.FALSE);
                getModelService().save(currentUser);
            }
        }

    }

    @Override
    public void checkoutForgottenPassword(final String email) {
        final Map<String, Object> processMap = Map.of(UID, email.toLowerCase(Locale.ENGLISH));
        final ForgottenPasswordProcessModel forgottenPasswordProcessModel = getBusinessProcessService()
                                                                                                       .createProcess("forgottenPassword-" + email + "-"
                                                                                                                      + System.currentTimeMillis(),
                                                                                                                      "forgottenPasswordProcess", processMap);
        forgottenPasswordProcessModel.setSite(getBaseSiteService().getCurrentBaseSite());
        forgottenPasswordProcessModel.setInCheckout(Boolean.TRUE);
        Boolean isStorefrontRequest = getSessionService().getAttribute(DistConstants.Session.IS_STOREFRONT_REQUEST);
        forgottenPasswordProcessModel.setStorefrontRequest(Objects.requireNonNullElse(isStorefrontRequest, false));
        getBusinessProcessService().startProcess(forgottenPasswordProcessModel);
    }

    @Override
    public void storeIPAddress() {
        final UserModel currentUser = getUserService().getCurrentUser();
        HttpServletRequest request = ipAddressService.getRequest();
        currentUser.setLastUserAgent(request.getHeader("User-Agent"));
        currentUser.setLastIPAddress(ipAddressService.getClientIpAddress());
        getModelService().save(currentUser);
        getModelService().refresh(currentUser);
    }

    @Override
    protected boolean updateSessionCurrency(final CurrencyData preferredCurrency, final CurrencyData defaultCurrency) {
        if (preferredCurrency != null) {
            final String currencyIsoCode = preferredCurrency.getIsocode();

            // Get the available currencies and check if the currency iso code is supported
            final Collection<CurrencyModel> currencies = getCommonI18NService().getAllCurrencies();
            for (final CurrencyModel currency : currencies) {
                if (StringUtils.equals(currency.getIsocode(), currencyIsoCode)) {
                    // Set the current currency
                    getStoreSessionFacade().setCurrentCurrency(currencyIsoCode);
                    return true;
                }
            }
        }

        // Fallback to the default
        getStoreSessionFacade().setCurrentCurrency(defaultCurrency.getIsocode());
        return false;
    }

    @Override
    public void updateSelectedProfileInformation(final CustomerData customerData) throws DuplicateUidException {
        final B2BCustomerModel customer = (B2BCustomerModel) getCurrentSessionCustomer();
        AddressModel contactAddress = customer.getContactAddress();
        if (contactAddress == null) {
            if (customer.getDefaultPaymentAddress() != null) {
                contactAddress = customer.getDefaultPaymentAddress();
            } else if (customer.getDefaultShipmentAddress() != null) {
                contactAddress = customer.getDefaultShipmentAddress();
            } else if (customer.getAddresses() != null && CollectionUtils.isNotEmpty(customer.getAddresses())) {
                contactAddress = customer.getAddresses().iterator().next();
            }
            customer.setContactAddress(contactAddress);
        }
        if (contactAddress != null && customerData.getDepartment() != null) {
            contactAddress.setDistDepartment(codelistService.getDistDepartment(customerData.getDepartment()));
            getModelService().save(contactAddress);// Update contactAddress in hybris
        }
        if (customerData.getFunctionCode() != null) {
            customer.setDistFunction(codelistService.getDistFunction(customerData.getFunctionCode()));
        }
        getModelService().save(customer); // Update contact in hybris
        erpCustomerService.updateContact(customer); // Update contact in sap
    }

    @Override
    public void updateProfile(final CustomerData customerData) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("customerData", customerData);
        Assert.hasText(customerData.getFirstName(), FIRST_NAME_CANNOT_BE_EMPTY);
        Assert.hasText(customerData.getLastName(), LAST_NAME_CANNOT_BE_EMPTY);
        Assert.hasText(customerData.getUid(), "The field [Uid] cannot be empty");

        final CustomerModel customer = getCurrentSessionCustomer();
        getCustomerReversePopulator().populate(customerData, customer);

        if (customer.getDefaultPaymentAddress() != null) {
            getModelService().save(customer.getDefaultPaymentAddress());
        }

        if (customer.getDefaultShipmentAddress() != null) {
            getModelService().save(customer.getDefaultShipmentAddress());
        }

        if (customer.getContactAddress() != null) {
            getModelService().save(customer.getContactAddress());
        }

        getModelService().save(customer);

        // update the customer (contact) also in ERP
        customerService.updateContact((B2BCustomerModel) customer);
    }

    @Override
    public List<CustomerData> searchEmployees(final String name, final String stateCode, final String sortCode) {
        return searchEmployees(name, stateCode, sortCode, false);
    }

    @Override
    public List<CustomerData> searchEmployees(final String name, final String stateCode, final String sortCode, final boolean includeCurrentUser) {
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
        final B2BUnitModel unit = b2bUnitService.getParent(currentCustomer);

        final Map<String, Object> params = new HashMap<>();
        final StringBuilder query = new StringBuilder("SELECT {c:")
                                                                   .append(B2BCustomerModel.PK)
                                                                   .append("} FROM {").append(B2BCustomerModel._TYPECODE).append(" AS c JOIN ")
                                                                   .append(B2BUnitModel._TYPECODE).append(" AS un ON {c:")
                                                                   .append(B2BCustomerModel.DEFAULTB2BUNIT).append("}={un:pk} LEFT JOIN ")
                                                                   .append(DistB2BBudgetModel._TYPECODE).append(" AS bd ON {bd:pk}={c:budget}} WHERE {un:")
                                                                   .append(B2BUnitModel.PK).append("}=").append(unit.getPk())
                                                                   .append((includeCurrentUser ? StringUtils.EMPTY
                                                                                               : " AND {c:pk} != " + currentCustomer.getPk()))
                                                                   .append(" AND {c:").append(B2BCustomerModel.ACTIVE).append("}=1");

        if (StringUtils.isNotBlank(name)) {
            params.put("name", ("%" + name + "%").toLowerCase());
            query.append(" AND LOWER({c:" + B2BCustomerModel.NAME + "}) LIKE ?name");
        }
        if (StringUtils.isNotBlank(stateCode)) {
            if (stateCode.equalsIgnoreCase(DistConstants.Company.USER_CONFIRMATION_PENDING)) {
                query.append(" AND {c:" + B2BCustomerModel.DOUBLEOPTINACTIVATED + "}=0");
            } else if (stateCode.equalsIgnoreCase(DistConstants.Company.ACTIVE)) {
                query.append(" AND {c:" + B2BCustomerModel.DOUBLEOPTINACTIVATED + "}=1");
            }
        }

        if (sortCode == null) {
            query.append(" ORDER BY {c:").append(CustomerModel.NAME).append("} ASC");
        } else {
            final String[] sort = sortCode.split(":");
            if (sort.length == 2) {
                if ("byName".equals(sort[0])) {
                    query.append(" ORDER BY {c:").append(CustomerModel.NAME).append("} ").append(sort[1].toUpperCase());
                } else if ("byStatus".equals(sort[0])) {
                    query.append(" ORDER BY {c:").append(B2BCustomerModel.ACTIVE).append("}, {c:").append(B2BCustomerModel.DOUBLEOPTINACTIVATED).append("} ")
                         .append(sort[1].toUpperCase());
                } else if ("byYearlyBudget".equals(sort[0])) {
                    query.append(" ORDER BY {bd:").append(DistB2BBudgetModel.YEARLYBUDGET).append("} ").append(sort[1].toUpperCase());
                } else {
                    query.append(" ORDER BY {c:").append(CustomerModel.NAME).append("} ASC");
                }
            } else {
                query.append(" ORDER BY {c:").append(CustomerModel.NAME).append("} ASC");
            }
        }

        final SearchResult<B2BCustomerModel> result = flexibleSearchService.search(query.toString(), params.isEmpty() ? null : params);
        if (result.getResult().isEmpty()) {
            return Collections.emptyList();
        }

        final List<CustomerData> customers = new ArrayList<>();
        for (final B2BCustomerModel c : result.getResult()) {
            boolean ok = isB2BAdminUser(c);
            if (!ok && CollectionUtils.isNotEmpty(c.getApprovers())) {
                for (final B2BCustomerModel approver : c.getApprovers()) {
                    if (approver.getPk().getLongValue() == currentCustomer.getPk().getLongValue()) {
                        ok = true;
                        break;
                    }
                }
            }

            if (ok) {
                customers.add(b2BCustomerConverter.convert(c));
            }
        }
        return customers;
    }

    @Override
    public boolean canEditCompanyName(final AddressData addressdata) {
        return ((DistUserFacade) getUserFacade()).canEditCompanyName(distCustomerAccountService.getAddressForCode(getCurrentSessionCustomer(),
                                                                                                                  addressdata.getId()));
    }

    @Override
    public CustomerData getCustomerDataForUid(final String uid) {
        if (StringUtils.isNotBlank(uid)) {
            final UserModel user = getUserService().getUserForUID(uid);
            if (user != null) {
                return b2BCustomerConverter.convert((B2BCustomerModel) user);
            }
        }
        return null;
    }

    @Override
    public void forgottenMigratedUsersPassword(final String login, final String email) {
        Assert.hasText(login, "The field [uid] cannot be empty");
        Assert.hasText(email, "The field [email] cannot be empty");
        final B2BCustomerModel customerModel = getUserService().getUserForUID(login, B2BCustomerModel.class);
        if (StringUtils.equalsIgnoreCase(customerModel.getEmail(), email)) {
            getCustomerAccountService().forgottenPassword(customerModel);
        } else {
            throw new UnknownIdentifierException("Typed in email does not match with the one from the user. UID: '" + login + "' E-Mail: '" + email + "'");
        }
    }

    @Override
    public List<CountryData> getCountriesForRegistration(final SiteChannel channel) {
        return Converters.convertAll(distCustomerRegistrationService.getCountriesForRegistration(channel), countryConverter);
    }

    @Override
    public boolean updateShowProductBox(final boolean newValue) {
        return distCustomerAccountService.updateShowProductBox(getUserService().getCurrentUser(), newValue);
    }

    protected void validateFindContactResult(final FindContactResponseData findResult, final DistSalesOrgModel currentSalesOrg)
                                                                                                                                throws ExistingCustomerRegistrationException {
        if (!findResult.isCustomerFound()) {
            throw new ExistingCustomerRegistrationException(Reason.CUSTOMER_NOT_FOUND.getValue(), findResult, Reason.CUSTOMER_NOT_FOUND);
        }

        if (!findResult.isContactFound() && !currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()) {
            throw new ExistingCustomerRegistrationException(Reason.SELF_REGISTRATION_DISABLED.getValue(), findResult, Reason.SELF_REGISTRATION_DISABLED);
        }

        if (!findResult.isContactUnique()) {
            throw new ExistingCustomerRegistrationException("No unique contact was found in the ERP!", findResult, Reason.CONTACT_NOT_UNIQUE);
        }
    }

    protected FindContactResponseData findExistingCustomerAndContactInERP(final DistExistingCustomerRegisterData registerData) {
        final FindContactRequestData findContact = new FindContactRequestData();
        findContact.setErpCustomerId(registerData.getCustomerId());
        findContact.setFirstName(registerData.getFirstName());
        findContact.setLastName(registerData.getLastName());
        findContact.setEmail(registerData.getEmail());
        findContact.setSalesOrganization(distSalesOrgService.getCurrentSalesOrg().getCode());
        findContact.setOrganizationalNumber(registerData.getOrganizationalNumber());
        findContact.setVatNumber(registerData.getVatId());
        return customerService.findContact(findContact);
    }

    private void updateRegistrationData(final DistExistingCustomerRegisterData registerData, final FindContactResponseData findResult) {
        if (StringUtils.contains(findResult.getErpCustomerId(), registerData.getCustomerId())) {
            registerData.setCustomerId(findResult.getErpCustomerId());
            registerData.setContactId(findResult.getErpContactId());
        } else {
            throw new ErpCommunicationException("The returned erpCustomerId (" + findResult.getErpCustomerId() + ") is different than the requested one ("
                                                + registerData.getCustomerId() + ")!");
        }
    }

    @Override
    public boolean searchCustomer(final String customerId, final String customerName) {
        try {
            ReadCustomerResponse readcustomerResponse = customerService.readCustomer(distSalesOrgService.getCurrentSalesOrg().getCode(), customerId);
            if (CollectionUtils.isNotEmpty(readcustomerResponse.getAddresses())) {
                String companyName = getCompanyName(readcustomerResponse);
                LOG.info("companyName Found in ERP::{}", companyName);
                if (StringUtils.equalsIgnoreCase(customerName, companyName)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        } catch (ErpCommunicationException e) {
            LOG.warn("Could not find Customer in ERP: {}", customerId);
            return Boolean.FALSE;
        }
    }

    private String getCompanyName(ReadCustomerResponse readcustomerResponse) {
        AddressResponse addressResponse = readcustomerResponse.getAddresses().iterator().next();
        return Stream.of(addressResponse.getCompanyName1(), addressResponse.getCompanyName2(), addressResponse.getCompanyName3())
                     .filter(Objects::nonNull)
                     .collect(Collectors.joining(DistConstants.Punctuation.SPACE));
    }

    protected B2BUnitModel createUnit(final String uid, final CustomerType customerType, final DistRegisterData registerData) {
        final B2BUnitModel unit = getModelService().create(B2BUnitModel.class);
        unit.setUid(uid);
        unit.setCustomerType(customerType);

        // Setting the company name + locName (both current language and EN which
        // mandatory)
        final String unitName = CustomerType.B2B.equals(customerType)
                                                                      ? registerData.getCompanyName()
                                                                      : (registerData.getFirstName() + " " + registerData.getLastName());
        unit.setName(unitName);
        unit.setLocName(unitName);
        unit.setDunsID(registerData.getDuns());
        unit.setInvoiceEmail(registerData.getInvoiceEmail());
        unit.setVatID(registerData.getVatId());
        unit.setMovexCustomerID(registerData.getCustomerId());
        unit.setOrganizationalNumber(registerData.getOrganizationalNumber());
        unit.setSalesOrg(distSalesOrgService.getCurrentSalesOrg());
        if (StringUtils.isNotBlank(registerData.getCurrencyCode())) {
            unit.setCurrency(getCommonI18NService().getCurrency(registerData.getCurrencyCode()));
        }
        if (StringUtils.isNotBlank(registerData.getCountryCode())) {
            // DISTRELEC-7348 very important for the ExportShop to store the original
            // register country.
            final CountryModel country = getCommonI18NService().getCountry(registerData.getCountryCode());
            unit.setCountry(country);
        } else {
            unit.setCountry(namicsCommonI18NService.getCurrentCountry());
        }
        unit.setCompanyName2(registerData.getCompanyName2());
        unit.setCompanyName3(registerData.getCompanyName3());
        return unit;
    }

    protected B2BUnitModel createUnitForExistingCustomer(final String uid, final CustomerType customerType,
                                                         final DistExistingCustomerRegisterData registerData) {
        final B2BUnitModel unit = getModelService().create(B2BUnitModel.class);
        unit.setUid(uid);
        unit.setCustomerType(customerType);

        // Setting the company name + locName (both current language and EN which
        // mandatory)
        final String unitName = CustomerType.B2B.equals(customerType) || CustomerType.B2B_KEY_ACCOUNT.equals(customerType)
                                                                                                                           ? registerData.getCompanyName()
                                                                                                                           : (registerData.getFirstName() + " "
                                                                                                                              + registerData.getLastName());
        unit.setName(unitName);
        unit.setLocName(unitName);

        unit.setVatID(registerData.getVatId());
        unit.setOrganizationalNumber(registerData.getOrganizationalNumber());
        unit.setSalesOrg(distSalesOrgService.getCurrentSalesOrg());
        unit.setErpCustomerID(registerData.getCustomerId());
        // the unit must be persisted because later on is used to lookup existing
        // contacts
        getModelService().save(unit);
        return unit;
    }

    private B2BCustomerModel createEmployee(final String name, final DistSubUserData registerData, final B2BUnitModel unit) {
        final B2BCustomerModel customer = getModelService().create(B2BCustomerModel.class);
        final Set<PrincipalGroupModel> groups = new HashSet<>();
        groups.add(getUserService().getUserGroupForUID(B2BConstants.B2BCUSTOMERGROUP));

        customer.setGroups(groups);
        final TitleModel title = getUserService().getTitleForCode(registerData.getTitleCode());
        setUidData(registerData.getLogin(), customer);
        customer.setTitle(title);
        customer.setName(name);
        customer.setCustomerType(unit.getCustomerType());
        customer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
        customer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
        customer.setEmail(registerData.getLogin());
        setCustomerFunction(customer, registerData.getFunctionCode());
        return customer;
    }

    private B2BCustomerModel createCustomer(final String name, final DistRegisterData registerData, final CustomerType customerType) {
        final B2BCustomerModel customer = getModelService().create(B2BCustomerModel.class);
        setCustomerGroups(customer, customerType);
        setUidData(registerData.getLogin(), customer);
        if (StringUtils.isNotEmpty(registerData.getTitleCode())) {
            customer.setTitle(getUserService().getTitleForCode(registerData.getTitleCode()));
        }
        customer.setName(name);
        customer.setCustomerType(customerType);
        customer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());

        if (StringUtils.isNotBlank(registerData.getCurrencyCode())) {
            customer.setSessionCurrency(getCustomerCurrency(registerData.getCurrencyCode()));
        } else {
            customer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
        }

        customer.setEmail(registerData.getEmail());
        customer.setEligibleForReevoo(Boolean.TRUE);
        customer.setPhoneMarketingConsent(registerData.isNewsletterOption());
        customer.setVat4(registerData.getVat4());
        customer.setLegalEmail(registerData.getLegalEmail());
        customer.setNewsletter(registerData.isNewsletterOption());
        setCustomerFunction(customer, registerData.getFunctionCode());
        return customer;
    }

    private B2BCustomerModel createCustomerForExistingCustomer(final String name, final DistExistingCustomerRegisterData registerData,
                                                               final CustomerType customerType) {
        final B2BCustomerModel customer = getModelService().create(B2BCustomerModel.class);
        setCustomerGroups(customer, customerType);
        setUidData(registerData.getLogin(), customer);
        customer.setErpContactID(registerData.getContactId());
        customer.setTitle(getUserService().getTitleForCode(registerData.getTitleCode()));
        customer.setName(name);
        customer.setEligibleForReevoo(Boolean.TRUE);
        customer.setCustomerType(customerType);
        customer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
        customer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
        customer.setEmail(registerData.getEmail());
        customer.setNewsletter(registerData.isNewsletterOption());
        customer.setPhoneMarketingConsent(registerData.isNewsletterOption());
        setCustomerFunction(customer, registerData.getFunctionCode());
        return customer;
    }

    private void setCustomerGroups(final B2BCustomerModel customer, final CustomerType customerType) {
        final Set<PrincipalGroupModel> groups = new HashSet<>();

        if (customerType.equals(CustomerType.B2C)) {
            groups.add(getUserService().getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID));
        } else if (customerType.equals(CustomerType.GUEST)) {
            groups.add(getUserService().getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID));
        } else if (customerType.equals(CustomerType.B2B)) {
            groups.add(getUserService().getUserGroupForUID(B2BConstants.B2BCUSTOMERGROUP));
            groups.add(getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP));
        }

        customer.setGroups(groups);
    }

    private void setUidData(final String login, final B2BCustomerModel newCustomer) {
        newCustomer.setUid(login.toLowerCase(Locale.getDefault()));
        newCustomer.setOriginalUid(login);
    }

    private CurrencyModel getCustomerCurrency(final String currencyCode) {
        final CurrencyModel currency = getCommonI18NService().getCurrency(currencyCode);
        final List<CurrencyModel> registrationCurrencies = getCommonI18NService().getAllCurrencies();
        if (registrationCurrencies.contains(currency)) {
            return currency;
        } else {
            return getCommonI18NService().getCurrentCurrency();
        }
    }

    private void setCustomerFunction(final B2BCustomerModel customer, final String functionCode) {
        if (StringUtils.isNotBlank(functionCode)) {
            customer.setDistFunction(codelistService.getDistFunction(functionCode));
        }
    }

    private AddressModel createUnitAddress(final DistRegisterData registerData, final B2BUnitModel unit) {
        final AddressModel address = createAddress(registerData);
        address.setTitle(getUserService().getTitleForCode(registerData.getTitleCode()));
        address.setPobox(registerData.getPoBox());
        if (StringUtils.isNotBlank(registerData.getMobileNumber())) {
            address.setPhone2(registerData.getMobileNumber());
        }
        address.setOwner(unit);
        address.setCompany(registerData.getCompanyName());
        address.setCompanyName2(registerData.getCompanyName2());
        address.setCompanyName3(registerData.getCompanyName3());
        address.setBillingAddress(Boolean.TRUE);
        address.setShippingAddress(Boolean.TRUE);
        return address;
    }

    private AddressModel createContactAddress(final DistRegisterData registerData, final B2BCustomerModel newCustomer) {
        final AddressModel address = createAddress(registerData);
        address.setTitle(newCustomer.getTitle());
        address.setOwner(newCustomer);
        address.setContactAddress(Boolean.TRUE);
        return address;
    }

    private AddressModel createAddress(final DistRegisterData registerData) {
        final AddressModel address = getModelService().create(AddressModel.class);
        address.setFirstname(registerData.getFirstName());
        address.setLastname(registerData.getLastName());
        address.setEmail(registerData.getEmail());
        address.setStreetname(registerData.getStreetName());
        address.setStreetnumber(registerData.getStreetNumber());
        address.setPostalcode(registerData.getPostalCode());
        address.setTown(registerData.getTown());
        address.setCountry(getCommonI18NService().getCountry(registerData.getCountryCode()));
        if (StringUtils.isNotBlank(registerData.getRegionCode())) {
            address.setRegion(getCommonI18NService().getRegion(address.getCountry(), registerData.getRegionCode()));
        }
        if (StringUtils.isNotBlank(registerData.getPhoneNumber())) {
            address.setPhone1(registerData.getPhoneNumber());
        }
        if (StringUtils.isNotBlank(registerData.getMobileNumber())) {
            address.setCellphone(registerData.getMobileNumber());
        }
        if (StringUtils.isNotBlank(registerData.getFaxNumber())) {
            address.setFax(registerData.getFaxNumber());
        }
        return address;
    }


    private AddressModel createContactAddressForSubUser(final DistSubUserData registerData, final B2BCustomerModel newSubUser) {
        final AddressModel address = getModelService().create(AddressModel.class);
        address.setTitle(newSubUser.getTitle());
        address.setFirstname(registerData.getFirstName());
        address.setLastname(registerData.getLastName());
        address.setEmail(registerData.getEmail());

        if (StringUtils.isNotBlank(registerData.getPhoneNumber())) {
            address.setPhone1(registerData.getPhoneNumber());
        }
        if (StringUtils.isNotBlank(registerData.getMobileNumber())) {
            address.setCellphone(registerData.getMobileNumber());
        }
        if (StringUtils.isNotBlank(registerData.getFaxNumber())) {
            address.setFax(registerData.getFaxNumber());
        }
        address.setOwner(newSubUser);
        address.setContactAddress(Boolean.TRUE);
        if (StringUtils.isNotEmpty(registerData.getDepartmentCode())) {
            address.setDistDepartment(codelistService.getDistDepartment(registerData.getDepartmentCode()));
        }
        return address;
    }

    private AddressModel createContactAddressForExistingCustomer(final DistExistingCustomerRegisterData registerData,
                                                                 final B2BCustomerModel existingCustomer) {
        final AddressModel address = getModelService().create(AddressModel.class);
        address.setTitle(existingCustomer.getTitle());
        address.setFirstname(registerData.getFirstName());
        address.setLastname(registerData.getLastName());
        address.setEmail(registerData.getEmail());

        if (StringUtils.isNotBlank(registerData.getPhoneNumber())) {
            address.setPhone1(registerData.getPhoneNumber());
        }
        if (StringUtils.isNotBlank(registerData.getMobileNumber())) {
            address.setCellphone(registerData.getMobileNumber());
        }
        if (StringUtils.isNotBlank(registerData.getFaxNumber())) {
            address.setFax(registerData.getFaxNumber());
        }
        if (StringUtils.isNotBlank(registerData.getCountryCode())) {
            address.setCountry(getCommonI18NService().getCountry(registerData.getCountryCode()));
        }
        address.setOwner(existingCustomer);
        address.setContactAddress(Boolean.TRUE);
        return address;
    }

    protected boolean updateSessionChannel(final CustomerType customerType, final ChannelData defaultChannel) {

        if (customerType != null) {
            String channelType = "B2C";

            final String customerCode = customerType.getCode();
            if (customerCode.equals(CustomerType.B2B.getCode()) || customerCode.equals(CustomerType.B2B_KEY_ACCOUNT.getCode())
                    || customerCode.equals(CustomerType.B2E.getCode()) || customerCode.equals(CustomerType.OCI.getCode())
                    || customerCode.equals(CustomerType.ARIBA.getCode())) {
                channelType = "B2B";
            } else if (customerType.getCode().equals(CustomerType.B2C.getCode())) {
                channelType = "B2C";
            }
            // Get the available channels and check if the channeltype is supported
            final Collection<ChannelData> channels = getStoreSessionFacade().getAllChannels();
            for (final ChannelData channel : channels) {
                if (StringUtils.equals(channel.getType(), channelType)) {
                    // Set the current channel
                    getStoreSessionFacade().setCurrentChannel(channelType);
                    return true;
                }
            }
        }

        // Fallback to the default
        getStoreSessionFacade().setCurrentChannel(defaultChannel.getType());
        return false;
    }

    private B2BBudgetExceededPermissionModel createBudgetExceededPermission(final B2BCustomerModel customer, final B2BUnitModel unit) {
        final B2BBudgetExceededPermissionModel b2BBudgetExceededPermission = getModelService().create(B2BBudgetExceededPermissionModel.class);
        b2BBudgetExceededPermission.setCode("B2BBudgetExceededPermissionFor" + customer.getCustomerID());
        b2BBudgetExceededPermission.setActive(Boolean.TRUE);
        b2BBudgetExceededPermission.setUnit(unit);
        getModelService().save(b2BBudgetExceededPermission);
        return b2BBudgetExceededPermission;
    }

    protected DistB2BRequestQuotationPermissionModel createRequestQuotationPermission(final B2BCustomerModel customer) {
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = getModelService().create(DistB2BRequestQuotationPermissionModel.class);
        requestQuotationPermission.setCode("RequestQuotationPermission_" + customer.getCustomerID());
        requestQuotationPermission.setActive(Boolean.TRUE);
        requestQuotationPermission.setUnit(customer.getDefaultB2BUnit());
        requestQuotationPermission.setMessage("User is allowed to request quotations");
        requestQuotationPermission.setCustomers(Collections.singletonList(customer));
        getModelService().save(requestQuotationPermission);
        return requestQuotationPermission;
    }

    protected StandardDateRange createInitialDateRangeForBudget() {
        final Calendar budgetStartDate = Calendar.getInstance();
        budgetStartDate.setTime(new Date());
        budgetStartDate.set(Calendar.MONTH, Calendar.JANUARY);
        budgetStartDate.set(Calendar.DATE, 1);
        budgetStartDate.set(Calendar.HOUR, 0);
        budgetStartDate.set(Calendar.MINUTE, 0);
        budgetStartDate.set(Calendar.SECOND, 0);

        final Calendar budgetEndDate = Calendar.getInstance();
        budgetEndDate.setTime(new Date());
        budgetEndDate.add(Calendar.YEAR, NUMBER_OF_YEARS);
        budgetEndDate.set(Calendar.MONTH, Calendar.DECEMBER);
        budgetEndDate.set(Calendar.DATE, 31);
        budgetEndDate.set(Calendar.HOUR, 11);
        budgetEndDate.set(Calendar.MINUTE, 59);
        budgetEndDate.set(Calendar.SECOND, 59);

        return new StandardDateRange(budgetStartDate.getTime(), budgetEndDate.getTime());
    }

    protected boolean isB2BAdminUser(final UserModel user) {
        return getUserService().isMemberOfGroup(user, getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP));
    }

    @Override
    public boolean validateResetPasswordToken(final String token) {
        if (token != null) {
            final ForgottenPasswordProcessModel passwordModel = distCustomerAccountService.getForgottenPasswordProcessForToken(token);
            if (passwordModel == null) {
                return false;
            }
            distCustomerAccountService.removeForgotPasswordToken(passwordModel.getCustomer(), false);
            return distCustomerAccountService.isResetPasswordTokenValid(token);
        }
        return false;
    }

    @Override
    public boolean validateInitialPasswordToken(String token, boolean migration) {
        if (token != null) {
            SetInitialPasswordProcessModel process = distCustomerAccountService.getInitialPasswordProcessForToken(token);
            if (process == null) {
                return false;
            }
            distCustomerAccountService.removeInitialPasswordToken(process.getCustomer(), false);
            return distCustomerAccountService.isInitialPasswordTokenValid(token, migration);
        }
        return false;
    }

    @Override
    public List<DistCategoryIndexData> getTopCategories() {
        List<DistCategoryIndexData> topCategories = distCategoryFacade.getCategoryIndex();
        String ignoredCategories = configurationService.getConfiguration().getString("searchBar.ignoredCategoryCodes");
        if (isBlank(ignoredCategories)) {
            return topCategories;
        }
        Set<String> ignoredCategoryCodes = Arrays.stream(ignoredCategories.split(","))
                                                 .map(String::trim)
                                                 .collect(Collectors.toSet());
        return topCategories.stream()
                            .filter(category -> !ignoredCategoryCodes.contains(category.getCode()))
                            .collect(Collectors.toList());
    }

    @Override
    public List<DistCategoryIndexData> getTopCategoriesIgnoreObsolete() {
        List<DistCategoryIndexData> topCategories = getTopCategories();
        String ignoredObsoleteCategories = configurationService.getConfiguration().getString("obsolescence.ignoredCategoryCodes");
        if (isBlank(ignoredObsoleteCategories)) {
            return topCategories;
        }
        Set<String> ignoredObsoleteCategoryCodes = Arrays.stream(ignoredObsoleteCategories.split(","))
                                                         .map(String::trim)
                                                         .collect(Collectors.toSet());
        return topCategories.stream()
                            .filter(category -> !ignoredObsoleteCategoryCodes.contains(category.getCode()))
                            .collect(Collectors.toList());
    }

    @Override
    public void updateMarketingCookieConsent(final boolean isMarketingCookieEnabled) {
        distCustomerAccountService.updateMarketingCookieConsent(isMarketingCookieEnabled);
    }

    @Override
    public void updateLogin(final String newLogin) {
        final CustomerModel customer = getCurrentSessionCustomer();
        if (!ObjectUtils.isEmpty(customer.getContactAddress())) {
            customer.getContactAddress().setEmail(newLogin);
            getModelService().save(customer);
        }
        // update the customer (contact) also in ERP
        customerService.updateContact((B2BCustomerModel) customer);
    }

    @Override
    public boolean doesCustomerExistForUid(final String uid) {
        try {
            getUserService().getUserForUID(uid);
            return true;
        } catch (final UnknownIdentifierException e) {
            return false;
        }
    }

    @Override
    public List<String> formatCompanyName(String companyName) {
        String companyNameLinesCombined = WordUtils.wrap(companyName, MAX_FIELD_LENGTH_ERP, DistConstants.Punctuation.NEW_LINE, Boolean.TRUE);
        String[] companyNameLines = companyNameLinesCombined.split(DistConstants.Punctuation.NEW_LINE);

        if (isCompanyNameLongerThanSupported(companyNameLines)) {
            int lengthToBeAppended = calculateMaxLengthToBeAppended(companyNameLines);
            if (lengthToBeAppended > 0) {
                String shortenedLineInformation = trimInformationFromExtraLine(companyNameLines[3], lengthToBeAppended);
                return Arrays.asList(companyNameLines[0], companyNameLines[1], combineLastLineAndExtraLine(companyNameLines, shortenedLineInformation));
            }
        }
        return List.of(companyNameLines);
    }

    private String combineLastLineAndExtraLine(String[] companyNameLines, String shortenedLineInformation) {
        return companyNameLines[2] + StringUtils.SPACE + shortenedLineInformation;
    }

    private String trimInformationFromExtraLine(String value, int calculatedMaxLengthToBeAppended) {
        return value.substring(0, calculatedMaxLengthToBeAppended);
    }

    private int calculateMaxLengthToBeAppended(String[] companyNameLines) {
        return MAX_FIELD_LENGTH_ERP - (SEPARATOR_LENGTH + companyNameLines[2].length());
    }

    private boolean isCompanyNameLongerThanSupported(String[] companyNameLines) {
        return companyNameLines.length > NUMBER_OF_COMPANY_NAME_FIELDS;
    }

    @Override
    public void updateVatIdForGuest(String codiceFiscale) {
        if (getStoreSessionFacade().isCurrentShopItaly() && StringUtils.isNotEmpty(codiceFiscale)) {
            final B2BCustomerModel currentCustomer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();
            final B2BUnitModel unit = b2bUnitService.getParent(currentCustomer);
            unit.setVatID(codiceFiscale);
            getModelService().save(unit);
        }
    }

    @Override
    public String getCodiceFiscaleForGuest() {
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();
        final B2BUnitModel unit = b2bUnitService.getParent(currentCustomer);
        return unit.getVatID();
    }

    @Override
    @CxTransaction
    public void convertGuestToB2CAndRegisterInSMC(final String pwd, final String orderGUID) throws DuplicateUidException {
        changeGuestToCustomer(pwd, orderGUID);
        B2BCustomerModel customer = (B2BCustomerModel) getCurrentUser();
        saveObsolescenceCategories(customer, false);
        getModelService().save(customer);
    }

    @Override
    @CxTransaction
    public void convertGuestToB2CAndRegisterInBloomreach(final String pwd, final String orderGUID) throws DuplicateUidException {
        changeGuestToCustomer(pwd, orderGUID);
        B2BCustomerModel customer = (B2BCustomerModel) getCurrentUser();
        saveObsolescenceCategories(customer, false);
        getModelService().save(customer);
        createUserInBloomreach(getDistConsentData(customer));
    }

    @Override
    public boolean isJobRoleShown(RegistrationType registrationType, CustomerType customerType) {
        return List.of(BELGIUM, FRANCE, NETHERLANDS).contains(getStoreSessionFacade().getCurrentCountry().getIsocode())
                && RegistrationType.STANDALONE.equals(registrationType)
                && CustomerType.B2B.equals(customerType);
    }

    private DistConsentData getDistConsentData(B2BCustomerModel customer) {
        final DistConsentData distConsentData = new DistConsentData();
        distConsentData.setUid(customer.getUid());
        distConsentData.setFirstName(customer.getDefaultPaymentAddress().getFirstname());
        distConsentData.setLastName(customer.getDefaultPaymentAddress().getLastname());
        distConsentData.setTitleCode(customer.getDefaultPaymentAddress().getTitle().getCode());
        distConsentData.setErpContactId(customer.getErpContactID());
        distConsentData.setRole(StringUtils.EMPTY);
        distConsentData.setPhoneNumber(customer.getDefaultPaymentAddress().getPhone1());
        distConsentData.setMobileNumber(customer.getDefaultPaymentAddress().getCellphone());
        distConsentData.setActiveSubscription(Boolean.FALSE);
        distConsentData.setNpsSubscription(Boolean.FALSE);
        distConsentData.setIsAnonymousUser(Boolean.FALSE);
        distConsentData.setIsRegistration(Boolean.TRUE);
        distConsentData.setCountryCode(customer.getDefaultPaymentAddress().getCountry().getIsocode());
        return distConsentData;
    }

    private void saveObsolescenceCategories(final B2BCustomerModel customer, final boolean isNewsletterOption) {
        final List<ObsolescenceCategoryModel> obsoleCategoriesList = getObsolescenceCategories(isNewsletterOption);
        customer.setObsolescenceCategories(obsoleCategoriesList);
        customer.setOptedForObsolescence(isNewsletterOption);
        customer.setAllObsolCatSelected(isNewsletterOption);
    }

    private List<ObsolescenceCategoryModel> getObsolescenceCategories(final boolean isObsolescenceCategorySelected) {
        final List<ObsolescenceCategoryModel> obsoleCategoriesList = new ArrayList<>();

        for (final CategoryData category : getTopCategoriesIgnoreObsolete()) {
            final CategoryModel cat = categoryService.getCategoryForCode(category.getCode());
            final ObsolescenceCategoryModel obsolescenceCategoryModel = new ObsolescenceCategoryModel();
            obsolescenceCategoryModel.setCategory(cat);
            obsolescenceCategoryModel.setObsolCategorySelected(isObsolescenceCategorySelected);
            getModelService().save(obsolescenceCategoryModel);
            obsoleCategoriesList.add(obsolescenceCategoryModel);
        }

        return obsoleCategoriesList;
    }

    @Override
    @CxTransaction
    public void activateCustomer(final DistExistingCustomerRegisterData registerData, final CustomerType customerType)
            throws DuplicateUidException, ExistingCustomerRegistrationException {
        validateParameterNotNullStandardMessage("registerData", registerData);
        final boolean isB2BCustomer = CustomerType.B2B.equals(customerType);
        // STEP 1: Try to find the customer/contact and validate the response. If the
        // customer or contact was not found an
        // ExistingCustomerRegistrationException is thrown, which has to be handled in
        // the controller to display corresponding error
        // messages in the frontend
        final FindContactResponseData findResult = findExistingCustomerAndContactInERP(registerData);
        final DistSalesOrgModel currentSalesOrg = distSalesOrgService.getCurrentSalesOrg();
        validateFindContactResult(findResult, currentSalesOrg);

        if (isB2BCustomer) {
            validateCompanyName(registerData, currentSalesOrg);
        }

        // STEP 2: The customer was successfully found, so update the registration data
        // based on the found information the ERP and go
        // ahead with the registration
        updateRegistrationData(registerData, findResult);

        try {
            // STEP 3: Check if a unit is already existing for the given erpCustomerId
            // If yes the contact gets attached to the existing unit, otherwise the unit
            // gets created and the user gets attached to this one
            B2BUnitModel existingUnit = getOrCreateB2BUnit(currentSalesOrg, registerData, customerType);

            // STEP 4: Check if a customer is already existing for the given erpContactId.
            // If the erpContactId is not present than we create a new one
            B2BCustomerModel existingCustomer = getExistingCustomer(registerData, findResult, existingUnit);
            if (existingCustomer != null) {
                // The contact is already existing in hybris. So the customer has probably
                // forgotten his login/password. We should redirect
                // the customer to the corresponding form
                throw new DuplicateUidException(String.format(CUSTOMER_ALREADY_EXISTS_IN_HYBRIS, registerData.getCustomerId(), registerData.getContactId()));
            } else {
                // The contact is NOT existing in hybris, so create it!
                final String name = getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName());
                existingCustomer = createCustomerForExistingCustomer(name, registerData, customerType);
                b2bUnitService.addMember(existingUnit, existingCustomer);
                existingCustomer.setDefaultB2BUnit(existingUnit);

                if (isB2BCustomer) {
                    existingCustomer.setShowAllOrderhistory(getDefaultShowAllOrderHistory());

                    final List<ObsolescenceCategoryModel> obsoleCategoriesList = getObsolescenceCategories(registerData.isNewsletterOption());
                    existingCustomer.setObsolescenceCategories(obsoleCategoriesList);
                    existingCustomer.setOptedForObsolescence(false);
                    existingCustomer.setAllObsolCatSelected(false);
                }

                // STEP 5: Create a new contact address for the new contact
                final AddressModel contactAddress = createContactAddressForExistingCustomer(registerData, existingCustomer);
                getModelService().save(contactAddress);
                existingCustomer.setContactAddress(contactAddress);
                getModelService().save(existingCustomer);

                if (isB2BCustomer) {
                    setCurrentUserAsActive(existingCustomer);

                    // STEP 6: Register the new contact and customer in hybris and the ERP if necessary
                    distCustomerAccountService.registerExisting(existingCustomer, findResult.isContactFound(), registerData.getPassword(),
                                                                Boolean.TRUE);
                    distCustomerAccountService.updateConsentConditionRequired(existingCustomer, existingCustomer.isRsCustomer());

                    // STEP 7: Add the new contact as an approver to the new unit
                    b2bCommerceUnitService.addApproverToUnit(existingUnit, existingCustomer);

                    // STEP 8: Create permissions for the new contact
                    createPermissionsForCustomer(existingCustomer, existingUnit);

                    getUserService().setPassword(existingCustomer, registerData.getPassword());
                } else {
                    // STEP 6: Register the new contact and customer in hybris and the ERP if necessary
                    distCustomerAccountService.registerExisting(existingCustomer, findResult.isContactFound(), registerData.getPassword());

                    changeCurrentCartUser(existingCustomer.getUid());

                    // STEP 7: Fire customer activation event
                    distCustomerAccountService.raiseNewCustomerActivationEvent(existingCustomer);
                    registerData.setErpContactId(existingCustomer.getErpContactID());
                }
            }
        } catch (final ModelSavingException ex) {
            if (ex.getCause() instanceof AmbiguousUniqueKeysException) {
                throw new DuplicateUidException(ex);
            } else {
                throw new ModelSavingException("Cannot activate customer!", ex);
            }
        }
    }

    private void setCurrentUserAsActive(B2BCustomerModel existingCustomer) {
        // required because the session is running from the background and user itself is not active
        getUserService().setCurrentUser(existingCustomer);
    }

    private B2BCustomerModel getExistingCustomer(DistExistingCustomerRegisterData registerData, FindContactResponseData findResult, B2BUnitModel existingUnit) {
        return !findResult.isContactFound() ? null
                                            : b2bCommerceUnitService.getB2BCustomerByErpContactId(existingUnit, registerData.getContactId());
    }

    @Override
    public String generateRsActivationLink(String uid) {
        return getActivationUrl(uid);
    }

    @Override
    public void setInitialPasswordAndActivateCustomer(SetInitialPasswordForm setInitialPasswordForm) throws TokenInvalidatedException {
        distCustomerAccountService.setInitialPasswordAndActivateCustomer(setInitialPasswordForm.getToken(), setInitialPasswordForm.getPassword(),
                                                                         setInitialPasswordForm.isMigration());
    }

    private String getActivationUrl(String uid) {
        String token = distCustomerAccountService.generateTokenForRsCustomer(uid);
        return distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(getBaseSiteService().getCurrentBaseSite(), true, INITIAL_PASSWORD_URL,
                                                                             getQueryParams(uid, token));
    }

    private String getQueryParams(String uid, String token) {
        return "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) +
               "&email=" + URLEncoder.encode(uid, StandardCharsets.UTF_8) +
               UTM_SOURCE + "br_srv" +
               UTM_MEDIUM + "email" +
               UTM_CAMPAIGN + "rs_customer_activation" +
               UTM_TERM + "rs_customer_activation";
    }

    @Override
    public void removeForgotPasswordToken() {
        distCustomerAccountService.removeForgotPasswordToken((CustomerModel) getUserService().getCurrentUser(), true);
    }

    @Override
    protected DistrelecStoreSessionFacade getStoreSessionFacade() {
        return (DistrelecStoreSessionFacade) super.getStoreSessionFacade();
    }

    @Override
    public void confirmDoubleOptforResetPwd(final String token) {
        distCustomerAccountService.confirmDoubleOptforResetPwd(token);
    }

    protected LanguageData getCurrentLanguage() {
        return getStoreSessionFacade().getCurrentLanguage();
    }

    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

}
