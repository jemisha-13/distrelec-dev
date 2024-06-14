package com.namics.distrelec.b2b.facades.order.cart.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.core.service.order.exceptions.InvalidQuantityForSapCatalogException;
import com.namics.distrelec.b2b.core.service.order.exceptions.PunchoutException;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.distrelec.b2b.facades.message.queue.data.AddToCartBulkRequestData;
import com.namics.distrelec.b2b.facades.message.queue.data.AddToCartBulkResponseData;
import com.namics.distrelec.b2b.facades.message.queue.data.BulkProductData;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.product.DistrelecOutOfStockNotificationFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_OUTOFSTOCK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistB2BCartFacadeUnitTest {
    @Mock
    private ModelService modelService;

    @Mock
    private B2BCartService cartService;

    @Mock
    private DistCartFacade distCartFacade;

    @Mock
    private CartData cartData;

    @Mock
    private DistrelecProductFacade productFacade;

    @Mock
    private DistrelecOutOfStockNotificationFacade outOfStockFacade;

    @Mock
    private ErpStatusUtil erpStatusUtil;

    @Spy
    @InjectMocks
    private DefaultDistB2BCartFacade defaultDistB2BCartFacade;

    @Test
    public void testSetVoucherCodeToRedeem() {
        // given
        String voucherCode = "VOUCHER_123";
        CartModel cartModel = mock(CartModel.class);

        // when
        when(defaultDistB2BCartFacade.getSessionCartModel()).thenReturn(cartModel);

        defaultDistB2BCartFacade.setVoucherCodeToRedeem(voucherCode);

        ArgumentCaptor<DistErpVoucherInfoModel> captor = ArgumentCaptor.forClass(DistErpVoucherInfoModel.class);
        verify(cartModel, times(1)).setErpVoucherInfo(captor.capture());
        DistErpVoucherInfoModel capturedInfo = captor.getValue();

        // then
        verify(modelService, times(1)).save(cartModel);
        assertThat(capturedInfo.getCode(), equalTo(voucherCode));
        assertThat(capturedInfo.getReturnERPCode(), equalTo("00"));
        assertThat(capturedInfo.isCalculatedInERP(), is(false));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetVoucherCodeToRedeemWithNullVoucherCode() {
        defaultDistB2BCartFacade.setVoucherCodeToRedeem(null);
    }

    @Test
    public void testGetVoucherReturnCodeFromCurrentCartValidCode() {
        // given
        DistErpVoucherInfoData voucherInfoData = mock(DistErpVoucherInfoData.class);
        String erpCode = "00";

        // when
        when(voucherInfoData.getReturnERPCode()).thenReturn(erpCode);
        when(voucherInfoData.getValid()).thenReturn(true);
        when(distCartFacade.getSessionCart()).thenReturn(cartData);
        when(cartData.getErpVoucherInfoData()).thenReturn(voucherInfoData);

        String result = defaultDistB2BCartFacade.getVoucherReturnCodeFromCurrentCart();

        // then
        assertThat(result, equalTo(erpCode));
    }

    @Test
    public void testGetVoucherReturnCodeFromCurrentCartInvalidCode() {
        // given
        DistErpVoucherInfoData voucherInfoData = mock(DistErpVoucherInfoData.class);

        // when
        when(voucherInfoData.getReturnERPCode()).thenReturn("01");
        when(distCartFacade.getSessionCart()).thenReturn(cartData);
        when(cartData.getErpVoucherInfoData()).thenReturn(voucherInfoData);

        String result = defaultDistB2BCartFacade.getVoucherReturnCodeFromCurrentCart();

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetVoucherReturnCodeFromCurrentCartInvalidFlag() {
        // given
        DistErpVoucherInfoData voucherInfoData = mock(DistErpVoucherInfoData.class);

        // when
        when(voucherInfoData.getReturnERPCode()).thenReturn("00");
        when(voucherInfoData.getValid()).thenReturn(false);
        when(distCartFacade.getSessionCart()).thenReturn(cartData);
        when(cartData.getErpVoucherInfoData()).thenReturn(voucherInfoData);

        String result = defaultDistB2BCartFacade.getVoucherReturnCodeFromCurrentCart();

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetVoucherReturnCodeFromCurrentCartNoVoucherInfoData() {
        // when
        when(distCartFacade.getSessionCart()).thenReturn(cartData);
        when(cartData.getErpVoucherInfoData()).thenReturn(null);

        String result = defaultDistB2BCartFacade.getVoucherReturnCodeFromCurrentCart();

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testIsSessionCartCalculatedWhenNoSessionCart() {
        // given
        when(defaultDistB2BCartFacade.hasSessionCart()).thenReturn(Boolean.FALSE);

        // when
        boolean result = defaultDistB2BCartFacade.isSessionCartCalculated();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsSessionCartCalculatedWhenSessionCartNotCalculated() {
        // given
        CartModel cartModel = mock(CartModel.class);

        // when
        when(defaultDistB2BCartFacade.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartModel.getCalculated()).thenReturn(Boolean.FALSE);
        when(defaultDistB2BCartFacade.getSessionCartModel()).thenReturn(cartModel);

        boolean result = defaultDistB2BCartFacade.isSessionCartCalculated();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsSessionCartCalculatedWhenSessionCartCalculated() {
        // given
        CartModel cartModel = mock(CartModel.class);

        when(defaultDistB2BCartFacade.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartModel.getCalculated()).thenReturn(Boolean.TRUE);
        when(defaultDistB2BCartFacade.getSessionCartModel()).thenReturn(cartModel);

        // when
        boolean result = defaultDistB2BCartFacade.isSessionCartCalculated();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testResetVoucherOnCurrentCartNoSessionCart() {
        // when
        when(defaultDistB2BCartFacade.hasSessionCart()).thenReturn(Boolean.FALSE);

        defaultDistB2BCartFacade.resetVoucherOnCurrentCart();

        // then
        verifyZeroInteractions(modelService);
    }

    @Test
    public void testResetVoucherOnCurrentCartNoErpVoucherInfo() {
        // given
        CartModel cartModel = mock(CartModel.class);

        // when
        when(defaultDistB2BCartFacade.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartModel.getErpVoucherInfo()).thenReturn(null);
        when(defaultDistB2BCartFacade.getSessionCartModel()).thenReturn(cartModel);

        defaultDistB2BCartFacade.resetVoucherOnCurrentCart();

        // then
        verifyZeroInteractions(modelService);
    }

    @Test
    public void testResetVoucherOnCurrentCartWithErpVoucherInfo() {
        // given
        CartModel cartModel = mock(CartModel.class);
        DistErpVoucherInfoModel mockVoucherInfo = mock(DistErpVoucherInfoModel.class);

        // when
        when(defaultDistB2BCartFacade.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartModel.getErpVoucherInfo()).thenReturn(mockVoucherInfo);
        when(defaultDistB2BCartFacade.getSessionCartModel()).thenReturn(cartModel);

        defaultDistB2BCartFacade.resetVoucherOnCurrentCart();

        // then
        verify(cartModel).setErpVoucherInfo(null);
        verify(modelService).save(cartModel);
    }

    @Test
    public void testAddToCartBulkSuccessfully() throws CommerceCartModificationException {
        // given
        String productCode = "productCode123";
        long quantity = 5L;
        List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC);
        ProductData productData = mock(ProductData.class);
        BulkProductData bulkProductData = mock(BulkProductData.class);
        List<BulkProductData> productList = new ArrayList<>();
        productList.add(bulkProductData);

        AddToCartBulkRequestData request = new AddToCartBulkRequestData();
        request.setProducts(productList);
        CartModificationData cartModificationData = mock(CartModificationData.class);
        CartModel cartModel = mock(CartModel.class);

        // when
        when(bulkProductData.getProductCode()).thenReturn(productCode);
        when(bulkProductData.getQuantity()).thenReturn(5L);
        when(defaultDistB2BCartFacade.getCartFacade()).thenReturn(distCartFacade);
        doNothing().when(distCartFacade).checkIsProductBuyable(productCode, quantity);
        when(cartModificationData.getQuantityAdded()).thenReturn(5L);
        when(distCartFacade.addToCart(any(AddToCartParams.class))).thenReturn(cartModificationData);

        AddToCartBulkResponseData result = defaultDistB2BCartFacade.addToCartBulk(request);

        // then
        assertThat(result.getCartModifications(), hasSize(1));
        assertThat(result.getCartModifications().get(0), equalTo(cartModificationData));
    }

    @Test
    public void testAddToCartBulkWithProductLowStockException() {
        // given
        String productCode = "productCode123";
        String normalizedProductCode = productCode.replace(DistConstants.Punctuation.DASH, StringUtils.EMPTY);
        long quantity = 5L;
        List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC);
        ProductData productData = mock(ProductData.class);

        AddToCartBulkRequestData request = getAddToCartBulkRequestData(productCode, quantity);

        when(defaultDistB2BCartFacade.getCartFacade()).thenReturn(distCartFacade);
        when(productFacade.getProductForCodeAndOptions(productCode, PRODUCT_OPTIONS)).thenReturn(productData);
        when(productData.getCode()).thenReturn(productCode);

        doThrow(new ProductLowStockException("ProductLowStockException")).when(distCartFacade).checkIsProductBuyable(productCode, quantity);

        // when
        AddToCartBulkResponseData result = defaultDistB2BCartFacade.addToCartBulk(request);

        // then
        assertThat(result.getPhaseOutProducts(), hasSize(1));
        assertThat(result.getPhaseOutProducts().get(0).getCode(), equalTo(normalizedProductCode));
    }

    @Test
    public void testAddToCartBulkWithInvalidQuantityForSapCatalogException() {
        // given
        String productCode = "productCode123";
        String normalizedProductCode = productCode.replace(DistConstants.Punctuation.DASH, StringUtils.EMPTY);
        long quantity = 5L;
        List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC);
        ProductData productData = mock(ProductData.class);

        AddToCartBulkRequestData request = getAddToCartBulkRequestData(productCode, quantity);

        // when
        when(defaultDistB2BCartFacade.getCartFacade()).thenReturn(distCartFacade);
        when(productFacade.getProductForCodeAndOptions(productCode, PRODUCT_OPTIONS)).thenReturn(productData);
        when(productData.getCode()).thenReturn(productCode);

        doThrow(new InvalidQuantityForSapCatalogException("message","reason")).when(distCartFacade).checkIsProductBuyable(productCode, quantity);

        AddToCartBulkResponseData result = defaultDistB2BCartFacade.addToCartBulk(request);

        // then
        assertThat(result.getErrorProducts(), hasSize(1));
        assertThat(result.getErrorProducts().get(0).getCode(), equalTo(normalizedProductCode));
    }

    @Test
    public void testAddToCartBulkWithException() throws CommerceCartModificationException {
        // given
        String productCode = "productCode123";
        String normalizedProductCode = productCode.replace(DistConstants.Punctuation.DASH, StringUtils.EMPTY);
        long quantity = 5L;
        List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC);
        ProductData productData = mock(ProductData.class);
        CartModel cartModel = mock(CartModel.class);

        AddToCartBulkRequestData request = getAddToCartBulkRequestData(productCode, quantity);

        // when
        when(defaultDistB2BCartFacade.getCartFacade()).thenReturn(distCartFacade);
        when(productFacade.getProductForCodeAndOptions(productCode, PRODUCT_OPTIONS)).thenReturn(productData);
        when(productData.getCode()).thenReturn(productCode);

        doThrow(new RuntimeException()).when(distCartFacade).checkIsProductBuyable(productCode, quantity);
        when(cartService.getSessionCart()).thenReturn(cartModel);

        AddToCartBulkResponseData result = defaultDistB2BCartFacade.addToCartBulk(request);

        // then
        assertThat(result.getErrorProducts(), hasSize(1));
        assertThat(result.getErrorProducts().get(0).getCode(), equalTo(normalizedProductCode));
        verify(distCartFacade).removeFromCart(normalizedProductCode);
    }

    @Test
    public void testAddToCartBulkWithPunchoutException() {
        // given
        String productCode = "productCode123";
        String normalizedProductCode = productCode.replace(DistConstants.Punctuation.DASH, StringUtils.EMPTY);
        long quantity = 5L;
        List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC);
        ProductData productData = mock(ProductData.class);

        AddToCartBulkRequestData request = getAddToCartBulkRequestData(productCode, quantity);

        // when
        when(defaultDistB2BCartFacade.getCartFacade()).thenReturn(distCartFacade);
        when(productFacade.getProductForCodeAndOptions(productCode, PRODUCT_OPTIONS)).thenReturn(productData);
        when(productData.getCode()).thenReturn(productCode);

        doThrow(new PunchoutException("message", Collections.emptyList())).when(distCartFacade).checkIsProductBuyable(productCode, quantity);

        AddToCartBulkResponseData result = defaultDistB2BCartFacade.addToCartBulk(request);

        // then
        assertThat(result.getPunchOutProducts(), hasSize(1));
        assertThat(result.getPunchOutProducts().get(0).getCode(), equalTo(normalizedProductCode));
    }

    @Test
    public void testAddToCartBulkWithUnknownIdentifierException() {
        // given
        String productCode = "productCode123";
        long quantity = 5L;
        List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC);
        ProductData productData = mock(ProductData.class);
        CartModel cartModel = mock(CartModel.class);

        AddToCartBulkRequestData request = getAddToCartBulkRequestData(productCode, quantity);

        // when
        when(defaultDistB2BCartFacade.getCartFacade()).thenReturn(distCartFacade);
        when(cartService.getSessionCart()).thenReturn(cartModel);

        doThrow(new UnknownIdentifierException("UnknownIdentifierException")).when(distCartFacade).checkIsProductBuyable(productCode, quantity);

        AddToCartBulkResponseData result = defaultDistB2BCartFacade.addToCartBulk(request);

        // then
        assertThat(result.getCartModifications(), is(empty()));
        assertThat(result.getPunchOutProducts(), is(empty()));
        assertThat(result.getErrorProducts(), is(empty()));
        assertThat(result.getPhaseOutProducts(), is(empty()));
    }

    @Test
    public void testAddToCartBulkWithZeroQuantityAdded() throws CommerceCartModificationException {
        // given
        String productCode = "productCode123";
        String normalizedProductCode = productCode.replace(DistConstants.Punctuation.DASH, StringUtils.EMPTY);
        long quantity = 5L;
        List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC);
        ProductData productData = mock(ProductData.class);
        CartModificationData cartModification = mock(CartModificationData.class);
        AddToCartBulkRequestData request = getAddToCartBulkRequestData(productCode, quantity);
        CartModel cartModel = mock(CartModel.class);

        // when
        when(cartModification.getQuantityAdded()).thenReturn(0L);
        when(defaultDistB2BCartFacade.getCartFacade()).thenReturn(distCartFacade);
        when(productFacade.getProductForCodeAndOptions(productCode, PRODUCT_OPTIONS)).thenReturn(productData);
        when(productData.getCode()).thenReturn(productCode);
        when(distCartFacade.addToCart(any(AddToCartParams.class))).thenReturn(cartModification);

        AddToCartBulkResponseData result = defaultDistB2BCartFacade.addToCartBulk(request);

        // then
        assertThat(result.getPhaseOutProducts(), hasSize(1));
        assertThat(result.getPhaseOutProducts().get(0).getCode(), equalTo(normalizedProductCode));
    }

    @Test
    public void testAddToCartBulkWithLessQuantityAdded() throws CommerceCartModificationException {
        // given
        String productCode = "productCode123";
        String normalizedProductCode = productCode.replace(DistConstants.Punctuation.DASH, StringUtils.EMPTY);
        long quantity = 10L;
        List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC);
        ProductData productData = mock(ProductData.class);
        CartModificationData cartModification = mock(CartModificationData.class);
        AddToCartBulkRequestData request = getAddToCartBulkRequestData(productCode, quantity);
        CartModel cartModel = mock(CartModel.class);

        // when
        when(cartModification.getQuantityAdded()).thenReturn(5L);
        when(defaultDistB2BCartFacade.getCartFacade()).thenReturn(distCartFacade);
        when(productFacade.getProductForCodeAndOptions(productCode, PRODUCT_OPTIONS)).thenReturn(productData);
        when(productData.getCode()).thenReturn(productCode);
        when(distCartFacade.addToCart(any(AddToCartParams.class))).thenReturn(cartModification);
        AddToCartBulkResponseData result = defaultDistB2BCartFacade.addToCartBulk(request);

        // then
        assertThat(result.getPhaseOutProducts(), hasSize(1));
        assertThat(result.getPhaseOutProducts().get(0).getCode(), equalTo(normalizedProductCode));
    }

    @Test
    public void testEmptyCartWhenCartAlreadyEmpty() throws CalculationException {
        // given
        when(defaultDistB2BCartFacade.getSessionCartModel()).thenReturn(null);

        // when
        CartData result = defaultDistB2BCartFacade.emptyCart();

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testEmptyCartWithItems() throws CalculationException {
        // given
        CartModel cartModel = mock(CartModel.class);
        List<AbstractOrderEntryModel> entries = Arrays.asList(mock(AbstractOrderEntryModel.class));

        // when
        when(defaultDistB2BCartFacade.getSessionCartModel()).thenReturn(cartModel);
        when(cartModel.getEntries()).thenReturn(entries);

        CartData result = defaultDistB2BCartFacade.emptyCart();

        // then
        verify(modelService).removeAll(entries);
        verify(cartModel).setErpVoucherInfo(null);
        verify(modelService).save(cartModel);
        verify(modelService).refresh(cartModel);
        verify(defaultDistB2BCartFacade).recalculateCart();
        assertThat(result, is(nullValue()));
    }

    @Test(expected = CalculationException.class)
    public void testEmptyCartThrowsCalculationException() throws CalculationException {
        // given
        CartModel cartModel = mock(CartModel.class);
        List<AbstractOrderEntryModel> entries = Arrays.asList(mock(AbstractOrderEntryModel.class));

        when(defaultDistB2BCartFacade.getSessionCartModel()).thenReturn(cartModel);
        when(cartModel.getEntries()).thenReturn(entries);
        doThrow(CalculationException.class).when(distCartFacade).recalculateCart();

        // when
        defaultDistB2BCartFacade.emptyCart();
    }

    @Test
    public void testUpdateCustomerEmailForOOSWhenEmailIsEmpty() {
        // given
        String customerEmail = "";
        List<String> articleNumbers = Arrays.asList("123", "456");

        // when
        boolean result = defaultDistB2BCartFacade.updateCustomerEmailForOOS(customerEmail, articleNumbers);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testUpdateCustomerEmailForOOSWhenArticleNumbersListIsEmpty() {
        // given
        String customerEmail = "test@distrelec.com";
        List<String> articleNumbers = new ArrayList<>();

        // when
        boolean result = defaultDistB2BCartFacade.updateCustomerEmailForOOS(customerEmail, articleNumbers);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testUpdateCustomerEmailForOOSWithDiscontinuedProduct() {
        // given
        String customerEmail = "test@distrelec.com";
        List<String> articleNumbers = Arrays.asList("123", "456");
        ProductData discontinuedProduct = mock(ProductData.class);
        OrderEntryData discontinuedOrderEntry = mock(OrderEntryData.class);
        CartData cart = mock(CartData.class);

        // when
        when(cart.getEntries()).thenReturn(Arrays.asList(discontinuedOrderEntry));
        when(defaultDistB2BCartFacade.getSessionCart()).thenReturn(cart);
        when(erpStatusUtil.getErpSalesStatusFromConfiguration(ATTRIBUTE_NO_PRODUCT_FOR_SALE_OUTOFSTOCK)).thenReturn(Collections.singletonList(ATTRIBUTE_NO_PRODUCT_FOR_SALE_OUTOFSTOCK));
        when(discontinuedProduct.getSalesStatus()).thenReturn(ATTRIBUTE_NO_PRODUCT_FOR_SALE_OUTOFSTOCK);
        when(discontinuedProduct.getCode()).thenReturn("123");
        when(discontinuedOrderEntry.getProduct()).thenReturn(discontinuedProduct);

        boolean result = defaultDistB2BCartFacade.updateCustomerEmailForOOS(customerEmail, articleNumbers);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testUpdateCustomerEmailForOOSWithNoOutOfStockItems() {
        // given
        String customerEmail = "test@distrelec.com";
        List<String> articleNumbers = Arrays.asList("123", "456");
        CartData emptyCart = mock(CartData.class);

        // when
        when(emptyCart.getEntries()).thenReturn(new ArrayList<>());
        when(defaultDistB2BCartFacade.getSessionCart()).thenReturn(emptyCart);

        boolean result = defaultDistB2BCartFacade.updateCustomerEmailForOOS(customerEmail, articleNumbers);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testUpdateCustomerEmailForOOSSuccessful() {
        // given
        String customerEmail = "test@distrelec.com";
        List<String> articleNumbers = Arrays.asList("123", "456");
        OrderEntryData orderEntry = mock(OrderEntryData.class);
        ProductData product = mock(ProductData.class);
        CartData cartWithOutOfStockItem = mock(CartData.class);

        // when
        when(cartWithOutOfStockItem.getEntries()).thenReturn(Arrays.asList(orderEntry));
        when(defaultDistB2BCartFacade.getSessionCart()).thenReturn(cartWithOutOfStockItem);
        when(product.getCode()).thenReturn("123");
        when(orderEntry.getProduct()).thenReturn(product);
        when(outOfStockFacade.saveBackOrderOutOfStock(customerEmail, Arrays.asList(orderEntry))).thenReturn(true);

        boolean result = defaultDistB2BCartFacade.updateCustomerEmailForOOS(customerEmail, articleNumbers);

        // then
        assertThat(result, is(true));
    }

    private AddToCartBulkRequestData getAddToCartBulkRequestData(String productCode, long quantity) {
        BulkProductData bulkProductData = new BulkProductData();
        bulkProductData.setProductCode(productCode);
        bulkProductData.setQuantity(quantity);

        List<BulkProductData> productList = new ArrayList<>();
        productList.add(bulkProductData);

        AddToCartBulkRequestData request = new AddToCartBulkRequestData();
        request.setProducts(productList);
        return request;
    }
}
