/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistNpsProcessModel;

/**
 * {@code DistNetPromoterScoreEventListener}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class DistNetPromoterScoreEventListener extends AbstractDistEventListener<DistNetPromoterScoreEvent, DistNpsProcessModel>  implements NetPromotorScoreHelperInterface {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.event.AbstractDistEventListener#populate(de.hybris.platform.servicelayer.event.events.AbstractEvent,
     * de.hybris.platform.processengine.model.BusinessProcessModel)
     */
    @Override
    public void populate(final DistNetPromoterScoreEvent event, final DistNpsProcessModel npsProcessModel) {
        NetPromotorScoreHelperInterface.super.ipopulate(event, npsProcessModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.event.AbstractDistEventListener#createTarget()
     */
    @Override
    public DistNpsProcessModel createTarget() {
        return (DistNpsProcessModel) getBusinessProcessService().createProcess("netPromoterScore_" + System.currentTimeMillis(),
                "netPromoterScoreEmailProcess");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.event.AbstractDistEventListener#createTarget(de.hybris.platform.servicelayer.event.events.
     * AbstractEvent)
     */
    @Override
    public DistNpsProcessModel createTarget(final DistNetPromoterScoreEvent event) {
        return (DistNpsProcessModel) getBusinessProcessService().createProcess("netPromoterScore_" + event.getCode(), "netPromoterScoreEmailProcess");
    }
}
