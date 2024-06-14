package com.namics.distrelec.b2b.facades.product.impl;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.environment.RuntimeEnvironmentService;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.product.data.PIMAlternateResult;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.service.product.impl.DefaultDistProductService;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.distrelec.b2b.core.util.LocalDateUtil;
import com.namics.distrelec.b2b.facades.product.converters.ProductAvailabilityConverter;
import com.namics.distrelec.b2b.facades.product.data.PickupStockLevelData;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult.PunchoutReason.COUNTRY;
import static com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult.PunchoutReason.CUSTOMER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistrelecProductFacadeUnitTest {

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private ProductAvailabilityConverter productAvailabilityConverter;

    @Mock
    private DistrelecCMSSiteService cmsSiteService;

    @Mock
    private Converter<ProductModel, ProductData> productReferenceTargetConverter;

    @Mock
    private DistSalesOrgProductService distSalesOrgProductService;

    @Mock
    private DefaultDistProductService distProductService;

    @Mock
    private ModelService modelService;

    @Mock
    private RuntimeEnvironmentService runtimeEnvironmentService;

    @Mock
    private DistCartService distCartService;

    @Mock
    private ErpStatusUtil erpStatusUtil;

    @InjectMocks
    private DefaultDistrelecProductFacade distrelecProductFacade;

    private static final String PRODUCT_CODE_1 = "30326080";
    private static final String PRODUCT_CODE_2 = "30326090";
    private static final List<String> productCodes = Arrays.asList(PRODUCT_CODE_1, PRODUCT_CODE_2);

    private static final String WAREHOUSE_CODE = "Warehouse";

    @Before
    public void setUp(){
        when(runtimeEnvironmentService.isHeadless()).thenReturn(false);
    }

    @Test
    public void testGetAvailability() {
        ProductAvailabilityExtModel availabilityExtModel1 = mock(ProductAvailabilityExtModel.class);
        ProductAvailabilityExtModel availabilityExtModel2 = mock(ProductAvailabilityExtModel.class);
        List<ProductAvailabilityExtModel> productAvailabilityExtModelList = Arrays.asList(availabilityExtModel1, availabilityExtModel2);
        when(availabilityService.getAvailability(productCodes, Boolean.FALSE)).thenReturn(productAvailabilityExtModelList);

        ProductAvailabilityData availabilityData1 = mock(ProductAvailabilityData.class);
        ProductAvailabilityData availabilityData2 = mock(ProductAvailabilityData.class);

        when(productAvailabilityConverter.convert(availabilityExtModel1)).thenReturn(availabilityData1);
        when(productAvailabilityConverter.convert(availabilityExtModel2)).thenReturn(availabilityData2);

        List<ProductAvailabilityData> productAvailabilityDataList = distrelecProductFacade.getAvailability(productCodes);

        assertThat(productAvailabilityDataList, hasSize(2));
    }

    @Test
    public void testGetPickupAvailabilityDate() {
        LocalDate today = LocalDate.now();
        Map<String, Integer> productQuantityMap = Map.of(PRODUCT_CODE_1, 3);

        PickupStockLevelData pickupStockLevelData = new PickupStockLevelData();
        pickupStockLevelData.setWarehouseCode(WAREHOUSE_CODE);
        pickupStockLevelData.setStockLevel(10);
        List<PickupStockLevelData> pickupStockLevelList = new ArrayList<>();
        pickupStockLevelList.add(pickupStockLevelData);

        ProductAvailabilityData productAvailabilityData = new ProductAvailabilityData();
        productAvailabilityData.setProductCode(PRODUCT_CODE_1);
        productAvailabilityData.setStockLevelPickup(pickupStockLevelList);
        productAvailabilityData.setRequestedQuantity(3);

        ProductAvailabilityExtModel availabilityExtModel = new ProductAvailabilityExtModel();
        availabilityExtModel.setProductCode(PRODUCT_CODE_1);
        List<ProductAvailabilityExtModel> productExtAvailabilityList = Arrays.asList(availabilityExtModel);

        when(availabilityService.getAvailability(anyList(), anyBoolean(), anyBoolean())).thenReturn(productExtAvailabilityList);
        when(productAvailabilityConverter.convert(anyObject())).thenReturn(productAvailabilityData);

        Pair<Boolean, Date> result = distrelecProductFacade.getPickupAvailabilityDate(productQuantityMap, WAREHOUSE_CODE);
        assertThat(result.getKey(),is(true));
        assertThat(result.getValue(), equalTo(LocalDateUtil.skipWeekendToNextWorkingDateAndConvertToDate(today)));
    }

    @Test
    public void testGetAvailabilityForEntries() {
        AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
        List<AbstractOrderEntryModel> orderEntryList = Arrays.asList(orderEntryModel);

        ProductAvailabilityExtModel availabilityExtModel = new ProductAvailabilityExtModel();
        availabilityExtModel.setProductCode(PRODUCT_CODE_1);
        List<ProductAvailabilityExtModel> productExtAvailabilityList = Arrays.asList(availabilityExtModel);

        ProductAvailabilityData productAvailabilityData = new ProductAvailabilityData();

        when(availabilityService.getAvailabilityForEntries(anyList(), anyBoolean())).thenReturn(productExtAvailabilityList);
        when(productAvailabilityConverter.convert(anyObject())).thenReturn(productAvailabilityData);

        List<ProductAvailabilityData> result = distrelecProductFacade.getAvailabilityForEntries(orderEntryList, Boolean.FALSE);
        assertThat(result, hasSize(orderEntryList.size()));
    }

    @Test
    public void testGetAvailabilityAllCountries() {
        CountryModel country = new CountryModel();
        country.setIsocode("CH");

        CMSSiteModel cmsSite = new CMSSiteModel();
        cmsSite.setCountry(country);

        ProductAvailabilityExtModel availabilityExtModel = new ProductAvailabilityExtModel();
        List<ProductAvailabilityExtModel> productExtAvailabilityList = Arrays.asList(availabilityExtModel);

        ProductAvailabilityData productAvailabilityData = new ProductAvailabilityData();

        when(cmsSiteService.getSites()).thenReturn(Arrays.asList(cmsSite));
        when(availabilityService.getAvailability(anyList(), anyBoolean())).thenReturn(productExtAvailabilityList);
        when(productAvailabilityConverter.convert(anyObject())).thenReturn(productAvailabilityData);

        List<ProductAvailabilityData> result = distrelecProductFacade.getAvailabilityAllCountries(Arrays.asList(PRODUCT_CODE_1), Boolean.FALSE);
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getCountry(), equalTo(country.getIsocode()));
    }

    @Test
    public void testGetSimilarProducts() {
        ProductModel product = mock(ProductModel.class);
        List<ProductModel> products = Arrays.asList(product);
        ProductData productData = mock(ProductData.class);

        when(distProductService.getProductForCode(anyString())).thenReturn(product);
        when(distProductService.getSimilarProducts(anyObject(), anyInt(), anyInt())).thenReturn(products);
        when(productReferenceTargetConverter.convert(anyObject())).thenReturn(productData);

        List<ProductData> similarProducts = distrelecProductFacade.getSimilarProducts(PRODUCT_CODE_1, 1, 1);
        assertThat(similarProducts, hasSize(1));
    }

    @Test
    public void testGetProductsReferences() {
        ProductModel product = mock(ProductModel.class);
        when(distProductService.getProductForCode(anyString())).thenReturn(product);

        ProductData reference1 = new ProductData();
        reference1.setCode(PRODUCT_CODE_1);
        reference1.setBuyable(Boolean.TRUE);

        when(distProductService.getProductsReferences(anyObject(), anyObject(), anyInt(), anyInt())).thenReturn(Arrays.asList(product));
        when(productReferenceTargetConverter.convert(anyObject())).thenReturn(reference1);

        List<ProductData> result = distrelecProductFacade.getProductsReferences(PRODUCT_CODE_1, Arrays.asList(ProductReferenceTypeEnum.DIST_ACCESSORY), 1, 1);
        assertThat(result, hasSize(1));
    }

    @Test
    public void testGetProductSalesStatusModel() {
        when(cmsSiteService.isViewedInSharedInternationalSite()).thenReturn(Boolean.TRUE);
        when(modelService.create(DistSalesStatusModel.class)).thenReturn(new DistSalesStatusModel());
        DistSalesStatusModel salesStatus = distrelecProductFacade.getProductSalesStatusModel(PRODUCT_CODE_1);
        assertThat(salesStatus.isBuyableInShop(), is(true));
    }

    @Test
    public void testIsProductReplaced() {
        LocalDate from = LocalDate.now();
        LocalDate until = from.plusMonths(6);

        ProductModel productModel = new ProductModel();
        productModel.setReplacementFromDate(convertToDate(from));
        productModel.setReplacementUntilDate(convertToDate(until));

        when(distProductService.getProductForCode(PRODUCT_CODE_1)).thenReturn(productModel);

        assertThat(distrelecProductFacade.isProductReplaced(PRODUCT_CODE_1), is(true));
    }

    @Test
    public void testIsProductExcluded() {
        ProductModel product = new ProductModel();
        product.setCode(PRODUCT_CODE_1);

        PunchoutFilterResult filterResult1 = new PunchoutFilterResult();
        filterResult1.setPunchedOutProduct(product);
        filterResult1.setPunchedOutProductHierarchy("hierarchy");
        filterResult1.setPunchedOutManufacturer("manufacturer");
        filterResult1.setPunchOutReason(COUNTRY);

        PunchoutFilterResult filterResult2 = new PunchoutFilterResult();
        filterResult2.setPunchedOutProduct(product);
        filterResult2.setPunchedOutProductHierarchy("hierarchy");
        filterResult2.setPunchedOutManufacturer("manufacturer");
        filterResult2.setPunchOutReason(CUSTOMER);

        when(distProductService.getProductForCode(PRODUCT_CODE_1)).thenReturn(product);
        when((distProductService.getPunchOutFilters(product))).thenReturn(Arrays.asList(filterResult1, filterResult2));

        boolean result = distrelecProductFacade.isProductExcluded(PRODUCT_CODE_1);
        assertThat(result, is(true));
    }

    @Test
    public void testIsProductExcludedForSiteChannel() {
        ProductModel product = new ProductModel();
        product.setCode(PRODUCT_CODE_1);

        SiteChannel channel = SiteChannel.B2C;

        PunchoutFilterResult filterResult1 = new PunchoutFilterResult();
        filterResult1.setPunchedOutProduct(product);
        filterResult1.setPunchedOutProductHierarchy("hierarchy");
        filterResult1.setPunchedOutManufacturer("manufacturer");
        filterResult1.setPunchOutReason(COUNTRY);

        PunchoutFilterResult filterResult2 = new PunchoutFilterResult();
        filterResult2.setPunchedOutProduct(product);
        filterResult2.setPunchedOutProductHierarchy("hierarchy");
        filterResult2.setPunchedOutManufacturer("manufacturer");
        filterResult2.setPunchOutReason(CUSTOMER);

        when(distProductService.getProductForCode(PRODUCT_CODE_1)).thenReturn(product);
        when((distProductService.getPunchOutFilters(product, channel))).thenReturn(Arrays.asList(filterResult1, filterResult2));

        boolean result = distrelecProductFacade.isProductExcludedForSiteChannel(PRODUCT_CODE_1, channel);
        assertThat(result, is(true));
    }

    @Test
    public void testGetMultipleProductAlternatives() {
        ProductModel product = mock(ProductModel.class);
        ProductModel product2 = mock(ProductModel.class);
        ProductData productData2 = new ProductData();
        productData2.setCode(PRODUCT_CODE_2);

        Map<String, String> codeCategoryMapping = Map.of(PRODUCT_CODE_2, WAREHOUSE_CODE);
        PIMAlternateResult pimAlternateResult = new PIMAlternateResult();
        pimAlternateResult.setAlternativeProducts(Arrays.asList(product2));
        pimAlternateResult.setCodeCategoryMapping(codeCategoryMapping);

        when(distProductService.getProductForCode(PRODUCT_CODE_1)).thenReturn(product);
        when(distProductService.getProductsReferencesForAlternative(anyList(), anyList())).thenReturn(pimAlternateResult);
        when(productReferenceTargetConverter.convert(anyObject())).thenReturn(productData2);

        List<ProductData> result = distrelecProductFacade.getMultipleProductAlternatives(PRODUCT_CODE_1);
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getAlternateCategory(), equalTo(WAREHOUSE_CODE));
    }

    @Test
    public void testGetMultipleProductAlternativesWithOffset() {
        ProductModel product = mock(ProductModel.class);
        ProductModel product2 = mock(ProductModel.class);
        ProductData productData2 = new ProductData();
        productData2.setCode(PRODUCT_CODE_2);

        Map<String, String> codeCategoryMapping = Map.of(PRODUCT_CODE_2, WAREHOUSE_CODE);
        PIMAlternateResult pimAlternateResult = new PIMAlternateResult();
        pimAlternateResult.setAlternativeProducts(Arrays.asList(product2));
        pimAlternateResult.setCodeCategoryMapping(codeCategoryMapping);

        when(distProductService.getProductForCode(PRODUCT_CODE_1)).thenReturn(product);
        when(distProductService.getProductsReferencesForAlternative(anyList(), anyList(), anyInt(), anyInt())).thenReturn(pimAlternateResult);
        when(productReferenceTargetConverter.convert(anyObject())).thenReturn(productData2);

        List<ProductData> result = distrelecProductFacade.getMultipleProductAlternatives(PRODUCT_CODE_1, 1, 1);
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getAlternateCategory(), equalTo(WAREHOUSE_CODE));
    }

    @Test
    public void testGetMinimumOrderQty() {
        ProductModel product = mock(ProductModel.class);
        DistSalesOrgProductModel salesOrgProduct = new DistSalesOrgProductModel();
        salesOrgProduct.setOrderQuantityMinimum(10L);

        when(distProductService.getProductForCode(PRODUCT_CODE_1)).thenReturn(product);
        when(distSalesOrgProductService.getCurrentSalesOrgProduct(product)).thenReturn(salesOrgProduct);

        long result = distrelecProductFacade.getMinimumOrderQty(PRODUCT_CODE_1);
        assertThat(result, equalTo(10L));
    }
    
    @Test
    public void testNotBlockedProduct() {
        // given
        ProductModel product = mock(ProductModel.class);

        // when
        when(distProductService.getProductSalesStatus(product)).thenReturn("30");
        when(erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_SALES_STATUS_BLOCKED)).thenReturn(List.of("90", "91", "92", "99"));

        boolean result = distrelecProductFacade.isBlockedProduct(product);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testBlockedProduct() {
        // given
        ProductModel product = mock(ProductModel.class);

        // when
        when(distProductService.getProductSalesStatus(product)).thenReturn("90");
        when(erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_SALES_STATUS_BLOCKED)).thenReturn(List.of("90", "91", "92", "99"));

        boolean result = distrelecProductFacade.isBlockedProduct(product);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testGetProductCodesForBlockedSalesStatusNoSessionCart() {
        // when
        when(distCartService.hasSessionCart()).thenReturn(false);

        List<String> result = distrelecProductFacade.getProductCodesForBlockedSalesStatus();

        // then
        assertThat(result, is(empty()));
    }

    @Test
    public void testGetProductCodesForBlockedSalesStatusEmptyCart() {
        // given
        CartModel cartModel = mock(CartModel.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getEntries()).thenReturn(Collections.emptyList());

        List<String> result = distrelecProductFacade.getProductCodesForBlockedSalesStatus();

        // then
        assertThat(result, is(empty()));
    }

    @Test
    public void testGetProductCodesForBlockedSalesStatusProductWithNonBlockedStatus() {
        // given
        CartModel cartModel = mock(CartModel.class);
        AbstractOrderEntryModel orderEntryModel1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel orderEntryModel2 = mock(AbstractOrderEntryModel.class);
        ProductModel productModel1 = mock(ProductModel.class);
        ProductModel productModel2 = mock(ProductModel.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getEntries()).thenReturn(List.of(orderEntryModel1, orderEntryModel2));
        when(orderEntryModel1.getProduct()).thenReturn(productModel1);
        when(orderEntryModel2.getProduct()).thenReturn(productModel2);

        List<String> result = distrelecProductFacade.getProductCodesForBlockedSalesStatus();

        // then
        assertThat(result, is(empty()));
    }

    @Test
    public void testGetProductCodesForBlockedSalesStatusProductWithBlockedStatus() {
        // given
        CartModel cartModel = mock(CartModel.class);
        AbstractOrderEntryModel orderEntryModel1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel orderEntryModel2 = mock(AbstractOrderEntryModel.class);
        ProductModel productModel1 = mock(ProductModel.class);
        ProductModel productModel2 = mock(ProductModel.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getEntries()).thenReturn(List.of(orderEntryModel1, orderEntryModel2));
        when(orderEntryModel1.getProduct()).thenReturn(productModel1);
        when(orderEntryModel2.getProduct()).thenReturn(productModel2);
        when(distProductService.getProductSalesStatus(productModel1)).thenReturn("90");
        when(distProductService.getProductSalesStatus(productModel2)).thenReturn("91");
        when(productModel1.getCode()).thenReturn("300400500");
        when(productModel2.getCode()).thenReturn("300400600");
        when(erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_SALES_STATUS_BLOCKED)).thenReturn(List.of("90", "91", "92", "99"));

        List<String> result = distrelecProductFacade.getProductCodesForBlockedSalesStatus();

        // then
        assertThat(result.get(0), equalTo("300400500"));
        assertThat(result.get(1), equalTo("300400600"));
    }

    private Date convertToDate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        return Date.from(localDate.atStartOfDay(zoneId).toInstant());
    }
}
