/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.strategies.impl;

import com.namics.distrelec.occ.core.strategies.BaseStoreForSiteSelectorStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Default implementation of {@link BaseStoreForSiteSelectorStrategy} which returns first base store from collection.
 */
public class DefaultBaseStoreForSiteSelectorStrategy implements BaseStoreForSiteSelectorStrategy {

    @Override
    public BaseStoreModel getBaseStore(final BaseSiteModel baseSiteModel) {
        return baseSiteModel.getStores().get(0);
    }
}
