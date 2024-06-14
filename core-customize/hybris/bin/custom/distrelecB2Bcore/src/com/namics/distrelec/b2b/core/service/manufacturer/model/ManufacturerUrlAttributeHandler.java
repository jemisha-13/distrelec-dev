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
 * Dynamic attribute handler for manufacturer URL.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ManufacturerUrlAttributeHandler extends AbstractDynamicAttributeHandler<String, DistManufacturerModel> {

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Override
    public String get(final DistManufacturerModel model) {
        String manufacturerUrl = null;

        final DistManufacturerCountryModel manufacturerCountry = distManufacturerService.getCountrySpecificManufacturerInformation(model);
        if (manufacturerCountry != null) {
            manufacturerUrl = manufacturerCountry.getManufacturerUrl();
        }
        if (manufacturerUrl == null) {
            manufacturerUrl = model.getGlobalManufacturerUrl();
        }

        return manufacturerUrl;
    }

}
