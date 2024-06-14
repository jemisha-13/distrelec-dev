/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media.dao;

import com.namics.distrelec.b2b.core.model.DistImage360Model;

import de.hybris.platform.catalog.model.CatalogVersionModel;

/**
 * DAO for image 360 related data access tasks.
 * 
 * @author pforster, Namics AG
 * @since Distrelec 3.0.0
 */
public interface DistImage360Dao {

    /**
     * Finds a image 360 by its catalog version and code.
     * 
     * @param image360Code
     * @return DistImage360
     */
    public DistImage360Model findImage360ByCode(final CatalogVersionModel catalogVersion, final String image360Code);
}
