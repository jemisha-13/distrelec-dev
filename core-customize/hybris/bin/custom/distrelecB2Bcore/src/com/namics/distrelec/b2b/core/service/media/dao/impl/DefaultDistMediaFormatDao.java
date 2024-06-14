/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.Collections;
import java.util.List;

import com.namics.distrelec.b2b.core.service.media.dao.DistMediaFormatDao;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

/**
 * Default implementation of {@link DistMediaFormatDao}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistMediaFormatDao extends DefaultGenericDao<MediaFormatModel> implements DistMediaFormatDao {

    public DefaultDistMediaFormatDao(final String typecode) {
        super(typecode);
    }

    @Override
    public List<MediaFormatModel> findMediaFormatsByQualifier(final String qualifier) {
        validateParameterNotNull(qualifier, "MediaFormat qualifier must not be null");

        return find(Collections.singletonMap(MediaFormatModel.QUALIFIER, (Object) qualifier));
    }

}
