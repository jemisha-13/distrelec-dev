/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url.impl;

import java.util.Locale;

import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;

/**
 * URL resolver for ContentPageModel instances. The pattern could be of the form: /{language}/{page-title}/cms/{page-label}
 */
public class DefaultContentPageUrlResolver extends AbstractUrlResolver<ContentPageModel> implements DistUrlResolver<ContentPageModel> {

    private final String CACHE_KEY = DefaultContentPageUrlResolver.class.getName();

    private String defaultPattern;
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
    protected String resolveInternal(final ContentPageModel source) {
        final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
        final String language = i18NService.getCurrentLocale().getLanguage();
        return resolve(source, currentBaseSite, language);
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.lang.String)
     */
    @Override
    public String resolve(final ContentPageModel source, final BaseSiteModel baseSite, final String language) {
        final String label = source.getLabel().replace("/", StringUtils.EMPTY);
        final String name = source.getName();
        final String title = source.getTitle();

        String url = getPattern(baseSite);
        url = url.replace("{language}", language);
        url = url.replace("{page-name}", name);
        url = url.replace("{page-title}", StringUtils.isNotEmpty(title) ? title : "");
        url = url.substring(0, url.indexOf("{page-label}"));

        return UrlResolverUtils.normalize(url) + label;
    }
    @Override
    public String resolve(final ContentPageModel source, final BaseSiteModel baseSite, final String language, final boolean isCanonical) {
        final String label = source.getLabel().replace("/", StringUtils.EMPTY);
        final String name = source.getName();
        final String title = source.getTitle();

        String url = getPattern(baseSite);
        if(isCanonical && null!=url && !url.isEmpty() ){
            url = url.replace("/{language}", "");
        }else{
         url = url.replace("{language}", language);
        }

        url = url.replace("{page-name}", name);
        url = url.replace("{page-title}", StringUtils.isNotEmpty(title) ? title : "");
        url = url.substring(0, url.indexOf("{page-label}"));

        return UrlResolverUtils.normalize(url) + label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.util.Locale)
     */
    @Override
    public String resolve(final ContentPageModel source, final BaseSiteModel baseSite, final Locale locale) {
        return resolve(source, baseSite, locale.getLanguage());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver#getKey(java.lang.Object)
     */
    @Override
    protected String getKey(final ContentPageModel source) {
        return CACHE_KEY + "." + source.getPk().toString();
    }

    protected String getPattern(final BaseSiteModel baseSite) {
        if (baseSite != null && baseSite.getContentUrlPattern() != null) {
            return baseSite.getContentUrlPattern();
        }

        return getDefaultPattern();
    }

    protected String getDefaultPattern() {
        return defaultPattern;
    }

    @Required
    public void setDefaultPattern(final String defaultPattern) {
        this.defaultPattern = defaultPattern;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
