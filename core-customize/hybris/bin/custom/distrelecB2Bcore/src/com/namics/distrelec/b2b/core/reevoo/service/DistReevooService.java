package com.namics.distrelec.b2b.core.reevoo.service;

import de.hybris.platform.core.model.product.ProductModel;

public interface DistReevooService {

	boolean isProductEligibleForReevoo(ProductModel product);
}
