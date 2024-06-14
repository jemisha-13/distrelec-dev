/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.user.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.Session;
import com.namics.distrelec.b2b.core.inout.erp.CustomerService;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.model.process.RequestInvoicePaymentModeEmailProcessModel;
import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.service.user.DistAddressService;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentData;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionData;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commercefacades.user.impl.DefaultUserFacade;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Default Distrelec implementation of {@link DistUserFacade}.
 *
 * @author rmeier, Namics AG
 * @author daehusir, Distrelec
 */
public class DefaultDistUserFacade extends DefaultUserFacade implements DistUserFacade {

    protected static final Logger LOG = LogManager.getLogger(DefaultDistUserFacade.class);

    public static final String REQUEST_INVOICE_PAYMENT_MODE_DURATION = "request.invoice.payment.method.duration";

    private static final String CREDIT_CARD_PAYMENT_MODE = "creditCard";

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    @Qualifier("erp.shippingOptionService")
    private ShippingOptionService shippingOptionService;

    @Autowired
    @Qualifier("erp.paymentOptionService")
    private PaymentOptionService paymentOptionService;

    @Autowired
    @Qualifier("erp.customerService")
    private CustomerService customerService;

    @Autowired
    private DistB2BCommerceUnitService b2bCommerceUnitService;

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private PaymentModeService paymentModeService;

    @Autowired
    @Qualifier("b2bDeliveryService")
    private DeliveryService deliveryService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    private BusinessProcessService businessProcessService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistrelecCMSSiteService distrelecCMSSiteService;

    @Autowired
    @Qualifier("deliveryModeConverter")
    private Converter<DeliveryModeModel, DeliveryModeData> distDeliveryModeConverter;

    @Autowired
    private Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter;

    @Autowired
    private Converter<DistDepartmentModel, DistDepartmentData> departmentConverter;

    @Autowired
    private Converter<DistFunctionModel, DistFunctionData> functionConverter;

    @Autowired
    private DistAddressService addressService;

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Override
    public void syncSessionCurrency() {
        if (!isAnonymousUser()) {
            super.syncSessionCurrency();
        } else {
            LOG.warn("anonymous user currency must not be synced");
        }
    }

    @Override
    public void syncSessionLanguage() {
        if (!isAnonymousUser()) {
            super.syncSessionLanguage();
        } else {
            LOG.debug("anonymous user language must not be synced");
        }
    }

    @Override
    public List<TitleData> getTitles() {
        return Converters.convertAll(getCustomerAccountService().getTitles().stream().filter(TitleModel::isActive).collect(Collectors.toList()),
                                     getTitleConverter());
    }

    @Override
    public List<AddressData> getAddressBook() {
        final Collection<AddressModel> addresses = getCustomerAccountService().getAddressBookEntries((CustomerModel) getUserService().getCurrentUser());
        if (addresses != null && !addresses.isEmpty()) {
            return Converters.convertAll(addresses, getAddressConverter());
        }
        return Collections.emptyList();
    }

    @Override
    public List<AddressData> getAddressBookDeliveryEntries() {
        final Collection<AddressModel> addresses = getCustomerAccountService().getAddressBookDeliveryEntries((CustomerModel) getUserService().getCurrentUser());
        if (addresses != null && !addresses.isEmpty()) {
            return Converters.convertAll(addresses, getAddressConverter());
        }
        return Collections.emptyList();
    }

    @Override
    public List<AddressData> getAddressBookDeliveryEntries(final String orderBy, final String orderType) {
        final Collection<AddressModel> addresses = getCustomerAccountService().getAddressBookDeliveryEntries((CustomerModel) getUserService().getCurrentUser(),
                                                                                                             orderBy, orderType);

        if (addresses != null && !addresses.isEmpty()) {
            return Converters.convertAll(addresses, getAddressConverter());
        }
        return Collections.emptyList();
    }

