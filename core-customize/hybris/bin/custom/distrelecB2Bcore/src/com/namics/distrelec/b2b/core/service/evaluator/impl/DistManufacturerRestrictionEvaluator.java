/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.evaluator.impl;

import com.namics.distrelec.b2b.core.service.data.DistRestrictionData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.restrictions.DistManufacturerRestrictionModel;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Evaluator for manufacturer restriction.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistManufacturerRestrictionEvaluator implements CMSRestrictionEvaluator<DistManufacturerRestrictionModel> {

    private static final Logger LOG = Logger.getLogger(DistManufacturerRestrictionEvaluator.class);

    public static final String MANUFACTURER = "manufacture";

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean evaluate(final DistManufacturerRestrictionModel distManufacturerRestrictionModel, final RestrictionData context) {
        DistManufacturerModel manufacturer = null;
        if (context instanceof DistRestrictionData) {
            DistRestrictionData restrictionData = (DistRestrictionData) context;
            manufacturer = restrictionData.getManufacturer();
        }
        if (manufacturer == null) {
            manufacturer = sessionService.getAttribute(MANUFACTURER);
        }
        if (manufacturer != null && CollectionUtils.isNotEmpty(distManufacturerRestrictionModel.getManufacturers())) {
            final String code = manufacturer.getCode();
            for (final DistManufacturerModel distManufacturer : distManufacturerRestrictionModel.getManufacturers()) {
                if (distManufacturer.getCode().equals(code)) {
                    return true;
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Could not evaluate DistManufacturerRestriction. DistRestrictionData contains no manufacturer. Returning false.");
        }
        // DISTRELEC-10670 : we shouldn't remove this attribute from session.
        // sessionService.removeAttribute(MANUFACTURER);
        return false;
    }
}
