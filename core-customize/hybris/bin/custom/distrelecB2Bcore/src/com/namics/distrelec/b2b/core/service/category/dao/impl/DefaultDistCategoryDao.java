/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.category.dao.impl;

import com.namics.distrelec.b2b.core.model.CategoryCountryModel;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;
import com.namics.distrelec.b2b.core.model.seo.DistCategoryPredecessorModel;
import com.namics.distrelec.b2b.core.service.category.dao.DistCategoryDao;
import de.hybris.platform.category.daos.impl.DefaultCategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.time.DateUtils.*;

/**
 * Default implementation of {@link DistCategoryDao}
 * 
 * @author pnueesch, Namics AG
 * @since Namics Extensions 1.0
 */
public class DefaultDistCategoryDao extends DefaultCategoryDao implements DistCategoryDao {

    private static final Logger LOG = LogManager.getLogger(DefaultDistCategoryDao.class);
    private static final List<Class<Long>> R_CLASS_LIST = Arrays.asList(Long.class);

    private static final String FIND_CAT_BY_LEVEL_RANGE_QUERY = "SELECT {c.pk} FROM {" + CategoryModel._TYPECODE + "! AS c JOIN "
            + DistPimCategoryTypeModel._TYPECODE + " AS dpct ON {c." + CategoryModel.PIMCATEGORYTYPE + "}={dpct.pk}} WHERE {c." + CategoryModel.LEVEL
            + "} >=?fromLevel AND {c." + CategoryModel.LEVEL + "} < ?toLevel AND {dpct." + DistPimCategoryTypeModel.VISIBLE + "}=1 ORDER BY {c."
            + CategoryModel.LEVEL + "} ASC, {c." + CategoryModel.NAME + "} ASC";

    private static final String FIND_SUCCESSOR_QUERY = "SELECT {pk} FROM {" + DistCategoryPredecessorModel._TYPECODE + "} WHERE {"
            + DistCategoryPredecessorModel.PREDECESSORID + "}=?" + DistCategoryPredecessorModel.PREDECESSORID + " AND {"
            + DistCategoryPredecessorModel.PREDECESSORTIMESTAMP + "} <= ?today ORDER BY {" + DistCategoryPredecessorModel.PREDECESSORTIMESTAMP + "} DESC";

    private static final String HAS_PRODUCTS_OR_SUB_CATEGORIES_QUERY = "SELECT COUNT({ca.pk}) FROM {Category! AS ca LEFT JOIN CategoryProductRelation! AS cpr ON {cpr.source}={ca.pk} "
            + "LEFT JOIN CategoryCategoryRelation! AS ccr ON {ccr.source}={ca.pk}} WHERE {ca.pk}=?source";

    private static final String PRODUCT_FAMILY_QUERY = "SELECT {c.pk} " +
            "FROM {Category as c " +
            "JOIN DistPimCategoryType as t ON {c.pimCategoryType} = {t.pk}} " +
            "WHERE {c.code} = ?code AND {t.code} = ?type";

    private static final String CATEGORY_HAS_PRODUCTS_QUERY = "SELECT count({c.pk}) from {category as c} "
    										+ "WHERE {c.pk} in(%s) AND EXISTS({{ SELECT {cpr.pk} FROM {CategoryProductRelation! as cpr " +
    														  "JOIN PRODUCT! as p on {cpr.target}={p.pk}" +
                                                              "JOIN distpricerow! as pr on {pr.product}={p.pk} " +
                                                              "JOIN cmssite! as cms on {pr.ug}={cms.userPriceGroup} " +
                                                              "JOIN ArticleApprovalStatus AS aas on {p.approvalStatus} = {aas.pk} " +
                                                              "JOIN DistSalesOrgProduct! AS dsop on {dsop.product}={p.pk} " +
                                                              "JOIN DistsalesOrg AS dso on {dsop.salesOrg}={dso.pk }" +
                                                              "JOIN DistSalesStatus AS dss on {dsop.salesStatus}={dss.pk}} " +
                                                              "WHERE " +
                                                              "{dso.pk} = ?salesOrg AND " +
                                                              "{cms.pk} = ?cmsSite AND " +
                                                              "{aas.code} = 'approved' AND " +
                                                              "{p.pimId} IS NOT NULL AND " +
                                                              "{p.code} IS NOT NULL AND " +
                                                              "({p.exclude} IS NULL OR {p.exclude} =?excluded)  AND " +
                                                              "{dss.visibleInShop} =?visibleInShop " +
                                                              "AND ({pr.startTime} IS NULL OR {pr.startTime} <= ?date) " +
                                                              "AND ({pr.endTime} IS NULL OR {pr.endTime} >= ?date) " +
                                                              "AND {c.pk}={cpr.source} "
                                                           + "}})" ;

