/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.service.url;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * Populating converter for content pages.
 */
public class ContentPageUrlConverter extends AbstractPopulatingConverter<ContentPageModel, ContentPageData> {
    private UrlResolver<ContentPageModel> contentPageUrlResolver;

    protected UrlResolver<ContentPageModel> getContentPageUrlResolver() {
        return contentPageUrlResolver;
    }

    @Required
    public void setContentPageUrlResolver(final UrlResolver<ContentPageModel> contentPageUrlResolver) {
        this.contentPageUrlResolver = contentPageUrlResolver;
    }

    @Override
    protected ContentPageData createTarget() {
        return new ContentPageData();
    }

    @Override
    public void populate(final ContentPageModel source, final ContentPageData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        final String url = getContentPageUrlResolver().resolve(source);
        final String name = source.getName();

        target.setUrl(url);
        target.setName(name);

        super.populate(source, target);
    }
}
