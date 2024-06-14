/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.DeactivateUserProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

public class DeactivateUserEmailContext extends CustomerEmailContext {

    private String adminEmailAddress;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        setAdminEmailAddress(((DeactivateUserProcessModel) businessProcessModel).getGroupAdmin().getContactEmail());
    }

    public String getAdminEmailAddress() {
        return adminEmailAddress;
    }

    public void setAdminEmailAddress(final String adminEmailAddress) {
        this.adminEmailAddress = adminEmailAddress;
    }
}
