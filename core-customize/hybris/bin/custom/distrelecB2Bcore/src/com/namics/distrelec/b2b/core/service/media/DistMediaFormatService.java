/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.media;

import de.hybris.platform.core.model.media.MediaFormatModel;

/**
 * Service for media related tasks.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface DistMediaFormatService {

    MediaFormatModel getMediaFormatForQualifier(String code);

}
