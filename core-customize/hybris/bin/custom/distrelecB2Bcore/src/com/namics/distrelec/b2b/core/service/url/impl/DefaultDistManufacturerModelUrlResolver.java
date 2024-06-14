/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url.impl;

import java.util.Locale;

import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;

/**
 * URL resolver for DistManufacturerData instances. <br>
 * The pattern could be of the form: /manufacturer/{manufacturer-code}
 */
public class DefaultDistManufacturerModelUrlResolver extends AbstractUrlResolver<DistManufacturerModel> implements DistUrlResolver<DistManufacturerModel> {

    private String pattern;

    private BaseSiteService baseSiteService;
    private CommonI18NService commonI18NService;

    @Autowired
    private I18NService i18NService;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver#resolveInternal(java.lang.Object)
     */
    @Override
    protected String resolveInternal(final DistManufacturerModel source) {
        return resolve(source, getBaseSiteService().getCurrentBaseSite(), i18NService.getCurrentLocale().getLanguage());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.lang.String)
     */
    @Override
    public String resolve(final DistManufacturerModel source, final BaseSiteModel baseSite, final String language) {
        String url = StringUtils.replace(getPattern(), "{manufacturer-name}", StringUtils.isNotBlank(source.getNameSeo()) ? source.getNameSeo() : "-");
        url = StringUtils.replace(url, "{language}", language);
        url = StringUtils.replace(url, "{manufacturer-code}", source.getCode());
        return UrlResolverUtils.normalize(url);
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.lang.String, boolean)
     */
    @Override
    public String resolve(final DistManufacturerModel source, final BaseSiteModel baseSite, final String language, final boolean isCanonical) {
        String url = StringUtils.replace(getPattern(), "{manufacturer-name}", StringUtils.isNotBlank(source.getNameSeo()) ? source.getNameSeo() : "-");
        if(isCanonical) {
            url = StringUtils.replace(url, "{language}", "");
        }else {
            url = StringUtils.replace(url, "{language}", language);
        }
        url = StringUtils.replace(url, "{manufacturer-code}", source.getCode());
        return UrlResolverUtils.normalize(url);
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.util.Locale)
     */
    @Override
    public String resolve(final DistManufacturerModel source, final BaseSiteModel baseSite, final Locale locale) {
        return resolve(source, baseSite, locale.getLanguage());
    }

    public String getPattern() {
        return this.pattern;
    }

    @Required
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
