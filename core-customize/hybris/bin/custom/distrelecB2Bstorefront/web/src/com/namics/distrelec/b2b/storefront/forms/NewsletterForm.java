/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

public interface NewsletterForm {
    public String getEmail();

    public String getFirstName();

    public String getLastName();

    public String getTitleCode();

    public boolean isMarketingConsent();
    
    public boolean isNpsConsent();
}
