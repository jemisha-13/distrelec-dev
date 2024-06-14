package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MvcResult;

import com.namics.distrelec.b2b.facades.category.DistCategoryIndexFacade;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.InvoiceHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;

@UnitTest
public class HomePageControllerTest extends AbstractPageControllerTest<HomePageController> {

    @InjectMocks
    protected HomePageController controller;

    @Mock
    private InvoiceHistoryFacade invoiceHistoryFacade;

    @Mock
    private OrderHistoryFacade orderHistoryFacade;

    @Mock
    private DistCategoryIndexFacade distCategoryIndexFacade;

    @Mock
    private MediaService mediaService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Mock
    private DistUserDashboardFacade distUserDashboardFacade;

    @Mock
    private DistCheckoutFacade distCheckoutFacade;

    @Override
    protected HomePageController getController() {
        return controller;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = createMockMvc();
        super.setUp();
        prepareMocks();
    }

    private void prepareMocks() throws Exception {
        CustomerData customerData = new CustomerData();
        B2BUnitData customerUnit = new B2BUnitData();

        customerData.setUnit(customerUnit);

        when(sessionService.getOrLoadAttribute(eq("currentUserData"), any()))
                                                                             .thenReturn(customerData);

        ContentPageModel cmsPage = mock(ContentPageModel.class);
        PageTemplateModel pageTemplate = mock(PageTemplateModel.class);

        when(cmsPage.getMasterTemplate())
                                         .thenReturn(pageTemplate);

        when(distCmsPageService.getFrontendTemplateName(any()))
                                                               .thenReturn("homepage");

        when(cmsPage.getTitle())
                                .thenReturn("Homepage");

        when(pageTitleResolver.resolveHomePageTitle(any()))
                                                           .thenReturn("Homepage");

        when(distCmsPageService.getHomepage())
                                              .thenReturn(cmsPage);

        when(distCmsPageService.getPageForLabelOrId(any(), any()))
                                                                  .thenReturn(cmsPage);

        MediaModel mediaModel = mock(MediaModel.class);

        when(mediaModel.getURL2())
                                  .thenReturn("media");

        when(mediaService.getMedia(any(), any()))
                                                 .thenReturn(mediaModel);

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("pageTitle", "Homepage");
        modelMap.put("metaDescription", "Meta description");
    }

    @Test
    public void testGetPageType() {
        assertThat(controller.getPageType())
                                            .withFailMessage("Wrong page type returned")
                                            .isEqualTo(ThirdPartyConstants.PageType.HOME);
    }

    @Test
    public void testGetPage() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLogoutParameter() throws Exception {
        MvcResult result = mockMvc.perform(get("/?logout=true&active=false"))
                                  .andExpect(status().is3xxRedirection())
                                  .andReturn();

        assertThat(result.getFlashMap().get(GlobalMessages.WARN_MESSAGES_HOLDER))
                                                                                 .withFailMessage("Warning message is not set")
                                                                                 .isNotNull();

        result = mockMvc.perform(get("/?logout=true&active=true"))
                        .andExpect(status().is3xxRedirection())
                        .andReturn();

        assertThat(result.getFlashMap().get(GlobalMessages.WARN_MESSAGES_HOLDER))
                                                                                 .withFailMessage("Warning message is set for inactive user")
                                                                                 .isNull();
    }

    @Test
    public void testNewInvoicesCountAndOpenOrderCount() throws Exception {
        SearchPageData<DistB2BInvoiceHistoryData> invoiceSearchPageData = mock(SearchPageData.class);
        List<DistB2BInvoiceHistoryData> invoiceResultList = mock(List.class);

        when(invoiceSearchPageData.getResults())
                                                .thenReturn(invoiceResultList);

        when(invoiceResultList.size())
                                      .thenReturn(5);

        when(invoiceHistoryFacade.getInvoiceSearchHistory(any()))
                                                                 .thenReturn(invoiceSearchPageData);

        SearchPageData<OrderHistoryData> orderSearchPageData = mock(SearchPageData.class);

        List<OrderHistoryData> orderResultList = new ArrayList<>();

        OrderHistoryData order1 = new OrderHistoryData();
        order1.setStatus(OrderStatus.ERP_STATUS_IN_PROGRESS);
        orderResultList.add(order1);

        OrderHistoryData order2 = new OrderHistoryData();
        order2.setStatus(OrderStatus.PENDING_APPROVAL);
        orderResultList.add(order2);

        OrderHistoryData order3 = new OrderHistoryData();
        order3.setStatus(OrderStatus.CANCELLED);
        orderResultList.add(order3);

        when(orderSearchPageData.getResults())
                                              .thenReturn(orderResultList);

        when(orderHistoryFacade.getOrderHistory(any(), any()))
                                                              .thenReturn(orderSearchPageData);
        when(orderHistoryFacade.getOrdersForStatuses(any()))
                                                            .thenReturn(Arrays.asList(order1, order2, order3));

        when(distUserDashboardFacade.getNewInvoicesCount()).thenReturn(5);
        when(distUserDashboardFacade.getOpenOrdersCount()).thenReturn(3);
        MvcResult result = mockMvc.perform(get("/"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();

        assertThat(modelMap.get("newInvoicesCount"))
                                                    .withFailMessage("Wrong number of new invoices returned")
                                                    .isEqualTo(5);

        assertThat(modelMap.get("openOrdersCount"))
                                                   .withFailMessage("Wrong number of open orders returned. Found %d, expected %d.",
                                                                    modelMap.get("openOrdersCount"),
                                                                    3)
                                                   .isEqualTo(3);
    }
}
