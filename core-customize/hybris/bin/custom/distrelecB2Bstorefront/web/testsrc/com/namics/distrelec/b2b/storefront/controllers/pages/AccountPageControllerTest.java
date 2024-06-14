/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistPaginationData;
import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.invoice.InvoiceHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.AccountBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.forms.InvoiceHistoryForm;
import com.namics.distrelec.b2b.storefront.forms.UpdateNewsletterForm;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

/**
 * {@code AccountPageControllerTest}
 * 
 *
 * @since Distrelec 7.2
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccountPageControllerTest extends AbstractPageControllerTest<AccountPageController> {

    private static final Logger LOG = LoggerFactory.getLogger(AccountPageControllerTest.class);

    private static final String CART_CODE = "0984HFNBAG";

    private static final String INVOICE_NUMBER = "7350298022";

    private static final String ORDER_NUMBER = "1000402161";

    private static final String CONTACT_ID = "00012485";

    private static final String ARTICLE_NUMBER = "18055840";

    private static final Date FROM_DATE = new Date();

    private static final Date TO_DATE = new Date();

    private static final Date FROM_DUE_DATE = new Date();

    private static final BigDecimal MINIMUM_TOTAL = BigDecimal.valueOf(0.0);

    private static final BigDecimal MAXIMUM_TOTAL = BigDecimal.valueOf(10000.0);

    private static final String SORT = "byDate";

    private static final String SORT_TYPE = "DESC";

    private static final String STATUS = "ALL";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:SS";

    private static int PAGE = 0;

    private static int PAGE_SIZE = 10;

    private static final int NUMBER_OF_GROUPS = 25;

    private PageableData pageSize10FirstPage;

    private static final String INVOICE_HISTORY_URL = "/my-account/invoice-history";

    private CustomerData user;

    @Mock
    private InvoiceHistoryFacade mockInvoiceHistoryFacade;

    @Mock
    private AccountBreadcrumbBuilder mockAccountBreadcrumbBuilder;

    @Mock
    private ContentPageModel mockContentPageModel;

    @Mock
    private DistNewsletterFacade mockNewsletterFacade;

    @Resource
    private WebApplicationContext context;

    @Mock
    private DistCheckoutFacade distCheckoutFacade;

    @Mock
    private CMSSiteModel cmsSiteModel;

    @Mock
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Mock
    private DistUserDashboardFacade distUserDashboardFacade;

    @InjectMocks
    private AccountPageController accountPageController;

    @Before
    @Override
    public void setUp() throws Exception {
        mockMvc = createMockMvc();
        super.setUp();

        // Call to setup the controller
        setUp(getController());

        pageSize10FirstPage = createPageableData(PAGE_SIZE, 0);

        when(b2bCustomerFacade.getCurrentCustomer()).thenReturn(distB2BCustomerData);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cartModel.getCode()).thenReturn(CART_CODE);
        when(cartModel.getUser()).thenReturn(currentUser);
        when(cartFacade.allowedToAccessCartWithCode(CART_CODE)).thenReturn(true);
        when(cartFacade.allowedToAccessCartWithCode(CART_CODE)).thenReturn(true);
        when(distCartDao.getCartForCode(CART_CODE)).thenReturn(Optional.ofNullable(cartModel));
        when(mockInvoiceHistoryFacade.getInvoiceSearchHistory(any())).thenReturn(createSearchPageData(pageSize10FirstPage));
        when(mockAccountBreadcrumbBuilder.getBreadcrumbs(anyString(), anyString())).thenReturn(Collections.emptyList());
        when(mockContentPageModel.getKeywords()).thenReturn("KEYWORD");
        when(mockContentPageModel.getDescription()).thenReturn("DISCRIPTION");
        when(distCmsPageService.getPageForLabelOrId(anyString())).thenReturn(mockContentPageModel);
        when(distCmsPageService.getPageForLabelOrId(anyString())).thenReturn(mockContentPageModel);

        when(distrelecCMSSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
        when(distrelecCMSSiteService.getStartPageLabelOrId(cmsSiteModel)).thenReturn("Account Page");
        when(cmsSiteModel.getActive()).thenReturn(true);
        when(distCmsPageService.getPageForLabelOrId(anyString(), eq(pagePreviewCriteriaData))).thenReturn(new ContentPageModel());
        when(mockContentPageModel.getKeywords()).thenReturn("Keyword");
        when(mockContentPageModel.getDescription()).thenReturn("Description");

        final SalesOrgData salesOrgData = createSalesOrgData("TEST_SALES_ORG");
        when(sessionService.getOrLoadAttribute(eq("currentSalesOrg"), any())).thenReturn(salesOrgData);

        user = createDistB2BCustomer("testCustomer");
        when(sessionService.getOrLoadAttribute(eq("currentUserData"), any())).thenReturn(user);
        when(distCmsPageService.getPageForLabelOrId(anyString())).thenReturn(mockContentPageModel);
        when(distCmsPageService.getPageForLabelOrId(anyString(), any(PagePreviewCriteriaData.class))).thenReturn(mockContentPageModel);
        when(distCmsPageService.getHomepage(any(PagePreviewCriteriaData.class))).thenReturn(mockContentPageModel);
        when(distCheckoutFacade.isAnonymousCheckout()).thenReturn(Boolean.FALSE);

        when(configuration.getKeys("xss.filter.rule"))
                                                      .thenReturn((new ArrayList<String>()).iterator());
        when(configuration.getKeys("distrelecB2Bstorefront.xss.filter.rule"))
                                                                             .thenReturn((new ArrayList<String>()).iterator());
    }

    @After
    public void tearDown() {
        user = null;
    }

    private SalesOrgData createSalesOrgData(final String code) {
        SalesOrgData salesOrgData = new SalesOrgData();
        salesOrgData.setCode(code);
        return salesOrgData;
    }

    @Override
    public void setUp(final AccountPageController accountPageController) throws Exception {
        super.setUp(accountPageController);
        accountPageController.setInvoiceHistoryFacade(mockInvoiceHistoryFacade);
        accountPageController.setAccountBreadcrumbBuilder(mockAccountBreadcrumbBuilder);
        accountPageController.setNewsletterFacade(mockNewsletterFacade);
        accountPageController.setUserFacade(userFacade);
    }

    private CustomerData createDistB2BCustomer(final String uid) {
        final CustomerData user = new CustomerData();
        user.setUid(uid);
        user.setUnit(new B2BUnitData());
        return user;
    }

    // @Test
    // uses static method which not possible to mock without powermock
    public void testResponseForIF12SearchMethod() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = mock(MockHttpServletRequest.class);
        when(mockHttpServletRequest.getServletPath()).thenReturn("/");

        final SalesOrgData salesOrg = new SalesOrgData();
        salesOrg.setCountryIsocode("CH");
        salesOrg.setCode("1100");
        when(storeSessionFacade.getCurrentSalesOrg()).thenReturn(salesOrg);
        when(sessionService.getOrLoadAttribute(eq("currentSalesOrg"), anyObject())).thenReturn(salesOrg);

        CustomerData currentUserData = new CustomerData();
        B2BUnitData unit = new B2BUnitData();
        unit.setErpCustomerId("11223344");
        currentUserData.setUnit(unit);
        when(sessionService.getOrLoadAttribute(eq("currentUserData"), anyObject())).thenReturn(currentUserData);
        B2BCustomerModel customerModel = new B2BCustomerModel();
        customerModel.setShowAllOrderhistory(Boolean.TRUE);
        given(userService.getCurrentUser()).willReturn(customerModel);

        // Method call
        InvoiceHistoryForm form = createInvoiceHistoryForm();
        MvcResult mvcResult = mockMvc.perform(post(INVOICE_HISTORY_URL, mockHttpServletRequest).content(asJsonString(form))).andExpect(status().isOk())
                                     .andReturn();

        // Result Verify
        Map<String, Object> modelMap = mvcResult.getModelAndView().getModel();
        InvoiceHistoryForm output = (InvoiceHistoryForm) modelMap.get("invoiceHistoryForm");

        assertTrue(output.getSortType().equals(form.getSortType()));
    }

    /**
     * This method is used to verify the newsletter update.
     *
     * @throws Exception
     */

    @Test
    public void testNewsLetterUpdate() throws Exception {
        when(b2bCustomerFacade.getCurrentCustomer()).thenReturn(user);

        final UpdateNewsletterForm form = createUpdateNewsletterForm();
        final SearchPageData<DistB2BInvoiceHistoryData> mockSearchPageData = new SearchPageData<>();
        final DistPaginationData mockPaginationData = new DistPaginationData();
        mockSearchPageData.setPagination(mockPaginationData);
        when(mockInvoiceHistoryFacade.getInvoiceSearchHistory(any())).thenReturn(mockSearchPageData);

        mockMvc.perform(post("/my-account/update-newsletter")
                                                             .content(asJsonString(form)))
               .andExpect(redirectedUrl("/my-account/my-account-information?no-cache=true"))
               .andReturn();
    }

    /**
     * This method is used to verify the cart output based on cart ID.
     *
     * @throws Exception
     */
    @Test
    public void showCartByIdTest() throws Exception {
        mockMvc.perform(get("/my-account/cart/" + CART_CODE)) //
               .andExpect(status().is3xxRedirection()) // The expected result is to redirect to the cart page
               .andExpect(redirectedUrl("/cart")); //
    }

    /**
     * This method is used to display the available payment options for customer.
     *
     * @throws Exception
     */
    @Test
    public void testPaymentOptionDisplay_Saved() throws Exception {

        // Set the request attributes
        final Model model = new ExtendedModelMap();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

        final List<DistPaymentModeData> paymentModeDataList = new ArrayList<>();
        final DistPaymentModeData distPaymentModeData = new DistPaymentModeData();
        distPaymentModeData.setCode("CreditCard");
        distPaymentModeData.setCreditCardPayment(true);
        distPaymentModeData.setDefaultPaymentMode(true);
        distPaymentModeData.setDescription("Credit Card");
        paymentModeDataList.add(distPaymentModeData);

        final List<CCPaymentInfoData> creditsCardsOptionsList = new ArrayList<>();
        CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();
        ccPaymentInfoData.setCardNumber("1111222233334444");
        ccPaymentInfoData.setCardType("VISA");
        ccPaymentInfoData.setDefaultPaymentInfo(true);
        ccPaymentInfoData.setIsValid(true);
        ccPaymentInfoData.setSaved(true);
        creditsCardsOptionsList.add(ccPaymentInfoData);

        // Mock Objects
        when(sessionService.getOrLoadAttribute(eq("isPaymentOptionsEditable"), anyObject())).thenReturn(Boolean.TRUE);
        when(userFacade.getSupportedPaymentModesForUser()).thenReturn(paymentModeDataList);
        when(userFacade.getCCPaymentInfos(true)).thenReturn(creditsCardsOptionsList);
        when(userFacade.canRequestInvoicePaymentMode()).thenReturn(true);
        when(userFacade.isInvoicePaymentModeRequested()).thenReturn(false);

        final String resultUrl = accountPageController.getPaymentAndDeliveryOptions(model, mockHttpServletRequest);
        assertEquals(ControllerConstants.Views.Pages.Account.AccountPaymentAndDeliveryOptionsPage, resultUrl);

        final List<CCPaymentInfoData> outputCreditsCardsList = (List<CCPaymentInfoData>) model.asMap().get("ccPaymentInfos");
        CCPaymentInfoData ccPaymentOutput = outputCreditsCardsList.get(0);
        assertEquals(ccPaymentOutput.getCardNumber(), ccPaymentInfoData.getCardNumber());
        assertEquals(ccPaymentOutput.getCardType(), ccPaymentInfoData.getCardType());

    }

    /**
     * This method is used to display the available payment options for customer.
     *
     * @throws Exception
     */
    @Test
    public void testPaymentOptionDisplay_unsaved() throws Exception {

        // Set the request attributes
        final Model model = new ExtendedModelMap();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

        final List<DistPaymentModeData> paymentModeDataList = new ArrayList<>();
        final DistPaymentModeData distPaymentModeData = new DistPaymentModeData();
        distPaymentModeData.setCode("CreditCard");
        distPaymentModeData.setCreditCardPayment(true);
        distPaymentModeData.setDefaultPaymentMode(true);
        distPaymentModeData.setDescription("Credit Card");
        paymentModeDataList.add(distPaymentModeData);

        final List<CCPaymentInfoData> creditsCardsOptionsList = new ArrayList<>();
        CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();
        ccPaymentInfoData.setCardNumber("1111222233334444");
        ccPaymentInfoData.setCardType("VISA");
        ccPaymentInfoData.setDefaultPaymentInfo(true);
        ccPaymentInfoData.setIsValid(true);
        ccPaymentInfoData.setSaved(true);
        creditsCardsOptionsList.add(ccPaymentInfoData);

        // Get customer Details
        CustomerData customerData = new CustomerData();
        customerData.setEmail("testuser@gmail.com");
        customerData.setCcPaymentInfos(creditsCardsOptionsList);

        // Mock Objects
        when(sessionService.getOrLoadAttribute(eq("isPaymentOptionsEditable"), anyObject())).thenReturn(Boolean.TRUE);
        when(userFacade.getSupportedPaymentModesForUser()).thenReturn(paymentModeDataList);
        when(userFacade.getCCPaymentInfos(true)).thenReturn(Collections.EMPTY_LIST);
        when(b2bCustomerFacade.getCurrentCustomer()).thenReturn(customerData);

        // Method call
        final String resultUrl = accountPageController.getPaymentAndDeliveryOptions(model, mockHttpServletRequest);

        // Validate Output
        assertEquals(ControllerConstants.Views.Pages.Account.AccountPaymentAndDeliveryOptionsPage, resultUrl);

        final List<CCPaymentInfoData> outputCreditsCardsList = (List<CCPaymentInfoData>) model.asMap().get("ccPaymentInfos");
        CCPaymentInfoData ccPaymentOutput = outputCreditsCardsList.get(0);
        assertEquals(ccPaymentOutput.getCardNumber(), ccPaymentInfoData.getCardNumber());
        assertEquals(ccPaymentOutput.getCardType(), ccPaymentInfoData.getCardType());

    }

    /**
     * This method is used to verify setting payment options.
     *
     * @throws Exception
     */
    @Test
    public void testSetDefaultPaymentOption() throws Exception {

        // Method call
        final String resultUrl = accountPageController.setDefaultPaymentOption("CreditCard");

        // Validate Output
        assertEquals(Boolean.TRUE, resultUrl.contains("/my-account/payment-and-delivery-options"));

    }

    /**
     * This method is used to verify setting payment options.
     *
     * @throws Exception
     */
    @Test
    public void testSetDefaultPaymentInfo() throws Exception {

        String paymentInfoID = "11223344";
        when(userFacade.setDefaultPaymentMode(anyString())).thenReturn(Boolean.TRUE);

        // Method call
        final String resultUrl = accountPageController.setDefaultPaymentInfo("CreditCard", paymentInfoID);

        // Validate Output
        assertEquals(Boolean.TRUE, resultUrl.contains("/my-account/payment-and-delivery-options"));
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest#getController()
     */
    @Override
    protected AccountPageController getController() {
        return accountPageController;
    }

    /**
     * Method to create Invoice history data.
     *
     * @return InvoiceHistoryForm
     */
    private InvoiceHistoryForm createInvoiceHistoryForm() {
        InvoiceHistoryForm form = new InvoiceHistoryForm();
        form.setArticleNumber(ARTICLE_NUMBER);
        form.setToDate(TO_DATE);
        form.setContactId(CONTACT_ID);
        form.setFromDate(FROM_DATE);
        form.setFromDueDate(FROM_DUE_DATE);
        form.setInvoiceNumber(INVOICE_NUMBER);
        form.setMaxTotal(MAXIMUM_TOTAL);
        form.setMinTotal(MINIMUM_TOTAL);
        form.setOrdernf(ORDER_NUMBER);
        form.setOrderNumber(ORDER_NUMBER);
        form.setPage(PAGE);
        form.setPageSize(PAGE_SIZE);
        form.setSort(SORT);
        form.setSortType(SORT_TYPE);
        form.setStatus(STATUS);
        return form;
    }

    /**
     * Method to create newsletter data.
     *
     * @return UpdateNewsletterForm
     */
    private UpdateNewsletterForm createUpdateNewsletterForm() {
        UpdateNewsletterForm newsletterForm = new UpdateNewsletterForm();
        newsletterForm.setMarketingConsent(Boolean.TRUE);
        newsletterForm.setSubscribePhoneMarketing(Boolean.TRUE);
        return newsletterForm;
    }

    /**
     * Method to convert onject to json string.
     *
     * @param obj
     * @return JsonString
     */
    public static String asJsonString(final Object obj) {
        String jsoncontent = "";

        try {
            final ObjectMapper mapper = new ObjectMapper();
            jsoncontent = mapper.writeValueAsString(obj);
            LOG.info(jsoncontent);
        } catch (IOException e1) {
            LOG.warn("Exception occurred during conversion of object to JSON", e1);
        }
        return jsoncontent;
    }

    /**
     * Method to create search page data for invoice.
     *
     * @param pageableData
     * @return SearchPageData
     */
    protected SearchPageData<DistB2BInvoiceHistoryData> createSearchPageData(final PageableData pageableData) {
        final SearchPageData<DistB2BInvoiceHistoryData> searchPageData = new SearchPageData<>();

        createPaginationData(pageableData);

        searchPageData.setPagination(createPaginationData(pageableData));
        final List<DistB2BInvoiceHistoryData> results = new ArrayList<>();
        DistB2BInvoiceHistoryData invoiceHistoryData = new DistB2BInvoiceHistoryData();
        invoiceHistoryData.setInvoiceNumber("112233");
        results.add(invoiceHistoryData);
        searchPageData.setResults(results);

        return searchPageData;
    }

    /**
     * Method to create page data for invoice.
     *
     * @param pageableData
     * @return DistPaginationData
     */
    protected DistPaginationData createPaginationData(final PageableData pageableData) {
        final DistPaginationData paginationData = new DistPaginationData();
        paginationData.setCurrentPage(pageableData.getCurrentPage());
        paginationData.setPageSize(pageableData.getPageSize());
        paginationData.setNumberOfPages((NUMBER_OF_GROUPS + pageableData.getPageSize() - 1) / pageableData.getPageSize());
        paginationData.setTotalNumberOfResults(NUMBER_OF_GROUPS);
        return paginationData;
    }

    /**
     * Method to create page-able data for invoice.
     *
     * @param pageSize
     * @param currentPage
     * @return PageableData
     */
    protected PageableData createPageableData(final int pageSize, final int currentPage) {
        final PageableData pageableData = new PageableData();
        pageableData.setPageSize(pageSize);
        pageableData.setCurrentPage(currentPage);
        return pageableData;
    }
}
