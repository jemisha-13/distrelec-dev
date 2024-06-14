package com.namics.distrelec.b2b.core.bomtool;

import de.hybris.platform.core.model.product.ProductModel;

public interface BomToolProductAvailabilityStrategy {

    boolean isAvailableForSale(ProductModel product);

    boolean isAvailableForSaleAfterStockIsDepleted(ProductModel product);

}
