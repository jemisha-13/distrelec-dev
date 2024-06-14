package com.namics.distrelec.b2b.storefront.controllers.cms;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.namics.distrelec.b2b.core.service.product.DistProductService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import com.namics.distrelec.b2b.core.model.cms2.components.DistFeaturedProductsComponentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * Test for {@link DistFeaturedProductsComponentController}
 */
@UnitTest
public class DistFeaturedProductsComponentControllerTest {

    @InjectMocks
    private DistFeaturedProductsComponentController controller;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private Model model;

    @Mock
    private DistFeaturedProductsComponentModel componentModel;

    @Mock
    private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;

    @Mock
    private DistProductService productService;

    private Map<String, String> testDataAttributes;
    private String testLinkText = "TestLink";
    private List<ProductModel> testProductList;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        testDataAttributes = new HashMap<>();
        testDataAttributes.put("key1", "value1");
        testDataAttributes.put("key2", "value2");
        testDataAttributes.put("key3", "value3");

        testLinkText = "TestLink";

        testProductList = new ArrayList<>();
        testProductList.add(new ProductModel());
        testProductList.add(new ProductModel());
        testProductList.add(new ProductModel());
    }

    @Test
    public void testFillModel() {
        when(componentModel.getDataAttributes())
                .thenReturn(testDataAttributes);
        when(componentModel.getLinkText())
                .thenReturn(testLinkText);
        when(componentModel.getProducts())
                .thenReturn(testProductList);
        when(productService.isProductBuyable(anyObject()))
                .thenReturn(Boolean.TRUE);

        controller.fillModel(httpServletRequest, model, componentModel);

        verify(productConfiguredPopulator, times(3)).populate(anyObject(), anyObject(), anyObject());

        verify(model, times(1)).addAttribute("dataAttributes", testDataAttributes);
        verify(model, times(1)).addAttribute("linkText", testLinkText);
        verify(model, times(1)).addAttribute(same("products"), anyObject());
        verify(model, times(1)).addAttribute(same("data2modelMap"), anyObject());
    }
}
