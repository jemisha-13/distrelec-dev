/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.manufacturer.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Dynamic attribute handler for support repair info.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class SupportRepairInfoAttributeHandler extends AbstractDynamicAttributeHandler<String, DistManufacturerModel> {

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Override
    public String get(final DistManufacturerModel model) {
        String supportRepairInfo = null;

        final DistManufacturerCountryModel manufacturerCountry = distManufacturerService.getCountrySpecificManufacturerInformation(model);
        if (manufacturerCountry != null) {
            supportRepairInfo = manufacturerCountry.getSupportRepairInfo();
        }
        if (supportRepairInfo == null) {
            supportRepairInfo = model.getGlobalSupportRepairInfo();
        }

        return supportRepairInfo;
    }

}
