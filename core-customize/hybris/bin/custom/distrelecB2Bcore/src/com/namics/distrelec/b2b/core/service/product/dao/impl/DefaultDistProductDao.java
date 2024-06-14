/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.dao.impl;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.model.*;
import com.namics.distrelec.b2b.core.service.product.dao.DistProductDao;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult.PunchoutReason;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.impl.DefaultProductDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.util.Collections.emptyList;

/**
 * Default implementation of {@link DistProductDao}.
 *
 * @since Distrelec 1.0
 */
public class DefaultDistProductDao extends DefaultProductDao implements DistProductDao {

    /**
     * Product VS sales organization query
     */
    private static final String PRODUCT_SALES_ORG_QUERY = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + " AS p JOIN "
            + DistSalesOrgProductModel._TYPECODE + " AS dsop ON {p." + ProductModel.PK + "}={dsop."
            + DistSalesOrgProductModel.PRODUCT + "} AND {dsop."
            + DistSalesOrgProductModel.SALESORG + "}=(?" + DistSalesOrgProductModel.SALESORG + ") JOIN "
            + DistSalesStatusModel._TYPECODE + " AS dss ON {dsop."
            + DistSalesOrgProductModel.SALESSTATUS + "}={dss." + DistSalesStatusModel.PK + "}} WHERE {p.PK}=(?"
            + DistSalesOrgProductModel.PRODUCT
            + ") AND {dss." + DistSalesStatusModel.VISIBLEINSHOP + "}=1 AND {dss."
            + DistSalesStatusModel.BUYABLEINSHOP + "}=1";

    /**
     * Product in hierarchy query
     */
    private static final String PRODUCTS_IN_HIERARCHY_QUERY = "SELECT {" + ProductModel._TYPECODE + "." + ProductModel.PK + "} FROM  {" + ProductModel._TYPECODE
            + "} WHERE {" + ProductModel._TYPECODE + "." + ProductModel.PRODUCTHIERARCHY + "}=(?"
            + ProductModel.PRODUCTHIERARCHY + ")";

    /**
     * Validity dates where close for the punchouts
     */
    private static final String PUNCHOUTS_VALIDITY_CLOSE = " {" + DistPunchOutFilterModel.VALIDFROMDATE + "} <= (?" + DistPunchOutFilterModel.VALIDFROMDATE
            + ") AND {" + DistPunchOutFilterModel.VALIDUNTILDATE + "} >= (?" //
            + DistPunchOutFilterModel.VALIDUNTILDATE + ")";

    /**
     * Manufacturer punchouts query
     */
    private static final String MANUFACTURER_PUNCHOUTS_QUERY = "SELECT {" + DistManufacturerPunchOutFilterModel.PK + "} FROM {"
            + DistManufacturerPunchOutFilterModel._TYPECODE + "} WHERE {"
            + DistManufacturerPunchOutFilterModel.MANUFACTURER + "} = (?"
            + DistManufacturerPunchOutFilterModel.MANUFACTURER + ") AND {"
            + DistManufacturerPunchOutFilterModel.ERPCUSTOMERID + "} = (?"
            + DistManufacturerPunchOutFilterModel.ERPCUSTOMERID + ") AND " + PUNCHOUTS_VALIDITY_CLOSE;

    /**
     * Product VS sales organization query
     */
    private static final String PRODUCT_SALES_STATUS_QUERY = "SELECT {" + DistSalesStatusModel.PK + "} FROM {" + DistSalesStatusModel._TYPECODE
            + " AS dss JOIN " + DistSalesOrgProductModel._TYPECODE + " AS dsop " + "ON {dsop."
            + DistSalesOrgProductModel.SALESSTATUS + "}={dss."
            + DistSalesStatusModel.PK + "}" + " JOIN " + DistSalesOrgProductModel.PRODUCT + " as p on {p."
            + DistSalesOrgProductModel.PK + "}={" + "dsop."
            + DistSalesOrgProductModel.PRODUCT + "}}" + " WHERE {dsop." + DistSalesOrgProductModel.SALESORG
            + "}=(?" + DistSalesOrgProductModel.SALESORG + ") "
            + "AND {p." + DistSalesOrgProductModel.PK + "}=(?" + DistSalesOrgProductModel.PRODUCT + ")";