    @Override
    public List<AddressData> getAddressBookPaymentEntries() {
        final Collection<AddressModel> addresses = getCustomerAccountService().getAddressBookPaymentEntries((CustomerModel) getUserService().getCurrentUser());
        if (addresses != null && !addresses.isEmpty()) {
            return Converters.convertAll(addresses, getAddressConverter());
        }
        return Collections.emptyList();
    }

    @Override
    public void addAddress(final AddressData addressData) {
        addAddress(addressData, true);
    }

    @Override
    public void setDefaultAddress(final AddressData addressData) {
        validateParameterNotNullStandardMessage("addressData", addressData);
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            return;
        }
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();

        final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, addressData.getId());
        getCustomerAccountService().setContactDefaultAddresses(currentCustomer, addressModel, addressData.isBillingAddress(), addressData.isShippingAddress());
    }

    @Override
    public AddressData addAddress(final AddressData addressData, final boolean updateErp) {
        validateParameterNotNullStandardMessage("addressData", addressData);

        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        if (!isEShopGroup()) {
            addressData.setEmail(currentCustomer.getEmail());
        }
        final B2BUnitModel unit = getB2bUnitService().getParent(currentCustomer);

        // Create the new address model
        final AddressModel newAddress = getModelService().create(AddressModel.class);
        getAddressReversePopulator().populate(addressData, newAddress);

        if (isAnonymousCheckout(currentCustomer)) {
            newAddress.setShippingAddress(Boolean.TRUE);
        } else if (updateErp) {
            newAddress.setOwner(unit);
            customerService.createAddress(unit, newAddress);
        }
        // Store the address against the company
        getB2bCommerceUnitService().saveAddressEntry(unit, newAddress);

        // Update the address ID in the newly created address
        addressData.setId(newAddress.getPk().toString());

        if (!isEShopGroup()) {
            // Set default delivery and Shipping address for customer.
            if (null == currentCustomer.getDefaultPaymentAddress() && addressData.isBillingAddress()) {
                currentCustomer.setDefaultPaymentAddress(newAddress);
                getModelService().save(currentCustomer);
                getModelService().refresh(currentCustomer);
            } else if (null == currentCustomer.getDefaultShipmentAddress() && addressData.isShippingAddress()) {
                currentCustomer.setDefaultShipmentAddress(newAddress);
                getModelService().save(currentCustomer);
                getModelService().refresh(currentCustomer);
            }
        }

        if (null == unit.getBillingAddress()) {
            unit.setBillingAddress(newAddress);
            getModelService().save(unit);
            getModelService().refresh(unit);
        }
        return getAddressConverter().convert(newAddress);
    }

    private boolean isAnonymousCheckout(final B2BCustomerModel currentCustomer) {
        return getCheckoutCustomerStrategy().isAnonymousCheckout() || CustomerType.GUEST.equals(currentCustomer.getCustomerType());
    }

    public boolean isEShopGroup() {
        // Instead of calculation this attribute for each request, calculate it once and put the value in the session.
        return getSessionService().getOrLoadAttribute("isEShopGroup", () -> getUserService().isMemberOfGroup(getUserService().getCurrentUser(),
                                                                                                             getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID)));
    }

    @Override
    public void editAddress(final AddressData addressData) {
        editAddress(addressData, true);
    }

    @Override
    public AddressData editAddress(final AddressData addressData, final boolean updateErp) {
        validateParameterNotNullStandardMessage("addressData", addressData);

        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();

        final B2BUnitModel unit = getB2bUnitService().getParent(currentCustomer);
        final AddressModel addressModel = getB2bCommerceUnitService().getAddressForCode(unit, addressData.getId());
        if (addressModel != null) {
            if (isKeyCustomerBlockedFromEditing(currentCustomer, addressModel)) {
                throw new IllegalArgumentException("Wrong customer type");
            }

            if (!canEditCompanyName(currentCustomer, addressModel)) {
                addressData.setCompanyName(addressModel.getCompany());
            }

            getAddressReversePopulator().populate(addressData, addressModel);
            if (updateErp && !CustomerType.GUEST.equals(currentCustomer.getCustomerType())) {
                getCustomerService().updateAddress(unit, addressModel);
            }
            getB2bCommerceUnitService().editAddressEntry(unit, addressModel);
            return getAddressConverter().convert(addressModel);
        }
        throw new IllegalArgumentException("Address " + addressData + " does not belong to the customer " + currentCustomer + " and can not be edited.");
    }

    private boolean isKeyCustomerBlockedFromEditing(final B2BCustomerModel currentCustomer, final AddressModel addressModel) {
        return CustomerType.B2B_KEY_ACCOUNT.equals(currentCustomer.getCustomerType()) && BooleanUtils.isTrue(addressModel.getBillingAddress())
                && addressService.isAddressValid(addressModel);
    }

    @Override
    public boolean canEditCompanyName(final AddressModel addressModel) {
        return canEditCompanyName((B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout(), addressModel);
    }

    @Override
    public void setDefaultShippingAddress(final AddressData address) {
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            return;
        }
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();

        final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, address.getId());
        getCustomerAccountService().setDefaultShippingAddress(currentCustomer, addressModel, address.isShippingAddress());
    }

    @Override
    public void setDefaultShippingAddressIfNotSet(final AddressData address) {
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            return;
        }
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
        if (currentCustomer.getDefaultShipmentAddress() == null) {
            final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, address.getId());
            getCustomerAccountService().setDefaultShippingAddress(currentCustomer, addressModel, address.isBillingAddress());
        }
    }

    @Override
    public void setDefaultBillingAddress(final AddressData address) {
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            return;
        }
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
        final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, address.getId());
        getCustomerAccountService().setDefaultBillingAddress(currentCustomer, addressModel, address.isBillingAddress());
    }

    @Override
    public void setDefaultBillingAddressIfNotSet(final AddressData address) {
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            return;
        }
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
        if (currentCustomer.getDefaultPaymentAddress() == null) {
            final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, address.getId());
            getCustomerAccountService().setDefaultBillingAddress(currentCustomer, addressModel, address.isBillingAddress());
        }
    }

    /**
     * Check whether or not the customer is allowed to edit his company name.
     *
     * @param currentCustomer
     *            the current customer.
     * @param addressModel
     *            the target address.
     * @return {@code true} if the current customer is allowed to edit his company name on the specified address, {@code false} otherwise.
     */
    private boolean canEditCompanyName(final B2BCustomerModel currentCustomer, final AddressModel addressModel) {
        if (currentCustomer == null || addressModel == null || BooleanUtils.isTrue(addressModel.getBillingAddress())) {
            return false;
        }

        return BooleanUtils.isTrue(addressModel.getShippingAddress()) //
                || getUserService().isAnonymousUser(currentCustomer) //
                || Stream.of(CustomerType.B2C, CustomerType.GUEST, CustomerType.B2E).anyMatch(ct -> ct.equals(currentCustomer.getCustomerType()));
    }

    @Override
    public void removeAddress(final AddressData addressData) {
        validateParameterNotNullStandardMessage("addressData", addressData);

        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        final B2BUnitModel company = currentCustomer.getDefaultB2BUnit();
        for (final AddressModel addressModel : getCustomerAccountService().getAddressBookEntries(currentCustomer)) {
            if (addressData.getId().equals(addressModel.getPk().getLongValueAsString())) {
                if (!isAnonymousCheckout(currentCustomer)
                        && StringUtils.equalsIgnoreCase(addressModel.getErpAddressID(), company.getErpCustomerID())) {
                    throw new IllegalArgumentException("Address ID " + addressModel.getErpAddressID() + " is the same as Customer ID "
                                                       + currentCustomer.getCustomerID());
                }
                // Only Shipping addresses
                if (BooleanUtils.isFalse(addressModel.getShippingAddress()) || BooleanUtils.isTrue(addressModel.getBillingAddress())) {
                    throw new IllegalArgumentException("Wrong address type");
                }
                getCustomerAccountService().deleteAddressEntry(currentCustomer, addressModel);
                return;
            }
        }
        throw new IllegalArgumentException("Address " + addressData + " does not belong to the customer " + currentCustomer + " and will not be removed.");
    }

    @Override
    public boolean isExistingUser(final String uid) {
        try {
            getUserService().getUserForUID(uid);
            return true;
        } catch (final UnknownIdentifierException | IllegalArgumentException uie) {
            return false;
        }
    }

    @Override
    public List<DeliveryModeData> getSupportedDeliveryModesForUser() {
        final B2BCustomerModel user = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        if (user != null) {
            return getSessionService().getOrLoadAttribute(Session.DELIVERY_MODES, () -> {
                final List<AbstractDistDeliveryModeModel> deliveryModes = getShippingOptionService().getSupportedShippingOptionsForUser(user,
                                                                                                                                        Collections.singletonList(getShippingOptionService().getPickupDeliveryModeCode()));
                if (deliveryModes == null || deliveryModes.isEmpty()) {
                    return Collections.emptyList();
                }

                final DeliveryModeModel defaultDeliveryMode = getShippingOptionService().getDefaultShippingOptionForUser(user);
                return deliveryModes.stream()
                                    .filter(Objects::nonNull)
                                    .map(dm -> {
                                        final DeliveryModeData deliveryModeData = getDistDeliveryModeConverter().convert(dm);
                                        deliveryModeData.setDefaultDeliveryMode(dm == defaultDeliveryMode);
                                        return deliveryModeData;
                                    }).collect(Collectors.toList());
            });
        }

        return Collections.emptyList();
    }

    @Override
    public List<DistPaymentModeData> getSupportedPaymentModesForUser() {
        final UserModel user = getUserService().getCurrentUser();

        if (getUserService().isAnonymousUser(user)) {
            return getValidPaymentModesFromCart();
        }
        if (user != null) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
            if (CustomerType.GUEST.equals(b2bCustomer.getCustomerType())) {
                return getValidPaymentModesFromCart();
            }
            final boolean useCache = b2bCustomer.getRequestedInvoicePaymentModeDate() == null;
            if (!useCache) {
                // invalidate cached payment methods in case they are changed
                getSessionService().removeAttribute(Session.PAYMENT_MODES);
            }
            return getSessionService().<List<DistPaymentModeData>> getOrLoadAttribute(Session.PAYMENT_MODES, () -> {
                final List<AbstractDistPaymentModeModel> paymentModesModel = getPaymentOptionService().getSupportedPaymentOptionsForUser(b2bCustomer, useCache);
                if (paymentModesModel == null || paymentModesModel.isEmpty()) {
                    return Collections.emptyList();
                }

                final AbstractDistPaymentModeModel defaultPaymentModeModel = getPaymentOptionService().getDefaultPaymentOptionForUser(b2bCustomer, useCache);
                return convertPaymentModes(defaultPaymentModeModel, paymentModesModel);
            });
        }
        return Collections.emptyList();
    }

    private List<DistPaymentModeData> getValidPaymentModesFromCart() {
        final CartModel cart = getCartService().getSessionCart();
        final List<AbstractDistPaymentModeModel> paymentModesModel = cart.getValidPaymentModes();
        return convertPaymentModes(cart.getPaymentMode(), paymentModesModel);
    }

    private List<DistPaymentModeData> convertPaymentModes(final PaymentModeModel defaultPaymentMode, final List<AbstractDistPaymentModeModel> paymentModes) {
        return DistUtils.getSortedPaymentModes(paymentModes).stream()
                        .filter(Objects::nonNull)
                        .map(pm -> {
                            final DistPaymentModeData pmd = getPaymentModeConverter().convert(pm);
                            pmd.setDefaultPaymentMode(pm == defaultPaymentMode);
                            return pmd;
                        }).collect(Collectors.toList());
    }

    @Override
    public boolean canRequestInvoicePaymentMode() {
        final List<DistPaymentModeData> paymentModes = getSupportedPaymentModesForUser();
        final CMSSiteModel cmsSite = getDistrelecCMSSiteService().getCurrentSite();

        final boolean isB2BCustomer = DistUtils.isB2BCustomer((CustomerModel) getUserService().getCurrentUser());
        final boolean hasInvoicePayment = paymentModes.stream().anyMatch(DistPaymentModeData::getInvoicePayment);
        final boolean hasDefinedEmail = getConfigurationService().getConfiguration().containsKey("request.invoice.payment.method.email.to." + cmsSite.getUid());

        return isB2BCustomer && !hasInvoicePayment && hasDefinedEmail;
    }

    @Override
    public void requestInvoicePaymentMode() {
        final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();

        // invalidate cached payment methods in case they are changed in the meanwhile
        getSessionService().removeAttribute(Session.PAYMENT_MODES);

        if (canRequestInvoicePaymentMode()) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;

            final String processId = String.format("requestInvoicePaymentModeEmail-%s-%s", b2bCustomer.getUid(), System.currentTimeMillis());
            final RequestInvoicePaymentModeEmailProcessModel processModel = getBusinessProcessService().createProcess(
                                                                                                                      processId,
                                                                                                                      "requestInvoicePaymentModeEmailProcess");

            processModel.setCustomer(b2bCustomer);
            processModel.setSite(getDistrelecCMSSiteService().getCurrentSite());

            getModelService().save(processModel);
            getBusinessProcessService().startProcess(processModel);

            b2bCustomer.setRequestedInvoicePaymentModeDate(new Date());
            getModelService().save(b2bCustomer);
        } else {
            LOG.warn("Customer {} is not eligible for invoice payment method request", customer.getUid());
        }
    }

    @Override
    public boolean isInvoicePaymentModeRequested() {
        final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
        if (!getUserService().isAnonymousUser(getUserService().getCurrentUser()) && DistUtils.isB2BCustomer(customer)) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;
            if (b2bCustomer.getRequestedInvoicePaymentModeDate() != null) {
                final List<DistPaymentModeData> paymentModes = getSupportedPaymentModesForUser();
                final boolean hasInvoicePayment = paymentModes.stream().anyMatch(paymentMode -> paymentMode.getInvoicePayment());

                if (hasInvoicePayment) {
                    // customer has invoice payment, so invalidate this
                    b2bCustomer.setRequestedInvoicePaymentModeDate(null);
                    getModelService().save(b2bCustomer);
                    return false;
                } else {
                    // check if expired
                    return !isInvoicePaymentModeRequestExpired(b2bCustomer);
                }
            }
        }
        return false;
    }

    /**
     * Must be called by {@link #isInvoicePaymentModeRequestExpired(B2BCustomerModel)} method only.
     */
    private boolean isInvoicePaymentModeRequestExpired(final B2BCustomerModel b2bCustomer) {
        final long days = TimeUnit.DAYS.convert(new Date().getTime() - b2bCustomer.getRequestedInvoicePaymentModeDate().getTime(), TimeUnit.MILLISECONDS);
        final int maxDuration = getConfigurationService().getConfiguration().getInt(REQUEST_INVOICE_PAYMENT_MODE_DURATION);
        final boolean expired = days > maxDuration;
        if (expired) {
            // expired, reset attribute
            b2bCustomer.setRequestedInvoicePaymentModeDate(null);
            getModelService().save(b2bCustomer);
        }
        return expired;
    }

    @Override
    public boolean setDefaultDeliveryMode(final String deliveryId) {
        // Shipping part
        if (StringUtils.isNotBlank(deliveryId)) {
            // We try to found a delivery mode
            final Collection<DeliveryModeData> deliveryInfoDatas = getSupportedDeliveryModesForUser();
            for (final DeliveryModeData deliveryModeData : deliveryInfoDatas) {
                if (deliveryModeData.getCode().compareTo(deliveryId) == 0) {
                    final AbstractDistDeliveryModeModel deliveryMode = (AbstractDistDeliveryModeModel) getDeliveryService()
                                                                                                                           .getDeliveryModeForCode(deliveryModeData.getCode());
                    getShippingOptionService().updateDefaultShippingOption(deliveryMode);
                    getSessionService().removeAttribute(Session.DELIVERY_MODES);
                    final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
                    currentCustomer.setDefaultDeliveryMethod(deliveryModeData.getCode());
                    getModelService().save(currentCustomer);
                    getModelService().refresh(currentCustomer);
                    return true;
                }
            }
            // no delivery mode found for this code
        }
        return false;
    }

    @Override
    public boolean setDefaultPaymentMode(final String paymentId) {
        // Shipping part
        if (StringUtils.isNotBlank(paymentId)) {

            final Collection<DistPaymentModeData> paymentModeDatas = getSupportedPaymentModesForUser();

            for (final DistPaymentModeData paymentMode : paymentModeDatas) {
                if (paymentMode.getCode().equalsIgnoreCase(paymentId)) {
                    final AbstractDistPaymentModeModel paymentModeModel = (AbstractDistPaymentModeModel) getPaymentModeService()
                                                                                                                                .getPaymentModeForCode(paymentMode.getCode());
                    getPaymentOptionService().updateDefaultPaymentOption(getCartService().getSessionCart(), paymentModeModel);
                    getSessionService().removeAttribute(Session.PAYMENT_MODES);
                    // If not credit card, we have to remove the default PaymentInfo
                    if (!paymentModeModel.getPaymentInfoType().getCode().equals(CreditCardPaymentInfoModel._TYPECODE)) {
                        getCustomerAccountService().clearDefaultPaymentInfo((CustomerModel) getUserService().getCurrentUser());
                    }
                    final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
                    currentCustomer.setDefaultPaymentMethod(paymentMode.getCode());
                    getModelService().save(currentCustomer);
                    getModelService().refresh(currentCustomer);
                    return true;
                }
            }
            // no payment mode found for this code
        }
        return false;
    }

    @Override
    public boolean setDefaultPaymentInfo(final String ccPaymentId) {
        if (StringUtils.isNotBlank(ccPaymentId)) {
            final List<CCPaymentInfoData> creditsCardsOptions = getCCPaymentInfos(true);
            if (CollectionUtils.isNotEmpty(creditsCardsOptions)) {
                for (final CCPaymentInfoData creditCard : creditsCardsOptions) {
                    if (creditCard.getId().compareTo(ccPaymentId) == 0) {
                        setDefaultPaymentInfo(creditCard);
                        setDefaultPaymentInfoForCustomer(ccPaymentId);
                        // Change the payment mode to credit card.
                        return setDefaultPaymentMode(CREDIT_CARD_PAYMENT_MODE);
                    }
                }
            } else {
                final CCPaymentInfoData ccPaymentInfoData = getCCPaymentInfoForCode(ccPaymentId);
                if (null != ccPaymentInfoData) {
                    // Set the credit card as default Credit card.
                    setDefaultPaymentInfo(ccPaymentInfoData);
                }
            }

            // no credit card found for this code
        }
        return false;
    }

    private void setDefaultPaymentInfoForCustomer(final String code) {
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
        final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService().getCreditCardPaymentInfoForCode(currentCustomer, code);
        currentCustomer.setDefaultPaymentInfo(ccPaymentInfoModel);
        getModelService().save(currentCustomer);
        getModelService().refresh(currentCustomer);
    }

    @Override
    public AddressData getAddressForCode(final String code) {
        final AddressModel defaultAddress = getCustomerAccountService().getAddressForCode(getCheckoutCustomerStrategy().getCurrentUserForCheckout(), code);
        return defaultAddress != null ? getAddressConverter().convert(defaultAddress) : null;
    }

    @Override
    public List<DistFunctionData> getFunctions() {
        return Converters.convertAll(getCustomerAccountService().getFunctions(), getFunctionConverter());
    }

    @Override
    public List<DistDepartmentData> getDepartments() {
        return Converters.convertAll(getCustomerAccountService().getDepartments(), getDepartmentConverter());
    }

    @Override
    protected DistCustomerAccountService getCustomerAccountService() {
        return (DistCustomerAccountService) super.getCustomerAccountService();
    }

    @Override
    public boolean isValidUserAddress(final String PK) {
        return getCustomerAccountService().getAddressForCode(getCheckoutCustomerStrategy().getCurrentUserForCheckout(), PK) != null;
    }

    @Override
    public List<String> getMemberCustomersForB2BUnit(final String b2bUnitId) {
        final Set<B2BCustomerModel> customers = getB2bUnitService().getB2BCustomers(getB2bUnitService().getUnitForUid(b2bUnitId));
        final List<String> orgContactList = customers.stream().map(contact -> contact.getErpContactID()).collect(Collectors.toList());
        return orgContactList;
    }

    @Override
    public void updateConsentConditionsRequiredFlag(final B2BCustomerModel customer, final boolean consentConditionRequired) {
        distCustomerAccountService.updateConsentConditionRequired(customer, consentConditionRequired);
    }

    // Getters & Setters

    public ShippingOptionService getShippingOptionService() {
        return shippingOptionService;
    }

    public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
        this.shippingOptionService = shippingOptionService;
    }

    public PaymentOptionService getPaymentOptionService() {
        return paymentOptionService;
    }

    public void setPaymentOptionService(final PaymentOptionService paymentOptionService) {
        this.paymentOptionService = paymentOptionService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

    public DistB2BCommerceUnitService getB2bCommerceUnitService() {
        return b2bCommerceUnitService;
    }

    public void setB2bCommerceUnitService(final DistB2BCommerceUnitService b2bCommerceUnitService) {
        this.b2bCommerceUnitService = b2bCommerceUnitService;
    }

    public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public PaymentModeService getPaymentModeService() {
        return paymentModeService;
    }

    public void setPaymentModeService(final PaymentModeService paymentModeService) {
        this.paymentModeService = paymentModeService;
    }

    public DeliveryService getDeliveryService() {
        return deliveryService;
    }

    public void setDeliveryService(final DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public Converter<DeliveryModeModel, DeliveryModeData> getDistDeliveryModeConverter() {
        return distDeliveryModeConverter;
    }

    public void setDistDeliveryModeConverter(final Converter<DeliveryModeModel, DeliveryModeData> distDeliveryModeConverter) {
        this.distDeliveryModeConverter = distDeliveryModeConverter;
    }

    public Converter<PaymentModeModel, DistPaymentModeData> getPaymentModeConverter() {
        return paymentModeConverter;
    }

    public void setPaymentModeConverter(final Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter) {
        this.paymentModeConverter = paymentModeConverter;
    }

    public Converter<DistDepartmentModel, DistDepartmentData> getDepartmentConverter() {
        return departmentConverter;
    }

    public void setDepartmentConverter(final Converter<DistDepartmentModel, DistDepartmentData> departmentConverter) {
        this.departmentConverter = departmentConverter;
    }

    public Converter<DistFunctionModel, DistFunctionData> getFunctionConverter() {
        return functionConverter;
    }

    public void setFunctionConverter(final Converter<DistFunctionModel, DistFunctionData> functionConverter) {
        this.functionConverter = functionConverter;
    }

    public DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(final DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistrelecCMSSiteService getDistrelecCMSSiteService() {
        return distrelecCMSSiteService;
    }

    public void setDistrelecCMSSiteService(final DistrelecCMSSiteService distrelecCMSSiteService) {
        this.distrelecCMSSiteService = distrelecCMSSiteService;
    }
}
