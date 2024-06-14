package com.namics.distrelec.b2b.core.message.queue.response.data;

import java.util.List;

public class RelatedData {
	private List<RelatedDataEntity> products;
	
	private List<RelatedDataEntity> manufacturers;
	
	private List<RelatedDataEntity> categories;

	public List<RelatedDataEntity> getProducts() {
		return products;
	}

	public void setProducts(List<RelatedDataEntity> products) {
		this.products = products;
	}

	public List<RelatedDataEntity> getManufacturers() {
		return manufacturers;
	}

	public void setManufacturers(List<RelatedDataEntity> manufacturers) {
		this.manufacturers = manufacturers;
	}

	public List<RelatedDataEntity> getCategories() {
		return categories;
	}

	public void setCategories(List<RelatedDataEntity> categories) {
		this.categories = categories;
	}
	
	
	
}
