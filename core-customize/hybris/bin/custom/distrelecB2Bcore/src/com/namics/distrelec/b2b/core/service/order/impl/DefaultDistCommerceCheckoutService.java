/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.PaymentMethods.CREDIT_CARD;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.util.Objects.nonNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.inout.erp.DistOrderHistoryCacheManager;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.order.DistCardPaymentService;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCheckoutService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorservices.order.impl.DefaultB2BCommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.PaymentModeService;

/**
 * DefaultDistCommerceCheckoutService extends DefaultCommerceCheckoutService
 * 
 * @author dsivakumaran, Namics AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.1
 * 
 */
public class DefaultDistCommerceCheckoutService extends DefaultB2BCommerceCheckoutService implements DistCommerceCheckoutService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistCommerceCheckoutService.class);

    @Autowired
    private DistOrderHistoryCacheManager distOrderHistoryCacheManager;

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Autowired
    private ShippingOptionService shippingOptionService;

    @Autowired
    private PaymentModeService paymentModeService;

    @Autowired
    private DistCardPaymentService cardPaymentService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Override
    public CommerceOrderResult placeOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException {
        LOG.info("Start place order");
        LOG.info("Order Code: {}", parameter.getCart().getCode());
        LOG.info("User: {}", parameter.getCart().getUser().getUid());
        LOG.info("Total price of order: {}", parameter.getCart().getTotalPrice());
        final CommerceOrderResult commerceOrderResult = getCommercePlaceOrderStrategy().placeOrder(parameter);
        if (commerceOrderResult.getOrder() != null) {
            distOrderHistoryCacheManager.clearHistoryCacheForOrder(commerceOrderResult.getOrder());
        }

        return commerceOrderResult;
    }

    @Override
    public boolean setPaymentMode(final CartModel cartModel, final PaymentModeModel paymentModeModel) {
        validateParameterNotNull(cartModel, "Cart model cannot be null");
        validateParameterNotNull(paymentModeModel, "Payment mode model cannot be null");

        cartModel.setPaymentMode(paymentModeModel);
        getModelService().save(cartModel);

        return true;
    }

    @Override
    public void setAttribute(final CartModel cart, final String attribute, final String value) {
        Assert.hasText(attribute, "Specify which attribute has to be set.");
        Assert.notNull(cart, "Cart cannot be null.");

        getModelService().setAttributeValue(cart, attribute, value);
        getModelService().save(cart);
    }

    @Override
    public void setDefaultPaymentAddress(CartModel cart) {
        Assert.notNull(cart, "Cart cannot be null.");
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();

        cart.setPaymentAddress(currentCustomer.getDefaultPaymentAddress());
        getModelService().save(cart);
    }

    @Override
    public void setDefaultDeliveryAddress(CartModel cart) {
        Assert.notNull(cart, "Cart cannot be null.");
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();

        cart.setDeliveryAddress(currentCustomer.getDefaultShipmentAddress());
        getModelService().save(cart);
    }

    @Override
    public void setDefaultPaymentMode(CartModel cart) {
        Assert.notNull(cart, "Cart cannot be null.");
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();

        PaymentModeModel defaultPaymentMode = paymentModeService.getPaymentModeForCode(currentCustomer.getDefaultPaymentMethod());
        if (nonNull(defaultPaymentMode)) {
            setPaymentMode(cart, defaultPaymentMode);
            if (CREDIT_CARD.equalsIgnoreCase(defaultPaymentMode.getCode()) && isDefaultPaymentInfoValid(currentCustomer.getDefaultPaymentInfo())) {
                cart.setPaymentInfo(currentCustomer.getDefaultPaymentInfo());
            }
        }
    }

    private boolean isDefaultPaymentInfoValid(PaymentInfoModel defaultPaymentInfo) {
        return defaultPaymentInfo instanceof CreditCardPaymentInfoModel && defaultPaymentInfo.isSaved()
                && cardPaymentService.isCreditCardExpiryDateValid((CreditCardPaymentInfoModel) defaultPaymentInfo);
    }

    @Override
    public void setDefaultDeliveryMode(CartModel cart) {
        Assert.notNull(cart, "Cart cannot be null.");
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();

        cart.setDeliveryMode(shippingOptionService.getDefaultShippingOptionForUser(currentCustomer));
        getModelService().save(cart);
    }

    @Override
    public void updateVatIdForGuest(String codiceFiscale) {
        if (distSalesOrgService.isCurrentSalesOrgItaly() && StringUtils.isNotEmpty(codiceFiscale)) {
            B2BCustomerModel currentCustomer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();
            B2BUnitModel unit = b2bUnitService.getParent(currentCustomer);
            unit.setVatID(codiceFiscale);
            getModelService().save(unit);
        }
    }

}
