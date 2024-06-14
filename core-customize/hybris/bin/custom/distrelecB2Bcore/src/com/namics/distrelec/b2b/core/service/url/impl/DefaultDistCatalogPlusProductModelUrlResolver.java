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

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.impl.DefaultProductModelUrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Resolves the correct SEO url for a Catalog+ product Add language pattern replacement
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 */
public class DefaultDistCatalogPlusProductModelUrlResolver extends DefaultProductModelUrlResolver implements DistUrlResolver<ProductModel> {

    private final String CACHE_KEY = DefaultContentPageUrlResolver.class.getName();

    private CommonI18NService commonI18NService;

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
        final String language = i18NService.getCurrentLocale().getLanguage();
        return resolve(source, getBaseSiteService().getCurrentBaseSite(), language);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.lang.String)
     */
    @Override
    public String resolve(final ProductModel source, final BaseSiteModel baseSite, final String language) {
        return resolve(source, baseSite, new Locale(language));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.url.DistUrlResolver#resolve(java.lang.Object,
     * de.hybris.platform.basecommerce.model.site.BaseSiteModel, java.util.Locale)
     */
    @Override
    public String resolve(final ProductModel source, final BaseSiteModel baseSite, final Locale locale) {
        final String productTypeName = StringUtils.hasText(source.getTypeName()) ? source.getTypeName() : "-";
        final ProductModel baseProduct = getProductAndCategoryHelper().getBaseProduct(source);
        String url = getPattern(baseSite);

        if (baseSite != null) {
            url = url.replace("{baseSite-uid}", baseSite.getUid());
        }

        url = url.replace("{category-path}", buildPathString(getCategoryPath(baseProduct)));
        url = url.replace("{product-name}", urlSafe(baseProduct.getName(locale)));
        url = url.replace("{product-code}", source.getCode());
        url = url.replace("{language}", locale.getLanguage());
        url = url.replace("{manufacturer-name}", urlSafe(source.getManufacturerName()));
        url = url.replace("{product-type}", urlSafe(productTypeName));
        url = url.replace("{supplierAID}", source.getCatPlusSupplierAID());

        return url;
    }
    
    @Override
    public String resolve(final ProductModel source, final BaseSiteModel baseSite, final String language, final boolean isCanonical) {
        final String productTypeName = StringUtils.hasText(source.getTypeName()) ? source.getTypeName() : "-";
        final Locale locale = LocaleUtils.toLocale(language);
        final ProductModel baseProduct = getProductAndCategoryHelper().getBaseProduct(source);
        String url = getPattern(baseSite);

        if (baseSite != null) {
            url = url.replace("{baseSite-uid}", baseSite.getUid());
        }

        url = url.replace("{category-path}", buildPathString(getCategoryPath(baseProduct)));
        url = url.replace("{product-name}", urlSafe(baseProduct.getName(locale)));
        url = url.replace("{product-code}", source.getCode());
        if(isCanonical && null!=url && !url.isEmpty() ){
            url = url.replace("/{language}", "");
        }else{
         url = url.replace("{language}", locale.getLanguage());
        }

        url = url.replace("{manufacturer-name}", urlSafe(source.getManufacturerName()));
        url = url.replace("{product-type}", urlSafe(productTypeName));
        url = url.replace("{supplierAID}", source.getCatPlusSupplierAID());

        return url;
    }

    protected String getPattern(final BaseSiteModel baseSite) {
        return (baseSite != null && baseSite.getCatalogPlusProductUrlPattern() != null) ? baseSite.getCatalogPlusProductUrlPattern() : getDefaultPattern();
    }

    @Override
    protected String getKey(final ProductModel source) {
        return CACHE_KEY + "." + source.getPk().toString();
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
