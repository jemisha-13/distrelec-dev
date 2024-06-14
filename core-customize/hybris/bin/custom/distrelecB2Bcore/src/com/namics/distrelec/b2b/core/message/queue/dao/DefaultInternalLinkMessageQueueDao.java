/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.dao;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.base.Objects;
import com.namics.distrelec.b2b.core.message.queue.Constants;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * {@code DefaultInternalLinkMessageQueueDao}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @author <a href="marco.dellanna@distrelec.com">Marco Dell'Anna</a>, Sly GmbH
 * @since Distrelec 6.1
 */
public class DefaultInternalLinkMessageQueueDao extends AbstractItemDao implements InternalLinkMessageDao {

    private static final Logger LOG = LogManager.getLogger(DefaultInternalLinkMessageQueueDao.class);

    private static final int RELATED_DATA_COLUMN = 0;
    private static final int RELATED__COUNT_COLUMN = 1;
    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    private DistSqlUtils distSqlUtils;

    protected int getMaxCount() {
        return getConfigurationService().getConfiguration().getInt(Constants.IL_MAX_LINKS_KEY, 5);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchRelatedProductsForProduct(de.hybris.platform.core.model.
     * product.ProductModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<ProductModel> fetchRelatedProductsForProduct(final ProductModel product, final CMSSiteModel site) {
        final CategoryModel category = product.getPrimarySuperCategory() != null ? product.getPrimarySuperCategory()
                : (CollectionUtils.isNotEmpty(product.getSupercategories()) ? product.getSupercategories().iterator().next() : null);
        if (category == null || CollectionUtils.isEmpty(category.getSupercategories())) {
            return Collections.EMPTY_LIST;
        }

        final CategoryModel superCategory = category.getSupercategories().stream() //
                .filter(c -> CategoryModel.class == c.getClass()) //
                .findFirst().orElse(null); // Take the first element if any, otherwise null

        if (superCategory == null) {
            return Collections.EMPTY_LIST;
        }

        final FlexibleSearchQuery query = new FlexibleSearchQuery(getRelatedProductDataQuery());
        query.addQueryParameter("superSuperCategory", superCategory);
        query.addQueryParameter("site", site);
        query.addQueryParameter("salesOrg", site.getSalesOrg());
        query.setCount(getMaxCount());
        return flexibleSearchService.<ProductModel> search(query).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchRelatedCategoriesForProduct(de.hybris.platform.core.model
     * .product.ProductModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<CategoryModel> fetchRelatedCategoriesForProduct(final ProductModel product, final CMSSiteModel site) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(getRelatedCategoryDataQuery());
        searchQuery.addQueryParameter("manufacturer", product.getManufacturer());
        searchQuery.addQueryParameter("salesOrg", site.getSalesOrg());

        return flexibleSearchService.<CategoryModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchRelatedManufacturersForProduct(de.hybris.platform.core.
     * model.product.ProductModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<DistManufacturerModel> fetchRelatedManufacturersForProduct(final ProductModel product, final CMSSiteModel site) {
        return fetchRelatedManufacturersForProduct(product, site, fetchRelatedCategoriesForProduct(product, site));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchRelatedManufacturersForProduct(de.hybris.platform.core.
     * model.product.ProductModel, de.hybris.platform.cms2.model.site.CMSSiteModel, java.util.List)
     */
    @Override
    public List<DistManufacturerModel> fetchRelatedManufacturersForProduct(final ProductModel product, final CMSSiteModel site,
            final List<CategoryModel> preSelectedCategories) {
        if (product == null || site == null || CollectionUtils.isEmpty(preSelectedCategories) //
                || product.getManufacturer() == null || site.getSalesOrg() == null) {
            return Collections.EMPTY_LIST;
        }

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(getRelatedManufacturerDataQuery());
        searchQuery.addQueryParameter("manufacturer", product.getManufacturer());
        searchQuery.addQueryParameter("salesOrg", site.getSalesOrg());
        searchQuery.addQueryParameter("categories", preSelectedCategories);

        return flexibleSearchService.<DistManufacturerModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchRelatedCategoriesForManufacturer(com.namics.distrelec.b2b
     * .core.model.DistManufacturerModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<CategoryModel> fetchRelatedCategoriesForManufacturer(final DistManufacturerModel manufacturer, final CMSSiteModel site) {
        final String queryText = getFetchRelatedCategoriesForManufacturerQuery();
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryText);
        searchQuery.addQueryParameter("manufacturer", manufacturer);
        searchQuery.addQueryParameter("salesOrg", site.getSalesOrg());
        searchQuery.addQueryParameter("catalogVersion", site.getDefaultCatalog().getActiveCatalogVersion());
        searchQuery.setCount(getMaxCount());
        LOG.debug("fetchCategoriesRelatedToManifacturer: {}, manufacturer: {}, salesOrg: {}, catalogVersion: {}", queryText, manufacturer, site.getSalesOrg(),
                site.getDefaultCatalog().getActiveCatalogVersion());
        return flexibleSearchService.<CategoryModel> search(searchQuery).getResult();

    }

    protected String getFetchRelatedCategoriesForManufacturerQuery() {
        return new StringBuilder("SELECT category FROM ({{ ") //
                .append("SELECT {c.pk} AS category, SUM({sl.available}) as stock ") //
                .append("FROM { ") //
                .append("  product as p ") //
                .append("  JOIN distmanufacturer AS m ON {m.pk}={p.manufacturer} ") //
                .append("  JOIN DistSalesOrgProduct AS so ON {so.product} = {p.pk} ") //
                .append("  JOIN DistSalesStatus AS st ON {so.salesStatus} = {st.pk} ") //
                .append("  JOIN StockLevel AS sl ON {p.code}={sl.productcode} ") //
                .append("  JOIN CategoryProductRelation AS cpr ON {cpr.target}={p.pk} ") //
                .append("  JOIN Category AS c ON {c.pk}={cpr.source} ") //
                .append("} WHERE ") //
                .append("  {m.pk}= ?manufacturer ") //
                .append("  AND {so.salesOrg} = ?salesOrg ") //
                .append("  AND {c.catalogVersion} = ?catalogVersion") //
                .append("  AND {st.buyableInShop} = 1 ") //
                .append("  AND {sl.available} > 0 ") //
                .append("GROUP BY {c.pk} ") //
                .append("}}) INNERTABLE ORDER BY stock DESC ").toString();
    }

    /*
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 2 (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchRelatedManufacturers(java.util.List,
     * de.hybris.platform.cms2.model.site.CMSSiteModel, java.util.List)
     */
    @Override
    public List<DistManufacturerModel> fetchRelatedManufacturers(final List<CategoryModel> categories, final CMSSiteModel site,
            final List<DistManufacturerModel> excludedManifacturers) {
        if (CollectionUtils.isEmpty(categories)) {
            return (Collections.emptyList());
        }
        final String queryText = new StringBuilder("SELECT {m.pk}, {m.code}, COUNT({m.pk}) AS count ") //
                .append("FROM { ") //
                .append("  product as p ") //
                .append("  JOIN distmanufacturer AS m ON {m.pk}={p.manufacturer} ") //
                .append("  JOIN DistSalesOrgProduct AS so ON {so.product} = {p.pk} ") //
                .append("  JOIN DistSalesStatus AS st ON {so.salesStatus} = {st.pk} ") //
                .append("  JOIN StockLevel AS sl ON {p.code}={sl.productcode} ") //
                .append("  JOIN CategoryProductRelation AS cpr ON {cpr.target}={p.pk} ") //
                .append("} WHERE ") //
                .append("  {so.salesOrg} = ?salesOrg ") //
                .append("  AND  {st.buyableInShop} = 1 ") //
                .append("  AND {sl.available} > 0 ") //
                .append("  AND {cpr.source} IN (?categories) ") //
                .append(CollectionUtils.isNotEmpty(excludedManifacturers) ? "  AND {m.pk} NOT IN (?excludedManifacturers) " : " ") //
                .append("GROUP BY {m.pk}, {m.code} ") //
                .append("ORDER BY count DESC").toString();
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryText);
        searchQuery.addQueryParameter("categories", categories);
        if (CollectionUtils.isNotEmpty(excludedManifacturers)) {
            searchQuery.addQueryParameter("excludedManifacturers", excludedManifacturers);
        }
        searchQuery.addQueryParameter("salesOrg", site.getSalesOrg());
        searchQuery.setCount(getMaxCount());
        LOG.debug("fetchRelatedManufacturers: {}, categories: {}, salesOrg: {}, excludedManifacturers: {}", queryText,
                Arrays.toString(categories.toArray()), site.getSalesOrg(), Arrays.toString(excludedManifacturers.toArray()));
        return flexibleSearchService.<DistManufacturerModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchNewArrivalsOfManufacturer(com.namics.distrelec.b2b.core.
     * model.DistManufacturerModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<ProductModel> fetchNewArrivalsOfManufacturer(final DistManufacturerModel manufacturerModel, final CMSSiteModel siteModel) {
        final String queryText = new StringBuilder("SELECT PR_PK FROM ({{ ") //
                .append("  SELECT {prod.pk} AS PR_PK, {so.creationTime} AS START_DATE FROM { ") //
                .append("    Product AS prod ") //
                .append("    JOIN distmanufacturer AS m ON {m.pk}={prod.manufacturer} ") //
                .append("    JOIN DistSalesOrgProduct AS so ON {so.product} = {prod.pk} ") //
                .append("    JOIN DistSalesStatus AS st ON {so.salesStatus} = {st.pk} ") //
                .append("    JOIN CatalogVersion AS cv ON {prod.catalogVersion} = {cv.pk} ") //
                .append("    JOIN Catalog AS c ON {cv.catalog} = {c.pk} ") //
                .append("    JOIN OrderEntry AS oe ON {oe.product}={prod.pk} ") //
                .append("    JOIN Order! AS ord ON {oe.order}={ord.pk} ") //
                .append("    JOIN StockLevel AS sl ON {prod.code}={sl.productcode} ") //
                .append("  } WHERE  ") //
                .append("    {m.pk}= ?manufacturer ") //
                .append("    AND {ord.site}= ?site ") //
                .append("    AND {so.salesOrg} = ?salesOrg ") //
                .append("    AND {st.buyableInShop} = 1 ") //
                .append("    AND {c.id} = 'distrelecProductCatalog' ") //
                .append("    AND {cv.version} = 'Online' ") //
                .append("    AND {sl.available} >= 0 ") //
                .append("    AND {so.creationTime} > ?startDate") //
                .append("  GROUP BY {prod.pk} , {so.creationTime}") //
                .append("}}) INNERTABLE ORDER BY START_DATE DESC ").toString(); //
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryText);
        searchQuery.addQueryParameter("manufacturer", manufacturerModel);
        searchQuery.addQueryParameter("site", siteModel);
        searchQuery.addQueryParameter("salesOrg", siteModel.getSalesOrg());
        final Date startDate = DateUtils.addMonths(DateUtils.truncate(new Date(), Calendar.DATE), -12);
        searchQuery.addQueryParameter("startDate", startDate);
        searchQuery.setCount(getMaxCount());

        LOG.debug("fetchNewArrivalsOfManufacturer: {}, manufacturer: {}, site: {}, salesOrg: {}, startDate: {}", queryText, manufacturerModel.getCode(),
                siteModel, siteModel.getSalesOrg(), startDate);

        return flexibleSearchService.<ProductModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchTopSellersOfManufacturer(com.namics.distrelec.b2b.core.
     * model.DistManufacturerModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<ProductModel> fetchTopSellersOfManufacturer(final DistManufacturerModel manufacturerModel, final CMSSiteModel siteModel) {

        StringBuilder additionalConditionSB = new StringBuilder();
        additionalConditionSB.append("EXISTS({{SELECT 1 FROM {")
                .append(" distmanufacturer AS m}") //
                .append(" WHERE {m.pk}={prod.manufacturer}") //
                .append("   AND {m.pk}= ?manufacturer }})");
        final String queryText = createTopSellersQuery(additionalConditionSB.toString(), false);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryText);
        searchQuery.addQueryParameter("manufacturer", manufacturerModel);
        searchQuery.addQueryParameter("site", siteModel);
        searchQuery.addQueryParameter("salesOrg", siteModel.getSalesOrg());
        searchQuery.setCount(getMaxCount());
        LOG.debug("fetchTopSellersOfManufacturer: {}, manufacturer: {}, site: {}, salesOrg: {}", queryText, manufacturerModel, siteModel,
                siteModel.getSalesOrg());
        return flexibleSearchService.<ProductModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchTopSellersInCategories(java.util.Collection,
     * de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<RelatedItemData<ProductModel>> fetchTopSellersInCategories(final Collection<CategoryModel> categories, final CMSSiteModel siteModel) {
        if (CollectionUtils.isEmpty(categories)) {
            LOG.warn("fetchTopSellersInCategories called with empty categories collection");
            return Collections.emptyList();
        }
        StringBuilder additionalConditionSB = new StringBuilder() //
                .append("EXISTS({{SELECT 1 FROM {") //
                .append(" CategoryProductRelation AS cpr}") //
                .append(" WHERE {cpr.target} = {prod.pk}") //
                .append("   AND {cpr.source} IN (?categories) }})");
        final String queryText = createTopSellersQuery(additionalConditionSB.toString(), false);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryText);
        searchQuery.addQueryParameter("categories", categories);
        searchQuery.addQueryParameter("site", siteModel);
        searchQuery.addQueryParameter("salesOrg", siteModel.getSalesOrg());
        searchQuery.setCount(getMaxCount());
        searchQuery.setResultClassList(Arrays.asList(ProductModel.class, Long.class));
        LOG.debug("fetchTopSellersInCategories: {}, categories: {}, site: {}, salesOrg: {}", queryText, categories, siteModel, siteModel.getSalesOrg());
        final List<List<Object>> searchResult = flexibleSearchService.<List<Object>> search(searchQuery).getResult();
        return searchResult.stream().map(list -> {
            return new RelatedItemData<>((ProductModel) list.get(0), (Long) list.get(1));
        }).collect(Collectors.toList());
    }

    protected String createTopSellersQuery(String additionalCondition, boolean additionalSalesOrgCondition) {
        final String queryText = new StringBuilder("SELECT {prod.pk} AS PR_PK, (1 + ") //
                .append("        ({{SELECT ").append(distSqlUtils.isNull("SUM({sl.available})", "0")) //
                .append("            FROM {StockLevel AS sl} WHERE {prod.code}={sl.productcode} AND {sl.available} > 0 }})") //
                .append("      ) * (1 + ") //
                .append("        ({{SELECT ").append(distSqlUtils.isNull("COUNT({oe.pk})", "0")) //
                .append("            FROM {OrderEntry AS oe JOIN Order! AS ord ON {oe.order}={ord.pk}} ") //
                .append("             WHERE {ord.site}= ?site AND {oe.product}={prod.pk} }})") //
                .append("  ) AS ORE_NR FROM { ") //
                .append("    Product AS prod ") //
                .append("  } WHERE ") //
                .append(additionalCondition)
                .append("      AND EXISTS({{SELECT 1 FROM {") //
                .append("          DistSalesOrgProduct AS so ") //
                .append("          JOIN DistSalesStatus AS st ON {so.salesStatus} = {st.pk}} ") //
                .append("        WHERE {so.product} = {prod.pk}") //
                .append("          AND {so.salesOrg} = ?salesOrg ") //
                .append(additionalSalesOrgCondition ? "          AND {so.creationTime} > ?startDate " : "") //
                .append("          AND {st.buyableInShop} = 1 }})") //
                .append("      AND EXISTS({{SELECT 1 FROM {") //
                .append("          CatalogVersion AS cv ") //
                .append("          JOIN Catalog AS c ON {cv.catalog} = {c.pk}} ") //
                .append("        WHERE {prod.catalogVersion} = {cv.pk} ") //
                .append("          AND {c.id} = 'distrelecProductCatalog' ") //
                .append("          AND {cv.version} = 'Online' }})") //
                .append("  ORDER BY ORE_NR DESC ").toString(); //
        return queryText;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchNewArrivalsOfCategories(java.util.Collection,
     * de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<ProductModel> fetchNewArrivalsOfCategories(final Collection<CategoryModel> categories, final CMSSiteModel siteModel) {
        if (CollectionUtils.isEmpty(categories)) {
            LOG.debug("fetchNewArrivalsOfCategories called with empty categories collection");
            return Collections.emptyList();
        }
        StringBuilder additionalConditionSB = new StringBuilder() //
                .append("EXISTS({{SELECT 1 FROM {") //
                .append(" CategoryProductRelation AS cpr}") //
                .append(" WHERE {cpr.target} = {prod.pk}") //
                .append("   AND {cpr.source} IN (?categories) }})");
        final String queryText = createTopSellersQuery(additionalConditionSB.toString(), true);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryText);
        searchQuery.addQueryParameter("categories", categories);
        searchQuery.addQueryParameter("site", siteModel);
        searchQuery.addQueryParameter("salesOrg", siteModel.getSalesOrg());

        // 12 months earlier at mid-night
        final Date startDate = DateUtils.addMonths(DateUtils.truncate(new Date(), Calendar.DATE), -12);

        searchQuery.addQueryParameter("startDate", startDate);
        searchQuery.setCount(getMaxCount());

        LOG.debug("fetchNewArrivalsOfCategory: {}, category: {}, site: {}, salesOrg: {}, startDate: {}", queryText, categories,
                siteModel.getUid(), siteModel.getSalesOrg().getCode(), startDate);

        return flexibleSearchService.<ProductModel> search(searchQuery).getResult();
    }

    /**
     * @return the product related products query.
     */
    private String getRelatedProductDataQuery() {
        StringBuilder additionalConditionSB = new StringBuilder() //
                .append("EXISTS({{SELECT 1 FROM {") //
                .append(" CategoryCategoryRelation AS ccr}") //
                .append(" WHERE {ccr.target}={prod.primarySuperCategory}") //
                .append("   AND  {ccr.source}=?superSuperCategory }})");
        final String queryText = createTopSellersQuery(additionalConditionSB.toString(), false);

        return queryText;
    }

    /**
     * @return the product related categories query
     */
    private String getRelatedCategoryDataQuery() {
        return new StringBuilder() //
                .append("SELECT SUP_CAT_PK FROM (") //
                .append("  {{") //
                .append("      SELECT {ccr.source} AS SUP_CAT_PK, {p.primarySuperCategory} AS PL_PK, COUNT({sl.pk}) AS STK FROM {") //
                .append("          Product AS p") //
                .append("          JOIN DistSalesOrgProduct AS so ON {so.product} = {p.pk}") //
                .append("          JOIN DistSalesStatus AS st ON {so.salesStatus} = {st.pk}") //
                .append("          JOIN CatalogVersion AS cv ON {p.catalogVersion} = {cv.pk}") //
                .append("          JOIN Catalog AS c ON {cv.catalog} = {c.pk}") //
                .append("          JOIN StockLevel AS sl ON {p.code}={sl.productcode}") //
                .append("          JOIN CategoryCategoryRelation AS ccr ON {ccr.target}={p.primarySuperCategory}") //
                .append("      } WHERE {p.manufacturer}=?manufacturer") //
                .append("          AND {so.salesOrg} = ?salesOrg") //
                .append("          AND {st.buyableInShop} = 1") //
                .append("          AND {c.id} = 'distrelecProductCatalog'") //
                .append("          AND {cv.version} = 'Online'") //
                .append("          AND {sl.available} > 0") //
                .append("      GROUP BY {p.primarySuperCategory},{ccr.source} ") //
                .append("  }}) INNERTABLE GROUP BY SUP_CAT_PK ORDER BY SUM(STK) DESC") //
                .toString();
    }

    /**
     * Build the product related manufacturers query.
     * 
     * @return the product related manufacturers query.
     */
    private String getRelatedManufacturerDataQuery() {
        return new StringBuilder().append("SELECT MAN_PK FROM (") //
                .append("   {{") //
                .append("       SELECT {p.manufacturer} AS MAN_PK, COUNT({sl.pk}) AS STK FROM {") //
                .append("           Product AS p") //
                .append("           JOIN CategoryCategoryRelation AS ccr ON {ccr.target}={p.primarySuperCategory}") //
                .append("           JOIN StockLevel AS sl ON {p.code}={sl.productcode}") //
                .append("           JOIN DistSalesOrgProduct AS so ON {so.product} = {p.pk}") //
                .append("           JOIN DistSalesStatus AS st ON {so.salesStatus} = {st.pk}") //
                .append("           JOIN CatalogVersion AS cv ON {p.catalogVersion} = {cv.pk}") //
                .append("           JOIN Catalog AS c ON {cv.catalog} = {c.pk}") //
                .append("       } WHERE {ccr.source} IN (?categories)") //
                .append("           AND {p.manufacturer} != ?manufacturer") //
                .append("           AND {so.salesOrg} = ?salesOrg") //
                .append("           AND {st.buyableInShop} = 1") //
                .append("           AND {c.id} = 'distrelecProductCatalog'") //
                .append("           AND {cv.version} = 'Online'") //
                .append("           AND {sl.available} > 0") //
                .append("       GROUP BY {p.manufacturer}") //
                .append("   }}) INNERTABLE GROUP BY MAN_PK ORDER BY SUM(STK) DESC") //
                .toString();
    }

    /**
     * @return the category related categories for category query
     */
    private String getRelatedCategoryFromCategoryDataQuery() {
        return new StringBuilder() //
                .append("SELECT SUP_CAT_PK, SUM(STK) AS P_COUNT FROM (") //
                .append("  {{") //
                .append("      SELECT {ccr.source} AS SUP_CAT_PK, {p.primarySuperCategory} AS PL_PK, COUNT({sl.pk}) AS STK FROM {") //
                .append("          Product AS p") //
                .append("          JOIN DistSalesOrgProduct AS so ON {so.product} = {p.pk}") //
                .append("          JOIN DistSalesStatus AS st ON {so.salesStatus} = {st.pk}") //
                .append("          JOIN CatalogVersion AS cv ON {p.catalogVersion} = {cv.pk}") //
                .append("          JOIN Catalog AS c ON {cv.catalog} = {c.pk}") //
                .append("          JOIN StockLevel AS sl ON {p.code}={sl.productcode}") //
                .append("          JOIN CategoryCategoryRelation AS ccr ON {ccr.target}={p.primarySuperCategory}") //
                .append("      } WHERE {p.manufacturer} IN (?manufacturer)") //
                .append("          AND {so.salesOrg} = ?salesOrg") //
                .append("          AND {st.buyableInShop} = 1") //
                .append("          AND {c.id} = 'distrelecProductCatalog'") //
                .append("          AND {cv.version} = 'Online'") //
                .append("          AND {sl.available} > 0") //
                .append("      GROUP BY {p.primarySuperCategory},{ccr.source} ") //
                .append("  }}) INNERTABLE GROUP BY SUP_CAT_PK ORDER BY SUM(STK) DESC") //
                .toString();
    }

    /**
     * @return the category related manufacturers for category query
     */
    private String getRelatedManufacturersFromCategoryDataQuery() {
        return new StringBuilder().append("SELECT MAN_PK, SUM(STK) AS P_COUNT FROM (") //
                .append("   {{") //
                .append("       SELECT {p.manufacturer} AS MAN_PK, COUNT({sl.pk}) AS STK FROM {") //
                .append("           Product AS p") //
                .append("           JOIN StockLevel AS sl ON {p.code}={sl.productcode}") //
                .append("           JOIN DistSalesOrgProduct AS so ON {so.product} = {p.pk}") //
                .append("           JOIN DistSalesStatus AS st ON {so.salesStatus} = {st.pk}") //
                .append("           JOIN CatalogVersion AS cv ON {p.catalogVersion} = {cv.pk}") //
                .append("           JOIN Catalog AS c ON {cv.catalog} = {c.pk}") //
                .append("       } WHERE {p.primarySuperCategory} IN (?categories)") //
                .append("           AND {so.salesOrg} = ?salesOrg") //
                .append("           AND {st.buyableInShop} = 1") //
                .append("           AND {c.id} = 'distrelecProductCatalog'") //
                .append("           AND {cv.version} = 'Online'") //
                .append("           AND {sl.available} > 0") //
                .append("       GROUP BY {p.manufacturer}") //
                .append("   }}) INNERTABLE GROUP BY MAN_PK ORDER BY P_COUNT DESC") //
                .toString();
    }

    /**
     * @return the product related products for category query.
     */
    private String getRelatedProductDataForCategoryQuery() {
        return new StringBuilder("SELECT PR_PK, ORE_NR AS P_COUNT FROM ({{") //
                .append("  SELECT {prod.pk} AS PR_PK, SUM({sl.available}) AS ORE_NR FROM {") //
                .append("     Product AS prod") //
                .append("     JOIN DistSalesOrgProduct AS so ON {so.product} = {prod.pk}") //
                .append("     JOIN DistSalesStatus AS st ON {so.salesStatus} = {st.pk}") //
                .append("     JOIN CatalogVersion AS cv ON {prod.catalogVersion} = {cv.pk}") //
                .append("     JOIN Catalog AS c ON {cv.catalog} = {c.pk}") //
                .append("     JOIN StockLevel AS sl ON {prod.code}={sl.productcode}") //
                .append("  } WHERE {prod.primarySuperCategory} IN (?categories) ") //
                .append("     AND {so.salesOrg} = ?salesOrg") //
                .append("     AND {st.buyableInShop} = 1") //
                .append("     AND {c.id} = 'distrelecProductCatalog'") //
                .append("     AND {cv.version} = 'Online'") //
                .append("     AND {sl.available} >= 0") //
                .append("    GROUP BY {prod.pk} ") //
                .append("}}) INNERTABLE ORDER BY ORE_NR DESC")//
                .toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchRelatedProductsForCategory(de.hybris.platform.category.
     * model.CategoryModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<RelatedItemData<ProductModel>> fetchRelatedProductsForCategory(final Collection<CategoryModel> categories, final CMSSiteModel site) {
        if (CollectionUtils.isEmpty(categories) || null == site) {
            return Collections.EMPTY_LIST;
        }
        final FlexibleSearchQuery query = new FlexibleSearchQuery(getRelatedProductDataForCategoryQuery());
        query.addQueryParameter("categories", categories);
        query.addQueryParameter("site", site);
        query.addQueryParameter("salesOrg", site.getSalesOrg());
        LOG.debug("Query: {}, categories: {}, site: {}, salesOrg: {}", query.getQuery(),
                Arrays.toString(categories.stream().map(c -> c.getCode()).toArray()), site.getUid(), site.getSalesOrg().getCode());
        query.setResultClassList(Arrays.asList(DistManufacturerModel.class, String.class));
        final List<List<Object>> searchResult = flexibleSearchService.<List<Object>> search(query).getResult();

        return searchResult.stream().map(list -> {
            return new RelatedItemData<>((ProductModel) list.get(RELATED_DATA_COLUMN), Long.valueOf(list.get(RELATED__COUNT_COLUMN).toString()));
        }).collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchRelatedCategoriesForCategory(List<CategoryModel>,
     * de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<RelatedItemData<CategoryModel>> fetchRelatedCategoriesForCategory(final List<DistManufacturerModel> manufacturers, final CMSSiteModel site) {
        if (null == manufacturers || manufacturers.isEmpty() || null == site) {
            return Collections.EMPTY_LIST;
        }
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(getRelatedCategoryFromCategoryDataQuery());
        searchQuery.addQueryParameter("manufacturer", manufacturers);
        searchQuery.addQueryParameter("salesOrg", site.getSalesOrg());
        searchQuery.setResultClassList(Arrays.asList(DistManufacturerModel.class, String.class));
        final List<List<Object>> searchResult = flexibleSearchService.<List<Object>> search(searchQuery).getResult();
        return searchResult.stream() //
                .map(result -> {
                    final CategoryModel category = (CategoryModel) result.get(RELATED_DATA_COLUMN);
                    return category == null || !Objects.equal(category.getClass(), CategoryModel.class) ? null
                            : new RelatedItemData<>((CategoryModel) result.get(RELATED_DATA_COLUMN),
                                    Long.valueOf(result.get(RELATED__COUNT_COLUMN).toString()));
                }).filter(rdi -> rdi != null).collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao#fetchRelatedManufacturersForCategory(Set<CategoryModel>,
     * de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<RelatedItemData<DistManufacturerModel>> fetchRelatedManufacturersForCategory(final Collection<CategoryModel> categories,
            final CMSSiteModel site) {
        if (null == categories || categories.isEmpty() || null == site) {
            return Collections.EMPTY_LIST;
        }
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(getRelatedManufacturersFromCategoryDataQuery());
        searchQuery.addQueryParameter("categories", categories);
        searchQuery.addQueryParameter("salesOrg", site.getSalesOrg());
        searchQuery.setResultClassList(Arrays.asList(DistManufacturerModel.class, String.class));
        // Fetching list of List<Object> as manufacturers and product count which belongs per manufacturer needs to saved, search query can
        // not directly fetch the data into non-model class
        final List<List<Object>> searchResult = flexibleSearchService.<List<Object>> search(searchQuery).getResult();
        return searchResult.stream() //
                .filter(result -> result != null && result.size() > 1 && result.get(RELATED_DATA_COLUMN) != null) //
                .map(result -> {
                    return new RelatedItemData<>((DistManufacturerModel) result.get(RELATED_DATA_COLUMN),
                            Long.valueOf(result.get(RELATED__COUNT_COLUMN).toString()));
                }).collect(Collectors.toList());
    }
    // Getters & Setters

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
