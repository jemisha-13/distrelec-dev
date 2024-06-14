/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.annotations.CxTransaction;
import com.namics.distrelec.b2b.core.constants.DistConstants.Session;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.order.DistOrderService;
import com.namics.distrelec.b2b.core.service.order.daos.DistB2BOrderDao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2b.services.impl.DefaultB2BOrderService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;

/**
 * Distrelec implementation of {@link B2BOrderService}.
 * 
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistOrderService extends DefaultB2BOrderService implements DistOrderService {

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Autowired
    private CommerceCartService commerceCartService;

    @Autowired
    private DistB2BOrderDao distB2BOrderDao;

    @Autowired
    @Qualifier("erp.shippingOptionService")
    private ShippingOptionService shippingOptionService;

    @Autowired
    private B2BWorkflowIntegrationService b2BWorkflowIntegrationService;

    @Override
    public OrderModel createOrderFromCart(final CartModel cart) throws InvalidCartException {
        final B2BCustomerModel currentB2BCustomer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();
        if (cart.getUnit() == null) {
            cart.setUnit(getB2bUnitService().getParent(currentB2BCustomer));
        }
        cart.setLocale(getI18nService().getCurrentLocale().toString());
        if (checkoutCustomerStrategy.isNotLimitedUserType(currentB2BCustomer)) {
            setFastCheckoutInfo(cart, currentB2BCustomer);
        }
        return getB2bCreateOrderFromCartStrategy().createOrderFromCart(cart);
    }

    @CxTransaction
    private void setFastCheckoutInfo(CartModel cart, B2BCustomerModel currentB2BCustomer) {
        if (nonNull(cart.getPaymentAddress()) && isNull(currentB2BCustomer.getDefaultPaymentAddress())) {
            currentB2BCustomer.setDefaultPaymentAddress(cart.getPaymentAddress());
        }

        if (nonNull(cart.getDeliveryAddress()) && isNull(currentB2BCustomer.getDefaultShipmentAddress())) {
            currentB2BCustomer.setDefaultShipmentAddress(cart.getDeliveryAddress());
        }

        if (nonNull(cart.getPaymentMode()) && isNull(currentB2BCustomer.getDefaultPaymentMethod())) {
            currentB2BCustomer.setDefaultPaymentMethod(cart.getPaymentMode().getCode());
        }

        if (nonNull(cart.getPaymentInfo()) && isNull(currentB2BCustomer.getDefaultPaymentInfo())) {
            currentB2BCustomer.setDefaultPaymentInfo(cart.getPaymentInfo());
        }

        if (nonNull(cart.getDeliveryMode()) && isNull(currentB2BCustomer.getDefaultDeliveryMethod())) {
            currentB2BCustomer.setDefaultDeliveryMethod(cart.getDeliveryMode().getCode());
            shippingOptionService.updateDefaultShippingOption((AbstractDistDeliveryModeModel) cart.getDeliveryMode());
            getSessionService().removeAttribute(Session.DELIVERY_MODES);
        }

        if (getModelService().isModified(currentB2BCustomer)) {
            getModelService().save(currentB2BCustomer);
            getModelService().refresh(currentB2BCustomer);
        }
    }

    @Override
    public OrderModel getOrderForErpCode(String erpCode) {
        return distB2BOrderDao.findOrderByErpCode(erpCode);
    }

    @Override
    public OrderModel getOrderForCodeWithSubUsers(final String orderCode, final BaseStoreModel store,
                                                  final List<UserModel> b2bUsersList) {
        return distB2BOrderDao.findOrderByCodeAndStoreAndSubUser(orderCode, store, b2bUsersList);
    }

    @Override
    public void setCustomerClientID(HttpServletRequest request, String cartCode) {
        final UserModel user = checkoutCustomerStrategy.getCurrentUserForCheckout();
        final CartModel cart = commerceCartService.getCartForCodeAndUser(cartCode, user);

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("_ga")) {
                    String[] cookieName = cookie.getValue().split("\\.");
                    cart.setClientID(cookieName[2] + "." + cookieName[3]);
                    getModelService().save(cart);
                    getModelService().refresh(cart);
                }
            }
        }

    }

    @Override
    public void updateProjectNumber(String orderCode, String workflowCode, String projectNumber) {
        final WorkflowActionModel workflowAction = b2BWorkflowIntegrationService.getActionForCode(workflowCode);
        if (workflowAction == null) {
            throw new UnknownIdentifierException("Can not find workflow action for code " + workflowCode);
        }
        OrderModel order = b2BWorkflowIntegrationService.getOrderFromAction(workflowAction);
        if (order == null) {
            throw new UnknownIdentifierException("Order with code " + orderCode + " not found");
        }
        order.setProjectNumber(projectNumber);
        getModelService().save(order);
    }

    @Override
    public List<OrderModel> findAllOrdersForGivenUserEmail(String email) {
        return distB2BOrderDao.findAllOrdersForGivenUserEmail(email);
    }
}
