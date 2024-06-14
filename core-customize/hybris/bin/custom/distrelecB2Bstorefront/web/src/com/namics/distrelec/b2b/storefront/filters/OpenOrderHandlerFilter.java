/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.filters;

import com.namics.distrelec.b2b.core.service.order.DistOpenOrderService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Issue: DISTRELEC-3852 This filter is responsible to clean the open order information on the cart.<br>
 * The open order information must be available only in the checkout process.
 *
 * In this way the cart calculation can be executed on the base of the openOrder flag.
 *
 *
 * @author DAEBERSANIF, Namics AG
 * @since Distrelec 2.0.m2
 *
 */
public class OpenOrderHandlerFilter extends OncePerRequestFilter {

    private CartService cartService;
    private DistOpenOrderService openOrderService;
    private ConfigurationService configurationService;

    private final String OPEN_ORDER_VALIDITY_URLS_CONFIG = "distrelec.openorder.validity.rooturl.list";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        if (cartService.hasSessionCart()) {
            final CartModel cart = cartService.getSessionCart();
            if (cart != null && cart.isOpenOrder()) {
                // Get the current path from HTTP request
                final String currentPath = request.getRequestURI();
                // Check if the open order info must be removed from cart
                final boolean unlinkOpenOrder = checkOpenOrderUrl(currentPath);

                if (unlinkOpenOrder) {
                    // remove the open order info to the cart
                    openOrderService.releaseOpenOrderFromCart(cart);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /*
     * This method will verify if the current url is one in which the openorder is valid.
     */
    private boolean checkOpenOrderUrl(final String currentPath) {
        boolean clean = true;

        // get allowed urls from configuration
        final List<String> configuredUrlsList = getConfiguredUrls();

        if (CollectionUtils.isNotEmpty(configuredUrlsList)) {
            for (final String configuredUrl : configuredUrlsList) {
                if (currentPath.startsWith(configuredUrl)) {
                    clean = false;
                }
            }
        }
        return clean;
    }

    private List<String> getConfiguredUrls() {
        final String configuredUrls = configurationService.getConfiguration().getString(OPEN_ORDER_VALIDITY_URLS_CONFIG, "");
        return Arrays.asList(configuredUrls.split(","));

    }

    // spring

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public DistOpenOrderService getOpenOrderService() {
        return openOrderService;
    }

    public void setOpenOrderService(final DistOpenOrderService openOrderService) {
        this.openOrderService = openOrderService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
