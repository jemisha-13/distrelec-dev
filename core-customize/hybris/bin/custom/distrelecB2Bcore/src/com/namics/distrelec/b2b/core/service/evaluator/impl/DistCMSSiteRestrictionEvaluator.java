package com.namics.distrelec.b2b.core.service.evaluator.impl;

import com.namics.distrelec.b2b.core.model.restrictions.DistCMSSiteRestrictionModel;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.core.Registry;

public class DistCMSSiteRestrictionEvaluator implements CMSRestrictionEvaluator<DistCMSSiteRestrictionModel> {

    @Override
    public boolean evaluate(DistCMSSiteRestrictionModel distCMSSiteRestrictionModel, RestrictionData restrictionData) {
        CMSSiteService cmsSiteService = Registry.getApplicationContext().getBean(DistrelecCMSSiteService.class);

        CMSSiteModel currentSite = cmsSiteService.getCurrentSite();

        return distCMSSiteRestrictionModel.getDisplayOnSites().contains(currentSite);
    }
}
