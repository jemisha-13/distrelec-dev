package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.PublishEventContext;

public class DistInternalLinkEventAsync extends DistInternalLinkEvent implements ClusterAwareEvent {

    public DistInternalLinkEventAsync(String code, RowType type, String site, String language, boolean force) {
        super(code, type, site, language, force);
    }

    @Override
    public boolean canPublish(PublishEventContext publishEventContext) {
        return publishEventContext.getTargetNodeGroups().contains("yHotfolderCandidate");
    }
}