    private static final String VISIBLE_CATEGORIES = "SELECT {c.pk}" +
                                                     "  FROM {Category AS c}" +
                                                     " WHERE EXISTS ({{" +
                                                     "    SELECT 1" +
                                                     "      FROM {CategoryProductRelation AS cpr" +
                                                     "        JOIN Product AS p ON {cpr.target} = {p.pk}" +
                                                     "        JOIN distpricerow As pr ON {pr.product} = {p.pk} " +
                                                     "        JOIN cmssite AS cms ON {pr.ug} = {cms.userPriceGroup}" +
                                                     "        JOIN ArticleApprovalStatus AS aas ON {p.approvalStatus} = {aas.pk}" +
                                                     "        JOIN DistSalesOrgProduct AS dsop ON {dsop.product} = {p.pk}" +
                                                     "        JOIN DistsalesOrg AS dso ON {dsop.salesOrg} = {dso.pk}" +
                                                     "        JOIN DistSalesStatus AS dss ON {dsop.salesStatus} = {dss.pk}" +
                                                     "      }" +
                                                     "    WHERE {c.pk} = {cpr.source}" +
                                                     "      AND {dso.pk} = ?salesOrg " +
                                                     "      AND {cms.pk} = ?cmsSite " +
                                                     "      AND {aas.code} = 'approved' " +
                                                     "      AND {p.pimId} IS NOT NULL " +
                                                     "      AND {p.code} IS NOT NULL " +
                                                     "      AND ({p.exclude} IS NULL OR {p.exclude} = ?excluded) " +
                                                     "      AND {dss.visibleInShop} = ?visibleInShop " +
                                                     "      AND ({pr.startTime} IS NULL OR {pr.startTime} <= ?date) " +
                                                     "      AND ({pr.endTime} IS NULL OR {pr.endTime} >= ?date) " +
                                                     "  }})";

    private static final String HAS_PRODUCTS_IN_FAMILY_QUERY = "SELECT COUNT({pk}) FROM {" + ProductModel._TYPECODE + "} WHERE {" + ProductModel.PIMFAMILYCATEGORYCODE + "} = ?code";

    public static final String FAMILY_TYPE_CODE = "Familie";

    @Override
    public CategoryCountryModel findCountrySpecificCategoryInformation(final CategoryModel category, final CountryModel country) {
        validateParameterNotNull(category, "Category must not be null!");
        validateParameterNotNull(country, "Country must not be null!");

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(CategoryCountryModel.PK).append("} FROM {").append(CategoryCountryModel._TYPECODE).append("} ").append("WHERE {")
                .append(CategoryCountryModel.CATEGORY).append("} = ?").append(CategoryCountryModel.CATEGORY).append(" AND ").append("{")
                .append(CategoryCountryModel.COUNTRY).append("} = ?").append(CategoryCountryModel.COUNTRY);

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(CategoryCountryModel.CATEGORY, category);
        params.put(CategoryCountryModel.COUNTRY, country);

        final SearchResult<CategoryCountryModel> searchResult = getFlexibleSearchService().search(queryString.toString(), params);
        final List<CategoryCountryModel> result = searchResult.getResult();
        if (CollectionUtils.isNotEmpty(result)) {
            if (result.size() > 1) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Found multiple CategoryCountry for category with code '" + category.getCode() + "' and country '" + country.getIsocode() + "'");
                }
            }
            return result.get(0);
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public List<CategoryModel> getAll() {
        // The query should only check the categories but not the sub-types such as ClassificationClassModel
        final String query = "SELECT {pk} FROM {" + CategoryModel._TYPECODE + "!}";
        final SearchResult<CategoryModel> searchResult = getFlexibleSearchService().search(query);
        return CollectionUtils.isNotEmpty(searchResult.getResult()) ? searchResult.getResult() : new ArrayList<CategoryModel>();
    }

