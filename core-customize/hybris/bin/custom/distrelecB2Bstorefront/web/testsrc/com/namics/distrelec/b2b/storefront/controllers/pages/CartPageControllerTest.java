package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.facades.adobe.datalayer.impl.DefaultDistDigitalDatalayerFacade;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;
import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@UnitTest
public class CartPageControllerTest extends AbstractPageControllerTest<CartPageController> {

    private static final String PRODUCT_CODE = "11221122";

    private Model model;

    private MockHttpServletRequest mockHttpServletRequest;

    @InjectMocks
    private CartPageController cartPageController;

    @Mock
    private DistB2BCartFacade mockDistB2BCartFacade;

    @Mock
    private ContentPageModel mockContentPageModel;

    @Mock
    private SimpleBreadcrumbBuilder mockSimpleBreadcrumbBuilder;

    @Mock
    private DefaultDistDigitalDatalayerFacade mockDefaultDistDigitalDatalayerFacade;

    @Mock
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Mock
    private DistUserDashboardFacade distUserDashboardFacade;

    @Mock
    private DistCheckoutFacade distCheckoutFacade;


    @Override
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setUp();
        setUp(getController());

        when(mockContentPageModel.getKeywords()).thenReturn("KEYWORD");
        when(mockContentPageModel.getDescription()).thenReturn("DESCRIPTION");
        when(mockContentPageModel.getTitle()).thenReturn("CartPage");
        when(distCmsPageService.getPageForLabelOrId(anyString(), any())).thenReturn(mockContentPageModel);
        when(mockSimpleBreadcrumbBuilder.getBreadcrumbs(anyString(), anyString())).thenReturn(Collections.emptyList());

