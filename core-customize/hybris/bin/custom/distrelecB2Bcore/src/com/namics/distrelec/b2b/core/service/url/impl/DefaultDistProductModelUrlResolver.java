/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url.impl;

import java.util.Locale;

import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.impl.DefaultProductModelUrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * If available: Replace product with replacementProduct and add request parameter replacementFor= <br/>
 * If EOL: Replace product URL with EOL landing page URL <br/>
 * Add language pattern replacement
 */
public class DefaultDistProductModelUrlResolver extends DefaultProductModelUrlResolver implements DistUrlResolver<ProductModel> {

    private String eolLandingPage;
    private CommonI18NService commonI18NService;
    private String canonicalPattern;

    @Autowired
    private I18NService i18NService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.commerceservices.url.impl.DefaultProductModelUrlResolver#resolveInternal(de.hybris.platform.core.model.product.
     * ProductModel)
     */
    @Override
    protected String resolveInternal(final ProductModel source) {
        return resolveInternalLanguageReplacement(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.lang.String)
     */
    @Override
    public String resolve(final ProductModel source, final BaseSiteModel baseSite, final String language) {
        return resolve(source, baseSite, LocaleUtils.toLocale(language));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.util.Locale)
     */
    @Override
    public String resolve(final ProductModel source, final BaseSiteModel baseSite, final Locale locale) {
        final DistManufacturerModel manufacturer = source.getManufacturer();
        final String manufacturerName = (manufacturer != null && StringUtils.hasText(manufacturer.getNameSeo())) ? manufacturer.getNameSeo() : "-";
        final String productTypeName = (StringUtils.hasText(source.getTypeNameSeo())) ? source.getTypeNameSeo() : "-";
        final String nameSeo = source.getNameSeo(locale);

        String url = getPattern(baseSite);

        if (baseSite != null) {
            url = url.replace("{baseSite-uid}", baseSite.getUid());
        }

        /* /{language}/{product-name}-{manufacturer-name}-{product-type}/p/{product-code} */
        url = url.replace("{language}", locale.getLanguage());
        url = url.replace("{product-name}", StringUtils.hasText(nameSeo) ? nameSeo : "-");
        url = url.replace("{manufacturer-name}", manufacturerName);
        url = url.replace("{product-type}", urlSafe(productTypeName));
        url = url.replace("{product-code}", source.getCode());

        return UrlResolverUtils.normalize(url);
    }
    
    @Override
    public String resolve(final ProductModel source, final BaseSiteModel baseSite, final String language, boolean isCanonical) {
        Locale locale = LocaleUtils.toLocale(language);
        final DistManufacturerModel manufacturer = source.getManufacturer();
        final String manufacturerName = (manufacturer != null && StringUtils.hasText(manufacturer.getNameSeo())) ? manufacturer.getNameSeo() : "-";
        final String productTypeName = (StringUtils.hasText(source.getTypeNameSeo())) ? source.getTypeNameSeo() : "-";
        final String nameSeo = source.getNameSeo(locale);

        String url = getCanonicalPattern(baseSite);
        
        if (baseSite != null) {
            url = url.replace("{baseSite-uid}", baseSite.getUid());
        }

        /* /{language}/{product-name}-{manufacturer-name}-{product-type}/p/{product-code} */
        if(isCanonical && null!=url && !url.isEmpty() ){
            url = url.replace("/{language}", "");
        }else{
         url = url.replace("{language}", locale.getLanguage());
        }
        url = url.replace("{product-name}", StringUtils.hasText(nameSeo) ? nameSeo : "-");
        url = url.replace("{manufacturer-name}", manufacturerName);
        url = url.replace("{product-type}", urlSafe(productTypeName));
        url = url.replace("{product-code}", source.getCode());

        return UrlResolverUtils.normalize(url);
    }
    protected String getPattern(final BaseSiteModel baseSite) {
        if (baseSite != null && baseSite.getProductUrlPattern() != null) {
            return baseSite.getProductUrlPattern();
        }

        return getDefaultPattern();
    }
    
    protected String getCanonicalPattern(final BaseSiteModel baseSite) {
        if (baseSite != null && baseSite.getProductUrlPattern() != null) {
            return baseSite.getProductUrlPattern();
        }

        return getCanonicalPattern();
    }

    protected String resolveInternalLanguageReplacement(final ProductModel source) {
        final String language = i18NService.getCurrentLocale().getLanguage();
        return resolve(source, getBaseSiteService().getCurrentBaseSite(), language);
    }

    public String getEolLandingPage() {
        return eolLandingPage;
    }

    @Required
    public void setEolLandingPage(final String eolLandingPage) {
        this.eolLandingPage = eolLandingPage;
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
    public String getCanonicalPattern()
    {
        return canonicalPattern;
    }

    @Required
    public void setCanonicalPattern(final String canonicalPattern)
    {
        this.canonicalPattern = canonicalPattern;
    }

}
