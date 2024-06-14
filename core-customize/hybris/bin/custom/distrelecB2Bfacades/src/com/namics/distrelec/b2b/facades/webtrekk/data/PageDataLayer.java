/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.data;

public class PageDataLayer {

	private String onsiteSearchResults;
	private String searchPhrase;
	private String error;
	private String contentGroup;
	private String area;
	private String pageID;
	private String customerID;

	public String getOnsiteSearchResults() {
		return onsiteSearchResults;
	}

	public void setOnsiteSearchResults(final String onsiteSearchResults) {
		this.onsiteSearchResults = onsiteSearchResults;
	}

	public String getSearchPhrase() {
		return searchPhrase;
	}

	public void setSearchPhrase(final String searchPhrase) {
		this.searchPhrase = searchPhrase;
	}

	public String getError() {
		return error;
	}

	public void setError(final String error) {
		this.error = error;
	}

	public String getContentGroup() {
		return contentGroup;
	}

	public void setContentGroup(final String contentGroup) {
		this.contentGroup = contentGroup;
	}

	public String getArea() {
		return area;
	}

	public void setArea(final String area) {
		this.area = area;
	}

	public String getPageID() {
		return pageID;
	}

	public void setPageID(final String pageID) {
		this.pageID = pageID;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(final String customerID) {
		this.customerID = customerID;
	}

}
