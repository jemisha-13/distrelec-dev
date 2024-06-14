/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.manufacturer.model;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Dynamic attribute handler for support emails.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class SupportEmailsAttributeHandler extends AbstractDynamicAttributeHandler<List<String>, DistManufacturerModel> {

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Override
    public List<String> get(final DistManufacturerModel model) {
        List<String> supportEmails = null;

        final DistManufacturerCountryModel manufacturerCountry = distManufacturerService.getCountrySpecificManufacturerInformation(model);
        if (manufacturerCountry != null) {
            supportEmails = manufacturerCountry.getSupportEmails();
        }
        if (CollectionUtils.isEmpty(supportEmails)) {
            supportEmails = model.getGlobalSupportEmails();
        }

        return supportEmails;
    }

}
