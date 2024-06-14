/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.site.impl;

import javax.annotation.Resource;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.service.site.strategies.DistrelecBaseStoreSelectorStrategy;
import com.namics.distrelec.b2b.core.service.site.strategies.impl.DefaultDistrelecBaseStoreSelectorStrategy;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.impl.DefaultBaseStoreService;

/**
 * Default implementation of {@link DistrelecBaseStoreService}.
 * 
 * @author daehusir, Distrelec
 */
public class DefaultDistrelecBaseStoreService extends DefaultBaseStoreService implements DistrelecBaseStoreService, Serializable {
    private final static Logger LOG = LogManager.getLogger(DefaultDistrelecBaseStoreService.class);

    @Autowired
    private SessionService sessionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService siteService;

    @Autowired
    private DistrelecBaseStoreSelectorStrategy distrelecBaseStoreSelectorStrategy;

    @Autowired
    private UserService userService;

    @Override
    public SiteChannel getCurrentChannel(final BaseSiteModel currentSite) {
        final SiteChannel currentChannel = getSessionService().getAttribute(DistConstants.Session.CHANNEL);
        if (currentChannel != null) {
            return currentChannel;
        } else if (currentSite != null) {
            return currentSite.getChannel();
        } else {
            if (userService.getCurrentUser() instanceof EmployeeModel) {
                return SiteChannel.B2C;
            }
            LOG.warn("No channel was defined in the session and no current site is available either. So the default channel B2B is returned!");
            return SiteChannel.B2B;
        }
    }

    @Override
    public BaseStoreModel getCurrentBaseStore() {
        final BaseSiteModel baseSite =getSiteService().getCurrentBaseSite();
        if(LOG.isDebugEnabled() && null==baseSite) {
            LOG.debug("No baseSite was defined in DefaultDistrelecBaseStoreService.getCurrentBaseStore");
        }
        final SiteChannel currentChannel = getCurrentChannel(baseSite);
        if(LOG.isDebugEnabled() && null==currentChannel) {
            LOG.debug("No channel was defined in DefaultDistrelecBaseStoreService.getCurrentChannel",((null!=baseSite)?baseSite.getUid():""));
        }
        return getDistrelecBaseStoreSelectorStrategy().getCurrentBaseStore(currentChannel);
    }

    @Override
    public BaseStoreModel getCustomersBaseStore(final B2BCustomerModel customer) {
        return getDistrelecBaseStoreSelectorStrategy().getCurrentBaseStore(SiteChannel.valueOf(getSiteChannelForCustomer(customer)), customer);
    }

    private String getSiteChannelForCustomer(final B2BCustomerModel customer) {
        if (customer.getCustomerType() != null && customer.getCustomerType().getCode() != null && customer.getCustomerType().getCode().equalsIgnoreCase("b2c")) {
            return "B2C";
        } else {
            return "B2B";
        }
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public BaseSiteService getSiteService() {
        return siteService;
    }

    public void setSiteService(final BaseSiteService siteService) {
        this.siteService = siteService;
    }

    public DistrelecBaseStoreSelectorStrategy getDistrelecBaseStoreSelectorStrategy() {
        return distrelecBaseStoreSelectorStrategy;
    }

    public void setDistrelecBaseStoreSelectorStrategy(final DistrelecBaseStoreSelectorStrategy distrelecBaseStoreSelectorStrategy) {
        this.distrelecBaseStoreSelectorStrategy = distrelecBaseStoreSelectorStrategy;
    }

}
