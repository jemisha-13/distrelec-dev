package com.namics.distrelec.b2b.core.service.sitemap.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.namics.distrelec.b2b.core.service.sitemap.EntityNames;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

public class DistProductFamilyWebSitemapHreflangUrlsProvider extends DistWebSitemapHreflangUrlsProvider<CategoryModel> {

    private static final String PRODUCT_FAMILY_SEARCH_QUERY = "SELECT {ca.pk} FROM {Category! AS ca JOIN DistPimCategoryType as pim ON {ca.pimcategorytype}={pim.pk}} WHERE {pim.code}=?pimCode";

    private static final String PRODUCT_FAMILY_PIM_CODE = "sitemap.pim.code.family";

    @Override
    protected List<CMSSiteModel> getCmsSites(CategoryModel entity) {
        return List.of(getCmsSiteModel());
    }

    @Override
    protected String getEntityCode(final CategoryModel entity) {
        return entity.getCode();
    }

    @Override
    public String getEntityName() {
        return EntityNames.PRODUCT_FAMILY.name();
    }

    @Override
    protected void initQuery() {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCT_FAMILY_SEARCH_QUERY);
        searchQuery.addQueryParameter("pimCode", getConfigurationService().getConfiguration().getString(PRODUCT_FAMILY_PIM_CODE));
        setFlexibleSearchQuery(searchQuery);
    }


    @Override
    protected Matcher provideExcludePattern(CMSSiteModel cmsSiteModel) {
        return Pattern.compile(getCmsSiteUrlPrefix(cmsSiteModel) + "/noProductFamily").matcher("");
    }

}
