package com.distrelec.solrfacetsearch.dao.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;

@IntegrationTest
public class DefaultDistProductSearchExportDAOIntegrationTest extends ServicelayerTransactionalTest {

    private static final String COUNTRY_ISOCODE = "CH";

    private static final String CMS_SITE_ID = "distrelec_CH";

    private static final String SALES_ORG_CODE = "7310";

    private static final String PRODUCT_ONE_CODE = "8500027_sample";

    private static final String PRODUCT_TWO_CODE = "8500043_sample";

    private static final String PRODUCT_THREE_CODE = "8500035_sample";

    @Resource
    private DistProductSearchExportDAO distProductSearchExportDAO;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource
    private ProductService productService;

    @Resource
    private DistSalesOrgService distSalesOrgService;

    @Resource
    private DistSalesOrgProductService distSalesOrgProductService;

    private CountryModel country;

    private CMSSiteModel cmsSite;

    private ProductModel productOne;

    private ProductModel productTwo;

    private ProductModel productThree;

    private DistSalesOrgModel distSalesOrg;

    private DistSalesOrgProductModel salesOrgProduct;

    private DistSalesOrgProductModel salesOrgProductDeprecated;

    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bcore/test/testErpCodelist.impex", "utf-8");
        importCsv("/distrelecfusionintegration/test/testProductSearchExportDao.impex", "utf-8");

        country = commonI18NService.getCountry(COUNTRY_ISOCODE);
        cmsSite = (CMSSiteModel) baseSiteService.getBaseSiteForUID(CMS_SITE_ID);
        distSalesOrg = distSalesOrgService.getSalesOrgForCode(SALES_ORG_CODE);

        productOne = productService.getProductForCode(PRODUCT_ONE_CODE);
        productTwo = productService.getProductForCode(PRODUCT_TWO_CODE);
        productThree = productService.getProductForCode(PRODUCT_THREE_CODE);

        salesOrgProduct = distSalesOrgProductService.getSalesOrgProduct(productOne, distSalesOrg);
        salesOrgProductDeprecated = distSalesOrgProductService.getSalesOrgProduct(productTwo, distSalesOrg);
    }

    @Test
    public void testGetTaxFactor() {
        double taxFactor = distProductSearchExportDAO.getTaxFactor(productOne, cmsSite, salesOrgProduct);

        assertThat(taxFactor, is(1.08));
    }

    @Test
    public void testGetTaxFactorForUndefinedTax() {
        double taxFactor = distProductSearchExportDAO.getTaxFactor(productTwo, cmsSite, salesOrgProductDeprecated);

        assertThat(taxFactor, is(1d));
    }

    @Test
    public void testGetDistSalesOrgProductModels() {
        DistSalesOrgProductModel result = distProductSearchExportDAO.getDistSalesOrgProductModels(productOne, cmsSite);

        assertThat(result, notNullValue());
        assertThat(result, is(this.salesOrgProduct));
    }

    @Test
    public void testGetDistSalesOrgProductModelsNotFound() {
        DistSalesOrgProductModel result = distProductSearchExportDAO.getDistSalesOrgProductModels(productThree, cmsSite);

        assertThat(result, nullValue());
    }

    @Test
    public void testGetProductCountry() {
        Optional<ProductCountryModel> productCountry = distProductSearchExportDAO.getProductCountry(productOne, country);

        assertThat(productCountry.isPresent(), is(TRUE));
    }

    @Test
    public void testGetProductCountryNotFound() {
        Optional<ProductCountryModel> productCountry = distProductSearchExportDAO.getProductCountry(productThree, country);

        assertThat(productCountry.isPresent(), is(FALSE));
    }

    @Test
    public void testGetTotalStockForProduct() {
        Set<WarehouseModel> warehouses = new HashSet<>();
        warehouses.addAll(cmsSite.getDeliveryWarehouses());
        warehouses.addAll(cmsSite.getPickupWarehouses());

        long totalStockForProduct = distProductSearchExportDAO.getTotalStockForProduct(productOne, warehouses);

        assertThat(totalStockForProduct, is(300l));
    }

    @Test
    public void testGetTotalStockForProductNoStock() {
        Set<WarehouseModel> warehouses = new HashSet<>();
        warehouses.addAll(cmsSite.getDeliveryWarehouses());
        warehouses.addAll(cmsSite.getPickupWarehouses());

        long totalStockForProduct = distProductSearchExportDAO.getTotalStockForProduct(productThree, warehouses);

        assertThat(totalStockForProduct, is(0l));
    }

    @Test
    public void testHasActivePunchOutFilter() {
        boolean hasActivePunchOutFilter = distProductSearchExportDAO.hasActivePunchOutFilter(cmsSite, country, productTwo);

        assertThat(hasActivePunchOutFilter, is(TRUE));
    }

    @Test
    public void testHasActivePunchOutFilterFalse() {
        boolean hasActivePunchOutFilter = distProductSearchExportDAO.hasActivePunchOutFilter(cmsSite, country, productOne);

        assertThat(hasActivePunchOutFilter, is(FALSE));
    }

    @Test
    public void testGetChannelsWithPunchOutFilters() {
        List<EnumerationValueModel> channelsWithPunchOutFilters = distProductSearchExportDAO.getChannelsWithPunchOutFilters(productTwo);

        assertThat(channelsWithPunchOutFilters, notNullValue());
        assertThat(channelsWithPunchOutFilters.size(), is(1));
        assertThat(channelsWithPunchOutFilters.get(0).getCode(), is(SiteChannel.B2B.getCode()));
    }

    @Test
    public void testGetChannelsWithPunchOutFiltersEmpty() {
        List<EnumerationValueModel> channelsWithPunchOutFilters = distProductSearchExportDAO.getChannelsWithPunchOutFilters(productOne);

        assertThat(channelsWithPunchOutFilters, notNullValue());
        assertThat(channelsWithPunchOutFilters.size(), is(0));
    }

    @Test
    public void testIsVisibleInSalesOrg() {
        boolean visibleInSalesOrg = distProductSearchExportDAO.isVisibleInSalesOrg(productOne, distSalesOrg);

        assertThat(visibleInSalesOrg, is(TRUE));
    }

    @Test
    public void testIsVisibleInSalesOrgFalse() {
        boolean visibleInSalesOrg = distProductSearchExportDAO.isVisibleInSalesOrg(productThree, distSalesOrg);

        assertThat(visibleInSalesOrg, is(FALSE));
    }

}
