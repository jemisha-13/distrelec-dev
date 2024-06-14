/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfSingleResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.List;

import com.namics.distrelec.b2b.core.service.media.DistMediaContainerService;
import com.namics.distrelec.b2b.core.service.media.dao.DistMediaContainerDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.media.impl.DefaultMediaContainerService;

/**
 * Default implementation of DistMediaContainerService.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.1
 * 
 */
@SuppressWarnings("deprecation")
public class DefaultDistMediaContainerService extends DefaultMediaContainerService implements DistMediaContainerService {

    @Override
    public MediaContainerModel getMediaContainerForQualifier(final CatalogVersionModel catalogVersion, final String qualifier) {
        validateParameterNotNull(catalogVersion, "Argument catalogVersion cannot be null");
        validateParameterNotNull(qualifier, "Argument qualifier cannot be null");

        final List<MediaContainerModel> result = ((DistMediaContainerDao) getMediaContainerDao()).findMediaContainersByQualifier(catalogVersion, qualifier);

        validateIfSingleResult(result, "No media container with qualifier " + qualifier + " in catalog version " + catalogVersion + " can be found.",
                "More than one media container with qualifier " + qualifier + " in catalog version " + catalogVersion + " found.");
        return result.iterator().next();
    }

}
