/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

/**
 * AbstractDistrelecCustomerEvent
 * 
 * 
 * @author nbenothman, Distrelec
 * @since Distrelec 2.0
 * @param <T>
 *            extends BaseSiteModel
 */
public abstract class AbstractDistrelecCustomerEvent<T extends BaseSiteModel> extends AbstractCommerceUserEvent<T> {

    private String emailDisplayName;
    private String emailSubjectMsg;

    public String getEmailDisplayName() {
        return emailDisplayName;
    }

    public void setEmailDisplayName(String emailDisplayName) {
        this.emailDisplayName = emailDisplayName;
    }

    public String getEmailSubjectMsg() {
        return emailSubjectMsg;
    }

    public void setEmailSubjectMsg(String emailSubjectMsg) {
        this.emailSubjectMsg = emailSubjectMsg;
    }
}
