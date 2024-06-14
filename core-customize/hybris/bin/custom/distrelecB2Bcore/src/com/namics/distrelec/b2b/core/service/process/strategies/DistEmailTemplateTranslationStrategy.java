/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */

package com.namics.distrelec.b2b.core.service.process.strategies;

import java.util.Map;

import de.hybris.platform.commons.model.renderer.RendererTemplateModel;

/**
 * Copied from hybris 5.
 * 
 */
public interface DistEmailTemplateTranslationStrategy {

    Map<String, Object> translateMessagesForTemplate(final RendererTemplateModel renderTemplate, final String languageIso, final String siteUid);
}
