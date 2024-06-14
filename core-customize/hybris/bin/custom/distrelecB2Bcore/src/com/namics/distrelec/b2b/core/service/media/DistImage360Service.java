/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media;

import com.namics.distrelec.b2b.core.model.DistImage360Model;

import de.hybris.platform.catalog.model.CatalogVersionModel;

/**
 * This service provides functionality for {@link DistImage360Model}.
 * 
 * @author pforster, Namics AG
 * @since Distrelec 3.0.0
 */
public interface DistImage360Service {

    /**
     * Gets an 360 image by its catalog version and code.
     * 
     * @param catalogVersion
     * @param code
     * @return DistImage360Model
     */
    public DistImage360Model getImage360(final CatalogVersionModel catalogVersion, final String code);

}
