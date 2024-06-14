/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.productfinder;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderConfigurationModel;

import de.hybris.platform.category.model.CategoryModel;

/**
 * Service for product finder configuration.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface DistProductFinderService {

    DistProductFinderConfigurationModel getProductFinderConfiguration(CategoryModel category);

}
