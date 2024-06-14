package com.namics.distrelec.b2b.core.timestampupdate.dao;

import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;

public interface DistTimeStampDao {
	
	boolean updateProductFirstAppearanceDate();
    
    ProductModel getProductInfoPimId(String pimId);
    
    List<List<String>> searchProductsForExport();

}
