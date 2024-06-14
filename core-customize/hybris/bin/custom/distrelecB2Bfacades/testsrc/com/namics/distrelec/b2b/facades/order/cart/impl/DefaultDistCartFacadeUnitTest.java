package com.namics.distrelec.b2b.facades.order.cart.impl;

import com.namics.distrelec.b2b.core.service.cart.dao.DistCartDao;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCartService;
import com.namics.distrelec.b2b.core.service.order.exceptions.BlockedProductException;
import com.namics.distrelec.b2b.core.service.order.exceptions.InvalidQuantityForSapCatalogException;
import com.namics.distrelec.b2b.core.service.order.exceptions.PunchoutException;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.facades.order.Constants;
import com.namics.distrelec.b2b.facades.product.impl.DefaultDistrelecProductFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistCartFacadeUnitTest {

    @Mock
    private DistCommerceCartService distCommerceCartService;

    @Mock
    private DistCartService distCartService;

    @Mock
    private CartModel cartModel;

    @Mock
    private DistCartDao distCartDao;

    @Mock
    private SessionService sessionService;

    @Mock
    private DistProductService productService;

    @Mock
    private DefaultDistrelecProductFacade defaultDistrelecProductFacade;

    @Spy
    @InjectMocks
    private DefaultDistCartFacade defaultDistCartFacade;

    @Test
    public void testRecalculateCartWithSessionCart() throws CalculationException {
        // given
        CartModel cartModel = mock(CartModel.class);

        // when
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cartModel);

        defaultDistCartFacade.recalculateCart();

        // then
        verify(distCommerceCartService).recalculateCart(any(CommerceCartParameter.class));
    }

    @Test
    public void testRecalculateCartWithoutSessionCart() throws CalculationException {
        // when
        when(distCartService.hasSessionCart()).thenReturn(false);

        defaultDistCartFacade.recalculateCart();

        // then
        verify(distCommerceCartService, never()).recalculateCart(any(CommerceCartParameter.class));
    }

    @Test
    public void testLoadCartCartExists() {
        // given
        String cartCode = "validCartCode";
        CartModel cartModel = mock(CartModel.class);
        List<CartModel> cartList = Collections.singletonList(cartModel);

        // when
        when(distCartDao.find(Collections.singletonMap(CartModel.CODE, cartCode))).thenReturn(cartList);

        defaultDistCartFacade.loadCart(cartCode);

        // then
        verify(distCartService).setSessionCart(cartModel);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void testLoadCartCartDoesNotExist() {
        // given
        String cartCode = "invalidCartCode";

        // when
        when(distCartDao.find(Collections.singletonMap(CartModel.CODE, cartCode))).thenReturn(Collections.emptyList());

        defaultDistCartFacade.loadCart(cartCode);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void testLoadCartMultipleCartsFound() {
        // given
        String cartCode = "duplicateCartCode";
        CartModel cartModel1 = mock(CartModel.class);
        CartModel cartModel2 = mock(CartModel.class);
        List<CartModel> cartList = Arrays.asList(cartModel1, cartModel2);

        // when
        when(distCartDao.find(Collections.singletonMap(CartModel.CODE, cartCode))).thenReturn(cartList);

        defaultDistCartFacade.loadCart(cartCode);
    }

    @Test
    public void testEmptySessionCartWhenCartExists() {
        // when
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cartModel);

        defaultDistCartFacade.emptySessionCart();

        // then
        verify(distCartService).removeBackupCart();
        verify(sessionService).setAttribute(eq(Constants.DIST_BACKUP_CART), any(CartModel.class));
        verify(distCartService).removeSessionCart(false);
        verify(distCartService, times(2)).getSessionCart();
    }

    @Test
    public void testEmptySessionCartWhenCartDoesNotExist() {
        // when
        when(distCartService.hasSessionCart()).thenReturn(false);

        defaultDistCartFacade.emptySessionCart();

        // then
        verify(distCartService, never()).removeBackupCart();
        verify(sessionService, never()).setAttribute(eq(Constants.DIST_BACKUP_CART), any(CartModel.class));
        verify(distCartService, never()).removeSessionCart(false);
        verify(distCartService, never()).getSessionCart();
    }

    @Test
    public void testRestoreSessionCartWhenBackupCartExists() {
        // when
        when(sessionService.getAttribute(Constants.DIST_BACKUP_CART)).thenReturn(cartModel);

        defaultDistCartFacade.restoreSessionCart();

        // then
        verify(distCartService).setSessionCart(cartModel);
        verify(sessionService).removeAttribute(Constants.DIST_BACKUP_CART);
    }

    @Test
    public void testRestoreSessionCartWhenBackupCartDoesNotExist() {
        // when
        when(sessionService.getAttribute(Constants.DIST_BACKUP_CART)).thenReturn(null);

        defaultDistCartFacade.restoreSessionCart();

        // then
        verify(distCartService, never()).setSessionCart(any(CartModel.class));
        verify(sessionService, never()).removeAttribute(Constants.DIST_BACKUP_CART);
    }

    @Test
    public void testIsProductInCartWhenProductIsInCart() {
        // given
        String productCode = "product123";

        // when
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cartModel);
        when(distCartDao.productsInCart(cartModel, productCode)).thenReturn(Collections.singletonList(productCode));

        // then
        assertThat(defaultDistCartFacade.isProductInCart(productCode), is(true));
    }

    @Test
    public void testIsProductInCartWhenProductIsNotInCart() {
        // given
        String productCode = "product123";

        // when
        when(distCartService.hasSessionCart()).thenReturn(true);
        when(distCartService.getSessionCart()).thenReturn(cartModel);
        when(distCartDao.productsInCart(cartModel, productCode)).thenReturn(Collections.emptyList());

        // then
        assertThat(defaultDistCartFacade.isProductInCart(productCode), is(false));
    }

    @Test
    public void testIsProductInCartWhenNoSessionCart() {
        // given
        String productCode = "product123";

        // when
        when(distCartService.hasSessionCart()).thenReturn(false);

        // then
        assertThat(defaultDistCartFacade.isProductInCart(productCode), is(false));
    }

    @Test
    public void testGetOrderEntryWhenEntryExists() {
        // given
        long entryNumber = 1L;
        AbstractOrderEntryModel orderEntry = mock(AbstractOrderEntryModel.class);

        // when
        when(distCartService.getSessionCart()).thenReturn(cartModel);
        when(defaultDistCartFacade.getCommerceCartService()).thenReturn(distCommerceCartService);
        when(distCommerceCartService.getOrderEntry(entryNumber, cartModel)).thenReturn(orderEntry);

        AbstractOrderEntryModel result = defaultDistCartFacade.getOrderEntry(entryNumber);

        // then
        assertThat(result, equalTo(orderEntry));
    }

    @Test
    public void testGetOrderEntryWhenEntryDoesNotExist() {
        // given
        long entryNumber = 1L;

        // when
        when(distCartService.getSessionCart()).thenReturn(cartModel);
        when(defaultDistCartFacade.getCommerceCartService()).thenReturn(distCommerceCartService);
        when(distCommerceCartService.getOrderEntry(entryNumber, cartModel)).thenReturn(null);

        AbstractOrderEntryModel result = defaultDistCartFacade.getOrderEntry(entryNumber);

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void shouldReturnNullWhenCartNotPresent() throws CalculationException {
        // when
        when(distCartService.getSessionCart()).thenReturn(null);

        CartData result = defaultDistCartFacade.emptyCart();

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void shouldPrependZerosWhenLoginIdIsShorterThanMaxLength() {
        // given
        String loginID = "12345";
        int maxLength = 10;

        // when
        String result = defaultDistCartFacade.formatErpID(loginID, maxLength);

        // then
        assertThat(result, equalTo("0000012345"));
    }

    @Test
    public void shouldReturnSameStringWhenLoginIdIsEqualToMaxLength() {
        // given
        String loginID = "1234567890";
        int maxLength = 10;

        // when
        String result = defaultDistCartFacade.formatErpID(loginID, maxLength);

        // then
        assertThat(result, equalTo("1234567890"));
    }

    @Test
    public void shouldReturnSameStringWhenLoginIdIsLongerThanMaxLength() {
        // given
        String loginID = "1234567890123";
        int maxLength = 10;

        // when
        String result = defaultDistCartFacade.formatErpID(loginID, maxLength);

        // then
        assertThat(result, equalTo("1234567890123"));
    }

    @Test(expected = InvalidQuantityForSapCatalogException.class)
    public void testCheckIsProductBuyableInvalidSAPCatalogQuantity() {
        // given
        ProductModel productModel = mock(ProductModel.class);

        // when
        when(productService.getProductForCode("validCode")).thenReturn(productModel);
        when(productService.isSAPCatalogProduct(productModel)).thenReturn(true);

        defaultDistCartFacade.checkIsProductBuyable("validCode", 2);
    }

    @Test(expected = PunchoutException.class)
    public void testCheckIsProductBuyableNotBuyableWithPunchoutFilters() {
        // given
        ProductModel product = mock(ProductModel.class);
        PunchoutFilterResult punchoutFilterResult = mock(PunchoutFilterResult.class);

        // when
        when(productService.getProductForCode("validCode")).thenReturn(product);
        when(productService.isProductBuyable(product)).thenReturn(false);
        when(productService.getPunchOutFilters(product)).thenReturn(Collections.singletonList(punchoutFilterResult));

        defaultDistCartFacade.checkIsProductBuyable("validCode", 1);
    }

    @Test(expected = ProductLowStockException.class)
    public void testCheckIsProductBuyableNotBuyableWithoutPunchoutFilters() {
        // given
        ProductModel product = mock(ProductModel.class);

        // when
        when(productService.getProductForCode("validCode")).thenReturn(product);
        when(productService.isProductBuyable(product)).thenReturn(false);
        when(productService.getPunchOutFilters(product)).thenReturn(Collections.emptyList());

        defaultDistCartFacade.checkIsProductBuyable("validCode", 1);
    }

    @Test(expected = ProductLowStockException.class)
    public void testCheckIsProductBuyableEndOfLife() {
        // given
        ProductModel product = mock(ProductModel.class);

        // when
        when(productService.getProductForCode("validCode")).thenReturn(product);
        when(productService.isEndOfLife(product)).thenReturn(true);
        when(productService.isProductBuyable(product)).thenReturn(true);

        defaultDistCartFacade.checkIsProductBuyable("validCode", 1);
    }

    @Test
    public void testCheckIsProductBuyableNoException() {
        // given
        ProductModel product = mock(ProductModel.class);

        // when
        when(productService.getProductForCode("validCode")).thenReturn(product);
        when(productService.isSAPCatalogProduct(product)).thenReturn(false);
        when(productService.isProductBuyable(product)).thenReturn(true);
        when(productService.isEndOfLife(product)).thenReturn(false);

        defaultDistCartFacade.checkIsProductBuyable("validCode", 1);
    }

    @Test
    public void testRemoveBlockedProductsFromCartIfNoProductsForRemove() {
        // when
        when(defaultDistrelecProductFacade.getProductCodesForBlockedSalesStatus()).thenReturn(Collections.emptyList());

        List<CartModificationData> result = defaultDistCartFacade.removeBlockedProductsFromCart();

        // then
        assertThat(result, hasSize(0));
    }

    @Test
    public void testRemoveBlockedProductsFromCart() throws CommerceCartModificationException {
        // when
        when(defaultDistrelecProductFacade.getProductCodesForBlockedSalesStatus()).thenReturn(List.of("300400500", "300400501"));
        doReturn(mock(CartModificationData.class)).when(defaultDistCartFacade).updateCartEntry("300400500", 0);
        doReturn(mock(CartModificationData.class)).when(defaultDistCartFacade).updateCartEntry("300400501", 0);

        List<CartModificationData> result = defaultDistCartFacade.removeBlockedProductsFromCart();

        // then
        assertThat(result, hasSize(2));
    }

    @Test(expected = BlockedProductException.class)
    public void testCheckIsProductBuyable() {
        // given
        String productCode = "300400500";
        ProductModel productModel = mock(ProductModel.class);

        // when
        when(productService.getProductForCode(productCode)).thenReturn(productModel);
        when(defaultDistrelecProductFacade.isBlockedProduct(productModel)).thenReturn(true);

        defaultDistCartFacade.checkIsProductBuyable(productCode, 0);
    }
}
