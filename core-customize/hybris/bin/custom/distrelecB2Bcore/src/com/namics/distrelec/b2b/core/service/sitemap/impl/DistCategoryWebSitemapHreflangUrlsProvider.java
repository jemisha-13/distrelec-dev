package com.namics.distrelec.b2b.core.service.sitemap.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.sitemap.EntityNames;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

public class DistCategoryWebSitemapHreflangUrlsProvider extends DistWebSitemapHreflangUrlsProvider<CategoryModel> {

    private static final String NON_EMPTY_CATEGORY_SEARCH_QUERY = "SELECT {ca.pk} FROM {Category! AS ca JOIN CategoryCMSSiteEntry AS ce ON {ce.category}={ca.pk}} WHERE {ce.cmsSite}=?cmsSite";

    @Autowired
    private DistCategoryService distCategoryService;

    @Override
    protected List<CMSSiteModel> getCmsSites(CategoryModel entity) {
        return distCategoryService.getAvailableCMSSitesForCategory(entity);
    }

    @Override
    protected String getEntityCode(final CategoryModel entity) {
        return entity.getCode();
    }

    @Override
    public String getEntityName() {
        return EntityNames.CATEGORY.name();
    }

    @Override
    protected void initQuery() {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(NON_EMPTY_CATEGORY_SEARCH_QUERY);
        searchQuery.addQueryParameter("cmsSite", getCmsSiteModel());
        setFlexibleSearchQuery(searchQuery);
    }

    @Override
    protected Matcher provideExcludePattern(CMSSiteModel cmsSiteModel) {
        return Pattern.compile(getCmsSiteUrlPrefix(getCmsSiteModel()) + "/(([a-z]{2}|-)/)?c/.+").matcher("");
    }
}
