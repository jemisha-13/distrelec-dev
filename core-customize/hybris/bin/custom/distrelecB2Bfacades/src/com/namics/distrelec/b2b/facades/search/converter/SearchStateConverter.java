/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Converter for {@link SearchStateData} POJOs.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class SearchStateConverter extends AbstractPopulatingConverter<SearchQueryData, SearchStateData> {

    private final Logger LOG = LoggerFactory.getLogger(SearchStateConverter.class);

    private String searchPath;

    private String outletPath;

    private String newPath;

    private UrlResolver<CategoryData> categoryDataUrlResolver;

    private UrlResolver<DistManufacturerData> distManufacturerDataUrlResolver;

    private Converter<SearchQueryData, String> searchQueryConverter;

    @Autowired
    @Qualifier("distProductFamilyUrlResolver")
    private DistUrlResolver<CategoryModel> productFamilyUrlResolver;

    @Autowired
    private DistCategoryService categoryService;

    @Override
    public void populate(final SearchQueryData source, final SearchStateData target) {
        final SearchQueryData queryData = new SearchQueryData();
        queryData.setValue(getSearchQueryConverter().convert(source));
        target.setQuery(queryData);

        if (DistSearchType.CATEGORY.equals(source.getSearchType())) {
            populateCategorySearchUrl(source, target);
        } else if (DistSearchType.MANUFACTURER.equals(source.getSearchType())) {
            populateManufacturerSearchUrl(source, target);
        } else if (DistSearchType.OUTLET.equals(source.getSearchType())) {
            populateOutletSearchUrl(target);
        } else if (DistSearchType.NEW.equals(source.getSearchType())) {
            populateNewSearchUrl(target);
        } else {
            populateFreeTextSearchUrl(target);
        }
        super.populate(source, target);
    }

    protected void populateCategorySearchUrl(final SearchQueryData source, final SearchStateData target) {
        if (target.getQuery() != null && target.getQuery().getValue() != null
                && target.getQuery().getValue().contains("productFamilyCode")) {
            final Map<String, String> queryMap = getQueryMap(target.getQuery().getValue());
            final String productFamilyCode = queryMap.get("filter_productFamilyCode");
            try {
                final CategoryModel productFamily = categoryService.getCategoryForCode(productFamilyCode);
                productFamilyUrlResolver.resolve(productFamily);
                target.setUrl(productFamilyUrlResolver.resolve(productFamily) + buildUrlQueryString(target));
            } catch (UnknownIdentifierException | AmbiguousIdentifierException e) {
                LOG.warn("Product family with code: {} is not found!", productFamilyCode);
            }
        } else {
            target.setUrl(getCategoryUrl(source) + buildUrlQueryString(target));
        }
    }

    private static Map<String, String> getQueryMap(final String query) {
        final String[] params = query.split("&");
        final Map<String, String> map = new HashMap<>();

        for (final String param : params) {
            if (param.contains("=")) {
                final String name = param.split("=")[0];
                final String value = param.split("=")[1];
                map.put(name, value);
            }
        }
        return map;
    }

    protected void populateManufacturerSearchUrl(final SearchQueryData source, final SearchStateData target) {
        target.setUrl(getManufacturerUrl(source) + buildUrlQueryString(target));
    }

    protected void populateOutletSearchUrl(final SearchStateData target) {
        target.setUrl(getOutletPath() + buildUrlQueryString(target));
    }

    protected void populateNewSearchUrl(final SearchStateData target) {
        target.setUrl(getNewPath() + buildUrlQueryString(target));
    }

    protected void populateFreeTextSearchUrl(final SearchStateData target) {
        target.setUrl(getSearchPath() + buildUrlQueryString(target));
    }

    protected String getCategoryUrl(final SearchQueryData source) {
        final CategoryData categoryData = new CategoryData();
        categoryData.setCode(source.getCode());
        return getCategoryDataUrlResolver().resolve(categoryData);
    }

    protected String getManufacturerUrl(final SearchQueryData source) {
        final DistManufacturerData manufacturerData = new DistManufacturerData();
        manufacturerData.setCode(source.getCode());
        return getDistManufacturerDataUrlResolver().resolve(manufacturerData);
    }

    protected String buildUrlQueryString(final SearchStateData target) {
        final StringBuilder queryUrl = new StringBuilder();
        queryUrl.append("?q=").append(target.getQuery().getValue());
        return queryUrl.toString();
    }

    @Override
    protected SearchStateData createTarget() {
        return new SearchStateData();
    }

    protected String getSearchPath() {
        return searchPath;
    }

    @Required
    public void setSearchPath(final String searchPath) {
        this.searchPath = searchPath;
    }

    protected UrlResolver<CategoryData> getCategoryDataUrlResolver() {
        return categoryDataUrlResolver;
    }

    protected String getOutletPath() {
        return outletPath;
    }

    @Required
    public void setOutletPath(final String outletPath) {
        this.outletPath = outletPath;
    }

    public String getNewPath() {
        return newPath;
    }

    @Required
    public void setNewPath(final String newPath) {
        this.newPath = newPath;
    }

    @Required
    public void setCategoryDataUrlResolver(final UrlResolver<CategoryData> categoryDataUrlResolver) {
        this.categoryDataUrlResolver = categoryDataUrlResolver;
    }

    public UrlResolver<DistManufacturerData> getDistManufacturerDataUrlResolver() {
        return distManufacturerDataUrlResolver;
    }

    @Required
    public void setDistManufacturerDataUrlResolver(
                                                   final UrlResolver<DistManufacturerData> distManufacturerDataUrlResolver) {
        this.distManufacturerDataUrlResolver = distManufacturerDataUrlResolver;
    }

    protected Converter<SearchQueryData, String> getSearchQueryConverter() {
        return searchQueryConverter;
    }

    @Required
    public void setSearchQueryConverter(final Converter<SearchQueryData, String> searchQueryConverter) {
        this.searchQueryConverter = searchQueryConverter;
    }

}
