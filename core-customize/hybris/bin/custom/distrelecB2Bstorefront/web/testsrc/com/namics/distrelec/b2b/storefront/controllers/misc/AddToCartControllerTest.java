package com.namics.distrelec.b2b.storefront.controllers.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
public class AddToCartControllerTest {

    private static final String SAP_CATALOG_ORDER_ARTICLES = "sap.catalog.order.articles";

    private static final String EMPTY_STRING = StringUtils.EMPTY;

    private static final String DISTRELEC_NOPRODUCT_FORSALE_SALESTATUS = "distrelec.noproduct.forsale.salestatus";

    private static final String SWITZERLAND_SALES_ORG = "7310";

    private static final String PRODUCT_CODE = "11036712";

    private static final String PRODUCT_FULL_NAME = "Multipurpose Tool Multi-Purpose 420HC Knife / 420HC Serrated Knife " +
                                                    "/ Can / Bottle Opener / Can / Bottle Opener / Large Screwdriver / Medium Screwdriver / Oxygen Tank Wrench "
                                                    +
                                                    "/ Phillips Screwdriver / Saw / Small Screwdriver / Spring";

    private static final String PRODUCT = "product";

    @Mock
    private DistB2BCartFacade cartFacade;

    @Mock
    private DistrelecProductFacade productFacade;

    @Mock
    private ProductService productService;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CommerceCommonI18NService commerceCommonI18NService;

    @Mock
    private ErpStatusUtil erpStatusUtil;

    @InjectMocks
    private AddToCartController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        prepareMocks();
    }

    private void prepareMocks() throws CommerceCartModificationException {
        final ProductData productData = createProductData();
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configurationService.getConfiguration().getString(SAP_CATALOG_ORDER_ARTICLES, EMPTY_STRING)).thenReturn(EMPTY_STRING);
        when(configurationService.getConfiguration().getString(DISTRELEC_NOPRODUCT_FORSALE_SALESTATUS, EMPTY_STRING)).thenReturn(EMPTY_STRING);
        when(productFacade.getProductForCodeAndOptions(anyString(), anyListOf(ProductOption.class))).thenReturn(productData);
        when(productFacade.isProductBuyable(anyString())).thenReturn(true);
        when(productFacade.isEndOfLife(anyString())).thenReturn(false);
        when(productFacade.getProductSalesStatus(anyString())).thenReturn(SWITZERLAND_SALES_ORG);
        when(cartFacade.addToCart(anyString(), anyLong(), anyString(), anyBoolean())).thenReturn(createCartModificationData());
        when(cartFacade.getSessionCart()).thenReturn(new CartData());
        when(erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_GENERIC))
                                                                                                                                        .thenReturn(Stream.of("40,41,50,52,60,90".split(","))
                                                                                                                                                          .collect(Collectors.toList()));
    }

    @Test
    public void testProductNameIsTruncated() throws Exception {
        final MvcResult result = mockMvc.perform(post(AddToCartController.CART_ADD_REQUEST_MAPPING)
                                                                                                   .contentType("application/json")
                                                                                                   .param("productCodePost", "11036712")
                                                                                                   .param("qty", "0")
                                                                                                   .param("q", ""))
                                        .andExpect(status().isOk())
                                        .andReturn();

        final Map<String, Object> modelMap = result.getModelAndView().getModel();
        final ProductData product = (ProductData) modelMap.get(PRODUCT);
        assertNotNull(product);
        assertEquals(product.getName().length(), 180);
    }

    private CartModificationData createCartModificationData() {
        final CartModificationData cartData = new CartModificationData();
        cartData.setQuantityAdded(1L);
        cartData.setQuantity(1L);
        final OrderEntryData data = createOrderEntry();
        cartData.setEntry(data);
        return cartData;
    }

    private OrderEntryData createOrderEntry() {
        final OrderEntryData data = new OrderEntryData();
        return data;
    }

    private ProductData createProductData() {
        final ProductData productData = new ProductData();
        productData.setCode(PRODUCT_CODE);
        productData.setName(PRODUCT_FULL_NAME);
        return productData;
    }
}
