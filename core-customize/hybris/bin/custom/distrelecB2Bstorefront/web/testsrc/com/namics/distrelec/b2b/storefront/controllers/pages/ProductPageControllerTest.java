/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.message.queue.cassandra.DistRelatedDataFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ProductBreadcrumbBuilder;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * {@code ErrorPageControllerTest}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.2
 */
@UnitTest
public class ProductPageControllerTest extends AbstractPageControllerTest<ProductPageController> {

    private static final String P_FOUND = "14022002";

    private static final String P_FOUND_URL = "/en/test-lead-mm-red-100-cm-75-mm-cat-staeubli-electrical-connectors-sml-100cm-red/p/" + P_FOUND;

    private static final String P_FOUND_SALES_STATUS_CODE = "30";

    @Autowired
    private WebApplicationContext context;

    @InjectMocks
    private ProductPageController productPageController;

    @Mock
    private DistSalesStatusModel productSalesStatusModel;

    @Mock
    private ProductModel productModel;

    @Mock
    private ProductData productData;

    @Mock
    private CartModel cartModel;

    @Mock
    private ProductPageModel pageModel;

    @Mock
    private CMSSiteModel cmsSiteModel;

    @Mock
    private SalesOrgData salesOrgData;

    @Mock
    private CustomerData distB2BCustomerData;

    @Mock
    private BaseStoreModel baseStoreModel;

    @Mock
    private DistUrlResolver<ProductModel> productModelUrlResolver;

    @Mock
    private DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver;

    @Mock
    private DistRelatedDataFacade distRelatedDataFacade;

    @Mock
    private CommerceProductService commerceProductService;

    @Mock
    private ProductBreadcrumbBuilder productBreadcrumbBuilder;

    //

    /**
     * @throws java.lang.Exception
     */
    @Before
    @Override
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productPageController).build();

        super.setUp();

        Mockito.when(productModel.getCatPlusSupplierAID()).thenReturn(null);
        Mockito.when(productModel.getCode()).thenReturn(P_FOUND);

        Mockito.when(distRelatedDataFacade.findProductRelatedData(anyString())).thenReturn(null);
        Mockito.when(commerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(any(ProductModel.class)))
               .thenReturn(Collections.EMPTY_LIST);
        Mockito.when(productModelUrlResolver.resolve(productModel)).thenReturn(P_FOUND_URL);
        Mockito.when(productSalesStatusModel.isVisibleInShop()).thenReturn(Boolean.TRUE);
        Mockito.when(productSalesStatusModel.getCode()).thenReturn(P_FOUND_SALES_STATUS_CODE);
        Mockito.when(productBreadcrumbBuilder.getBreadcrumbs(any(ProductModel.class), anyString(), any())).thenReturn(Collections.EMPTY_LIST);

        Mockito.when(productService.getProductForCode(P_FOUND)).thenReturn(productModel);
        Mockito.when(distrelecProductFacade.getProductSalesStatusModel(P_FOUND)).thenReturn(productSalesStatusModel);
        Mockito.when(distrelecProductFacade.getProductSalesStatus(P_FOUND)).thenReturn(P_FOUND_SALES_STATUS_CODE);
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest#setUp(com.namics.distrelec.b2b.storefront.
     * controllers.pages.AbstractPageController)
     */
    @Override
    public void setUp(final ProductPageController controller) throws Exception {
        super.setUp(controller);
        controller.setProductModelUrlResolver(productModelUrlResolver);
        controller.setCatalogPlusProductModelUrlResolver(catalogPlusProductModelUrlResolver);
        controller.setCartFacade(cartFacade);
        controller.setDistRelatedDataFacade(distRelatedDataFacade);
        controller.setCommerceProductService(commerceProductService);
        controller.setProductBreadcrumbBuilder(productBreadcrumbBuilder);
    }

    /**
     * Test method for
     * {@link com.namics.distrelec.b2b.storefront.controllers.pages.ErrorPageController#notFound(org.springframework.ui.Model, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     * 
     * @throws Exception
     */
    @Test
    public final void testGet() throws Exception {
        mockMvc.perform(get(P_FOUND_URL)).andExpect(status().isOk());
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest#getController()
     */
    @Override
    protected ProductPageController getController() {
        return productPageController;
    }
}
