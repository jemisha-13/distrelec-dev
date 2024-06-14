/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.site;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Extending the hybris BaseStoreService.
 * 
 * @author daehusir, Distrelec
 * 
 */
public interface DistrelecBaseStoreService extends BaseStoreService {

    /**
     * Returns the current session channel. If not set the default channel of the current site is returned. If the current site is not
     * available either, B2B is returned.
     * 
     * @param currentSite
     *            the current site
     * @return the current session channel.
     */
    SiteChannel getCurrentChannel(final BaseSiteModel currentSite);

    /**
     * Returns the base store for the given customer, based on the customers base site and customer type.
     * 
     * @param customer
     *            the customer
     * @return the base store for the given customer
     */
    BaseStoreModel getCustomersBaseStore(final B2BCustomerModel customer);

}
