/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.evaluator.impl;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.model.restrictions.DistUrlRestrictionModel;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;

/**
 * {@code DistUrlRestrictionEvaluator}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.2
 */
public class DistUrlRestrictionEvaluator implements CMSRestrictionEvaluator<DistUrlRestrictionModel> {

    private static final String REQUEST_URL_ATTR_NAME = "requestURL";

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator#evaluate(de.hybris.platform.cms2.model.restrictions
     * .AbstractRestrictionModel, de.hybris.platform.cms2.servicelayer.data.RestrictionData)
     */
    @Override
    public boolean evaluate(final DistUrlRestrictionModel urlRestriction, final RestrictionData context) {

        if (urlRestriction != null && StringUtils.isNotBlank(urlRestriction.getUrl()) && context.getValue(REQUEST_URL_ATTR_NAME) != null) {
            final String requestURL = (String) context.getValue(REQUEST_URL_ATTR_NAME);
            boolean value = true;
            if (urlRestriction.isRegex()) {
                value = requestURL.matches(urlRestriction.getUrl());
            } else {
                value = StringUtils.equals(urlRestriction.getUrl(), requestURL)
                        || (requestURL.contains("/cms") && StringUtils.endsWith(requestURL, urlRestriction.getUrl()));
            }

            return urlRestriction.isInverse() ^ value;
        }

        return true;
    }
}
