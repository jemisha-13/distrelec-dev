/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.cms.impl;

import javax.servlet.http.HttpServletRequest;

import com.namics.distrelec.b2b.core.service.cms.DistCMSContentSlotService;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService;

/**
 * {@code DefaultDistCMSContentSlotService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.2
 */
public class DefaultDistCMSContentSlotService extends DefaultCMSContentSlotService implements DistCMSContentSlotService {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected RestrictionData populate(final HttpServletRequest request) {
        final RestrictionData restrictionData = super.populate(request);
        final Object request_uri = request.getAttribute("javax.servlet.forward.request_uri");
        restrictionData.setValue("requestURL", request_uri != null ? request_uri.toString() : request.getRequestURI());
        return restrictionData;
    }
}
