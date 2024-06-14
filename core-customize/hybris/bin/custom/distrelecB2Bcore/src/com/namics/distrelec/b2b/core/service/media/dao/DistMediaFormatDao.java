/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media.dao;

import java.util.List;

import de.hybris.platform.core.model.media.MediaFormatModel;

/**
 * DAO for media related data access tasks.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface DistMediaFormatDao {

    List<MediaFormatModel> findMediaFormatsByQualifier(String qualifier);

}
