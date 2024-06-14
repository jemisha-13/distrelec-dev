package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.PublishEventContext;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.util.List;
import java.util.Set;

public class DistManufacturerPunchoutEvent extends AbstractEvent implements ClusterAwareEvent {

    private Set<CMSSiteModel> cmsSites;

    public DistManufacturerPunchoutEvent(Set<CMSSiteModel> cmsSites) {
        this.cmsSites = cmsSites;
    }

    @Override
    public boolean canPublish(PublishEventContext publishEventContext) {
        return true;
    }

    public Set<CMSSiteModel> getCmsSites() {
        return cmsSites;
    }
}
