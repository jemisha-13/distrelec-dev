/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.data;

public class ShopDataLayer {

	private String shop;
	private String channel;
	private String country;
	private String countryName;
	private String language;
	private String languageName;
	private String currency;

	public String getShop() {
		return shop;
	}

	public void setShop(final String shop) {
		this.shop = shop;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(final String channel) {
		this.channel = channel;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(final String languageName) {
		this.languageName = languageName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(final String currency) {
		this.currency = currency;
	}

}
