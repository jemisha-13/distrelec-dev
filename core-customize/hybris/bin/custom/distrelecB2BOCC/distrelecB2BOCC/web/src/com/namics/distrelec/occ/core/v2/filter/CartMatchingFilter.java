/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Filter that puts cart from the requested url into the session.
 */
public class CartMatchingFilter extends AbstractUrlMatchingFilter {
    public static final String REFRESH_CART_PARAM = "refreshCart";

    public static final String FF_TRACK_CART_PARAMETER = "cart";

    private String regexp;

    private CartLoaderStrategy cartLoaderStrategy;

    private SessionService sessionService;

    private UserService userService;

    private CMSSiteService cmsSiteService;

    private CartService cartService;

    private boolean cartRefreshedByDefault = false;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
      throws ServletException, IOException {
        String cartId = getCartId(request);
        if (isNotBlank(cartId)) {
            cartLoaderStrategy.loadCart(cartId, shouldCartBeRefreshed(request));
            setSessionVariables();
        }

        filterChain.doFilter(request, response);
    }

    private String getCartId(HttpServletRequest request) {
        if (isCartTrackRequest(request)) {
            return request.getParameter(FF_TRACK_CART_PARAMETER);
        }
        return getValue(request, regexp);
    }

    private boolean isCartTrackRequest(HttpServletRequest request) {
        return getPath(request).contains("/fftrack") && isNotBlank(request.getParameter(FF_TRACK_CART_PARAMETER));
    }

    private void setSessionVariables() {
        CustomerType customerType = getCartCustomerType();

        if (CustomerType.GUEST.equals(customerType) && BooleanUtils.isTrue(getCmsSiteService().getCurrentSite().getGuestCheckoutEnabled())) {
            getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
        }
        getSessionService().setAttribute(DistConstants.Headless.HEADLESS_CART, Boolean.TRUE);
    }

    private CustomerType getCartCustomerType() {
        if (cartService.hasSessionCart()) {
            CartModel cartModel = cartService.getSessionCart();
            if (cartModel != null && cartModel.getUser() instanceof B2BCustomerModel) {
                B2BCustomerModel customer = (B2BCustomerModel) cartModel.getUser();
                return customer.getCustomerType();
            }
        }
        return null;
    }

    protected boolean shouldCartBeRefreshed(final HttpServletRequest request) {
        final String refreshParam = request.getParameter(REFRESH_CART_PARAM);
        return refreshParam == null ? isCartRefreshedByDefault() : Boolean.parseBoolean(refreshParam);
    }

    protected String getRegexp() {
        return regexp;
    }

    @Required
    public void setRegexp(final String regexp) {
        this.regexp = regexp;
    }

    public CartLoaderStrategy getCartLoaderStrategy() {
        return cartLoaderStrategy;
    }

    @Required
    public void setCartLoaderStrategy(final CartLoaderStrategy cartLoaderStrategy) {
        this.cartLoaderStrategy = cartLoaderStrategy;
    }

    public boolean isCartRefreshedByDefault() {
        return cartRefreshedByDefault;
    }

    public void setCartRefreshedByDefault(final boolean cartRefreshedByDefault) {
        this.cartRefreshedByDefault = cartRefreshedByDefault;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
