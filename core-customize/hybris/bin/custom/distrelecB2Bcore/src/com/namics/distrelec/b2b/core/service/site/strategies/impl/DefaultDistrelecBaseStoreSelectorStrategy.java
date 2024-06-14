/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.site.strategies.impl;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.namics.distrelec.b2b.core.service.site.strategies.DistrelecBaseStoreSelectorStrategy;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.strategies.impl.DefaultBaseStoreSelectorStrategy;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Extends DefaultBaseStoreSelectorStrategy with a look up to the current session channel to determine the current base store.
 * 
 * @author daehusir, Distrelec
 * 
 */
public class DefaultDistrelecBaseStoreSelectorStrategy extends DefaultBaseStoreSelectorStrategy implements DistrelecBaseStoreSelectorStrategy, Serializable {
    private final static Logger LOG = LogManager.getLogger(DefaultDistrelecBaseStoreSelectorStrategy.class);
    
    
    @Resource(name = "baseSiteService")
    private BaseSiteService siteService;

    @Override
    public BaseStoreModel getCurrentBaseStore() {
        throw new UnsupportedOperationException(
                "Please use getCurrentBaseStore(final SiteChannel currentChannel) from the DistrelecBaseStoreSelectorStrategy instead.");
    }

    @Override
    public BaseStoreModel getCurrentBaseStore(final SiteChannel currentChannel) {
        BaseStoreModel result = null;
        final BaseSiteModel currentSite = getSiteService().getCurrentBaseSite();
        if (currentSite != null) {
            final List<BaseStoreModel> storeModels = currentSite.getStores();
            if (CollectionUtils.isNotEmpty(storeModels)) {
                for (final BaseStoreModel store : storeModels) {
                    if (SiteChannel.B2C.equals(store.getChannel()) && SiteChannel.B2C.equals(currentChannel)) {
                        return store;
                    } else if (SiteChannel.B2B.equals(store.getChannel()) && SiteChannel.B2B.equals(currentChannel)) {
                        return store;
                    }
                }

                result = storeModels.get(0);
            }else {
                if(LOG.isDebugEnabled()) {
                    LOG.debug("StoreModels not found in DefaultDistrelecBaseStoreSelectorStrategy.getCurrentBaseStore, BaseSite: {}",currentSite.getUid());
                }
            }
        }else {
            if(LOG.isDebugEnabled()) {
                LOG.debug("CurrentSite is null in DefaultDistrelecBaseStoreSelectorStrategy.getCurrentBaseStore, BaseSite: {}");
            }
        }
        return result;
    }

    @Override
    public BaseStoreModel getCurrentBaseStore(final SiteChannel currentChannel, final B2BCustomerModel customer) {
        BaseStoreModel result = null;
        final BaseSiteModel currentSite = customer.getCustomersBaseSite();
        if (currentSite != null) {
            final List<BaseStoreModel> storeModels = currentSite.getStores();
            if (CollectionUtils.isNotEmpty(storeModels)) {
                for (final BaseStoreModel store : storeModels) {
                    if (SiteChannel.B2C.equals(store.getChannel()) && SiteChannel.B2C.equals(currentChannel)) {
                        return store;
                    } else if (SiteChannel.B2B.equals(store.getChannel()) && SiteChannel.B2B.equals(currentChannel)) {
                        return store;
                    }
                }

                result = storeModels.get(0);
            }
        }
        return result;
    }

    public BaseSiteService getSiteService() {
        return siteService;
    }
}
