package com.namics.distrelec.b2b.storefront.pages.checkout;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.distrelec.b2b.facades.backorder.BackOrderFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.order.data.DistAvailabilityData;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest;
import com.namics.distrelec.b2b.storefront.controllers.pages.checkout.DistCheckoutBackOrderDetailsController;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService.SessionAttributeLoader;

public class DistCheckoutBackOrderDetailsControllerTest extends AbstractPageControllerTest<DistCheckoutBackOrderDetailsController> {

    private final String BACKORDER_URL = "/checkout/backorderDetails";

    @Mock
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Mock
    private BackOrderFacade defaultBackOrderFacade;

    @Mock
    private DistCheckoutFacade checkoutFacade;

    @Mock
    private ErpStatusUtil erpStatusUtil;

    @InjectMocks
    private DistCheckoutBackOrderDetailsController controller;

    private MockMvc mockMvc;

    private MockHttpSession session;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        super.setUp();
        session = new MockHttpSession();
    }

    @Override
    protected DistCheckoutBackOrderDetailsController getController() {
        return controller;
    }

    @Test
    public final void testBasePath() throws Exception {
        when(checkoutFacade.getCheckoutCart()).thenReturn(createCheckoutCart());

        ContentPageModel contentPageModel = mock(ContentPageModel.class);
        when(contentPageModel.getUid()).thenReturn("UID");
        when(contentPageModel.getTitle()).thenReturn("Title");

        when(distWebtrekkFacade.getWtBasicParameters(any(ContentPageModel.class))).thenReturn(Collections.emptyList());
        when(distWebtrekkFacade.getWtAdvancedParameters(any(ContentPageModel.class))).thenReturn(Collections.emptyList());

        when(pageTitleResolver.resolveContentPageTitle(anyString())).thenReturn("PAGE TITLE");

        when(userService.getCurrentUser()).thenReturn(new UserModel());
        when(userService.getUserForUID(DistConstants.User.B2BEESHOPGROUP_UID)).thenReturn(new UserModel());

        when(configuration.getBoolean(DistConfigConstants.FEATURE_DATALAYER, true)).thenReturn(Boolean.FALSE);
        when(configurationService.getConfiguration()).thenReturn(configuration);

        SessionAttributeLoader<Boolean> sessionAttributeLoader = mock(SessionAttributeLoader.class);

        when(sessionAttributeLoader.load()).thenReturn(false);
        when(sessionService.getOrLoadAttribute("isEShopGroup", sessionAttributeLoader)).thenReturn(false);

        doNothing().when(distWebtrekkFacade).addTeaserTrackingId(any(Model.class), anyString());
        when(distCmsPageService.getPageForLabelOrId("checkoutBackOrderDetailsPage")).thenReturn(new ContentPageModel());

        Configuration configuration = mock(Configuration.class);
        when(configuration.getString("erp.new.sales.status.enabled")).thenReturn("true");

        // when(configurationService.getConfiguration().getString("erp.new.sales.status.enabled")).thenReturn("true");
        // when(erpStatusUtil.isNewErpSalesStatusEnabled("true")).thenReturn(true);
        // when(erpStatusUtil.getErpStatusFromConfig("erp.new.sales.status.backorder")).thenReturn(Collections.singletonList("12"));

        final List<ProductData> alternativeProduct = Collections.singletonList(createProductData("ALT-001", "30"));
        when(defaultBackOrderFacade.getBackOrderAlternativeProducts(anyString())).thenReturn(alternativeProduct);

        final ProductData familyProduct = createProductData("ALT-001", "30");
        when(defaultBackOrderFacade.getProductFamilyProducts(anyString())).thenReturn(createListOfProductData(familyProduct));

        final ProductAvailabilityData availabilityData = createProductAvailabilityData("ALT-001", 10);
        when(distrelecProductFacade.getAvailability(anyMapOf(String.class, Integer.class),
                                                    eq(Boolean.FALSE))).thenReturn(Collections.singletonList(availabilityData));

        final MvcResult mvcResult = mockMvc.perform(get(BACKORDER_URL)
                                                                      .flashAttrs(createModelAttributes())
                                                                      .session(session))
                                           .andExpect(status().isOk())
                                           .andReturn();
    }

    private Map<String, Object> createModelAttributes() {
        final Map<String, Object> modelAttributes = new HashMap<>();
        modelAttributes.put("isEShopGroup", false);
        modelAttributes.put("isRequestedDeliveryDateEnabled", false);
        modelAttributes.put("minRequestedDeliveryDate", calculateDate(3));
        modelAttributes.put("maxRequestedDeliveryDate", calculateDate(15));
        return modelAttributes;
    }

    private List<DistAvailabilityData> createAvailabilities() {
        final DistAvailabilityData data = new DistAvailabilityData();
        data.setEstimatedDate(calculateDate(5));
        return new ArrayList<>(Collections.singletonList(data));
    }

    private ProductAvailabilityData createProductAvailabilityData(final String productCode, final int totalStockLevel) {
        final ProductAvailabilityData data = new ProductAvailabilityData();
        data.setStockLevelTotal(totalStockLevel);
        data.setProductCode(productCode);
        return data;
    }

    private ProductData createProductData(final String code, final String salesStatus) {
        final ProductData data = new ProductData();
        data.setCode(code);
        data.setSalesStatus(salesStatus);
        return data;
    }

    private List<ProductData> createListOfProductData(final ProductData... objects) {
        return Arrays.asList(objects);
    }

    private CartData createCheckoutCart() {
        final CartData cartData = new CartData();
        cartData.setEntries(createCartEntries(false));
        return cartData;
    }

    private List<OrderEntryData> createCartEntries(final boolean profitable) {
        final OrderEntryData data = new OrderEntryData();
        data.setProduct(createProduct());
        data.setBackOrderProfitable(profitable);
        data.setBackOrderedQuantity(0L);
        data.setQuantity(5L);
        data.setAvailabilities(createAvailabilities());
        data.setEntryNumber(1);
        return new ArrayList<>(Collections.singletonList(data));
    }

    private ProductData createProduct() {
        ProductData data = new ProductData();
        data.setCode("12345678");
        return data;
    }

    private Date calculateDate(int amountOfDays) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, amountOfDays);
        return calendar.getTime();
    }
}
