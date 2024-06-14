/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product;

import com.namics.distrelec.b2b.core.model.ProductCountryModel;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface ProductCountryService {

    ProductCountryModel getProductCountry(CountryModel country, ProductModel product);

    ProductCountryModel getCurrentProductCountry(ProductModel product);

}
