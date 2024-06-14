/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistQuoteEmailProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * Velocity context for a quote product price email.
 */
public class DistQuoteEmailContext extends CustomerEmailContext {

	private String company;
	private String firstName;
	private String lastName;
	private String customerEmail;
	private String phone;
	private Long quantity;
	private String comment;
	private String reference;
	private Boolean isTenderProcess;

	@Override
	public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
		super.init(businessProcessModel, emailPageModel);
		if (businessProcessModel instanceof DistQuoteEmailProcessModel) {
			final DistQuoteEmailProcessModel distQuoteProductPriceProcess = (DistQuoteEmailProcessModel) businessProcessModel;
			if (distQuoteProductPriceProcess.getCustomer() instanceof B2BCustomerModel) {
				final B2BCustomerModel b2bCustomer = (B2BCustomerModel) getCustomer(distQuoteProductPriceProcess);
				put(FROM_EMAIL, b2bCustomer.getEmail());
				put(FROM_DISPLAY_NAME, b2bCustomer.getName());
				put(EMAIL, getQuoteProductPriceEmail());
				put(DISPLAY_NAME, get(EMAIL));
				setCompany(distQuoteProductPriceProcess.getCompany());
				setFirstName(distQuoteProductPriceProcess.getFirstName());
				setLastName(distQuoteProductPriceProcess.getLastName());
				setCustomerEmail(distQuoteProductPriceProcess.getCustomerEmail());
				setPhone(distQuoteProductPriceProcess.getPhoneNumber());
				setComment(distQuoteProductPriceProcess.getComment());
				setReference(distQuoteProductPriceProcess.getReference());
				setIsTenderProcess(distQuoteProductPriceProcess.isTenderProcess());
			}
		}
	}

	private String getQuoteProductPriceEmail() {
		return getEmail(DistConstants.PropKey.Email.QUOTE_PRODUCT_PRICE_EMAIL_PREFIX,
				DistConstants.PropKey.Email.QUOTE_PRODUCT_PRICE_EMAIL_DEFAULT);
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(final Long quantity) {
		this.quantity = quantity;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(final String company) {
		this.company = company;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(final String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public Boolean getIsTenderProcess() {
		return isTenderProcess;
	}

	public void setIsTenderProcess(final Boolean isTenderProcess) {
		this.isTenderProcess = isTenderProcess;
	}

}
