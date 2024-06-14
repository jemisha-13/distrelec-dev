/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.manufacturer.model;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.restrictions.DistManufacturerRestrictionModel;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import de.hybris.platform.util.localization.Localization;

/**
 * Dynamic attribute handler for manufacturer restriction description.
 * 
 * @author pbuschi, Namics AG
 * 
 */
public class DistManufacturerRestrictionDescription extends AbstractDynamicAttributeHandler<String, DistManufacturerRestrictionModel> {

    @Override
    public String get(final DistManufacturerRestrictionModel model) {
        final Collection<DistManufacturerModel> manufacturers = model.getManufacturers();
        final StringBuilder result = new StringBuilder();
        if (CollectionUtils.isNotEmpty(manufacturers)) {
            final String localizedString = Localization.getLocalizedString("type.DistManufacturerRestriction.description.text");
            result.append((localizedString == null) ? "Page only applies on manufacturers:" : localizedString);
            for (final DistManufacturerModel manufacturer : manufacturers) {
                result.append(" ").append(manufacturer.getName()).append(" (").append(manufacturer.getCode()).append(");");
            }
        }
        return result.toString();
    }

}
