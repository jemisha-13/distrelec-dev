package com.namics.distrelec.b2b.core.service.sitemap.impl;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ErpSalesStatus.ATTRIBUTE_SITEMAP_END_OF_LIFE;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.COMMA;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.sitemap.EntityNames;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

public class DistProductWebSitemapHreflangUrlsProvider extends DistWebSitemapHreflangUrlsProvider<ProductModel> {

    private static final String REGEXP_SUFFIX = "/(([a-z]{2}|-)/)?p/[0-9]+";

    @Autowired
    private DistProductService productService;

    @Override
    protected List<CMSSiteModel> getCmsSites(ProductModel entity) {
        return productService.getAvailableCMSSitesByProduct(entity);
    }

    @Override
    protected String getEntityCode(final ProductModel entity) {
        return entity.getCode();
    }

    @Override
    protected Date getLastModifiedDate(final ProductModel entity) {
        return entity.getLastModifiedErp();
    }

    @Override
    public String getEntityName() {
        return EntityNames.PRODUCT.name();
    }

    @Override
    protected void initQuery() {
        String queryBuilder = "SELECT {p." + ProductModel.PK + "}, {cms." + CMSSiteModel.PK + "}  FROM {" + ProductModel._TYPECODE + " AS p" +
                              " JOIN " + DistSalesOrgProductModel._TYPECODE + " AS o ON {o.product} = {p.pk}" +
                              " JOIN " + CMSSiteModel._TYPECODE + " AS cms ON {cms.salesOrg} = {o.salesOrg}" +
                              " JOIN " + DistSalesStatusModel._TYPECODE + " AS dss ON {o.salesStatus} = {dss.pk} }" +
                              " WHERE {p." + ProductModel.PIMID + "} IS NOT NULL" +
                              " AND {p." + ProductModel.CODE + "} IS NOT NULL" +
                              " AND ({p." + ProductModel.EXCLUDE + "} IS NULL OR {p." + ProductModel.EXCLUDE + "} != 1)" +
                              " AND {dss." + DistSalesStatusModel.VISIBLEINSHOP + "} = 1 AND {dss." + DistSalesStatusModel.BUYABLEINSHOP + "} = 1" +
                              " AND {cms." + CMSSiteModel.UID + "} != 'distrelec_TR' AND {cms.uid} != 'distrelec'" +
                              " AND EXISTS ( {{ SELECT 1 FROM {" + DistSalesOrgProductModel._TYPECODE
                              + " AS so} WHERE {so:product} = {p:pk} AND {so:salesOrg} = ?salesOrg" +
                              " AND EXISTS ( {{ SELECT 1 from {" + DistSalesStatusModel._TYPECODE
                              + " AS st} WHERE {so:salesStatus} = {st:pk} AND {st:code} NOT IN (?eolStatusCodes) AND {st:buyableInShop} = 1 }} ) }} )" +
                              " AND EXISTS ( {{ SELECT 1 from {" + CatalogVersionModel._TYPECODE
                              + " AS cv} WHERE {p:catalogVersion} = {cv:pk} AND {cv:version} = 'Online'" +
                              " AND EXISTS ( {{ SELECT 1 from {" + CatalogModel._TYPECODE
                              + " AS c} WHERE {cv:catalog} = {c:pk} AND {c:id} = 'distrelecProductCatalog' }} ) }} )";

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryBuilder);
        searchQuery.addQueryParameter("salesOrg", getCmsSiteModel().getSalesOrg());
        searchQuery.addQueryParameter("eolStatusCodes", getEolStatusCodes());
        setNewResultClassList(searchQuery);
        setFlexibleSearchQuery(searchQuery);
    }

    private List<String> getEolStatusCodes() {
        final String eolCodes = getConfigurationService().getConfiguration().getString(ATTRIBUTE_SITEMAP_END_OF_LIFE);
        return StringUtils.isEmpty(eolCodes) ? new ArrayList<>() : Stream.of(eolCodes.split(COMMA)).collect(Collectors.toList());
    }

    private void setNewResultClassList(final FlexibleSearchQuery searchQuery) {
        final List<Class<?>> resultClassList = List.of(ProductModel.class, CMSSiteModel.class);
        searchQuery.setResultClassList(resultClassList);
    }

    @Override
    protected Matcher provideExcludePattern(CMSSiteModel cmsSiteModel) {
        return Pattern.compile(getCmsSiteUrlPrefix(cmsSiteModel) + REGEXP_SUFFIX).matcher("");
    }

    @Override
    protected Map<ProductModel, List<CMSSiteModel>> getEntitiesAndSites() {

        long queryTime = System.currentTimeMillis();
        List<List<Object>> results = getFlexibleSearchService().<List<Object>> search(getFlexibleSearchQuery()).getResult();
        LOG.info("Querying took: {}ms", System.currentTimeMillis() - queryTime);

        long collectingTime = System.currentTimeMillis();
        Map<ProductModel, List<CMSSiteModel>> map = new HashMap<>();
        for (List<Object> row : results) {
            ProductModel key = (ProductModel) row.get(0);
            CMSSiteModel value = (CMSSiteModel) row.get(1);
            if (map.containsKey(key)) {
                map.get(key).add(value);
            } else {
                List<CMSSiteModel> list = new ArrayList<>();
                list.add(value);
                map.put(key, list);
            }
        }
        LOG.info("Collecting entities and cms sites took: {}ms", System.currentTimeMillis() - collectingTime);

        return map;
    }
}
