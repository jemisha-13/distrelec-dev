/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.filters;

import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AnonymousCheckoutFilter extends OncePerRequestFilter {

    @Autowired
    private CartService cartService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        if (!request.isSecure() && checkoutCustomerStrategy.isAnonymousCheckout() && getCartService().hasSessionCart()) {
            final CartModel cartModel = getCartService().getSessionCart();
            cartModel.setDeliveryAddress(null);
            cartModel.setDeliveryMode(null);
            cartModel.setPaymentInfo(null);
            getCartService().saveOrder(cartModel);
            getSessionService().removeAttribute(WebConstants.ANONYMOUS_CHECKOUT);
        }

        filterChain.doFilter(request, response);
    }

    protected CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public CheckoutCustomerStrategy getCheckoutCustomerStrategy() {
        return checkoutCustomerStrategy;
    }

    public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy) {
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
    }

}
