/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url.impl;

import java.util.List;
import java.util.Locale;

import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.url.impl.DefaultCategoryModelUrlResolver;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Add language pattern replacement to the DefaultCategoryModelUrlResolver.
 */
public class DefaultDistCategoryModelUrlResolver extends DefaultCategoryModelUrlResolver implements DistUrlResolver<CategoryModel> {

    private CommonI18NService commonI18NService;
    private BaseSiteService baseSiteService;

    @Autowired
    private I18NService i18NService;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.commerceservices.url.impl.DefaultCategoryModelUrlResolver#resolveInternal(de.hybris.platform.category.model.
     * CategoryModel)
     */
    @Override
    protected String resolveInternal(final CategoryModel source) {
        final String language = i18NService.getCurrentLocale().getLanguage();
        return resolve(source, getBaseSiteService().getCurrentBaseSite(), language);
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.lang.String)
     */
    @Override
    public String resolve(final CategoryModel source, final BaseSiteModel baseSite, final String language) {
        return resolve(source, baseSite, LocaleUtils.toLocale(language));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.util.Locale)
     */
    @Override
    public String resolve(final CategoryModel source, final BaseSiteModel baseSite, final Locale locale) {
        final String categoryPath = buildPathString(getCategoryPath(source), locale);
        final String categoryCode = source.getCode();

        // Replace pattern values
        String url = getPattern(baseSite);
        url = url.replace("{language}", locale.getLanguage());
        url = url.replace("{category-path}", categoryPath);
        url = url.replace("{category-code}", categoryCode);

        return UrlResolverUtils.normalize(url);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.util.Locale, boolean)
     */
    @Override
    public String resolve(final CategoryModel source, final BaseSiteModel baseSite, final String language, final boolean isCanonical) {
        final Locale locale = LocaleUtils.toLocale(language);
        final String categoryPath = buildPathString(getCategoryPath(source), locale);
        final String categoryCode = source.getCode();
        
        // Replace pattern values
        String url = getPattern(baseSite);
        if(isCanonical && null!=url && !url.isEmpty() ){
            url = url.replace("/{language}", "");
        }else{
         url = url.replace("{language}", locale.getLanguage());
        }

        url = url.replace("{category-path}", categoryPath);
        url = url.replace("{category-code}", categoryCode);

        return UrlResolverUtils.normalize(url);
    }
    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.commerceservices.url.impl.DefaultCategoryModelUrlResolver#getPattern()
     */
    @Override
    protected String getPattern() {
        return getPattern(getBaseSiteService().getCurrentBaseSite());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.commerceservices.url.impl.DefaultCategoryModelUrlResolver#buildPathString(java.util.List)
     */
    @Override
    protected String buildPathString(final List<CategoryModel> path) {
        return buildPathString(path, new Locale(i18NService.getCurrentLocale().getLanguage()));
    }

    protected String getPattern(final BaseSiteModel baseSite) {
        if (baseSite != null && !StringUtils.isBlank(baseSite.getCategoryUrlPattern())) {
            return baseSite.getCategoryUrlPattern();
        }
        return super.getPattern();
    }

    protected String buildPathString(final List<CategoryModel> path, final Locale locale) {
        final StringBuilder result = new StringBuilder();

        for (final CategoryModel currentCat : path) {
            if (currentCat.getLevel() != null && currentCat.getLevel().intValue() != 0 && StringUtils.isNotBlank(currentCat.getNameSeo(locale))) {
                result.append(currentCat.getNameSeo(locale)).append('/');
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    @Override
    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Override
    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }
}
