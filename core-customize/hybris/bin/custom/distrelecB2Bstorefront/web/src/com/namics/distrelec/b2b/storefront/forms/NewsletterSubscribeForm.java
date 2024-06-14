package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class NewsletterSubscribeForm {

    private String email;

    private boolean personalization;
    
    private boolean checkout;

    @NotBlank(message = "{newsletter.email.invalid}")
    @Size(min = 1, max = 175, message = "{newsletter.email.invalid}")
    @Email(message = "{newsletter.email.invalid}")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public boolean isPersonalization() {
        return personalization;
    }

    public void setPersonalization(boolean personalization) {
        this.personalization = personalization;
    }

	public boolean isCheckout() {
		return checkout;
	}

	public void setCheckout(boolean checkout) {
		this.checkout = checkout;
	}

	
    
    
}
