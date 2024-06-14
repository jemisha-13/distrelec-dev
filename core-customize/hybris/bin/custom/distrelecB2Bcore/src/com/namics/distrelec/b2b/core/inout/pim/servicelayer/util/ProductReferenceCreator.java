/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.util;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimProductReferenceDto;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface ProductReferenceCreator {

    ProductReferenceModel create(final PimProductReferenceDto productReferenceDto, final ProductModel sourceProduct, final ProductModel targetProduct);

}
