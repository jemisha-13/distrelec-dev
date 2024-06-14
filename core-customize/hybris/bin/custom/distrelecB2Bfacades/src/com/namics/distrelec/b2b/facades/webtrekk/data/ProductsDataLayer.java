/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.data;

import java.util.List;

public class ProductsDataLayer {

	private String productID;
	private String productName;
	private String productCategoryPath;
	private String productcategoryLevel1;
	private String productManufacturer;
	private List<ProductCostPerUnit> productCostPerUnit;

	public String getProductID() {
		return productID;
	}

	public void setProductID(final String productID) {
		this.productID = productID;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(final String productName) {
		this.productName = productName;
	}

	public String getProductCategoryPath() {
		return productCategoryPath;
	}

	public void setProductCategoryPath(final String productCategoryPath) {
		this.productCategoryPath = productCategoryPath;
	}

	public String getProductcategoryLevel1() {
		return productcategoryLevel1;
	}

	public void setProductcategoryLevel1(final String productcategoryLevel1) {
		this.productcategoryLevel1 = productcategoryLevel1;
	}

	public String getProductManufacturer() {
		return productManufacturer;
	}

	public void setProductManufacturer(final String productManufacturer) {
		this.productManufacturer = productManufacturer;
	}

	public List<ProductCostPerUnit> getProductCostPerUnit() {
		return productCostPerUnit;
	}

	public void setProductCostPerUnit(final List<ProductCostPerUnit> productCostPerUnit) {
		this.productCostPerUnit = productCostPerUnit;
	}
}
