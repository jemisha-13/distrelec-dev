/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.checkout.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.PaymentMethods.CREDIT_CARD;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Shipping.METHOD_PICKUP;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.annotations.CxTransaction;
import com.namics.distrelec.b2b.core.blocking.rule.service.DistBlockedB2BCustomerRuleService;
import com.namics.distrelec.b2b.core.blocking.rule.service.DistBlockedUserAddressRuleService;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.erp.*;
import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedB2BCustomerRuleModel;
import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedUserAddressRuleModel;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.order.DistCardPaymentService;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCheckoutService;
import com.namics.distrelec.b2b.core.service.order.DistCommercePaymentService;
import com.namics.distrelec.b2b.core.service.order.exceptions.PurchaseBlockedException;
import com.namics.distrelec.b2b.core.service.process.payment.PaymentNotifyProcessHelper;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.service.user.DistAddressService;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.hybris.toolbox.DateTimeUtils;
import com.namics.hybris.toolbox.items.SessionUtil;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * A default implementation of a checkout facade for a distrelec.
 */
public class DefaultDistCheckoutFacade extends DefaultB2BCheckoutFacade implements DistCheckoutFacade {

    private static final Logger LOG = LogManager.getLogger(DefaultDistCheckoutFacade.class);

    private static final List<String> CALIBRATION_TG = Arrays.asList("1005", "1015", "2005", "2015", "3005", "3015", "0005", "0006");

    private static final String PAYMENT_SUCCESS_CODE = "00000000";

    private final Set<String> orderPlacementLocks = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter;

    @Autowired
    @Qualifier("deliveryModeConverter")
    private Converter<DeliveryModeModel, DeliveryModeData> distDeliveryModeConverter;

    @Autowired
    private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

    @Autowired
    @Qualifier("creditCardPaymentInfoConverter")
    private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private PaymentModeService paymentModeService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DistCommercePaymentService distCommercePaymentService;

    @Autowired
    @Qualifier("b2bCommerceCheckoutService")
    private DistCommerceCheckoutService distCommerceCheckoutService;

    @Autowired
    private DistB2BCommerceBudgetService b2bCommerceBudgetService;

    @Autowired
    @Qualifier("erp.orderCalculationService")
    private OrderCalculationService orderCalculationService;

    @Autowired
    @Qualifier("erp.paymentOptionService")
    private PaymentOptionService paymentOptionService;

    @Autowired
    @Qualifier("erp.shippingOptionService")
    private ShippingOptionService shippingOptionService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistUserFacade userFacade;

    @Autowired
    @Qualifier("distSalesOrgProductService")
    private DistSalesOrgProductService distSalesOrgProductService;

    @Autowired
    private BusinessProcessService businessProcessService;

    @Autowired
    @Qualifier("erp.customerService")
    private CustomerService customerService;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private DistBlockedB2BCustomerRuleService distBlockedB2BCustomerRuleService;

    @Autowired
    private DistBlockedUserAddressRuleService distBlockedUserAddressRuleService;

    @Autowired
    private DistAddressService distAddressService;

    @Autowired
    private DistCartService distCartService;

    @Autowired
    private DistCardPaymentService distCardPaymentService;

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    @Qualifier("defaultDistCartFacade")
    private DistCartFacade distCartFacade;

    @Override
    public boolean setDeliveryAddress(final AddressData addressData) {
        return setDeliveryAddress(addressData, Boolean.FALSE);
    }

