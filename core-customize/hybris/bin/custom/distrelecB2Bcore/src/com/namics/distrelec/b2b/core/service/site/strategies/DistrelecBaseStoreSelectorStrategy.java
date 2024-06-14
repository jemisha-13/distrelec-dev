/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.site.strategies;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.strategies.BaseStoreSelectorStrategy;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * DistrelecBaseStoreSelectorStrategy extends {@link BaseStoreSelectorStrategy}.
 * 
 * @author daehusir, Distrelec
 * 
 */
public interface DistrelecBaseStoreSelectorStrategy extends BaseStoreSelectorStrategy {

    BaseStoreModel getCurrentBaseStore(final SiteChannel currentChannel);

    BaseStoreModel getCurrentBaseStore(final SiteChannel currentChannel, final B2BCustomerModel customer);
}
