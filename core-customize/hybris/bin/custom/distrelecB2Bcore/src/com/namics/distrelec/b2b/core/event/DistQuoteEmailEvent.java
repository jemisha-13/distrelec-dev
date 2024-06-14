/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.util.List;

/**
 * Event object for quote product price functionality.
 */
public class DistQuoteEmailEvent  extends AbstractCommerceUserEvent<BaseSiteModel> {
	
	private String title;
    private String company;
    private String firstName;
    private String lastName;
    private String customerEmail;
    private String phone;
    private List<DistQuoteEmailProduct> productRow;
    private String comment;
    private String reference;
    private Boolean isTenderProcess;
    private Boolean isFromCart;

	public DistQuoteEmailEvent(final DistQuoteEmailEventBuilder builder) {
		super();
		this.title = builder.title;
		this.company = builder.company;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.customerEmail = builder.customerEmail;
		this.phone = builder.phone;
		this.productRow = builder.productRow;
		this.comment = builder.comment;
		this.reference = builder.reference;
		this.isTenderProcess = builder.isTenderProcess;
		this.isFromCart = builder.isFromCart;
	}

	public String getTitle() {
		return title;
	}

	public String getCompany() {
		return company;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public String getPhone() {
		return phone;
	}

	public List<DistQuoteEmailProduct> getProductRow() {
		return productRow;
	}

	public String getComment() {
		return comment;
	}

	public String getReference() {
		return reference;
	}

	public Boolean getIsTenderProcess() {
		return isTenderProcess;
	}

	public Boolean getIsFromCart(){ return isFromCart; }


	public static class DistQuoteEmailEventBuilder {
		private String title;
		private String company;
		private String firstName;
		private String lastName;
		private String customerEmail;
		private String phone;
		private List<DistQuoteEmailProduct> productRow;
		private String comment;
		private String reference;
		private Boolean isTenderProcess;
		private Boolean isFromCart;

		public DistQuoteEmailEventBuilder(String title, String firstName, String lastName, String customerEmail, String company, String phone){
			this.title = title;
			this.firstName = firstName;
			this.lastName = lastName;
			this.customerEmail = customerEmail;
			this.company = company;
			this.phone = phone;
		}

		public DistQuoteEmailEventBuilder setQuotation(List<DistQuoteEmailProduct> productRow, String comment, String reference, Boolean isTenderProcess){
			this.productRow = productRow;
			this.comment = comment;
			this.reference = reference;
			this.isTenderProcess = isTenderProcess;
			return this;
		}

		public DistQuoteEmailEventBuilder setFromCart(Boolean isFromCart){
			this.isFromCart = isFromCart;
			return this;
		}

		public DistQuoteEmailEvent build(){
			return new DistQuoteEmailEvent(this);
		}

	}
}
