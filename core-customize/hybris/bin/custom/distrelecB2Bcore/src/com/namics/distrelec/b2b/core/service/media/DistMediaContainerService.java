/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaContainerService;

/**
 * DistMediaContainerService extends MediaContainerService
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public interface DistMediaContainerService extends MediaContainerService {

    /**
     * Returns the media container for the given qualifier and catalog version
     * 
     * @param catalogVersion
     *            the catalog version of the media
     * @param qualifier
     *            the qualifier of the media container
     * @return the media container for the given qualifier and catalog version
     * @throws UnknownIdentifierException
     *             if the media container can not be found
     * @throws AmbiguousIdentifierException
     *             if more than one media container can not be found
     */
    MediaContainerModel getMediaContainerForQualifier(final CatalogVersionModel catalogVersion, final String qualifier) throws UnknownIdentifierException,
            AmbiguousIdentifierException;

}
