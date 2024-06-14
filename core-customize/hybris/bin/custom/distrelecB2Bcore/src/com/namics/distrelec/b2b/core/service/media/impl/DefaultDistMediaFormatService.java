/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfSingleResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.lang.String.format;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.media.DistMediaFormatService;
import com.namics.distrelec.b2b.core.service.media.dao.DistMediaFormatDao;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

/**
 * Default implementation of {@link DistMediaFormatService}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistMediaFormatService extends AbstractBusinessService implements DistMediaFormatService {

    @Autowired
    private DistMediaFormatDao mediaFormatDao;

    @Override
    public MediaFormatModel getMediaFormatForQualifier(final String qualifier) {
        validateParameterNotNull(qualifier, "MediaFormat qualifier must not be null");
        final List<MediaFormatModel> mediaFormats = mediaFormatDao.findMediaFormatsByQualifier(qualifier);

        validateIfSingleResult(mediaFormats, format("MediaFormat with qualifier '%s' not found", qualifier),
                format("MediaFormat qualifier '%s' is not unique. %d MediaFormat found.", qualifier, Integer.valueOf(mediaFormats.size())));

        return mediaFormats.get(0);
    }

}
