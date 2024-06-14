package com.namics.distrelec.occ.core.v2.forms;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class NewsletterSubscribeForm {

    private String email;

    private boolean personalization;

    private boolean checkout;

    private String placement = new String("footer");

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

    public void setPersonalization(final boolean personalization) {
        this.personalization = personalization;
    }

    public boolean isCheckout() {
        return checkout;
    }

    public void setCheckout(final boolean checkout) {
        this.checkout = checkout;
    }

    /**
     * @return the placement
     */
    public String getPlacement() {
        return placement;
    }

    /**
     * @param placement
     *            the placement to set
     */
    public void setPlacement(final String placement) {
        this.placement = placement;
    }

}
