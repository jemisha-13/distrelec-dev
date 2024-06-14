package com.namics.distrelec.b2b.core.timestampupdate.service;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

public interface DistTimeStampService {
	
	boolean updateProductFirstAppearanceDate();
    
    ProductModel getProductInfoPimId(final String pimId);
    
    List<List<String>> searchProductsForExport();

}
