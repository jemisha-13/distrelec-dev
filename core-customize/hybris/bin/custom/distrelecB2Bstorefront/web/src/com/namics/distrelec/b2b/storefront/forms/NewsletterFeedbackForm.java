package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class NewsletterFeedbackForm {

    private String email;
    
    private String alternateEmail;
    
    private String unsubscribeReason;
    
    private String category;
    
    private String language;

    @NotBlank(message = "{newsletter.email.invalid}")
    @Size(min = 1, max = 175, message = "{newsletter.email.invalid}")
    @Email(message = "{newsletter.email.invalid}")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
    
    @NotBlank(message = "{newsletter.email.invalid}")
    @Size(min = 1, max = 175, message = "{newsletter.email.invalid}")
    @Email(message = "{newsletter.email.invalid}")
	public String getAlternateEmail() {
		return alternateEmail;
	}

	public void setAlternateEmail(String alternateEmail) {
		this.alternateEmail = alternateEmail;
	}

	public String getUnsubscribeReason() {
		return unsubscribeReason;
	}

	public void setUnsubscribeReason(String unsubscribeReason) {
		this.unsubscribeReason = unsubscribeReason;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
    
	
    

}
