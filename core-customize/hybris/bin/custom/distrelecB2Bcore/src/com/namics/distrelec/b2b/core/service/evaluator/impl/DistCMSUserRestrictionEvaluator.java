/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.evaluator.impl;

import de.hybris.platform.cms2.model.restrictions.CMSUserRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.impl.CMSUserRestrictionEvaluator;

/**
 * {@code DistCMSUserRestrictionEvaluator}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.13
 */
public class DistCMSUserRestrictionEvaluator extends CMSUserRestrictionEvaluator {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.cms2.servicelayer.services.evaluator.impl.CMSUserRestrictionEvaluator#evaluate(de.hybris.platform.cms2.model.
     * restrictions.CMSUserRestrictionModel, de.hybris.platform.cms2.servicelayer.data.RestrictionData)
     */
    @Override
    public boolean evaluate(final CMSUserRestrictionModel cmsUserRestriction, final RestrictionData context) {
        return cmsUserRestriction == null || (cmsUserRestriction.isInverse() ^ super.evaluate(cmsUserRestriction, context));
    }

}
