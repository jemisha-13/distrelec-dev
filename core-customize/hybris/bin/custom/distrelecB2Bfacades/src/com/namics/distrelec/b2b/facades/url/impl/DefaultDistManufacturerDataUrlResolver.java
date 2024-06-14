/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.url.impl;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import org.springframework.beans.factory.annotation.Required;

import java.util.Locale;

/**
 * URL resolver for DistManufacturerData instances. <br>
 * The pattern could be of the form: /{language}/manufacturer/{manufacturer-urlId}
 */
public class DefaultDistManufacturerDataUrlResolver extends AbstractUrlResolver<DistManufacturerData> implements DistUrlResolver<DistManufacturerData> {

    private String pattern;
    private DistManufacturerService distManufacturerService;
    private DistUrlResolver<DistManufacturerModel> distManufacturerModelUrlResolver;

    @Override
    protected String resolveInternal(final DistManufacturerData source) {
        return getDistManufacturerModelUrlResolver().resolve(distManufacturerService.getManufacturerByCode(source.getCode()));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.lang.String)
     */
    @Override
    public String resolve(final DistManufacturerData source, final BaseSiteModel baseSite, final String language) {
        return getDistManufacturerModelUrlResolver().resolve(distManufacturerService.getManufacturerByCode(source.getCode()), baseSite, language);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.util.Locale)
     */
    @Override
    public String resolve(final DistManufacturerData source, final BaseSiteModel baseSite, final Locale locale) {
        return resolve(source, baseSite, locale.getLanguage());
    }

    public String getPattern() {
        return this.pattern;
    }

    @Required
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

    public DistManufacturerService getDistManufacturerService() {
        return distManufacturerService;
    }

    @Required
    public void setDistManufacturerService(final DistManufacturerService distManufacturerService) {
        this.distManufacturerService = distManufacturerService;
    }

    public DistUrlResolver<DistManufacturerModel> getDistManufacturerModelUrlResolver() {
        return distManufacturerModelUrlResolver;
    }

    @Required
    public void setDistManufacturerModelUrlResolver(final DistUrlResolver<DistManufacturerModel> distManufacturerModelUrlResolver) {
        this.distManufacturerModelUrlResolver = distManufacturerModelUrlResolver;
    }

    @Override
    public String resolve(final DistManufacturerData source, final BaseSiteModel baseSite, final String locale, final boolean isCanonical) {
        // YTODO Auto-generated method stub
        return null;
    }
}
