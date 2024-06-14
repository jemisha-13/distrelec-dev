/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Form object for send feedback.
 */
public class NewsletterUnsubscribeForm {

	private String reason;
    private String email;
    private String category;
    private String alternateEmail;
    private String otherReason;
    private String smcOutboubdId;
    
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@NotBlank(message = "{newsletter.email.invalid}")
    @Size(min = 1, max = 175, message = "{newsletter.email.invalid}")
    @Email(message = "{newsletter.email.invalid}")
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
    @Email(message = "{newsletter.email.invalid}")
	public String getAlternateEmail() {
		return alternateEmail;
	}
	public void setAlternateEmail(String alternateEmail) {
		this.alternateEmail = alternateEmail;
	}
	public String getOtherReason() {
		return otherReason;
	}
	public void setOtherReason(String otherReason) {
		this.otherReason = otherReason;
	}
	public String getSmcOutboubdId() {
		return smcOutboubdId;
	}
	public void setSmcOutboubdId(String smcOutboubdId) {
		this.smcOutboubdId = smcOutboubdId;
	}
	
	
   
}
