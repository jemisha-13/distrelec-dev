/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.data;

public class ProductData {

	private String productID;
	private String productName;
	private String productcategoryLevel1;
	private String productCategoryPath;
	private String productManufacturer;
	private String productTotalCost;
	private String productQuantity;

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

	public String getProductcategoryLevel1() {
		return productcategoryLevel1;
	}

	public void setProductcategoryLevel1(final String productcategoryLevel1) {
		this.productcategoryLevel1 = productcategoryLevel1;
	}

	public String getProductCategoryPath() {
		return productCategoryPath;
	}

	public void setProductCategoryPath(final String productCategoryPath) {
		this.productCategoryPath = productCategoryPath;
	}

	public String getProductManufacturer() {
		return productManufacturer;
	}

	public void setProductManufacturer(final String productManufacturer) {
		this.productManufacturer = productManufacturer;
	}

	public String getProductTotalCost() {
		return productTotalCost;
	}

	public void setProductTotalCost(final String productTotalCost) {
		this.productTotalCost = productTotalCost;
	}

	public String getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(final String productQuantity) {
		this.productQuantity = productQuantity;
	}
}
