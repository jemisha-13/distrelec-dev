/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BPaymentTypePopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Populates a CartModel to a B2BCartData.
 *
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 *
 */
public class DistB2BCartPopulator implements Populator<CartModel, CartData> {

    @Autowired
    private UserService userService;

    @Autowired
    private B2BOrderService b2bOrderService;

    @Autowired
    @Qualifier("b2BCustomerConverter")
    private AbstractPopulatingConverter<UserModel, CustomerData> b2bCustomerConverter;

    @Autowired
    private B2BPaymentTypePopulator b2bPaymentTypePopulator;

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Override
    public void populate(final CartModel cartModel, final CartData b2BCartData) {
        b2BCartData.setPurchaseOrderNumber(cartModel.getPurchaseOrderNumber());
        final CheckoutPaymentType paymentType = cartModel.getPaymentType() != null ? cartModel.getPaymentType() : CheckoutPaymentType.ACCOUNT;
        final B2BPaymentTypeData paymentTypeData = new B2BPaymentTypeData();
        getB2bPaymentTypePopulator().populate(paymentType, paymentTypeData);
        b2BCartData.setPaymentType(paymentTypeData);

        if (!userService.isAnonymousUser(cartModel.getUser())) {
            b2BCartData.setB2bCustomerData(b2bCustomerConverter.convert(cartModel.getUser()));
        }
        b2BCartData.setQuoteAllowed(Boolean.valueOf(getB2bOrderService().isQuoteAllowed(cartModel)));
    }

    public B2BPaymentTypePopulator getB2bPaymentTypePopulator() {
        return b2bPaymentTypePopulator;
    }

    public void setB2bPaymentTypeConverter(final B2BPaymentTypePopulator b2bPaymentTypeConverter) {
        this.b2bPaymentTypePopulator = b2bPaymentTypeConverter;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public B2BOrderService getB2bOrderService() {
        return b2bOrderService;
    }

    public void setB2bOrderService(final B2BOrderService b2bOrderService) {
        this.b2bOrderService = b2bOrderService;
    }

    public AbstractPopulatingConverter<UserModel, CustomerData> getB2bCustomerConverter() {
        return b2bCustomerConverter;
    }

    public void setB2bCustomerConverter(final AbstractPopulatingConverter<UserModel, CustomerData> b2bCustomerConverter) {
        this.b2bCustomerConverter = b2bCustomerConverter;
    }

    public CheckoutCustomerStrategy getCheckoutCustomerStrategy() {
        return checkoutCustomerStrategy;
    }

    public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy) {
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
    }

}
