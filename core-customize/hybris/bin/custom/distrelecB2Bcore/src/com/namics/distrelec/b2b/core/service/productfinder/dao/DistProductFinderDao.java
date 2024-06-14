/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.productfinder.dao;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderConfigurationModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

/**
 * DAO for product finder configuration.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface DistProductFinderDao extends Dao {

    DistProductFinderConfigurationModel getProductFinderConfigurationByCategory(CategoryModel category);

}
