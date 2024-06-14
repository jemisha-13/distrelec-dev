/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.core.service.media.DistImage360Service;
import com.namics.distrelec.b2b.core.service.media.dao.DistImage360Dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Default Implementation of {@link DistImage360Service};
 * 
 * @author pforster, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class DefaultDistImage360Service implements DistImage360Service {

    @Autowired
    private DistImage360Dao image360Dao;

    @Override
    public DistImage360Model getImage360(CatalogVersionModel catalogVersion, String code) {
        DistImage360Model image360Model = image360Dao.findImage360ByCode(catalogVersion, code);
        if (image360Model == null) {
            throw new UnknownIdentifierException("Image 360 with code [" + code + "] not found!");
        }
        return image360Model;
    }

}
