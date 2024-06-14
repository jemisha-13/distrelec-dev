/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.security.filters;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.order.Constants;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@code DistLogoutFilter}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public class DistLogoutFilter extends LogoutFilter {

    private static final Logger LOG = LogManager.getLogger(DistLogoutFilter.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ModelService modelService;

    /**
     * Create a new instance of {@code DistLogoutFilter}
     * 
     * @param logoutSuccessHandler
     * @param handlers
     */
    public DistLogoutFilter(final LogoutSuccessHandler logoutSuccessHandler, final LogoutHandler... handlers) {
        super(logoutSuccessHandler, handlers);
    }

    /**
     * Create a new instance of {@code DistLogoutFilter}
     * 
     * @param logoutSuccessUrl
     * @param handlers
     */
    public DistLogoutFilter(final String logoutSuccessUrl, final LogoutHandler... handlers) {
        super(logoutSuccessUrl, handlers);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.web.authentication.logout.LogoutFilter#doFilter(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        // The following actions must be executed only during the logout
        if (requiresLogout((HttpServletRequest) req, (HttpServletResponse) res)) {
            // Step 1: before destroying the session.
            try {
                // For B2E customers, we remove the cart during the logout.
                if (getUserService().isMemberOfGroup(getUserService().getCurrentUser(),
                        getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID))) {
                    getCartService().removeSessionCart();
                }
                if (getSessionService().getAttribute(Constants.DIST_BACKUP_CART) != null) {
                    final CartModel cart = getSessionService().<CartModel> getAttribute(Constants.DIST_BACKUP_CART);
                    getModelService().remove(cart);
                }
            } catch (final Exception e) {
                LOG.error("An error occur while performing before logout actions!", e);
            }
        }

        // Step 2: call original method for logout
        super.doFilter(req, res, chain);
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
