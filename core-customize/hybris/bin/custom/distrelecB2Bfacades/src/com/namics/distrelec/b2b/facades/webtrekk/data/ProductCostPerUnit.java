/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.data;

public class ProductCostPerUnit {
	private String cost;
	private String costThreshold;

	public String getCost() {
		return cost;
	}

	public void setCost(final String cost) {
		this.cost = cost;
	}

	public String getCostThreshold() {
		return costThreshold;
	}

	public void setCostThreshold(final String costThreshold) {
		this.costThreshold = costThreshold;
	}
}