    private static final String SALES_STATUS_FOR_PRODUCTS_QUERY = "SELECT {p.code}, {dss.code} FROM {" + DistSalesStatusModel._TYPECODE + " AS dss JOIN "
            + DistSalesOrgProductModel._TYPECODE + " AS dsop ON {dsop."
            + DistSalesOrgProductModel.SALESSTATUS + "}={dss.pk} JOIN " + ProductModel._TYPECODE
            + " AS p ON {dsop.product}={p.pk}} WHERE {dsop." + DistSalesOrgProductModel.SALESORG
            + "}=?salesOrg AND {p." + ProductModel.CODE
            + "} IN (?productCodes)";

    /**
     * Query to fetch a list of products given by their codes.
     */
    private static final String PRODUCTS_BY_CODES_QUERY = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} WHERE {" + ProductModel.CODE
            + "} IN (?codes) OR {" + ProductModel.CODEMOVEX + "} IN (?codes) OR {" + ProductModel.CODEELFA
            + "} IN (?codes) OR {" + ProductModel.CODENAVISION
            + "} IN (?codes) ORDER BY {" + ProductModel.CODE + "}";

    /**
     * Query to fetch a list of products given by their codes.
     */
    private static final String PRODUCTS_BY_SAP_CODES_QUERY = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} WHERE {"
            + ProductModel.CODE + "} IN (?codes) ORDER BY {" + ProductModel.CODE + "}";

    /**
     * Query to fetch a list of cmsSites given by product code.
     */
    private static final String CMSSITES_BY_PRODUCT_QUERY = "SELECT {" + CMSSiteModel.PK + "} FROM {" + CMSSiteModel._TYPECODE + " AS C JOIN "
            + DistSalesOrgModel._TYPECODE + " AS S ON {C." + CMSSiteModel.SALESORG + "} = {S."
            + DistSalesOrgModel.PK + "} JOIN "
            + DistSalesOrgProductModel._TYPECODE + " AS o ON {S." + DistSalesOrgModel.PK + "} = {o."
            + DistSalesOrgProductModel.SALESORG + "} JOIN "
            + DistSalesStatusModel._TYPECODE + " AS dss ON {o." + DistSalesOrgProductModel.SALESSTATUS
            + "} = {dss." + DistSalesStatusModel.PK + "}} WHERE {o."
            + DistSalesOrgProductModel.PRODUCT + "} = (?" + DistSalesOrgProductModel.PRODUCT + ") AND {dss."
            + DistSalesStatusModel.VISIBLEINSHOP
            + "} = 1 AND {dss." + DistSalesStatusModel.BUYABLEINSHOP + "} = 1";

    /**
     * DistSalesOrgProductModel query
     */
    private static final String DIST_PRODUCT_SALES_ORG_QUERY = "SELECT {pk} FROM {" + DistSalesOrgProductModel._TYPECODE + "} WHERE {"
            + DistSalesOrgProductModel.SALESORG + "}=?salesOrg AND {" + DistSalesOrgProductModel.PRODUCT
            + "}=?product";

    private static final String CONTAINS_SALES_STATUS_PRODUCT_WHICH_IS_NOT_IN_STATUS_QUERY = "SELECT 1 FROM {" + DistSalesOrgProductModel._TYPECODE
            + " AS dsop} WHERE "
            + " EXISTS ({{SELECT 1 FROM {" + ProductModel._TYPECODE
            + " AS p} WHERE {p." + ProductModel.CODE
            + "}=?" + DistSalesOrgProductModel.PRODUCT + " AND {p." + ProductModel.PK
            + "}={dsop."
            + DistSalesOrgProductModel.PRODUCT
            + "}}})"
            + " AND EXISTS ({{SELECT 1 FROM {" + DistSalesOrgModel._TYPECODE
            + " AS so} WHERE {so."
            + DistSalesOrgModel.CODE + "}=?" + DistSalesOrgProductModel.SALESORG
            + " AND {so."
            + DistSalesOrgModel.PK + "}={dsop."
            + DistSalesOrgProductModel.SALESORG + "}}})"
            + " AND EXISTS ({{SELECT 1 FROM {" + DistSalesStatusModel._TYPECODE
            + " AS ss} WHERE {ss." + DistSalesStatusModel.CODE + "}<>?"
            + DistSalesOrgProductModel.SALESSTATUS + " AND {ss."
            + DistSalesStatusModel.PK + "}={dsop."
            + DistSalesOrgProductModel.SALESSTATUS + "}}})";

    /**
     * Query to fetch a list of products given by their codes.
     */
    private static final String PRODUCTS_BY_CODES_OR_MPN_QUERY = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} WHERE {"
            + ProductModel.TYPENAME + "} = ?mpn ORDER BY {" + ProductModel.TYPENAME + "}";

    /**
     * Query to fetch existing product codes from the list of codes
     */
    private static final String EXISTING_PRODUCT_CODES = "SELECT {" + ProductModel.CODE + "} FROM {" + ProductModel._TYPECODE + "} WHERE {" + ProductModel.CODE
            + "} IN (?productCodes)";

    /**
     * Query to fetch products which have sales status 60, 61 or 62 and lastModifiedErp timestamp older than x
     */
    private static final String EOL_PRODUCTS_OLDER_THAN = "SELECT {p." + ProductModel.PK + "} from {" + ProductModel._TYPECODE + " as p}\n"
            + "WHERE EXISTS ({{\n"
            + "SELECT {dsp." + DistSalesOrgProductModel.PRODUCT + "}\n"
            + "FROM {" + DistSalesOrgProductModel._TYPECODE + " as dsp\n"
            + "JOIN " + DistSalesStatusModel._TYPECODE + " as dss on {dsp." + DistSalesOrgProductModel.SALESSTATUS + "} = {dss." + DistSalesStatusModel.PK + "}}\n"
            + "WHERE {dsp." + DistSalesOrgProductModel.PRODUCT + "} = {p." + ProductModel.PK + "}\n"
            + "AND {dss." + DistSalesStatusModel.ENDOFLIFEINSHOP + "} = 1\n"
            + "AND ((EXISTS ({{\n"
            + "SELECT {pr." + ProductReferenceModel.PK + "}\n"
            + "FROM {ProductReference as pr}\n"
            + "WHERE {pr." + ProductReferenceModel.SOURCE + "} = {dsp." + DistSalesOrgProductModel.PRODUCT + "}}})\n"
            + "AND {dsp." + DistSalesOrgProductModel.ENDOFLIFEDATE + "} < ?expirationDateWithAlternatives)\n"
            + "OR\n"
            + "(NOT EXISTS ({{\n"
            + "SELECT {pr." + ProductReferenceModel.PK + "}\n"
            + "FROM {" + ProductReferenceModel._TYPECODE + " as pr}\n"
            + "WHERE {pr." + ProductReferenceModel.SOURCE + "} = {dsp." + DistSalesOrgProductModel.PRODUCT + "}}})"
            + "AND {dsp." + DistSalesOrgProductModel.ENDOFLIFEDATE + "} < ?expirationDateWithoutAlternatives)\n"
            + ")}})";

    private DistFlexibleSearchExecutionService flexibleSearchExecutionService;

    /**
     * Create a new instance of {@code DefaultDistProductDao}
     *
     * @param typecode
     */
    public DefaultDistProductDao(final String typecode) {
        super(typecode);
    }

    /**
     * Search product with the main article number. If not product can be found we search also for products that matching the article number
     * of Movex, Elfa or Navision.
     */
    @Override
    public List<ProductModel> findProductsByCode(final String code) {
        List<ProductModel> products = super.findProductsByCode(code);
        if (CollectionUtils.isEmpty(products)) {
            products = find(Collections.singletonMap(ProductModel.CODEMOVEX, (Object) code));
            if (CollectionUtils.isEmpty(products)) {
                products = find(Collections.singletonMap(ProductModel.CODEELFA, (Object) code));
                if (CollectionUtils.isEmpty(products)) {
                    products = find(Collections.singletonMap(ProductModel.CODENAVISION, (Object) code));
                }
            }
        }

        return products;
    }

    /**
     * Search product with the main article number. If not product can be found we search also for products that matching the article number
     * of Movex, Elfa or Navision or by Manufacturer Part Number.
     */
    @Override
    public List<ProductModel> findProductByMPN(final String mpn) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCTS_BY_CODES_OR_MPN_QUERY);
        searchQuery.addQueryParameter("mpn", mpn);
        return getFlexibleSearchService().<ProductModel>search(searchQuery).getResult();
    }

    @Override
    public List<ProductModel> findProductsByCodes(final List<String> productCodes) {
        if (CollectionUtils.isEmpty(productCodes)) {
            return Collections.emptyList();
        }

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCTS_BY_CODES_QUERY);
        searchQuery.addQueryParameter("codes", productCodes);
        return getFlexibleSearchService().<ProductModel>search(searchQuery).getResult();
    }

    @Override
    public List<ProductModel> findProductsBySapCodes(List<String> productCodes) {
        if (CollectionUtils.isEmpty(productCodes)) {
            return Collections.emptyList();
        }

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCTS_BY_SAP_CODES_QUERY);
        searchQuery.addQueryParameter("codes", productCodes);
        return getFlexibleSearchService().<ProductModel>search(searchQuery).getResult();
    }

    @Override
    public List<PunchoutFilterResult> findPunchOutFilters(final DistSalesOrgModel salesOrg, final B2BUnitModel company, final SiteChannel customerType,
                                                          final CountryModel country, final ProductModel product, final Date validity) {
        return findPunchOutFilters(salesOrg, company, customerType, Collections.singletonList(country), Collections.singletonList(product), validity);
    }

    @Override
    public List<PunchoutFilterResult> findPunchOutFilters(final DistSalesOrgModel salesOrg, final B2BUnitModel company, final SiteChannel customerType,
                                                          final Collection<CountryModel> countries, final ProductModel product, final Date validity) {
        final List<ProductModel> products = product != null ? Collections.singletonList(product) : emptyList();
        return findPunchOutFilters(salesOrg, company, customerType, countries, products, validity);
    }

    @Override
    public List<PunchoutFilterResult> findPunchOutFilters(final DistSalesOrgModel salesOrg, final B2BUnitModel company, final SiteChannel customerType,
                                                          final CountryModel country, final List<ProductModel> products, final Date validity) {
        return findPunchOutFilters(salesOrg, company, customerType, Collections.singletonList(country), products, validity);
    }

    @Override
    public List<PunchoutFilterResult> findPunchOutFilters(final DistSalesOrgModel salesOrg, final B2BUnitModel company, final SiteChannel customerType,
                                                          final Collection<CountryModel> countries, final List<ProductModel> products, final Date validity) {
        final List<PunchoutFilterResult> punchoutFilterResult = new ArrayList<>();
        addPunchouts(punchoutFilterResult, getCustomerPunchouts(salesOrg, company, products, validity), products);
        addPunchouts(punchoutFilterResult, getCountryPunchouts(salesOrg, countries, products, validity), products);
        addPunchouts(punchoutFilterResult, getCustomerTypePunchouts(customerType, products, validity), products);
        addPunchouts(punchoutFilterResult, getManufacturerPunchouts(company, products, validity), products);
        return punchoutFilterResult;
    }

    @Override
    public boolean productIsListedForSalesOrg(ProductModel product, DistSalesOrgModel salesOrg) {
        FlexibleSearchQuery query = new FlexibleSearchQuery(PRODUCT_SALES_ORG_QUERY);
        query.addQueryParameter(DistSalesOrgProductModel.PRODUCT, product);
        query.addQueryParameter(DistSalesOrgProductModel.SALESORG, salesOrg);

        return CollectionUtils.isNotEmpty(getFlexibleSearchService().search(query).getResult());
    }

    @Override
    public String getProductSalesStatus(final ProductModel product, final DistSalesOrgModel salesOrg) {
        final DistSalesStatusModel result = getProductSalesStatusModel(product, salesOrg);
        return result != null ? result.getCode() : org.apache.commons.lang.StringUtils.EMPTY;
    }

    @Override
    public DistSalesStatusModel getProductSalesStatusModel(final ProductModel product, final DistSalesOrgModel salesOrg) {
        final Map<String, Object> params = new HashMap<>();
        params.put(DistSalesOrgProductModel.PRODUCT, product);
        params.put(DistSalesOrgProductModel.SALESORG, salesOrg);
        return searchDistSalesStatusModel(PRODUCT_SALES_STATUS_QUERY, params);
    }

    @Override
    public DistSalesOrgProductModel getDistSalesOrgProductModel(final ProductModel product, final DistSalesOrgModel salesOrg) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(DIST_PRODUCT_SALES_ORG_QUERY);
        searchQuery.addQueryParameter(DistSalesOrgProductModel.SALESORG, salesOrg);
        searchQuery.addQueryParameter(DistSalesOrgProductModel.PRODUCT, product);
        final SearchResult<DistSalesOrgProductModel> searchResult = getFlexibleSearchService().search(searchQuery);
        return searchResult.getCount() > 0 ? searchResult.getResult().get(0) : null;
    }

    @Override
    public boolean containsSalesStatusForProductWhichIsNotInStatus(String productCode, String salesOrgCode, String salesStatus) {
        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(CONTAINS_SALES_STATUS_PRODUCT_WHICH_IS_NOT_IN_STATUS_QUERY);
        searchQuery.addQueryParameter(DistSalesOrgProductModel.PRODUCT, productCode);
        searchQuery.addQueryParameter(DistSalesOrgProductModel.SALESORG, salesOrgCode);
        searchQuery.addQueryParameter(DistSalesOrgProductModel.SALESSTATUS, salesStatus);
        SearchResult result = getFlexibleSearchService().search(searchQuery);
        return !result.getResult().isEmpty();
    }

    @Override
    public Map<String, String> getSalesStatusForProducts(final List<String> productCodes, final DistSalesOrgModel salesOrg) {
        if (CollectionUtils.isEmpty(productCodes)) {
            return MapUtils.EMPTY_MAP;
        }

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(SALES_STATUS_FOR_PRODUCTS_QUERY);
        searchQuery.addQueryParameter("salesOrg", salesOrg);
        searchQuery.addQueryParameter("productCodes", productCodes);
        searchQuery.setResultClassList(Arrays.asList(String.class, String.class));
        final SearchResult<List<String>> searchResult = getFlexibleSearchService().search(searchQuery);
        final Map<String, String> resultMap = new HashMap<>();
        for (final List<String> row : searchResult.getResult()) {
            resultMap.put(row.get(0), row.get(1));
        }

        return resultMap;
    }

    @Override
    public List<ProductModel> getProductForPimId(final String pimId) {
        validateParameterNotNull(pimId, "Parameter pimId must not be null!");
        return find(Collections.singletonMap(ProductModel.PIMID, pimId));
    }

    protected List<ProductModel> getProductsInHierarchy(final String hierarchyCode, final List<ProductModel> products) {
        Assert.notNull(hierarchyCode, "Given hierarchyCode cannot be null");

        final Map<String, Object> params = new HashMap<>();
        params.put(ProductModel.PRODUCTHIERARCHY, hierarchyCode);

        String query = PRODUCTS_IN_HIERARCHY_QUERY;

        if (CollectionUtils.isNotEmpty(products)) {
            query = query + " AND {" + ProductModel._TYPECODE + "." + ProductModel.PK + "} IN (?products)";
            params.put("products", products);
        }

        final SearchResult<ProductModel> result = getFlexibleSearchService().search(query, params);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : ListUtils.EMPTY_LIST;
    }

    @Override
    public ProductModel findProductByTypeOrCode(final String code) {
        validateParameterNotNull(code, "Product Type code must not be null!");
        final ProductModel product = getByTypeName(code);
        return product != null ? product : getProductByCode(code);
    }

    @Override
    public ProductModel findCatalogPlusProduct(final CatalogVersionModel catalogVersion, final String catPlusSupplierAID) {
        validateParameterNotNull(catalogVersion, "CatalogVersion must not be null!");
        validateParameterNotNull(catPlusSupplierAID, "Product catPlusSupplierAID must not be null!");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(ProductModel.CATPLUSSUPPLIERAID, catPlusSupplierAID);
        parameters.put(ProductModel.CATALOGVERSION, catalogVersion);

        try {
            final List<ProductModel> list = find(parameters);
            if (!list.isEmpty()) {
                return list.get(0);
            }
        } catch (final Exception exp) {
            // NOP
        }

        return null;
    }

    @Override
    public List<CMSSiteModel> getCMSSitesByProduct(ProductModel product) {
        final Map<String, Object> params = new HashMap<>();
        params.put(DistSalesOrgProductModel.PRODUCT, product);

        final SearchResult<CMSSiteModel> result = getFlexibleSearchService().search(CMSSITES_BY_PRODUCT_QUERY, params);

        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : ListUtils.EMPTY_LIST;
    }

    @Override
    public List<String> findExistingProductCodes(final List<String> codes) throws SQLException {
        Map<String, Object> params = new HashMap<>();
        params.put("productCodes", codes);
        List<String> existingCodes = new ArrayList<>();
        ResultSet result = null;

        try {
            result = flexibleSearchExecutionService.execute(EXISTING_PRODUCT_CODES, params);

            while (result.next()) {
                existingCodes.add(result.getString(1));
            }
        } finally {
            flexibleSearchExecutionService.closeResultSet(result);
        }

        return existingCodes;
    }

    @Override
    public List<ProductModel> findEOLProductsForRemoval(Date expirationDateWithAlternatives, Date expirationDateWithoutAlternatives, Integer resultSize) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(EOL_PRODUCTS_OLDER_THAN);
        searchQuery.addQueryParameter("expirationDateWithAlternatives", expirationDateWithAlternatives);
        searchQuery.addQueryParameter("expirationDateWithoutAlternatives", expirationDateWithoutAlternatives);
        if(resultSize != null){
            searchQuery.setCount(resultSize);
        }
        final SearchResult<ProductModel> searchResult = getFlexibleSearchService().search(searchQuery);
        return searchResult.getResult();
    }

    protected List<PunchoutFilterResult> createPunchoutFilterResult(List<DistPunchOutFilterModel> punchoutFilters) {
        List<PunchoutFilterResult> result = new ArrayList<>();
        for (final DistPunchOutFilterModel punchoutFilter : punchoutFilters) {
            PunchoutFilterResult punchoutFilterResult = new PunchoutFilterResult();
            if (punchoutFilter instanceof DistProductPunchOutFilterModel) {
                punchoutFilterResult.setPunchedOutProduct(((DistProductPunchOutFilterModel) punchoutFilter).getProduct());
                punchoutFilterResult.setPunchedOutProductHierarchy(((DistProductPunchOutFilterModel) punchoutFilter).getProductHierarchy());
            }

            if (punchoutFilter instanceof DistCUPunchOutFilterModel) {
                punchoutFilterResult.setPunchOutReason(PunchoutReason.CUSTOMER);
                punchoutFilterResult.setPunchedOutProductHierarchy(((DistCUPunchOutFilterModel) punchoutFilter).getProductHierarchy());
                punchoutFilterResult.setPunchedOutProduct(((DistCUPunchOutFilterModel) punchoutFilter).getProduct());
            } else if (punchoutFilter instanceof DistCTPunchOutFilterModel) {
                punchoutFilterResult.setPunchOutReason(PunchoutReason.CUSTOMER_TYPE);
                punchoutFilterResult.setPunchedOutProductHierarchy(((DistCTPunchOutFilterModel) punchoutFilter).getProductHierarchy());
                punchoutFilterResult.setPunchedOutProduct(((DistCTPunchOutFilterModel) punchoutFilter).getProduct());
            } else if (punchoutFilter instanceof DistCOPunchOutFilterModel) {
                punchoutFilterResult.setPunchOutReason(PunchoutReason.COUNTRY);
                punchoutFilterResult.setPunchedOutProductHierarchy(((DistCOPunchOutFilterModel) punchoutFilter).getProductHierarchy());
                punchoutFilterResult.setPunchedOutProduct(((DistCOPunchOutFilterModel) punchoutFilter).getProduct());
            }
            result.add(punchoutFilterResult);
        }
        return result;
    }

    protected void addPunchouts(final List<PunchoutFilterResult> list1, final List<PunchoutFilterResult> list2, final List<ProductModel> products) {
        for (final PunchoutFilterResult punchout : list2) {
            addIfNotInList(list1, punchout, punchout.getPunchedOutProduct());
            if (punchout.getPunchedOutProductHierarchy() != null) {
                final List<ProductModel> productsInHierarchy = getProductsInHierarchy(punchout.getPunchedOutProductHierarchy(), products);
                for (final ProductModel product : productsInHierarchy) {
                    addIfNotInList(list1, punchout, product);
                }
            }
        }
    }

    protected void addIfNotInList(final List<PunchoutFilterResult> result, final PunchoutFilterResult punchout, final ProductModel product) {
        if (product != null) {
            for (final PunchoutFilterResult punchoutFilterResult : result) {
                if (punchoutFilterResult.getPunchedOutProduct() != null && punchoutFilterResult.getPunchedOutProduct().getCode().equals(product.getCode())) {
                    // The ordinal of the enum is also the priority if more than one punchout applies for the same product
                    // The highest prio has the enum with the lowest ordinal
                    if (punchoutFilterResult.getPunchOutReason().ordinal() > punchout.getPunchOutReason().ordinal()) {
                        punchoutFilterResult.setPunchOutReason(punchout.getPunchOutReason());
                        if (punchout.getPunchOutReason() == PunchoutReason.MANUFACTURER) {
                            punchoutFilterResult.setPunchedOutManufacturer(punchout.getPunchedOutManufacturer());
                        }
                    }
                }
            }
        }

        if (StringUtils.hasText(punchout.getPunchedOutProductHierarchy())) {
            final PunchoutFilterResult punchoutFilterResult = new PunchoutFilterResult();
            punchoutFilterResult.setPunchedOutProduct(product);
            punchoutFilterResult.setPunchOutReason(punchout.getPunchOutReason());
            punchoutFilterResult.setPunchedOutProductHierarchy(punchout.getPunchedOutProductHierarchy());
            result.add(punchoutFilterResult);
        } else {
            result.add(punchout);
        }
    }

    /**
     * Retrieve all customer related punchouts
     *
     * @param salesOrg the sales organization
     * @param company  the customer
     * @param products the list of products
     * @param validity the validity date
     * @return a list of {@code PunchoutFilterResult}
     */
    protected List<PunchoutFilterResult> getCustomerPunchouts(final DistSalesOrgModel salesOrg, final B2BUnitModel company, final List<ProductModel> products,
                                                              final Date validity) {
        if (company == null || company.getErpCustomerID() == null) {
            return ListUtils.EMPTY_LIST;
        }

        final Map<String, Object> params = new HashMap<>();
        final StringBuilder query = getBaseQuery(DistCUPunchOutFilterModel._TYPECODE, params, salesOrg, products, validity);
        query.append(" AND {").append(DistCUPunchOutFilterModel.ERPCUSTOMERID).append("}=(?").append(DistCUPunchOutFilterModel.ERPCUSTOMERID).append(")");
        params.put(DistCUPunchOutFilterModel.ERPCUSTOMERID, company.getErpCustomerID());

        return createPunchoutFilterResult(search(query.toString(), params));
    }

    /**
     * Retrieve all country related punchouts
     *
     * @param salesOrg  the sales organization
     * @param countries the list of countries
     * @param products  the list of products
     * @param validity  the validity date
     * @return a list of {@code PunchoutFilterResult}
     */
    protected List<PunchoutFilterResult> getCountryPunchouts(final DistSalesOrgModel salesOrg, final Collection<CountryModel> countries,
                                                             final List<ProductModel> products, final Date validity) {
        if (CollectionUtils.isEmpty(countries)) {
            return emptyList();
        }

        final Map<String, Object> params = new HashMap<>();
        final StringBuilder query = getBaseQuery(DistCOPunchOutFilterModel._TYPECODE, params, salesOrg, products, validity);
        query.append(" AND {").append(DistCOPunchOutFilterModel.COUNTRY).append("} IN (?" + DistCOPunchOutFilterModel.COUNTRY + ")");
        params.put(DistCOPunchOutFilterModel.COUNTRY, countries);

        return createPunchoutFilterResult(search(query.toString(), params));
    }

    /**
     * Retrieve all customer type related punchouts
     *
     * @param channel  the customer type channel
     * @param products the list of products
     * @param validity the validity date
     * @return a list of {@code PunchoutFilterResult}
     */
    protected List<PunchoutFilterResult> getCustomerTypePunchouts(final SiteChannel channel, final List<ProductModel> products, final Date validity) {
        if (channel == null) {
            return ListUtils.EMPTY_LIST;
        }

        final Map<String, Object> params = new HashMap<>();
        final StringBuilder query = getBaseQuery(DistCTPunchOutFilterModel._TYPECODE, params, null, products, validity);
        query.append(" AND {").append(DistCTPunchOutFilterModel.CUSTOMERTYPE).append("}=(?").append(DistCTPunchOutFilterModel.CUSTOMERTYPE).append(")");
        params.put(DistCTPunchOutFilterModel.CUSTOMERTYPE, channel);

        return createPunchoutFilterResult(search(query.toString(), params));
    }

    /**
     * Retrieve all manufacturer related punchouts
     *
     * @param company  the customer company
     * @param products the list of products
     * @param validity the validity date
     * @return a list of {@code PunchoutFilterResult}
     */
    protected List<PunchoutFilterResult> getManufacturerPunchouts(final B2BUnitModel company, final List<ProductModel> products, final Date validity) {
        if (company == null || company.getErpCustomerID() == null) {
            return new ArrayList<>();
        }

        final List<PunchoutFilterResult> result = new ArrayList<>();
        final Map<String, Object> params = new HashMap<>();
        params.put(DistManufacturerPunchOutFilterModel.ERPCUSTOMERID, company.getErpCustomerID());
        final Date validityDate = removeTime(validity);
        params.put(DistPunchOutFilterModel.VALIDFROMDATE, validityDate);
        params.put(DistPunchOutFilterModel.VALIDUNTILDATE, validityDate);

        for (final ProductModel product : products) {
            if (product.getManufacturer() != null) {
                params.put(DistManufacturerPunchOutFilterModel.MANUFACTURER, product.getManufacturer());
                final List<DistPunchOutFilterModel> manufacturerPunchouts = search(MANUFACTURER_PUNCHOUTS_QUERY, params);

                if (!manufacturerPunchouts.isEmpty()) {
                    final String manufacturerName = ((DistManufacturerPunchOutFilterModel) manufacturerPunchouts.get(0)).getManufacturer().getName();
                    final PunchoutFilterResult punchoutFilterResult = new PunchoutFilterResult();
                    punchoutFilterResult.setPunchedOutProduct(product);
                    punchoutFilterResult.setPunchedOutManufacturer(manufacturerName);
                    punchoutFilterResult.setPunchOutReason(PunchoutReason.MANUFACTURER);
                    result.add(punchoutFilterResult);
                }
            }
        }

        return result;
    }

    protected List<DistPunchOutFilterModel> search(final String query, final Map<String, Object> params) {
        final SearchResult<DistPunchOutFilterModel> result = getFlexibleSearchService().search(query, params);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : ListUtils.EMPTY_LIST;
    }

    /**
     * Build the base query to search for the punchouts
     *
     * @param typeCode the punchout type
     * @param params   the query parameters map
     * @param salesOrg the sales organization
     * @param products the list of products
     * @param validity the validity date
     * @return a {@code StringBuilder} containing the search query.
     */
    protected StringBuilder getBaseQuery(final String typeCode, final Map<String, Object> params, final DistSalesOrgModel salesOrg,
                                         final List<ProductModel> products, final Date validity) {

        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistProductPunchOutFilterModel.PK).append("} FROM {").append(typeCode).append("} WHERE ")
                .append(PUNCHOUTS_VALIDITY_CLOSE);
        final Date validityDate = removeTime(validity);
        params.put(DistPunchOutFilterModel.VALIDFROMDATE, validityDate);
        params.put(DistPunchOutFilterModel.VALIDUNTILDATE, validityDate);

        if (CollectionUtils.isNotEmpty(products)) {
            query.append(" AND ({").append(DistProductPunchOutFilterModel.PRODUCT).append("} IN (?").append(DistProductPunchOutFilterModel.PRODUCT).append(")");
            params.put(DistProductPunchOutFilterModel.PRODUCT, products);

            final List<String> productHierarchies = getProductHierarchies(products);

            if (CollectionUtils.isNotEmpty(productHierarchies)) {
                query.append(" OR {").append(DistProductPunchOutFilterModel.PRODUCTHIERARCHY).append("} IN (?")
                        .append(DistProductPunchOutFilterModel.PRODUCTHIERARCHY).append(")");
                params.put(DistProductPunchOutFilterModel.PRODUCTHIERARCHY, productHierarchies);
            }
            query.append(")");
        }

        if (salesOrg != null) {
            query.append(" AND {").append(DistSalesOrgPunchOutFilterModel.SALESORG).append("} = (?").append(DistSalesOrgPunchOutFilterModel.SALESORG)
                    .append(")");
            params.put(DistSalesOrgPunchOutFilterModel.SALESORG, salesOrg);
        }

        return query;
    }

    protected List<String> getProductHierarchies(final List<ProductModel> products) {
        final List<String> productHierarchies = new ArrayList<>();
        for (final ProductModel product : products) {
            if (product.getProductHierarchy() != null) {
                productHierarchies.add(product.getProductHierarchy());
            }
        }
        return productHierarchies;
    }

    /**
     * Find the product by its code number
     *
     * @param code the article code number
     * @return the product having the specified code number
     */
    protected ProductModel getProductByCode(final String code) {
        final List<ProductModel> products = findProductsByCode(code);
        return CollectionUtils.isNotEmpty(products) ? products.get(0) : null;
    }

    /**
     * Find the product having the specified type name
     *
     * @param typeName the product type name
     * @return the product having the specified type name
     */
    protected ProductModel getByTypeName(final String typeName) {
        try {
            final List<ProductModel> list = find(Collections.singletonMap(ProductModel.TYPENAME, typeName));
            return CollectionUtils.isEmpty(list) ? null : list.get(0);
        } catch (final Exception exp) {
            return null;
        }
    }

    protected Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private DistSalesStatusModel searchDistSalesStatusModel(String productSalesStatusQuery, Map<String, Object> params) {
        final SearchResult<DistSalesStatusModel> searchResult = getFlexibleSearchService().search(productSalesStatusQuery, params);
        if (CollectionUtils.isNotEmpty(searchResult.getResult())) {
            List<DistSalesStatusModel> results = searchResult.getResult();
            return (results.get(0));
        }
        return null;
    }

    public void setFlexibleSearchExecutionService(DistFlexibleSearchExecutionService flexibleSearchExecutionService) {
        this.flexibleSearchExecutionService = flexibleSearchExecutionService;
    }
}
