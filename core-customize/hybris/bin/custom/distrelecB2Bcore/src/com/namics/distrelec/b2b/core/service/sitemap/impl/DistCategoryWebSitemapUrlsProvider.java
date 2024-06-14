/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.impl;

import com.namics.distrelec.b2b.core.service.sitemap.EntityNames;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.regex.Pattern;

/**
 * {@code DistCategoryWebSitemapUrlsProvider}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistCategoryWebSitemapUrlsProvider extends DistWebSitemapUrlsProvider<CategoryModel> {

    private static final String NON_EMPTY_CATEGORY_SEARCH_QUERY = "SELECT {ca.pk} FROM {Category! AS ca JOIN CategoryCMSSiteEntry AS ce ON {ce.category}={ca.pk}} WHERE {ce.cmsSite}=?cmsSite";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#getEntityCode(de.hybris.platform.core.model.ItemModel)
     */
    @Override
    protected String getEntityCode(final CategoryModel entity) {
        return entity.getCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.sitemap.WebSitemapUrlsProvider#getEntityName()
     */
    @Override
    public String getEntityName() {
        return EntityNames.CATEGORY.name();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#initQuery()
     */
    @Override
    protected void initQuery() {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(NON_EMPTY_CATEGORY_SEARCH_QUERY + getQuerySuffix());
        searchQuery.addQueryParameter("cmsSite", getCmsSiteModel());
        setFlexibleSearchQuery(searchQuery);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#getQuerySuffix()
     */
    @Override
    protected String getQuerySuffix() {
        return " AND {ca." + CategoryModel.PK + "} > " + getLastPk() + " ORDER BY {ca." + CategoryModel.PK + "}";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#initExcludePattern()
     */
    @Override
    protected void initExcludePattern() {
        EXCLUDE_PATTERN_MATCHER = Pattern.compile(getCmsSiteUrlPrefix() + "/(([a-z]{2}|-)/)?c/.+").matcher("");
    }
}
