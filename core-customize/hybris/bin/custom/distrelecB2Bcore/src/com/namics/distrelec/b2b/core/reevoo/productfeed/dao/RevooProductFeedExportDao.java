package com.namics.distrelec.b2b.core.reevoo.productfeed.dao;

import java.util.List;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface RevooProductFeedExportDao {
	
	List<ProductModel> getEligilbleProducts(CMSSiteModel cmsSite);
	
	Boolean isProductEligible(CMSSiteModel cmsSite,ProductModel product);

}