        model = new ExtendedModelMap();
        mockHttpServletRequest = new MockHttpServletRequest();
    }

    @Override
    public void setUp(final CartPageController cartPageController) throws Exception {
        super.setUp(cartPageController);
        cartPageController.setCartFacade(mockDistB2BCartFacade);
        cartPageController.setSimpleBreadcrumbBuilder(mockSimpleBreadcrumbBuilder);
        cartPageController.setDistDigitalDatalayerFacade(mockDefaultDistDigitalDatalayerFacade);
    }

    @After
    public void tearDown() {
        model.asMap().clear();
        model = null;
        mockHttpServletRequest.removeAllParameters();
        mockHttpServletRequest = null;
    }

    @Override
    protected CartPageController getController() {
        return cartPageController;
    }

    @Test
    public void testRedeemVoucherEmpty() throws CMSItemNotFoundException, InvalidCartException {
        final List<OrderEntryData> orderEntries = createOrderEntries(createOrderEntryData(1l, 1l, 1l));
        final CartData cartData = createCartData(orderEntries);
        when(mockDistB2BCartFacade.getSessionCart()).thenReturn(cartData);
        when(mockDistB2BCartFacade.getSessionCartIfPresent()).thenReturn(cartData);

        final String resultUrl = cartPageController.redeemVoucherEmpty(model, mockHttpServletRequest);

        assertEquals(ControllerConstants.Views.Pages.Cart.CartPage, resultUrl);

        boolean voucherEmpty = (boolean) model.asMap().get("isVoucherEmpty");
        assertEquals(Boolean.TRUE, voucherEmpty);

        String voucherErrorKey = (String) model.asMap().get("voucherErrorMessageKey");
        assertEquals("checkoutvoucherbox.voucherError.error98", voucherErrorKey);
    }

    @Test
    public void testResetVoucher() throws CMSItemNotFoundException, InvalidCartException {
        final List<OrderEntryData> orderEntries = createOrderEntries(createOrderEntryData(1l, 1l, 1l));
        final CartData cartData = createCartData(orderEntries);

        when(mockDistB2BCartFacade.getSessionCart()).thenReturn(cartData);
        when(mockDistB2BCartFacade.getSessionCartIfPresent()).thenReturn(cartData);

        final String resultUrl = cartPageController.resetVoucher(model, mockHttpServletRequest);
        assertEquals(ControllerConstants.Views.Pages.Cart.CartPage, resultUrl);
    }

    @Test
    public void testRedeemVoucher() throws CMSItemNotFoundException, InvalidCartException {
        final String voucherCode = "knRWYOeVO8";
        final List<OrderEntryData> orderEntries = createOrderEntries(createOrderEntryData(1l, 1l, 1l));
        final CartData cartData = createCartData(orderEntries);

        final DistErpVoucherInfoData erpVoucherInfoData = new DistErpVoucherInfoData();
        erpVoucherInfoData.setReturnERPCode("00");
        erpVoucherInfoData.setCode("12345");
        erpVoucherInfoData.setValid(true);
        cartData.setErpVoucherInfoData(erpVoucherInfoData);

        when(mockDistB2BCartFacade.getSessionCart()).thenReturn(cartData);
        when(mockDistB2BCartFacade.getSessionCartIfPresent()).thenReturn(cartData);

        final String resultUrl = cartPageController.redeemVoucher(voucherCode, model, mockHttpServletRequest);

        assertEquals(ControllerConstants.Views.Pages.Cart.CartPage, resultUrl);
    }

    @Test
    @Ignore
    public void testMoqMultipleIsEqualToQuantity() throws CMSItemNotFoundException {
        final List<OrderEntryData> orderEntries = createOrderEntries(createOrderEntryData(10l, 10l, 2l));
        final CartData cartData = createCartData(orderEntries);
        cartData.setEntries(orderEntries);

        when(cartService.getSessionCart()).thenReturn(createCartModel());
        when(mockDistB2BCartFacade.getSessionCart()).thenReturn(cartData);
        when(mockDistB2BCartFacade.getSessionCartIfPresent()).thenReturn(cartData);

        long expectedValue = 10l;
        final Map<String, Object> attributeModel = model.asMap();
        final List<String> products = (List<String>) attributeModel.get(WebConstants.HAS_MOQ_UPDATED_SINCE_LAST_CART_LOAD);

        cartPageController.showCart(model, mockHttpServletRequest);

        assertFalse(products.contains(cartData.getCode()));
        assertEquals(expectedValue, orderEntries.get(0).getQuantity().longValue());
    }

    @Test
    @Ignore
    public void testMoqRoundedDown() throws CMSItemNotFoundException, CommerceCartModificationException {
        final List<OrderEntryData> orderEntries = createOrderEntries(createOrderEntryData(15l, 10l, 2l));
        final CartData cartData = createCartData(orderEntries);
        cartData.setEntries(orderEntries);

        when(cartService.getSessionCart()).thenReturn(createCartModel());
        when(mockDistB2BCartFacade.getSessionCart()).thenReturn(cartData);
        when(mockDistB2BCartFacade.getSessionCartIfPresent()).thenReturn(cartData);
        // getCartFacade().updateCartEntry(orderEntry.getEntryNumber(), updatedMoqQuantity);
        final CartModificationData modificationData = new CartModificationData();
        modificationData.setQuantity(14l);
        when(cartFacade.updateCartEntry(Mockito.anyInt(), Mockito.anyLong())).thenReturn(modificationData);

        cartPageController.showCart(model, mockHttpServletRequest);

        long expectedValue = 14l;
        final Map<String, Object> attributeModel = model.asMap();
        final List<String> products = (List<String>) attributeModel.get(WebConstants.HAS_MOQ_UPDATED_SINCE_LAST_CART_LOAD);
        assertTrue(products.contains(cartData.getCode()));
        assertEquals(expectedValue, modificationData.getQuantity());
    }

    @Test
    @Ignore
    public void testMoqRoundedUp() throws CMSItemNotFoundException, CommerceCartModificationException {
        final List<OrderEntryData> orderEntries = createOrderEntries(createOrderEntryData(153l, 300l, 15l));
        final CartData cartData = createCartData(orderEntries);
        cartData.setEntries(orderEntries);

        when(cartService.getSessionCart()).thenReturn(createCartModel());
        when(mockDistB2BCartFacade.getSessionCart()).thenReturn(cartData);
        when(mockDistB2BCartFacade.getSessionCartIfPresent()).thenReturn(cartData);

        final CartModificationData modificationData = new CartModificationData();
        modificationData.setQuantity(300l);
        when(cartFacade.updateCartEntry(Mockito.anyInt(), Mockito.anyLong())).thenReturn(modificationData);

        cartPageController.showCart(model, mockHttpServletRequest);

        long expectedValue = 300l;
        final Map<String, Object> attributeModel = model.asMap();
        final List<String> products = (List<String>) attributeModel.get(WebConstants.HAS_MOQ_UPDATED_SINCE_LAST_CART_LOAD);
        assertFalse(products.contains(cartData.getCode()));
        assertEquals(expectedValue, modificationData.getQuantity());
    }

    private CartModel createCartModel() {
        CartModel model = new CartModel();
        model.setEntries(createAbstractOrderEntryModels(createAbstractOrderEntryModel()));
        return model;
    }

    private CartData createCartData(final List<OrderEntryData> entries) {
        final CartData data = new CartData();
        data.setEntries(CollectionUtils.isEmpty(entries) ? new ArrayList<>() : entries);
        return data;
    }

    private List<OrderEntryData> createOrderEntries(final OrderEntryData... entries) {
        return new ArrayList<>(Arrays.asList(entries));
    }

    private OrderEntryData createOrderEntryData(final long quantity, final long minimum, final long step) {
        final OrderEntryData entryData = new OrderEntryData();
        entryData.setQuantity(quantity);
        entryData.setProduct(createProductData(minimum, step));
        return entryData;
    }

    private ProductData createProductData(final long minimum, final long step) {
        final ProductData productData = new ProductData();
        productData.setCode(PRODUCT_CODE);
        productData.setOrderQuantityMinimum(minimum);
        productData.setOrderQuantityStep(step);
        return productData;
    }

    private List<AbstractOrderEntryModel> createAbstractOrderEntryModels(final AbstractOrderEntryModel... models) {
        return new ArrayList<>(Arrays.asList(models));
    }

    private AbstractOrderEntryModel createAbstractOrderEntryModel() {
        return new AbstractOrderEntryModel();
    }
}
