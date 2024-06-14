package com.namics.distrelec.b2b.core.service.sitemap.impl;

import com.namics.distrelec.b2b.core.service.sitemap.EntityNames;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import java.util.regex.Pattern;

public class DistProductFamilyWebSitemapUrlsProvider extends DistWebSitemapUrlsProvider<CategoryModel> {

    private static final String PRODUCT_FAMILY_SEARCH_QUERY = "SELECT {ca.pk} FROM {Category! AS ca JOIN DistPimCategoryType as pim ON {ca.pimcategorytype}={pim.pk}} WHERE {pim.code}=?pimCode";
    private static final String PRODUCT_FAMILY_PIM_CODE = "sitemap.pim.code.family";
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
        return EntityNames.PRODUCT_FAMILY.name();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#initQuery()
     */
    @Override
    protected void initQuery() {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCT_FAMILY_SEARCH_QUERY + getQuerySuffix());
        searchQuery.addQueryParameter("pimCode", getConfigurationService().getConfiguration().getString(PRODUCT_FAMILY_PIM_CODE));
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
        EXCLUDE_PATTERN_MATCHER = Pattern.compile(getCmsSiteUrlPrefix() + "/noProductFamily").matcher("");
    }

    @Override
    public int getMaxEntitiesPerQuery() {
        return MAX_ENTITIES_PER_QUERY / 2;
    }

}
