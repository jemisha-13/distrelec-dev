package com.namics.distrelec.b2b.core.service.evaluator.impl;

import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import org.apache.log4j.Logger;

/**
 * Extends existed CMSCategoryRestrictionEvaluator because it does not log a CMSCategoryRestriction uid, so we can't fix the
 * restriction which is assigned to a context without assigned category or product.
 */
public class DistCMSCategoryRestrictionLoggerEvaluator implements CMSRestrictionEvaluator<CMSCategoryRestrictionModel> {
    private static final Logger LOG = Logger.getLogger(DistCMSCategoryRestrictionLoggerEvaluator.class);

    private CMSRestrictionEvaluator<CMSCategoryRestrictionModel> cmsCategoryRestrictionEvaluator;

    @Override
    public boolean evaluate(CMSCategoryRestrictionModel cmsCategoryRestrictionModel, RestrictionData context) {
        if (context != null && !context.hasCategory() && !context.hasProduct()) {
            LOG.warn(String.format("Could not evaluate DistCMSCategoryRestrictionLoggerEvaluator (CMSCategoryRestriction-uid: %s). RestrictionData contains neither a category or a product. Returning false.",
                    cmsCategoryRestrictionModel.getUid()));
        }
        return getCmsCategoryRestrictionEvaluator().evaluate(cmsCategoryRestrictionModel, context);
    }

    public void setCmsCategoryRestrictionEvaluator(CMSRestrictionEvaluator<CMSCategoryRestrictionModel> cmsCategoryRestrictionEvaluator) {
        this.cmsCategoryRestrictionEvaluator = cmsCategoryRestrictionEvaluator;
    }

    protected CMSRestrictionEvaluator<CMSCategoryRestrictionModel> getCmsCategoryRestrictionEvaluator() {
        return cmsCategoryRestrictionEvaluator;
    }
}
