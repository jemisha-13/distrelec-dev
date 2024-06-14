package com.namics.distrelec.b2b.core.service.product.data;

import java.util.List;
import java.util.Map;

import de.hybris.platform.core.model.product.ProductModel;

public class PIMAlternateResult {
	
	List<ProductModel> alternativeProducts;
	
	Map<String,String> codeCategoryMapping;

	public List<ProductModel> getAlternativeProducts() {
		return alternativeProducts;
	}

	public void setAlternativeProducts(List<ProductModel> alternativeProducts) {
		this.alternativeProducts = alternativeProducts;
	}

	public Map<String, String> getCodeCategoryMapping() {
		return codeCategoryMapping;
	}

	public void setCodeCategoryMapping(Map<String, String> codeCategoryMapping) {
		this.codeCategoryMapping = codeCategoryMapping;
	}
	

}
