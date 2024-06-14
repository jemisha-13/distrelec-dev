/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.evaluator.impl;

import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.impl.CMSUserGroupRestrictionEvaluator;

/**
 * {@code DistCMSUserGroupRestrictionEvaluator}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.13
 */
public class DistCMSUserGroupRestrictionEvaluator extends CMSUserGroupRestrictionEvaluator {

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.cms2.servicelayer.services.evaluator.impl.CMSUserGroupRestrictionEvaluator#evaluate(de.hybris.platform.cms2.model.
     * restrictions.CMSUserGroupRestrictionModel, de.hybris.platform.cms2.servicelayer.data.RestrictionData)
     */
    @Override
    public boolean evaluate(final CMSUserGroupRestrictionModel cmsUserGroupRestriction, final RestrictionData context) {
        return cmsUserGroupRestriction == null || (cmsUserGroupRestriction.isInverse() ^ super.evaluate(cmsUserGroupRestriction, context));
    }
}