    @Override
    public List<CategoryModel> findEmptyCategories() {
        final StringBuilder query = new StringBuilder();
        // Search only for categories that is not the source of any "CategoryCategoryRelation" or "CategoryProductRelation"
        query.append("SELECT {pk} FROM {").append(CategoryModel._TYPECODE).append("!}") //
                .append("  WHERE {pk} NOT IN") //
                .append("  (  {{") //
                .append("      SELECT {source} FROM {CategoryCategoryRelation!}") //
                .append("    }}") //
                .append("  )") //
                .append("  AND {pk} NOT IN") //
                .append("  (  {{") //
                .append("      SELECT {source} FROM {CategoryProductRelation!}") //
                .append("    }}") //
                .append("  )");

        final SearchResult<CategoryModel> searchResult = getFlexibleSearchService().search(query.toString());
        return CollectionUtils.isNotEmpty(searchResult.getResult()) ? searchResult.getResult() : new ArrayList<CategoryModel>();
    }

    @Override
    public List<CategoryModel> getCategoryByLevelRange(final int from, final int to) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_CAT_BY_LEVEL_RANGE_QUERY);
        searchQuery.addQueryParameter("fromLevel", Integer.valueOf(from));
        searchQuery.addQueryParameter("toLevel", Integer.valueOf(to));
        return getFlexibleSearchService().<CategoryModel> search(searchQuery).getResult();
    }

    @Override
    public CategoryModel findSuccessor(final String code) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_SUCCESSOR_QUERY);
        searchQuery.addQueryParameter(DistCategoryPredecessorModel.PREDECESSORID, code);
        searchQuery.addQueryParameter("today", new Date());
        final SearchResult<DistCategoryPredecessorModel> searchResult = getFlexibleSearchService().<DistCategoryPredecessorModel> search(searchQuery);

        return CollectionUtils.isNotEmpty(searchResult.getResult()) ? searchResult.getResult().get(0).getSuccessor() : null;
    }

    @Override
    public boolean hasProductsOrSubCategories(final CategoryModel category) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(HAS_PRODUCTS_OR_SUB_CATEGORIES_QUERY);
        searchQuery.addQueryParameter("source", category);
        searchQuery.setResultClassList(Arrays.asList(Long.class));
        return getFlexibleSearchService().<Long> search(searchQuery).getResult().get(0).intValue() > 0;
    }

    @Override
    public boolean hasMultipleProductsInFamily(final String code) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(HAS_PRODUCTS_IN_FAMILY_QUERY);
        searchQuery.addQueryParameter("code", code);
        searchQuery.setResultClassList(Arrays.asList(Long.class));
        return getFlexibleSearchService().<Long> search(searchQuery).getResult().get(0).intValue() > 1;
    }

    @Override
    public List<CategoryModel> findCategoriesByCodes(final List<String> codes) {
        final StringBuilder query = new StringBuilder("SELECT {cat." + CategoryModel.PK + "} ");
        query.append("FROM {" + CategoryModel._TYPECODE + " AS cat} ");
        query.append("WHERE {cat." + CategoryModel.CODE + "} IN (?" + CategoryModel.CODE).append(")");

        final Map<String, Object> params = (Map) Collections.singletonMap(CategoryModel.CODE, codes);

        final SearchResult<CategoryModel> searchRes = search(query.toString(), params);
        return searchRes.getResult();
    }

    @Override
    public Optional<CategoryModel> findProductFamily(String code) {
        FlexibleSearchQuery query = new FlexibleSearchQuery(PRODUCT_FAMILY_QUERY);
        query.addQueryParameter("code", code);
        query.addQueryParameter("type", FAMILY_TYPE_CODE);
        query.setResultClassList(Collections.singletonList(CategoryModel.class));
        try
        {
            CategoryModel result = searchUnique(query);
            return Optional.ofNullable(result);
        }
        catch (ModelNotFoundException ex)
        {
            // silent catch
        }
        return Optional.empty();
    }


    @Override
    public boolean categoryHasVisibleProduct(CategoryModel category, CMSSiteModel cmsSite) {

        if (category == null) {
            return false;
        }

        List<String> categoryAndAllSubCategoriesSPks = getCategoryAndAllSubCategoriesSPks(category);
        String pksString = categoryAndAllSubCategoriesSPks.stream().collect(joining(","));

        String categoryHasProductsQuery = String.format(CATEGORY_HAS_PRODUCTS_QUERY, pksString);
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(categoryHasProductsQuery);
        flexibleSearchQuery.addQueryParameter("cmsSite", cmsSite);
        flexibleSearchQuery.addQueryParameter("salesOrg", cmsSite.getSalesOrg());
        flexibleSearchQuery.addQueryParameter("date", truncate(new Date(), Calendar.HOUR));
        flexibleSearchQuery.addQueryParameter("visibleInShop", Boolean.TRUE);
        flexibleSearchQuery.addQueryParameter("excluded", Boolean.FALSE);
        flexibleSearchQuery.setResultClassList(R_CLASS_LIST);
        truncate(new Date(), Calendar.HOUR);
        
        int resultCount = getFlexibleSearchService().<Long>search(flexibleSearchQuery).getResult().get(0).intValue();

        boolean productsFound = resultCount > 0;
        if (!productsFound) {
            LOG.info("Category [{}] has no visible products for cmsSite:{}", category.getCode(), cmsSite.getUid());
        }
        return productsFound;
    }

    protected List<String> getCategoryAndAllSubCategoriesSPks(CategoryModel category) {

        if (CollectionUtils.isEmpty(category.getCategories())) {

            return List.of(category.getPk().toString());
        }

        List<String> collectedPks = category.getCategories().stream()
                .map(this::getCategoryAndAllSubCategoriesSPks)
                .flatMap(Collection::stream).collect(Collectors.toList());
        collectedPks.add(category.getPk().toString());
        return collectedPks;
    }

    @Override
    public Set<String> getAllVisibleCategoryCodes(CMSSiteModel cmsSite) {
        if (cmsSite == null) {
            return Set.of();
        }

        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(VISIBLE_CATEGORIES);

        flexibleSearchQuery.addQueryParameter("cmsSite", cmsSite);
        flexibleSearchQuery.addQueryParameter("salesOrg", cmsSite.getSalesOrg());
        flexibleSearchQuery.addQueryParameter("date", truncate(new Date(), Calendar.HOUR));
        flexibleSearchQuery.addQueryParameter("visibleInShop", Boolean.TRUE);
        flexibleSearchQuery.addQueryParameter("excluded", Boolean.FALSE);
        SearchResult<CategoryModel> search = flexibleSearchService.search(flexibleSearchQuery);
        return search.getResult().stream()
                     .map(this::getAllSuperCategoryCodes)
                     .flatMap(Collection::stream)
                     .collect(toSet());
    }

    private Set<String> getAllSuperCategoryCodes(CategoryModel category) {
        if (isEmpty(category.getSupercategories())) {
            return Set.of(category.getCode());
        }

        Set<String> superCategoryCodes = category.getSupercategories()
                                                 .stream()
                                                 .map(this::getAllSuperCategoryCodes)
                                                 .flatMap(Collection::stream)
                                                 .collect(toSet());
        superCategoryCodes.add(category.getCode());
        return superCategoryCodes;
    }

}
