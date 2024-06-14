/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.cxml.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.eprocurement.service.cxml.CxmlException;
import com.namics.distrelec.b2b.core.eprocurement.service.cxml.DistCxmlService;
import com.namics.distrelec.b2b.core.inout.erp.OrderCalculationService;

import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Default implementation for <code>DistCxmlService</code>.
 */
public class DefaultDistCxmlService extends AbstractBusinessService implements DistCxmlService {

    private static final Logger LOG = Logger.getLogger(DefaultDistCxmlService.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private PriceService priceService;

    @Autowired
    @Qualifier("erp.orderCalculationService")
    private OrderCalculationService orderCalculationService;

    @Autowired
    @Qualifier("distEProcurementCustomerConfigService")
    private DistEProcurementCustomerConfigService customerConfigService;

    @Override
    public boolean isCxmlCustomer() {
        final UserModel currentUser = userService.getCurrentUser();
        final UserGroupModel userGroup = userService.getUserGroupForUID(DistConstants.User.CXMLCUSTOMERGROUP_UID);
        if (currentUser == null || userGroup == null) {
            return false;
        }
        return userService.isMemberOfGroup(currentUser, userGroup) && CxmlManager.isCxmlSession(JaloSession.getCurrentSession());
    }

    @Override
    public String createCxmlOrderMessage() throws CxmlException, CalculationException {
        return CxmlManager.createCxmlOrderMessage(getCartService().getSessionCart(), getOutboundSection());
    }

    @Override
    public String getCxmlRedirectUrl() {
        return "/";
    }

    @Override
    public void doCxmlLogin(final HttpServletRequest request) throws CxmlException {
        try {
            CxmlManager.cxmlLogin(request);
        } catch (final Exception e) {
            LOG.error("CXML login failed", e);
            throw new CxmlException(e);
        }
    }

    /**
     * Helper method to get the current outbound section
     * 
     * @return {@link CxmlOutboundSection}
     */
    @Override
    public CxmlOutboundSection getOutboundSection() {
        final Object sessionAttribute = JaloSession.getCurrentSession().getAttribute(CxmlManager.ATTR_OUTBOUND_SECTION_DATA);
        if (sessionAttribute != null) {
            return (CxmlOutboundSection) sessionAttribute;
        }
        return null;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

}
