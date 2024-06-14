/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media.dao;

import java.util.List;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.media.dao.MediaContainerDao;

/**
 * DistMediaContainerDao extends MediaContainerDao.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.1
 * 
 */
public interface DistMediaContainerDao extends MediaContainerDao {

    /**
     * Find media containers by qualifier and catalog version.
     * 
     * @param catalogVersion
     * @param qualifier
     *            the qualifier
     * @return the list of media container models
     */
    List<MediaContainerModel> findMediaContainersByQualifier(CatalogVersionModel catalogVersion, String qualifier);
}
