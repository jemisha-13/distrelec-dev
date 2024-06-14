/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * Event to asynchronously update the newsletter status of a user. Event will be published to current cluster node only.
 * 
 * @author ascherrer, Namics AG
 * @since 2.0.26
 * 
 */
public class UpdateNewsletterStatusEvent extends AbstractEvent implements ClusterAwareEvent {
    private BaseSiteModel baseSite;
    private String languageIsocode;
    private String userUid;

    @Override
    public boolean publish(final int sourceNodeId, final int targetNodeId) {
        return sourceNodeId == targetNodeId;
    }

    // START GENERATED CODE

    public BaseSiteModel getBaseSite() {
        return baseSite;
    }

    public void setBaseSite(BaseSiteModel baseSite) {
        this.baseSite = baseSite;
    }

    public String getLanguageIsocode() { 
        return languageIsocode;
    }

    public void setLanguageIsocode(String languageIsocode) {
        this.languageIsocode = languageIsocode;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    // END GENERATED CODE
}
