package com.distrelec.solrfacetsearch.dao.impl;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.TaxRowModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultDistProductSearchExportDAO implements DistProductSearchExportDAO {

    private static final Logger LOG = LogManager.getLogger(DefaultDistProductSearchExportDAO.class);

    private static final String PRODUCT_PARAM = "product";

    private static final String COUNTRY_PARAM = "country";

    private static final String SALES_ORG_PARAM = "salesOrg";

    private static final String DATE_PARAM = "date";

    private static final String QUERY_TAX_ROW = "SELECT {pk} " +
                                                "  FROM {TaxRow} " +
                                                " WHERE {ug} = ?ug " +
                                                "   AND {pg} = ?pg";

    private static final String QUERY_SALESORGPRODUCT = "SELECT {pk} " +
                                                        "  FROM {DistSalesOrgProduct} " +
                                                        " WHERE {product} = ?product " +
                                                        "   AND {salesOrg} = ?salesOrg";

    private static final String QUERY_PRODUCTCOUNTRY = "SELECT {pk} " +
                                                       "  FROM {ProductCountry} " +
                                                       " WHERE {country} = ?country " +
                                                       "   AND {product} = ?product";

    private static final String QUERY_SUM_STOCK_LEVEL = "SELECT SUM({available}) " +
                                                        "  FROM {stocklevel} " +
                                                        " WHERE {productcode} = ?productCode " +
                                                        "   AND {warehouse} IN (?warehousePks)";

    private static final String QUERY_ACTIVE_COUNTRY_PUNCHOUTFILTERS = "SELECT 1 " +
                                                                       "  FROM {DistCOPunchOutFilter AS pof} " +
                                                                       " WHERE ({pof.product} = ?product " +
                                                                       "        OR ({pof.productHierarchy} = ?producthierarchy " +
                                                                       "            AND {pof.productHierarchy} IS NOT NULL))" +
                                                                       "   AND {pof.validFromDate} <= ?date " +
                                                                       "   AND {pof.validUntilDate} >= ?date " +
                                                                       "   AND {pof.salesOrg} = ?salesOrg " +
                                                                       "   AND {pof.country} = ?country";

    private static final String QUERY_ACTIVE_CUSTOMERTYPE_PUNCHOUTFILTERS = "SELECT {customertype} " +
                                                                            "  FROM {DistCTPunchOutFilter AS pof} " +
                                                                            " WHERE {pof.product} = ?product " +
                                                                            "   AND {pof.validFromDate} <= ?date " +
                                                                            "   AND {pof.validUntilDate} >= ?date ";

    private static final String QUERY_VISIBLE_IN_SHOP = "SELECT 1 " +
                                                        "  FROM {DistSalesOrgProduct AS dsop " +
                                                        "  JOIN DistSalesStatus AS dss ON {dsop.salesStatus} = {dss.pk}} " +
                                                        " WHERE {dsop.product} = ?product " +
                                                        "   AND {dsop.salesOrg} = ?salesOrg " +
                                                        "   AND {dss.visibleInShop} = ?visibleInShop";

    private FlexibleSearchService flexibleSearchService;

    public DefaultDistProductSearchExportDAO(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    @Override
    public double getTaxFactor(ProductModel product, CMSSiteModel cmsSite, DistSalesOrgProductModel saleOrgProduct) {
        FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_TAX_ROW);
        fsq.addQueryParameter("ug", cmsSite.getUserTaxGroup());
        fsq.addQueryParameter("pg", saleOrgProduct.getProductTaxGroup());
        List<TaxRowModel> taxRowModels = flexibleSearchService.<TaxRowModel> search(fsq).getResult();

        if (isNotEmpty(taxRowModels) && taxRowModels.get(0).getTax() != null) {
            return 1d + (taxRowModels.get(0).getTax().getValue()) / 100;
        }
        LOG.warn("No tax-value found for product[{}], gross prices will be equal to net-prices", product.getCode());
        return 1d;
    }

    @Override
    public DistSalesOrgProductModel getDistSalesOrgProductModels(final ProductModel product, final CMSSiteModel cmsSite) {
        FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_SALESORGPRODUCT);
        fsq.addQueryParameter(PRODUCT_PARAM, product);
        fsq.addQueryParameter(SALES_ORG_PARAM, cmsSite.getSalesOrg());

        List<DistSalesOrgProductModel> saleOrgProducts = flexibleSearchService.<DistSalesOrgProductModel> search(fsq).getResult();

        if (isNotEmpty(saleOrgProducts)) {
            return saleOrgProducts.get(0);
        }
        return null;
    }

    @Override
    public Optional<ProductCountryModel> getProductCountry(ProductModel product, CountryModel country) {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(QUERY_PRODUCTCOUNTRY);
        flexibleSearchQuery.addQueryParameter(COUNTRY_PARAM, country);
        flexibleSearchQuery.addQueryParameter(PRODUCT_PARAM, product);
        List<ProductCountryModel> productCountries = flexibleSearchService.<ProductCountryModel> search(flexibleSearchQuery).getResult();

        if (isEmpty(productCountries)) {
            LOG.debug("Found no ProductCountries for Product[{}] and Country[{}]", product.getCode(), country.getIsocode());
            return Optional.empty();
        }

        if (productCountries.size() > 1) {
            LOG.warn("Found %s ProductCountries for Product[{}] and Country [{}]", product.getCode(), country.getIsocode());
        }

        return Optional.of(productCountries.get(0));
    }

    @Override
    public long getTotalStockForProduct(final ProductModel product, final Set<WarehouseModel> warehouses) {
        if (isEmpty(warehouses)) {
            return 0;
        }

        FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_SUM_STOCK_LEVEL);
        fsq.setResultClassList(Collections.singletonList(Long.class));
        fsq.addQueryParameter("productCode", product.getCode());
        fsq.addQueryParameter("warehousePks", warehouses);

        SearchResult<Long> search = flexibleSearchService.search(fsq);
        List<Long> result = search.getResult();

        return isNotEmpty(result) && result.get(0) != null ? result.get(0) : 0;
    }

    @Override
    public boolean hasActivePunchOutFilter(CMSSiteModel cmsSite, CountryModel country, ProductModel product) {
        Map<String, Object> params = Map.of(SALES_ORG_PARAM, cmsSite.getSalesOrg(),
                                            COUNTRY_PARAM, country,
                                            DATE_PARAM, getTimeForQuery(),
                                            "producthierarchy", product.getProductHierarchy(),
                                            PRODUCT_PARAM, product);

        SearchResult<Object> search = flexibleSearchService.search(QUERY_ACTIVE_COUNTRY_PUNCHOUTFILTERS, params);

        return search.getCount() > 0;
    }

    @Override
    public List<EnumerationValueModel> getChannelsWithPunchOutFilters(ProductModel product) {
        Map<String, Object> params = Map.of(DATE_PARAM, getTimeForQuery(),
                                            PRODUCT_PARAM, product);

        SearchResult<EnumerationValueModel> search = flexibleSearchService.search(QUERY_ACTIVE_CUSTOMERTYPE_PUNCHOUTFILTERS, params);

        return search.getResult();
    }

    private Date getTimeForQuery() {
        return Date.from(ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES).toInstant());
    }

    @Override
    public boolean isVisibleInSalesOrg(ProductModel product, DistSalesOrgModel salesOrg) {
        Map<String, Object> params = Map.of("visibleInShop", TRUE,
                                            PRODUCT_PARAM, product,
                                            SALES_ORG_PARAM, salesOrg);

        SearchResult<Object> search = flexibleSearchService.search(QUERY_VISIBLE_IN_SHOP, params);
        return search.getCount() > 0;
    }

}
