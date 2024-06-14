/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.productfinder.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderConfigurationModel;
import com.namics.distrelec.b2b.core.service.productfinder.DistProductFinderService;
import com.namics.distrelec.b2b.core.service.productfinder.dao.DistProductFinderDao;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

/**
 * Default implementation of product finder service.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistProductFinderService extends AbstractBusinessService implements DistProductFinderService {

    @Autowired
    private DistProductFinderDao distProductFinderDao;

    @Override
    public DistProductFinderConfigurationModel getProductFinderConfiguration(final CategoryModel category) {
        return distProductFinderDao.getProductFinderConfigurationByCategory(category);
    }

}
