package com.namics.distrelec.b2b.core.service.evaluator.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.restrictions.DistChannelRestrictionModel;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Evaluator for Channel restriction.
 * 
 * @author Akshay
 * 
 */
public class DistChannelRestrictionEvaluator implements CMSRestrictionEvaluator<DistChannelRestrictionModel> {

    private static final Logger LOG = Logger.getLogger(DistChannelRestrictionEvaluator.class);

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Override
    public boolean evaluate(final DistChannelRestrictionModel distChannelRestrictionModel, final RestrictionData context) {
        final SiteChannel currentChannel = ((DistrelecBaseStoreService) baseStoreService).getCurrentChannel(baseSiteService.getCurrentBaseSite());
        if (distChannelRestrictionModel.getChannel() != null && currentChannel != null) {
            if (currentChannel.getCode().equals(distChannelRestrictionModel.getChannel().getCode())) {
                return true;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Could not evaluate DistChannelRestriction. Returning false.");
        }
        return false;
    }
}
