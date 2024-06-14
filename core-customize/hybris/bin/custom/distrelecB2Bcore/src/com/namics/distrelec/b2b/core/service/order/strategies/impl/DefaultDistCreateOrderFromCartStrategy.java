/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.strategies.impl;

import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import de.hybris.platform.b2b.strategies.BusinessProcessStrategy;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.impl.DefaultCreateOrderFromCartStrategy;

/**
 * Extend DefaultB2BCreateOrderFromCartStrategy to use predefine orderCode from cart.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistCreateOrderFromCartStrategy extends DefaultCreateOrderFromCartStrategy implements BusinessProcessStrategy {
    private List<BusinessProcessStrategy> businessProcessStrategies;

    @Override
    public OrderModel createOrderFromCart(final CartModel cart) throws InvalidCartException {
        final OrderModel orderFromCart = super.createOrderFromCart(cart);
        postProcessOrder(cart, orderFromCart);
        return orderFromCart;
    }

    @Override
    public void createB2BBusinessProcess(final OrderModel order) {
        OrderStatus status = order.getStatus();
        Assert.notNull(status, "Order status should have been set for order " + order.getCode());
        BusinessProcessStrategy businessProcessStrategy = getBusinessProcessStrategy(status.getCode());
        Assert.notNull(businessProcessStrategy,
                String.format("The strategy for creating a business process with name %s should have been created", new Object[] { status.getCode() }));
        businessProcessStrategy.createB2BBusinessProcess(order);
    }

    /**
     * @param cart
     * @param orderFromCart
     */
    protected void postProcessOrder(final CartModel cart, final OrderModel orderFromCart) {
        // Do nothing, as the approval process gets started afterwards (see DefaultDistCommerceCheckoutService)
    }

    public BusinessProcessStrategy getBusinessProcessStrategy(final String code) {
        BeanPropertyValueEqualsPredicate predicate = new BeanPropertyValueEqualsPredicate("processName", code);
        return (BusinessProcessStrategy) CollectionUtils.find(this.getBusinessProcessStrategies(), predicate);
    }

    @Override
    protected String generateOrderCode(final CartModel cart) {
        return cart.getErpOrderCode();
    }

    @Required
    public void setBusinessProcessStrategies(final List<BusinessProcessStrategy> businessProcessStrategies) {
        this.businessProcessStrategies = businessProcessStrategies;
    }

    protected List<BusinessProcessStrategy> getBusinessProcessStrategies() {
        return this.businessProcessStrategies;
    }
}
