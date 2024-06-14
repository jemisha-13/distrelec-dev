/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.model.restrictions.DistUrlRestrictionModel;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * {@code DistUrlRestrictionDescription}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.2
 */
public class DistUrlRestrictionDescriptionAttributeHandler extends AbstractDynamicAttributeHandler<String, DistUrlRestrictionModel> {

    @Override
    public String get(final DistUrlRestrictionModel model) {
        return StringUtils.EMPTY;
    }
}