    @Override
    public boolean setDeliveryAddress(final AddressData addressData, final boolean forceRecalculate) {
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            AddressModel addressModel = null;
            if (addressData != null) {
                addressModel = addressData.getId() == null ? createDeliveryAddressModel(addressData, cartModel)
                                                           : getDeliveryAddressModelForCode(addressData.getId());
            }

            final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true, forceRecalculate);
            parameter.setAddress(addressModel);
            parameter.setIsDeliveryAddress(addressData.isShippingAddress());
            parameter.setRecalculate(forceRecalculate);
            return getCommerceCheckoutService().setDeliveryAddress(parameter);
        }
        return false;
    }

    @Override
    public boolean removeDeliveryMode() {
        return removeDeliveryMode(true);
    }

    @Override
    public boolean removeDeliveryMode(final boolean forceRecalculate) {
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            return getCommerceCheckoutService().removeDeliveryMode(createCommerceCheckoutParameter(cartModel, true, forceRecalculate));
        }
        return false;
    }

    @Override
    public boolean setDeliveryMode(final String deliveryModeCode) {
        return setDeliveryMode(deliveryModeCode, true);
    }

    @Override
    public boolean setDeliveryMode(final String deliveryModeCode, final boolean forceRecalculate) {
        validateParameterNotNullStandardMessage("deliveryModeCode", deliveryModeCode);

        final CartModel cartModel = getCart();
        if (cartModel != null) {
            final DeliveryModeModel deliveryModeModel = getDeliveryService().getDeliveryModeForCode(deliveryModeCode);
            if (deliveryModeModel != null) {
                final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
                parameter.setDeliveryMode(deliveryModeModel);
                return getCommerceCheckoutService().setDeliveryMode(parameter);
            }
        }
        return false;
    }

    @Override
    public CartData getCheckoutCart() {
        final CartData cartData = super.getCheckoutCart();
        if (BooleanUtils.isTrue(cartData.getOpenOrder()) && BooleanUtils.isFalse(cartData.getOpenOrderNew())
                && StringUtils.isNotBlank(cartData.getOpenOrderErpCode())) {
            cartData.setDeliveryAddress(cartData.getDeliveryAddress());
            cartData.setDeliveryMode(cartData.getDeliveryMode());
        }
        cartData.setErpVoucherInfoData(cartData.getErpVoucherInfoData());
        return cartData;
    }

    @Override
    public OrderData placeOrder() throws InvalidCartException {
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            final B2BCustomerModel currentUser = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();
            if (cartModel.getUser().equals(currentUser)) {
                String cartCode = cartModel.getCode();
                boolean addedLock = orderPlacementLocks.add(cartCode);
                if (addedLock) {
                    try {
                        return placeOrderForGivenCart(cartModel);
                    } finally {
                        orderPlacementLocks.remove(cartCode);
                    }
                } else {
                    LOG.warn("Order for given cart {} is already being placed", cartCode);
                }
            }
        }
        return null;
    }

    @Override
    public void prepareCartForCheckout() {
        final CartModel cartModel = getCart();
        if (nonNull(cartModel)) {
            if (nonNull(cartModel.getPaymentInfo())
                    && !distCardPaymentService.isCreditCardExpiryDateValid((CreditCardPaymentInfoModel) cartModel.getPaymentInfo())) {
                cartModel.setPaymentInfo(null);
            }

            if (calculationService.requiresCalculation(cartModel)) {
                getCommerceCheckoutService().calculateCart(createCommerceCheckoutParameter(cartModel, true));
            }
        }
    }

    /**
     * @param cartModel
     * @throws InvalidCartException
     */
    @Override
    public OrderData placeOrderForGivenCart(final CartModel cartModel) throws InvalidCartException {
        cartModel.setCompleteDelivery(BooleanUtils.toBoolean(cartModel.getCompleteDelivery()));
        // Before placing the order
        beforePlaceOrder(cartModel);
        // Placing the order
        final OrderModel orderModel = placeOrder(cartModel);
        // After placing the order
        afterPlaceOrder(cartModel, orderModel);
        sessionService.removeAttribute("completeDelivery");
        // Paste paymentTransactions from cart and Convert the order to an order data
        if (orderModel != null) {
            if (configurationService.getConfiguration().getBoolean("distrelec.create.order.on.notify.enabled", false)) {
                // Trigger the success event to unblock the Payment Notify BP
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Firing Payment Notify Success Event to interrupt waiting!");
                }
                businessProcessService.triggerEvent(PaymentNotifyProcessHelper.buildProcessSuccessEventCode(orderModel));
            }
            return getOrderConverter().convert(orderModel);
        }
        return null;
    }

    @Override
    public boolean setDeliveryAddressIfAvailable() {
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            final CustomerModel currentCustomer = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
            if (!isEShopGroup() && !isAnonymousCheckout()) {
                if (cartModel.getDeliveryAddress() == null && currentCustomer.getDefaultShipmentAddress() != null
                        && Boolean.TRUE.equals(currentCustomer.getDefaultShipmentAddress().getShippingAddress())) {
                    saveDeliveryAddress(cartModel, currentCustomer.getDefaultShipmentAddress());
                    return true;
                }
            }

            if (cartModel.getDeliveryAddress() != null) {
                if (!userFacade.isValidUserAddress(cartModel.getDeliveryAddress().getPk().toString())) {
                    saveDeliveryAddress(cartModel, null);
                }
            }
            final AddressModel paymentAddress = cartModel.getPaymentAddress();
            if (cartModel.getDeliveryAddress() == null && isShippingAddress(paymentAddress)) {
                final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
                commerceCheckoutParameter.setCart(cartModel);
                commerceCheckoutParameter.setAddress(paymentAddress);
                return getCommerceCheckoutService().setDeliveryAddress(commerceCheckoutParameter);
            }
        }

        return false;
    }

    private boolean isShippingAddress(AddressModel paymentAddress) {
        return paymentAddress != null && Boolean.TRUE.equals(paymentAddress.getShippingAddress());
    }

    public boolean isEShopGroup() {
        // Instead of calculation this attribute for each request, calculate it once and
        // put the value in the session.
        return sessionService.getOrLoadAttribute("isEShopGroup",
                                                 () -> getUserService().isMemberOfGroup(getUserService().getCurrentUser(),
                                                                                        getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID)));
    }

    @Override
    public boolean setPaymentAddressIfAvailable() {
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            final CustomerModel currentCustomer = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
            if (!isEShopGroup() && !isAnonymousCheckout()) {
                if (cartModel.getPaymentAddress() == null && currentCustomer.getDefaultPaymentAddress() != null
                        && Boolean.TRUE.equals(currentCustomer.getDefaultPaymentAddress().getBillingAddress())) {
                    savePaymentAddress(cartModel, currentCustomer.getDefaultPaymentAddress());
                    return true;
                }

                final B2BUnitModel unit = ((B2BCustomerModel) currentCustomer).getDefaultB2BUnit();

                if (cartModel.getPaymentAddress() != null) {
                    final AddressData addressData = userFacade.getAddressForCode(cartModel.getPaymentAddress().getPk().toString());
                    if (addressData == null) {
                        LOG.error("{}{} Customer {} has wrong address on card. will be changed automatically. ", ErrorLogCode.ADDRESS_ERROR, ErrorSource.HYBRIS,
                                  currentCustomer.getPk().toString());
                        if (unit.getBillingAddress() != null) {
                            savePaymentAddress(cartModel, unit.getBillingAddress());
                            return true;
                        } else {
                            savePaymentAddress(cartModel, null);
                        }
                    }
                } else {
                    if (cartModel.getUser().equals(currentCustomer)) {
                        final AddressModel currentUserDefaultPaymentAddress = (currentCustomer.getDefaultPaymentAddress() != null) ? currentCustomer.getDefaultPaymentAddress()
                                                                                                                                   : unit.getBillingAddress();

                        AddressData addressData = null;
                        if (currentUserDefaultPaymentAddress != null) {
                            addressData = userFacade.getAddressForCode(currentUserDefaultPaymentAddress.getPk().toString());
                            if (addressData == null) {
                                LOG.error("{}{} Customer {} has wrong address on company.", ErrorLogCode.ADDRESS_ERROR, ErrorSource.HYBRIS,
                                          currentCustomer.getPk().toString());
                            }
                        }

                        // If there is a default and if the customer has just on billing address, the
                        // address gets set automatically as payment
                        // address of the current cart. Otherwise the customer has to choose a payment
                        // address manually
                        if (currentUserDefaultPaymentAddress != null && addressData != null && CollectionUtils.size(unit.getBillingAddresses()) == 1) {
                            savePaymentAddress(cartModel, unit.getBillingAddress());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isValidFastCheckout() {
        final CartModel cartModel = getCart();
        if (cartModel == null || CollectionUtils.isEmpty(cartModel.getEntries())) {
            LOG.error("Cart Model is empty");
            return Boolean.FALSE;
        }

        if (isAnonymousCheckout()) {
            LOG.info("The current user is anonymous and not applicable for fast checkout");
            return Boolean.FALSE;
        }

        if (isEShopGroup()) {
            LOG.info("The current user is B2E and not applicable for fast checkout");
            return Boolean.FALSE;
        }

        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        if (isNull(currentCustomer.getDefaultPaymentAddress())) {
            LOG.info("The current user: {} does not have an default Payment Address and it's not applicable for fast checkout", currentCustomer.getUid());
            return Boolean.FALSE;
        } else if (Boolean.FALSE.equals(currentCustomer.getDefaultPaymentAddress().getBillingAddress())) {
            LOG.info("The current user: {} does not have an Billing Address in payment info and it's not applicable for fast checkout",
                     currentCustomer.getUid());
            return Boolean.FALSE;
        }

        if (isSAPValidDeliveryModesChanged(currentCustomer)) {
            LOG.info("Default delivery mode from the current user: {} is not available in SAP valid delivery modes and it's not applicable for fast checkout",
                     currentCustomer.getUid());
            return Boolean.FALSE;
        }

        if (isSAPValidPaymentModesChanged(currentCustomer)) {
            LOG.info("Default payment method from the current user: {} is not available in SAP valid payment methods and it's not applicable for fast checkout",
                     currentCustomer.getUid());
            return Boolean.FALSE;
        }

        if (isNull(currentCustomer.getDefaultShipmentAddress())) {
            LOG.info("The current user: {} does not have an default Shipping Address and it's not applicable for fast checkout", currentCustomer.getUid());
            return Boolean.FALSE;
        } else if (Boolean.FALSE.equals(currentCustomer.getDefaultShipmentAddress().getShippingAddress())) {
            LOG.info("The current user: {} does not have an Shipping address and it's not applicable for fast checkout", currentCustomer.getUid());
            return Boolean.FALSE;
        }

        if (StringUtils.isEmpty(currentCustomer.getDefaultPaymentMethod())) {
            LOG.info("The current user: {} does not have a default Payment Mode.", currentCustomer.getUid());
            return Boolean.FALSE;
        } else if (CREDIT_CARD.equals(currentCustomer.getDefaultPaymentMethod())) {
            if (isNull(currentCustomer.getDefaultPaymentInfo())) {
                LOG.info("The current user: {} does not have a Default Payment Info.", currentCustomer.getUid());
                return Boolean.FALSE;
            } else if (!distCardPaymentService.isCreditCardExpiryDateValid((CreditCardPaymentInfoModel) currentCustomer.getDefaultPaymentInfo())) {
                LOG.info("The current user: {} does not have a valid Default Payment Info.", currentCustomer.getUid());
                distCustomerAccountService.clearDefaultPaymentInfo(currentCustomer);
                return Boolean.FALSE;
            }
        }

        if (StringUtils.isEmpty(currentCustomer.getDefaultDeliveryMethod())) {
            LOG.info("The current user: {} does not have an default Shipping Method and it's not applicable for fast checkout", currentCustomer.getUid());
            return Boolean.FALSE;
        }

        if (isPickDeliveryMode(currentCustomer.getDefaultDeliveryMethod()) && !currentSiteHasPickupWarehouse()) {
            LOG.info("The current user: {} has default delivery method Pickup, but no pickup warehouses are available and it's not applicable for fast checkout",
                     currentCustomer.getUid());
            return Boolean.FALSE;
        }

        if (shouldAllowEditBilling(currentCustomer.getCustomerType()) && !distAddressService.isAddressValid(currentCustomer.getDefaultPaymentAddress())) {
            LOG.info("The current user: {} does not have valid default Billing Address and it's not applicable for fast checkout", currentCustomer.getUid());
            return Boolean.FALSE;
        }

        if (isUserAddressBlocked(currentCustomer.getDefaultPaymentAddress())) {
            LOG.info("The current user: {} has blocked default Billing address and it's not applicable for fast checkout", currentCustomer.getUid());
            return Boolean.FALSE;
        }

        if (shouldAllowEditShipping(currentCustomer) && !distAddressService.isAddressValid(currentCustomer.getDefaultShipmentAddress())) {
            LOG.info("The current user: {} doesn't have valid default Shipping Address and it's not applicable for fast checkout", currentCustomer.getUid());
            return Boolean.FALSE;
        }

        if (isUserAddressBlocked(currentCustomer.getDefaultShipmentAddress())) {
            LOG.info("The current user: {} has blocked default Shipping address and it's not applicable for fast checkout", currentCustomer.getUid());
            return Boolean.FALSE;
        }

        distCommerceCheckoutService.setDefaultDeliveryMode(cartModel);
        distCommerceCheckoutService.setDefaultPaymentAddress(cartModel);
        distCommerceCheckoutService.setDefaultDeliveryAddress(cartModel);
        distCommerceCheckoutService.setDefaultPaymentMode(cartModel);

        return Boolean.TRUE;
    }

    private boolean currentSiteHasPickupWarehouse() {
        return isNotEmpty(cmsSiteService.getCurrentSite().getPickupWarehouses());
    }

    private boolean isPickDeliveryMode(String deliveryMode) {
        return deliveryMode.equals(shippingOptionService.getPickupDeliveryModeCode());
    }

    private boolean shouldAllowEditShipping(CustomerModel customer) {
        if (isDefaultBillingAsShippingAddress(customer)) {
            return shouldAllowEditBilling(customer.getCustomerType());
        }
        return true;
    }

    private boolean isDefaultBillingAsShippingAddress(CustomerModel customer) {
        return nonNull(customer.getDefaultPaymentAddress())
                && Objects.equals(customer.getDefaultPaymentAddress(), customer.getDefaultShipmentAddress());
    }

    private boolean isUserAddressBlocked(AddressModel address) {
        String countryIso = address.getCountry() != null ? address.getCountry().getIsocode() : null;
        return isUserAddressBlocked(address.getPostalcode(), address.getStreetname(), address.getStreetnumber(), address.getTown(), countryIso);
    }

    private boolean isSAPValidPaymentModesChanged(B2BCustomerModel currentCustomer) {
        final CartModel sessionCart = getCart();

        if (nonNull(sessionCart) && currentCustomer.getDefaultPaymentMethod() != null) {
            boolean isPaymentChanged = sessionCart.getValidPaymentModes()
                                                  .stream()
                                                  .noneMatch(paymentMode -> paymentMode.getCode().equals(currentCustomer.getDefaultPaymentMethod()));
            if (isPaymentChanged) {
                sessionCart.setPaymentMode(null);
                getModelService().save(sessionCart);
            }
            return isPaymentChanged;
        }

        return Boolean.FALSE;
    }

    private boolean isSAPValidDeliveryModesChanged(B2BCustomerModel currentCustomer) {
        final CartModel sessionCart = getCart();

        if (nonNull(sessionCart) && currentCustomer.getDefaultDeliveryMethod() != null) {
            boolean isDeliveryChanged = sessionCart.getValidDeliveryModes()
                                                   .stream()
                                                   .noneMatch(deliveryMode -> deliveryMode.getCode().equals(currentCustomer.getDefaultDeliveryMethod()));
            if (isDeliveryChanged) {
                sessionCart.setDeliveryMode(null);
                getModelService().save(sessionCart);
            }
            return isDeliveryChanged;
        }
        return Boolean.FALSE;
    }

    @Override
    public String getDefaultDeliveryMode() {
        final Collection<DeliveryModeData> deliveryInfoDatas = userFacade.getSupportedDeliveryModesForUser();
        return getDefaultDeliveryMode(deliveryInfoDatas);
    }

    private String getDefaultDeliveryMode(final Collection<DeliveryModeData> deliveryInfoDatas) {
        DeliveryModeData secondDelInfoData = new DeliveryModeData();
        if (!CollectionUtils.isEmpty(deliveryInfoDatas) && deliveryInfoDatas.size() == 1) {
            return (deliveryInfoDatas.iterator().next()).getCode();
        } else if (!CollectionUtils.isEmpty(deliveryInfoDatas) && deliveryInfoDatas.size() > 1) {

            for (final DeliveryModeData deliveryInfoData : deliveryInfoDatas) {
                final boolean deMode = (null != deliveryInfoData && null != deliveryInfoData.getCode())
                                                                                                        ? SessionUtil.isShippingModeSelectable(deliveryInfoData.getCode())
                                                                                                        : false;
                if (BooleanUtils.isTrue(deliveryInfoData.getDefaultDeliveryMode()) && deMode) {

                    return deliveryInfoData.getCode();

                } else if (deMode) {
                    secondDelInfoData = deliveryInfoData;

                }
            }
        }

        return secondDelInfoData.getCode();
    }

    @Override
    public boolean setPaymentAddress(final AddressData address) {
        if (address != null && address.getId() != null) {
            final AddressModel paymentAddress = getPaymentAddressModelForCode(address.getId());
            if (paymentAddress != null) {
                savePaymentAddress(getCart(), paymentAddress);
                if (!isAnonymousCheckout() && !isEShopGroup()) {
                    final B2BCustomerModel currentUser = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();

                    // Default Payment address not exists
                    if (currentUser.getDefaultPaymentAddress() == null && Boolean.TRUE.equals(paymentAddress.getBillingAddress())) {
                        currentUser.setDefaultPaymentAddress(paymentAddress);
                        getModelService().save(currentUser);
                        getModelService().refresh(currentUser);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public DeliveryModeData getDeliveryModeForCode(final String code) {
        final DeliveryModeModel deliveryMode = getDeliveryService().getDeliveryModeForCode(code);
        if (deliveryMode != null) {
            return distDeliveryModeConverter.convert(deliveryMode);
        }
        return null;
    }

    @Override
    public String getPickupDeliveryModeCode() {
        return shippingOptionService.getPickupDeliveryModeCode();
    }

    @Override
    public List<String> getStandardDeliveryModeCodes() {
        return Arrays.asList(DistConstants.Shipping.METHOD_STANDARD, DistConstants.Shipping.METHOD_NORMAL_PICKUP);
    }

    // todo remove from CheckoutFacade and migrate somewhere more appropriate
    @Override
    public List<CountryData> getDeliveryCountriesAndRegions() {
        final UserModel currentUser = getUserService().getCurrentUser();

        // DISTRELEC-7348 in case of export shop only the original country must be shown
        if (isRegisteredCustomerOfExportShop(currentUser)) {
            final B2BUnitModel unit = ((B2BCustomerModel) currentUser).getDefaultB2BUnit();
            // In some case we don't have a country on unit level then we check the country
            // at default billing address
            final CountryModel country = unit.getCountry() != null ? unit.getCountry() : unit.getBillingAddress().getCountry();
            return Collections.singletonList(getCountryConverter().convert(country));
        } else {
            return Converters.convertAll(sortCountries(getDeliveryService().getDeliveryCountriesForOrder(null)), getCountryConverter());
        }
    }

    private boolean isRegisteredCustomerOfExportShop(final UserModel currentUser) {
        if (!getUserService().isAnonymousUser(currentUser)) {
            final B2BUnitModel unit = ((B2BCustomerModel) currentUser).getDefaultB2BUnit();
            return (unit.getSalesOrg() != null) && (unit.getSalesOrg().getCode() != null) && ("7801".equals(unit.getSalesOrg().getCode()));
        }
        return false;
    }

    @Override
    public List<CountryData> getRegisterCountries(final CMSSiteModel cmsSite) {
        final Collection<BaseStoreModel> stores = cmsSite.getStores();
        for (final BaseStoreModel baseStore : stores) {
            if ("distrelec_EX_b2b".equals(baseStore.getUid())) {
                return Converters.convertAll(sortCountries(baseStore.getRegisterCountries()), getCountryConverter());
            }
        }
        return null;
    }

    @Override
    public RegionData getRegionForIsocode(final String countryIso, final String regionIso) {
        final CountryModel country = getCommonI18NService().getCountry(countryIso);
        if (country != null) {
            final RegionModel region = getCommonI18NService().getRegion(country, regionIso);
            final RegionData regionData = new RegionData();
            regionData.setIsocode(region.getIsocode());
            regionData.setName(region.getName());
            regionData.setCountryIso(country.getIsocode());
            return regionData;
        }
        return null;
    }

    @Override
    public List<AddressData> getShippingAddresses() {
        final CartModel cart = getCart();
        if (nonNull(cart)) {
            final CustomerModel customer = (CustomerModel) cart.getUser();
            if (customer != null) {
                final List<AddressModel> shippingAddresses = getDeliveryService().getSupportedDeliveryAddressesForOrder(cart, true);
                if (CollectionUtils.isNotEmpty(shippingAddresses)) {
                    return Converters.convertAll(shippingAddresses, getAddressConverter());
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<AddressData> getBillingAddresses() {
        final CartModel cart = getCart();
        if (nonNull(cart)) {
            final B2BCustomerModel customer = (B2BCustomerModel) cart.getUser();
            if (customer != null) {
                final B2BUnitModel unit = b2bUnitService.getParent(customer);
                if (CollectionUtils.isNotEmpty(unit.getBillingAddresses())) {
                    return Converters.convertAll(unit.getBillingAddresses(), getAddressConverter());
                }
            }

        }
        return Collections.emptyList();
    }

    @Override
    public void setPickupLocation(final String code) {
        final CartModel cart = getCart();
        if (nonNull(cart)) {
            if (StringUtils.isNotBlank(code)) {
                final WarehouseModel warehouse = warehouseService.getWarehouseForCode(code);
                if (warehouse != null && isWarehouseValid(warehouse)) {
                    cart.setPickupLocation(warehouse);
                }
            } else {
                cart.setPickupLocation(null);
            }
            getModelService().save(cart);
        }
    }

    @Override
    public void setRicevutaBancaria(final String bankCollectionCAB, final String bankCollectionABI, final String bankCollectionInstitute) {
        final CartModel cart = getCart();
        if (nonNull(cart) && DistUtils.isB2BCustomer(getCheckoutCustomerStrategy().getCurrentUserForCheckout())) {
            cart.setBankCollectionABI(bankCollectionABI);
            cart.setBankCollectionCAB(bankCollectionCAB);
            cart.setBankCollectionInstitute(bankCollectionInstitute);
            getModelService().save(cart);
        }
    }

    @Override
    public String setOrderNote(final String note) {
        final CartModel cart = getCart();
        if (nonNull(cart)) {
            ((DistCommerceCheckoutService) getCommerceCheckoutService()).setAttribute(cart, AbstractOrderModel.NOTE, note);
            return cart.getNote();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String setCostCenter(final String costCenter) {
        final CartModel cart = getCart();
        if (nonNull(cart) && DistUtils.isB2BCustomer(getCheckoutCustomerStrategy().getCurrentUserForCheckout())) {
            ((DistCommerceCheckoutService) getCommerceCheckoutService()).setAttribute(cart, AbstractOrderModel.COSTCENTER, costCenter);
            return cart.getCostCenter();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String setProjectNumber(final String projectNumber) {
        final CartModel cart = getCart();
        if (nonNull(cart) && DistUtils.isB2BCustomer(getCheckoutCustomerStrategy().getCurrentUserForCheckout())) {
            ((DistCommerceCheckoutService) getCommerceCheckoutService()).setAttribute(cart, AbstractOrderModel.PROJECTNUMBER, projectNumber);
            return cart.getProjectNumber();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void removePaymentInfo() {
        final CartModel cart = getCart();
        if (nonNull(cart)) {
            cart.setPaymentInfo(null);
            getModelService().save(cart);
        }
    }

    @Override
    public DistPaymentModeData getPaymentModeForCode(final String paymentModeCode) {
        final PaymentModeModel paymentMode = paymentModeService.getPaymentModeForCode(paymentModeCode);
        return paymentModeConverter.convert(paymentMode);
    }

    @Override
    public DistPaymentModeData getCreditCardPaymentMode() {
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            final List<AbstractDistPaymentModeModel> paymentModes = paymentOptionService.getSupportedPaymentOptions(cartModel, false);

            Optional<AbstractDistPaymentModeModel> ccPaymentMode = paymentModes.stream()
                                                                               .filter(paymentMode -> CreditCardPaymentInfoModel._TYPECODE.equals(paymentMode.getPaymentInfoType()
                                                                                                                                                             .getCode()))
                                                                               .findFirst();
            if (ccPaymentMode.isPresent()) {
                return paymentModeConverter.convert(ccPaymentMode.get());
            }

        }
        return null;
    }

    @Override
    public boolean setPaymentMode(final DistPaymentModeData paymentModeData) {
        validateParameterNotNullStandardMessage("paymentModeData", paymentModeData);

        final CartModel cartModel = getCart();
        if (cartModel != null) {
            final PaymentModeModel paymentModeModel = paymentModeService.getPaymentModeForCode(paymentModeData.getCode());
            if (paymentModeModel != null) {
                return distCommerceCheckoutService.setPaymentMode(cartModel, paymentModeModel);
            }
        }

        return false;
    }

    @Override
    @CxTransaction
    public void calculateOrder(final boolean simulate) throws CalculationException {
        CartModel cart = getCart();
        if (nonNull(cart)) {
            orderCalculationService.calculate(cart, simulate);
        }
    }

    @Override
    public CustomerData getCurrentCheckoutCustomer() {
        return b2BCustomerConverter.convert((B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout());
    }

    @Override
    public boolean isAnonymousCheckout() {
        if (getCartService().hasSessionCart()) {
            return getCheckoutCustomerStrategy().isAnonymousCheckout() || isCustomerTypeGuest();
        }
        return Boolean.FALSE;
    }

    private boolean isCustomerTypeGuest() {
        CartModel cart = getCart();
        if (nonNull(cart)) {
            return cart.getUser() instanceof B2BCustomerModel && CustomerType.GUEST.equals(((B2BCustomerModel) cart.getUser()).getCustomerType());
        }
        return Boolean.FALSE;
    }

    @Override
    public void finishAnonymousCheckout() {
        sessionService.removeAttribute(CheckoutCustomerStrategy.ANONYMOUS_CHECKOUT);
    }

    @Override
    public String getParameterizedPaymentUrl(final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        return distCommercePaymentService.getPaymentUrlForCart(getCart(), userAgent);
    }

    @Override
    public Map<String, String> getPaymentParameters(final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        return distCommercePaymentService.getPaymentParameters(getCart(), userAgent);
    }

    @Override
    public Map<String, String> handlePaymentNotify(final String requestBody, final String currencyCode) {
        return distCommercePaymentService.handlePaymentNotify(requestBody, currencyCode);
    }

    @Override
    public String handlePaymentSuccessFailure(final Map<String, String> allParameters) throws InvalidCartException {
        if (MapUtils.isNotEmpty(allParameters)) {
            final String paymentCode = distCommercePaymentService.handlePaymentSuccessFailure(allParameters);
            if (StringUtils.equals(paymentCode, PAYMENT_SUCCESS_CODE)) {
                OrderData order = placeOrder();
                if (isAnonymousCheckout()) {
                    return order.getGuid();
                }
                return order.getCode();
            }
            return paymentCode;
        } else {
            throw new IllegalStateException("Success/Failure failed because of no parameters!");
        }
    }

    @Override
    public boolean modifyPaymentModesIfBudgetApplied() {
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        final DistB2BBudgetModel budget = b2bCommerceBudgetService.getActiveBudget(currentCustomer);
        if (budget != null) {
            // this means that
            final CartModel cart = getCart();
            final List<AbstractDistPaymentModeModel> paymentModes = paymentOptionService.getSupportedPaymentOptions(cart, true);
            cart.setValidPaymentModes(paymentModes);

            if (cart.getPaymentMode() == null || !paymentModes.contains(cart.getPaymentMode())) {
                cart.setPaymentMode(paymentModes.get(0));
            }

            getModelService().save(cart);
            return true;
        }
        return false;
    }

    @Override
    public boolean isCancellationPolicyConfirmationRequired() {
        final String countryIsoCode = storeSessionFacade.getCurrentCountry().getIsocode();
        final String countriesWithoutCancellationPolicy = configurationService.getConfiguration()
                                                                              .getString(DistConstants.PropKey.Shop.COUNTRIES_WITHOUT_CANCELLATION_POLICY);

        // Do not display cancellation policy checkbox for configured countries
        return StringUtils.isBlank(countryIsoCode) || StringUtils.isBlank(countriesWithoutCancellationPolicy)
                || !countriesWithoutCancellationPolicy.contains(countryIsoCode);
    }

    @Override
    public boolean isRequestedDeliveryDateEnabledForCurrentCart() {

        // check if it's globally enabled
        if (configurationService.getConfiguration().getBoolean("delivery.date.future.enable.global", false) && hasCheckoutCart()) {
            // Check if it's country enabled
            final CartModel cart = getCart();
            if (cart == null || cart.getSite() == null) {
                return false;
            }
            final CMSSiteModel cmsSite = ((CMSSiteModel) cart.getSite());

            if (cmsSite.isRequestedDeliveryDateEnabled()) {
                final Set<DistSalesStatusModel> forbiddenStatuses = cmsSite.getRequestedDeliveryDateForbiddenSalesStatus();
                // check if cart allow to specify a requested delivery date
                for (final AbstractOrderEntryModel entry : cart.getEntries()) {
                    final DistSalesOrgProductModel productStatus = distSalesOrgProductService.getSalesOrgProduct(entry.getProduct(),
                                                                                                                 cmsSite.getSalesOrg());
                    if (forbiddenStatuses.contains(productStatus.getSalesStatus())) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public Date getMinimumRequestedDeliveryDateForCurrentCart() {
        return getWorkingDate(configurationService.getConfiguration().getInt("cart.minimurequested.deliverydays", 7));
    }

    private Date getWorkingDate(final int workingDaysToAdd) {
        final Calendar cal = DateTimeUtils.getTodaysMidnightPlus1MinuteAsCalender();
        for (int i = 0; i < workingDaysToAdd; i++) {
            do {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            } while (!isWorkingDay(cal));
        }
        return cal.getTime();
    }

    private static boolean isWorkingDay(final Calendar cal) {
        final int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY;
    }

    @Override
    public Date getMaximumRequestedDeliveryDateForCurrentCart() {
        final Date minRequestedDeliveryDate = getMinimumRequestedDeliveryDateForCurrentCart();
        final Date maxScheduledOrderDate = cmsSiteService.getCurrentSite().getMaxScheduledOrderDeliveryDate();
        final Date maxRequestedDeliveryDate = maxScheduledOrderDate != null ? maxScheduledOrderDate : getConfiguredDate(minRequestedDeliveryDate);
        return maxRequestedDeliveryDate.after(minRequestedDeliveryDate) ? maxRequestedDeliveryDate : minRequestedDeliveryDate;
    }
    private Date getConfiguredDate(final Date minRequestedDeliveryDate) {
        final int maxDeliveryDays = configurationService.getConfiguration().getInt("cart.max.deliverydays", 180);
        return DateUtils.addDays(minRequestedDeliveryDate, maxDeliveryDays);
    }

    @Override
    public void requestDeliveryDate(final Date deliveryDate) {
        final CartModel cart = getCart();
        if (nonNull(cart)) {
            cart.setRequestedDeliveryDate(deliveryDate);
            getModelService().save(cart);
            getModelService().refresh(cart);
        }
    }

    @Override
    public List<CountryData> getDeliveryCountriesForGuestCheckout(SiteChannel siteChannel) {
        BaseSiteModel currentSite = getBaseSiteService().getCurrentBaseSite();
        BaseStoreModel baseStore = currentSite.getStores()
                                              .stream()
                                              .filter(store -> store.getChannel().equals(siteChannel))
                                              .findFirst()
                                              .orElse(null);

        if (nonNull(baseStore)) {
            return Converters.convertAll(sortCountries(baseStore.getGuestCheckoutDeliveryCountries()), getCountryConverter());
        }
        return Collections.emptyList();
    }

    @Override
    public Date calculateScheduledDeliveryDate(String date, String dateFormat) throws ParseException {
        Date selectedDate = new SimpleDateFormat(dateFormat).parse(date);
        Date workingDayDate = fetchNextWorkingDayIfWeekend(selectedDate);

        Date minimumRequestDeliveryDate = getMinimumRequestedDeliveryDateForCurrentCart();
        Date maximumRequestDeliveryDate = getMaximumRequestedDeliveryDateForCurrentCart();
        if (workingDayDate.before(minimumRequestDeliveryDate)) {
            return minimumRequestDeliveryDate;
        }

        if (workingDayDate.after(maximumRequestDeliveryDate)) {
            return maximumRequestDeliveryDate;
        }
        return workingDayDate;
    }

    @Override
    public boolean isScheduleDeliveryAllowedForCurrentCart() {
        CartModel cart = getCart();
        if (nonNull(cart) && nonNull(cart.getDeliveryMode())) {
            if (storeSessionFacade.isCurrentShopExport() && isSelectedDeliveryModePickup(cart)) {
                return Boolean.TRUE;
            }
            return !isSelectedDeliveryModePickup(cart);
        }
        return Boolean.FALSE;
    }

    private boolean isSelectedDeliveryModePickup(CartModel cart) {
        return getPickupDeliveryModeCode().equals(cart.getDeliveryMode().getCode());
    }

    @Override
    public boolean isPickupAvailableForCart(CartData cartData) {
        return cartData.getValidDeliveryModes().stream()
                       .anyMatch(distDeliveryModeData -> distDeliveryModeData.getCode().equals(METHOD_PICKUP));
    }

    @Override
    public boolean isPickupAllowedForCart(CartData cartData) {
        return !isAnonymousCheckout() && !isEShopGroup() && (BooleanUtils.isFalse(cartData.getWaldom()) || storeSessionFacade.isCurrentShopExport());
    }

    @Override
    public boolean isPickupAllowedForCurrentCart() {
        CartModel cart = getCart();
        return nonNull(cart) && !isEShopGroup() && !distCartService.isRs(cart)
                && (BooleanUtils.isFalse(distCartService.isWaldom(cart)) || storeSessionFacade.isCurrentShopExport());
    }

    @Override
    public boolean isScheduleDeliveryAllowed(CartData cartData) {
        return !isAnonymousCheckout() && isStandardDeliveryModeSupportedForCart(cartData);
    }

    private boolean isStandardDeliveryModeSupportedForCart(CartData cartData) {
        if (CollectionUtils.isNotEmpty(cartData.getValidDeliveryModes())) {
            List<String> validDeliveryModesForCart = cartData.getValidDeliveryModes()
                                                             .stream()
                                                             .map(DeliveryModeData::getCode)
                                                             .collect(Collectors.toList());

            return CollectionUtils.containsAny(getStandardDeliveryModeCodes(), validDeliveryModesForCart);
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean isNotLimitedUserType() {
        return !isEShopGroup() && !isAnonymousCheckout();
    }

    @Override
    public boolean areDeliveryModeAndAddressesSet() {
        CartModel cart = getCart();
        return nonNull(cart) && nonNull(cart.getDeliveryMode())
                && distAddressService.isAddressValid(cart.getPaymentAddress())
                && (distAddressService.isAddressValid(cart.getDeliveryAddress()) || (METHOD_PICKUP).equals(cart.getDeliveryMode().getCode()));
    }

    @Override
    public boolean isBillingAddressShippable(AddressData billingAddress) {
        return nonNull(billingAddress) && billingAddress.isBillingAddress() && billingAddress.isShippingAddress() && isNotLimitedUserType();
    }

    @Override
    public boolean shouldDisplayCreditWarningForExportShop(CartData cartData) {
        if (!isAnonymousCheckout() && storeSessionFacade.isCurrentShopExport()) {
            return cartData.getValidPaymentModes().stream()
                           .noneMatch(paymentMode -> StringUtils.contains(paymentMode.getCode(), DistConstants.PaymentMethods.INVOICE));
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean shouldDisplayVatWarningForExportShop(CustomerData customer) {
        if (storeSessionFacade.isCurrentShopExport() && customer.getContactAddress() != null) {
            CountryData country = customer.getContactAddress().getCountry();
            if (country == null && (customer.getBillingAddress() != null)) {
                final B2BUnitData unit = customer.getUnit();
                country = unit == null ? null : unit.getCountry();
            }

            if (country != null && country.isEuropean()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean shouldAllowEditBilling(CustomerType customerType) {
        if (isAnonymousCheckout()) {
            // has to be here otherwise export shop will override it
            return Boolean.TRUE;
        }

        if (isEShopGroup()) {
            return Boolean.TRUE;
        }

        if (storeSessionFacade.isCurrentShopExport()
                && BooleanUtils.isNotTrue(sessionService.getAttribute(DistConstants.Session.IS_NEW_REGISTRATION))) {
            return Boolean.FALSE;
        }

        if (CustomerType.B2B_KEY_ACCOUNT.equals(customerType)) {
            return Boolean.FALSE;
        }

        if (CollectionUtils.size(getBillingAddresses()) > 1) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public boolean isCreditCardPaymentAllowed(CartData cartData) {
        if (CollectionUtils.isNotEmpty(cartData.getValidPaymentModes()) && !isEShopGroup()) {
            return cartData.getValidPaymentModes().stream()
                           .anyMatch(DistPaymentModeData::getCreditCardPayment);
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean isCodiceDestinatarioShown(CustomerData customer) {
        return DistConstants.CountryIsoCode.ITALY.equals(storeSessionFacade.getCurrentCountry().getIsocode())
                && (CustomerType.B2B_KEY_ACCOUNT.equals(customer.getCustomerType()) || CustomerType.B2B.equals(customer.getCustomerType()) || isEShopGroup());
    }

    @Override
    public boolean isSupportedExportCountry(String countryIsoCode) {
        return getBaseStoreService().getCurrentBaseStore().getRegisterCountries()
                                    .stream()
                                    .map(CountryModel::getIsocode)
                                    .anyMatch(code -> StringUtils.equals(countryIsoCode, code));
    }

    @Override
    public boolean isDeliveryModePriceVisible() {
        return BooleanUtils.isTrue(cmsSiteService.getCurrentSite().getCheckoutDeliveryMethodPricesShown());
    }

    @Override
    public void checkForPurchaseBlockedProducts() {
        List<String> purchaseBlockedProductCodes = availabilityService.getPurchaseBlockedProductCodes();

        if (CollectionUtils.isNotEmpty(purchaseBlockedProductCodes)) {
            throw new PurchaseBlockedException(purchaseBlockedProductCodes);
        }
    }

    protected boolean isWarehouseValid(final WarehouseModel warehouse) {
        final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();
        return cmsSite.getCheckoutPickupWarehouses().contains(warehouse);
    }

    protected AddressModel getPaymentAddressModelForCode(final String code) {
        Assert.notNull(code, "Parameter code cannot be null.");

        final CartModel cartModel = getCart();
        if (cartModel != null) {
            final B2BCustomerModel customer = (B2BCustomerModel) cartModel.getUser();
            if (customer != null) {
                final B2BUnitModel unit = b2bUnitService.getParent(customer);
                final Collection<AddressModel> addresses = unit.getBillingAddresses();
                if (CollectionUtils.isNotEmpty(addresses)) {
                    for (final AddressModel address : addresses) {
                        if (code.equals(address.getPk().toString())) {
                            return address;
                        }
                    }
                }
            }
        }
        return null;
    }

    protected void savePaymentAddress(final CartModel cart, final AddressModel address) {
        cart.setPaymentAddress(address);
        getModelService().save(cart);
    }

    protected void saveDeliveryAddress(final CartModel cart, final AddressModel address) {
        cart.setDeliveryAddress(address);
        getModelService().save(cart);
    }

    protected List<CountryModel> sortCountries(final Collection<CountryModel> countries) {
        final List<CountryModel> result = new ArrayList<>(countries);
        result.sort(CountryComparator.INSTANCE);
        return result;
    }

    public static class CountryComparator extends AbstractComparator<CountryModel> {
        public static final CountryComparator INSTANCE = new CountryComparator();

        @Override
        protected int compareInstances(final CountryModel country1, final CountryModel country2) {
            int result = (country1.getName() != null && country2.getName() != null) ? country1.getName().compareToIgnoreCase(country2.getName()) : BEFORE;
            if (EQUAL == result) {
                result = country1.getIsocode().compareToIgnoreCase(country2.getIsocode());
            }
            return result;
        }
    }

    @Override
    public boolean isCompleteDeliveryPossible() {
        final CartModel cart = getCart();
        if (nonNull(cart)) {
            if (BooleanUtils.isTrue(cart.getCompleteDelivery())) {
                return Boolean.TRUE;
            }
            if (distCartService.isCreditBlocked(cart) || CollectionUtils.size(cart.getEntries()) < 2 || isAnyCartEntryInvalid(cart)) {
                return Boolean.FALSE;
            }
            return cart.getEntries()
                       .stream()
                       .anyMatch(AbstractOrderEntryModel::getIsBackOrder);
        }
        return Boolean.FALSE;
    }

    private boolean isAnyCartEntryInvalid(CartModel cart) {
        return cart.getEntries()
                   .stream()
                   .filter(entry -> entry != null && entry.getProduct() != null)
                   .anyMatch(entry -> isCalibrationItem(entry.getProduct())
                           || distProductService.isProductBANS(entry.getProduct())
                           || StringUtils.equals(DistConstants.Product.WALDOM, entry.getMview()));
    }

    @Override
    public Date fetchNextWorkingDayIfWeekend(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        while (!isWorkingDay(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return calendar.getTime();
    }

    @Override
    public boolean isErrorHandled(final String paymentErrorDescription) {
        final String listOfErrorsHandled = configurationService.getConfiguration().getString("payment.errors.handled.list");
        final List<String> errorsHandled = Arrays.asList(listOfErrorsHandled.replaceAll("[^,a-zA-Z]+", "").toUpperCase().split(","));
        return errorsHandled.contains(paymentErrorDescription.replaceAll("[^,a-zA-Z]+", "").toUpperCase());

    }

    @Override
    public void setCompleteDeliveryOnCart(final boolean completeDelivery) {
        final CartModel cart = getCart();
        if (nonNull(cart)) {
            cart.setCompleteDelivery(completeDelivery);
            getModelService().save(cart);
        }
    }

    @Override
    public boolean hasUnallowedBackorders() {
        final CartModel cart = getCart();
        if (isNull(cart) || isBackOrderNotAllowedInCurrentBaseStore()) {
            return false;
        }

        return cart.getEntries()
                   .stream()
                   .filter(orderEntry -> Objects.nonNull(orderEntry.getIsBackOrderProfitable()))
                   .anyMatch(orderEntry -> !orderEntry.getIsBackOrderProfitable());

    }

    private boolean isBackOrderNotAllowedInCurrentBaseStore() {
        BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
        return BooleanUtils.isNotTrue(currentBaseStore != null && currentBaseStore.getBackorderAllowed());
    }

    /**
     * Create a new instance of #CommerceCheckoutParameter for the cart calculation.
     *
     * @param cart
     *            the cart to calculate
     * @param enableHooks
     * @param forceRecalculate
     *            a boolean flag to force the calculation on ERP side.
     * @return a new instance of #CommerceCheckoutParameter
     */
    protected CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart, final boolean enableHooks, final boolean forceRecalculate) {
        final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cart, enableHooks);
        parameter.setRecalculate(forceRecalculate);
        return parameter;
    }

    private boolean isCalibrationItem(final ProductModel product) {
        return product.getTransportGroup() != null && CALIBRATION_TG.contains(product.getTransportGroup().getCode());
    }

    /**
     * Saves Vat or Email details for Italian customers.
     *
     * @param vat4
     * @param legalEmail
     */
    @Override
    public void saveCustomerVatDetails(final String vat4, final String legalEmail) {
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        if (vat4 != null) {
            currentCustomer.setVat4(vat4);
        }
        if (legalEmail != null) {
            currentCustomer.setLegalEmail(legalEmail);
        }
        // Save in DB
        getModelService().save(currentCustomer);
        getModelService().refresh(currentCustomer);
        customerService.updateCustomer(currentCustomer);

    }

    /**
     * Saves codiceCIG or codiceCUP details for orders of Italian customers.
     *
     * @param codiceCIG
     * @param codiceCUP
     */
    @Override
    public void saveOrderCodiceDetails(final String codiceCUP, final String codiceCIG) {
        final CartModel cartModel = getCart();
        if (nonNull(cartModel)) {
            if (codiceCIG != null) {
                cartModel.setCodiceCIG(codiceCIG);
            }
            if (codiceCUP != null) {
                cartModel.setCodiceCUP(codiceCUP);
            }
            // Save in DB
            getModelService().save(cartModel);
            getModelService().refresh(cartModel);
        }
    }

    /**
     * Saves isReevooEligible for orders of DE customers.
     *
     * @param isReevooEligible
     */
    @Override
    public void saveReevooEligibleFlag(final Boolean isReevooEligible) {
        final CartModel cartModel = getCart();
        if (nonNull(cartModel) && nonNull(isReevooEligible)) {
            cartModel.setReevooEligible(isReevooEligible);
            getModelService().save(cartModel);
        }
    }

    @Override
    public boolean isGuestCheckoutEnabledForSite() {
        return BooleanUtils.isTrue(cmsSiteService.getCurrentSite().getGuestCheckoutEnabled());
    }

    @Override
    public boolean isCurrentCustomerBlocked() {
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        DistBlockedB2BCustomerRuleModel blockedCustomerRule = distBlockedB2BCustomerRuleService.findBlockedB2BCustomerRule(currentCustomer.getUid());
        return blockedCustomerRule != null;
    }

    @Override
    public boolean isUserAddressBlocked(String postalCode, String streetName, String streetNumber, String city, String countryIso) {
        DistBlockedUserAddressRuleModel blockedDeliveryAddressRule = distBlockedUserAddressRuleService.findBlockedUserAddressRule(postalCode, streetName,
                                                                                                                                  streetNumber, city,
                                                                                                                                  countryIso);
        return blockedDeliveryAddressRule != null;
    }

    @Override
    public boolean isAddressValid(AddressData addressData) {
        return addressData != null && addressData.getCountry() != null
                && StringUtils.isNotBlank(addressData.getLine1())
                && StringUtils.isNotBlank(addressData.getPostalCode())
                && StringUtils.isNotBlank(addressData.getTown())
                && (StringUtils.isNotBlank(addressData.getPhone1()) || StringUtils.isNotBlank(addressData.getCellphone()));
    }

    @Override
    public CCPaymentInfoData getPaymentInfo() {
        CartModel cart = getCart();
        if (nonNull(cart.getPaymentInfo())) {
            return creditCardPaymentInfoConverter.convert((CreditCardPaymentInfoModel) cart.getPaymentInfo());
        }
        return null;
    }

    @Override
    protected CartModel getCart() {
        CartModel cart = hasCheckoutCart() ? getCartService().getSessionCart() : null;
        if (isNull(cart)) {
            LOG.warn("Session cart is not present");
        }
        return cart;
    }

    @Override
    public List<DeliveryModeData> getValidDeliveryModesForCurrentCart(boolean isPickupAllowed) {
        CartModel cart = getCart();
        if (nonNull(cart)) {
            return distDeliveryModeConverter.convertAll(getValidDeliveryModes(isPickupAllowed, cart));
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isDeliveryAddressNotSetForCurrentCart() {
        CartModel cart = getCart();
        if (nonNull(cart)) {
            return isNull(cart.getDeliveryAddress());
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean isMinimumOrderValueNotReached(CartModel cart) {
        if (nonNull(cart)) {
            return nonNull(cart.getMissingMov()) && cart.getMissingMov() > 0;
        }
        return Boolean.FALSE;
    }

    private List<AbstractDistDeliveryModeModel> getValidDeliveryModes(boolean isPickupAllowed, CartModel cart) {
        return isPickupAllowed ? cart.getValidDeliveryModes()
                               : cart.getValidDeliveryModes()
                                     .stream()
                                     .filter(deliveryMode -> !METHOD_PICKUP.equals(deliveryMode.getCode()))
                                     .collect(Collectors.toList());
    }

    @Override
    public CountryData getCountryForIsocode(final String countryIso) {
        final CountryModel countryModel = getCommonI18NService().getCountry(countryIso);
        return countryModel != null ? getCountryConverter().convert(countryModel) : null;
    }

    @Override
    public void updateGuestAddress(AddressData addressData) throws CalculationException {
        distCommerceCheckoutService.updateVatIdForGuest(addressData.getCodiceFiscale());
        AddressData editedAddress = userFacade.editAddress(addressData, Boolean.FALSE);
        editedAddress.setCodiceFiscale(addressData.getCodiceFiscale());
        if (distSalesOrgService.isCurrentSalesOrgExport()) {
            distCartFacade.recalculateCart();
        }
    }

    @Override
    public void createGuestAddress(AddressData addressData) {
        distCommerceCheckoutService.updateVatIdForGuest(addressData.getCodiceFiscale());
        AddressData newAddress = userFacade.addAddress(addressData, Boolean.FALSE);
        newAddress.setCodiceFiscale(addressData.getCodiceFiscale());
        setPaymentAddress(newAddress);
        setDeliveryAddress(newAddress, Boolean.TRUE);
    }
}
