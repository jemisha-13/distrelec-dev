 package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.rma.CreateRMAOrderEntryDataForm;
import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.rma.GuestRMACreateRequestForm;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import com.namics.distrelec.b2b.facades.adobe.datalayer.DistDigitalDatalayerFacade;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.ReturnItem;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Rma;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.RmaData;
import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.rma.DistReturnRequestFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GuestReturnsRMAControllerTest extends AbstractPageControllerTest {

    private static final Logger LOG = LogManager.getLogger(GuestReturnsRMAControllerTest.class);

    @InjectMocks
    private GuestReturnsRMAController guestReturnsRMAController;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private BaseStoreService baseStoreService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Validator validator;

    @Mock
    private DistDigitalDatalayerFacade distDigitalDatalayerFacade;

    @Mock
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Mock
    private DistReturnRequestFacade distReturnRequestFacade;

    @Mock
    private DistCheckoutFacade distCheckoutFacade;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Mock
    private DistUserDashboardFacade distUserDashboardFacade;

    protected MockMvc mockMvc;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        super.setUp();

        // Call to setup the controller
        setUp(getController());

        // Set Request URl & Mock it.
        final StringBuffer buffer = new StringBuffer();
        buffer.append("https://localhost:9002/returns/claims");
        Mockito.when(request.getRequestURL()).thenReturn(buffer);
        when(request.getRequestURI()).thenReturn("/returns/claims");

        final List<Breadcrumb> list = new ArrayList<>();
        Mockito.when(simpleBreadcrumbBuilder.getBreadcrumbs(anyString())).thenReturn(list);

        HttpSession mockSession = mock(HttpSession.class);
        Mockito.when(mockSession.getAttribute("skipCaptcha"))
                .thenReturn(true);
        Mockito.when(request.getSession())
                .thenReturn(mockSession);

        ContentPageModel contentPage = mock(ContentPageModel.class);
        Mockito.when(distCmsPageService.getPageForLabelOrId(anyString(), any()))
                .thenReturn(contentPage);

        when(distCheckoutFacade.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
    }

    /**
     * Check whether the Guest user is able to submit the claim and an email is triggered.
     * 
     * @throws Exception
     */
    @Test
    public final void testSubmitReturnClaimForGuest() throws Exception {
        
        System.out.println("Running Test Case : testSubmitReturnClaimForGuest");

        final GuestRMACreateRequestForm guestRMACreateRequestForm = new GuestRMACreateRequestForm();
        guestRMACreateRequestForm.setArticleNumber("12341234");
        guestRMACreateRequestForm.setCustomerName("Test User");
        guestRMACreateRequestForm.setCustomerText("New Data Return");
        guestRMACreateRequestForm.setEmailAddress("sudarshan.tembhurnikar@datwyler.com");
        guestRMACreateRequestForm.setOrderNumber("12341234");
        guestRMACreateRequestForm.setPhoneNumber("9988776655");
        guestRMACreateRequestForm.setQuantity(2L);
        guestRMACreateRequestForm.setReturnReason("018");

        // Set the Digital Data layer for the Return Request raised Data
        final DigitalDatalayer digitalDatalayer = new DigitalDatalayer();
        final RmaData rmaData = new RmaData();
        final Rma rmaExpected = new Rma();

        rmaExpected.setOrderId("12341234");

        final List<ReturnItem> returnItemsExpectedList = new ArrayList<>();
        final ReturnItem returnItem1 = new ReturnItem();

        returnItem1.setItemNumber("12341234");
        returnItem1.setComment("Test Data");
        returnItem1.setRefundType("1");
        returnItem1.setReturnQty(Double.valueOf(2L));
        returnItem1.setReturnReason("018");

        returnItemsExpectedList.add(returnItem1);
        rmaExpected.setReturnItems(returnItemsExpectedList);
        rmaData.setRma(rmaExpected);

        digitalDatalayer.setRmaData(rmaData);

        final Model model = new ExtendedModelMap();
        model.addAttribute("digitaldata", digitalDatalayer);

        // Mock the validator
        Mockito.when(validator.supports(GuestRMACreateRequestForm.class)).thenReturn(Boolean.valueOf(true));

        final boolean result = guestReturnsRMAController.submitReturnClaimForGuest(guestRMACreateRequestForm, bindingResult);

        // Verify the output
        assertEquals(result, true);

        // Verify DigitalDatalayer from model
        final DigitalDatalayer digitalResult = (DigitalDatalayer) model.asMap().get("digitaldata");

        final Rma rmaResult = digitalResult.getRmaData().getRma();
        final List<ReturnItem> returnOutputList = rmaResult.getReturnItems();

        for (
                final ReturnItem returnItemsOutput : returnOutputList) {
            final ReturnItem returnItemsexpected = returnItemsExpectedList.stream()
                    .filter(returnItem -> returnItem.getItemNumber().equals(returnItemsOutput.getItemNumber())).findFirst().get();

            assertEquals(returnItemsexpected.getComment(), returnItemsOutput.getComment());
            assertEquals(returnItemsexpected.getItemStatus(), returnItemsOutput.getItemStatus());
            assertEquals(returnItemsexpected.getRefundType(), returnItemsOutput.getRefundType());
            assertEquals(returnItemsexpected.getReturnQty(), returnItemsOutput.getReturnQty());
            assertEquals(returnItemsexpected.getReturnReason(), returnItemsOutput.getReturnReason());
        }

        assertEquals(rmaExpected.getOrderId(), rmaResult.getOrderId());
        assertEquals(rmaExpected.getRmaHeaderStatus(), rmaResult.getRmaHeaderStatus());
        assertEquals(rmaExpected.getRmaId(), rmaResult.getRmaId());

    }
    
    
    @Test
    public final void testSubmitReturnClaimForGuestProcessError() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(Boolean.TRUE);

        final boolean result = guestReturnsRMAController.submitReturnClaimForGuest(null, bindingResult);

        // Verify the result
        assertFalse(result);
    }
    
    

    /**
     * Check if the email is triggered when the registered user submits the return claim.
     * 
     * @throws Exception
     */
    @Test
    public final void testSubmitReturnClaimForUser() throws Exception {
        final CreateRMARequestForm createRMARequestForm = new CreateRMARequestForm();
        final List<CreateRMAOrderEntryDataForm> createRMAOrderEntryDataFormList = new ArrayList<>();
        final CreateRMAOrderEntryDataForm createRMAOrderEntryDataForm = new CreateRMAOrderEntryDataForm();

        createRMARequestForm.setOrderId("000011");

        createRMAOrderEntryDataForm.setArticleNumber("12341234");
        createRMAOrderEntryDataForm.setCustomerText("Test User");
        createRMAOrderEntryDataForm.setItemNumber("12341234");
        createRMAOrderEntryDataForm.setQuantity(2L);
        createRMAOrderEntryDataForm.setRefundType("2");
        createRMAOrderEntryDataForm.setReturnReasonID("018");
        createRMAOrderEntryDataFormList.add(createRMAOrderEntryDataForm);
        createRMARequestForm.setOrderItems(createRMAOrderEntryDataFormList);

        // Set the Digital Data layer for the Return Request raised Data
        final DigitalDatalayer digitalDatalayer = new DigitalDatalayer();
        final RmaData rmaData = new RmaData();
        final Rma rmaExpected = new Rma();

        rmaExpected.setOrderId("12341234");
        rmaExpected.setRmaHeaderStatus("Return request created");
        rmaExpected.setRmaId("12100002");

        final List<ReturnItem> returnItemsExpectedList = new ArrayList<>();
        final ReturnItem returnItem1 = new ReturnItem();

        returnItem1.setItemNumber("12341234");
        returnItem1.setComment("Test Data");
        returnItem1.setRefundType("1");
        returnItem1.setReturnQty(Double.valueOf(2));
        returnItem1.setReturnReason("018");

        returnItemsExpectedList.add(returnItem1);
        rmaExpected.setReturnItems(returnItemsExpectedList);
        rmaData.setRma(rmaExpected);

        digitalDatalayer.setRmaData(rmaData);

        final Model model = new ExtendedModelMap();
        model.addAttribute("digitaldata", digitalDatalayer);

        // Mock the validator
        Mockito.when(validator.supports(CreateRMARequestForm.class)).thenReturn(Boolean.valueOf(true));

        DistReturnRequestFacade.UserRMARequestDataWrapper dataWrapper = new DistReturnRequestFacade.UserRMARequestDataWrapper();
        dataWrapper.setRmaId("12100002");
        dataWrapper.setCreateRMARequestForm(createRMARequestForm);

        BindingResult bindingResult = mock(BindingResult.class);

        final boolean result = guestReturnsRMAController.submitReturnClaimForUser(dataWrapper, bindingResult, request);

        // Verify the result
        assertEquals(true, result);

        // Verify DigitalDatalayer from model
        final DigitalDatalayer digitalResult = (DigitalDatalayer) model.asMap().get("digitaldata");

        final Rma rmaResult = digitalResult.getRmaData().getRma();
        final List<ReturnItem> returnOutputList = rmaResult.getReturnItems();

    for(
    final ReturnItem returnItemsOutput:returnOutputList)
    {
        final ReturnItem returnItemsexpected = returnItemsExpectedList.stream()
                    .filter(returnItem -> returnItem.getItemNumber().equals(returnItemsOutput.getItemNumber())).findFirst().get();

            assertEquals(returnItemsexpected.getComment(), returnItemsOutput.getComment());
            assertEquals(returnItemsexpected.getItemStatus(), returnItemsOutput.getItemStatus());
            assertEquals(returnItemsexpected.getRefundType(), returnItemsOutput.getRefundType());
            assertEquals(returnItemsexpected.getReturnQty(), returnItemsOutput.getReturnQty());
            assertEquals(returnItemsexpected.getReturnReason(), returnItemsOutput.getReturnReason());
        }

        assertEquals(rmaExpected.getOrderId(), rmaResult.getOrderId());
        assertEquals(rmaExpected.getRmaHeaderStatus(), rmaResult.getRmaHeaderStatus());
        assertEquals(rmaExpected.getRmaId(), rmaResult.getRmaId());

    }
    
    @Test
    public final void testSubmitReturnClaimForUserProcessError() throws Exception {
        final CreateRMAResponseData createRMAResponseData = new CreateRMAResponseData();
        createRMAResponseData.setRmaHeaderStatus("Return request created");
        createRMAResponseData.setRmaNumber("12100002");

        // Create JSON
        final String createRMAResponseDataJson = asJsonString(createRMAResponseData);

        final Model model = new ExtendedModelMap();
  
        // Mock the validator
        Mockito.when(validator.supports(CreateRMARequestForm.class)).thenReturn(Boolean.valueOf(true));
        BindingResult bindingResult = mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors())
                .thenReturn(true);
        final boolean result = guestReturnsRMAController.submitReturnClaimForUser(null, bindingResult, request);

        // Verify the result
        assertEquals(false, result);

    }

    @Test
    public final void testReturnAndClaimPage() throws Exception {
        // given
        final Model model = mock(Model.class);

        // when
        String result = guestReturnsRMAController.getReturnAndRepair(model, request);

        // then
        assertEquals("pages/account/returnAndClaimPage",result);
        
    }

    public static String asJsonString(final Object obj) {
        String jsoncontent = "";
        try {
            final ObjectMapper mapper = new ObjectMapper();
            jsoncontent = mapper.writeValueAsString(obj);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return jsoncontent;
    }

    @Override
    protected AbstractPageController getController() {
        return guestReturnsRMAController;
    }

    public void setUp(final GuestReturnsRMAController guestReturnsRMAController) throws Exception {
        guestReturnsRMAController.setBaseStoreService(baseStoreService);
        guestReturnsRMAController.setSimpleBreadcrumbBuilder(simpleBreadcrumbBuilder);
        guestReturnsRMAController.setConfigurationService(configurationService);
    }
}
