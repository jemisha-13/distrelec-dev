package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Page;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.PageInfo;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.b2b.storefront.forms.RegisterB2BForm;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UnitTest
public class CustomerRegistrationPageControllerTest extends AbstractPageControllerTest<CustomerRegistrationPageController> {

    @InjectMocks
    private CustomerRegistrationPageController controller;

    @Autowired
    private MockServletContext servletContext;

    @Mock
    private DistCustomerFacade customerFacade;

    @Mock
    private SessionService sessionService;

    @Mock
    private DistProductPriceQuotationFacade productPriceQuotationFacade;

    @Mock
    private DistCmsPageService cmsPageService;

    @Mock
    private DigitalDatalayer digitalDatalayer;

    @Mock
    private DistCheckoutFacade distCheckoutFacade;

    @Mock
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Mock
    private DistUserDashboardFacade distUserDashboardFacade;

    static Validator validator;

    @Before
    public void setUp() throws Exception {
        Validator mockValidator = mock(Validator.class);
        MockitoAnnotations.initMocks(this);
        mockMvc = createMockMvcWithValidator(mockValidator);
        super.setUp();

        SalesOrgData salesOrg = new SalesOrgData();
        salesOrg.setCode("1234");
        when(sessionService.getOrLoadAttribute(eq("currentSalesOrg"), anyObject()))
                                                                                   .thenReturn(salesOrg);

        LanguageData language = new LanguageData();
        when(sessionService.getOrLoadAttribute(eq("languages"), anyObject()))
                                                                             .thenReturn(Collections.singleton(language));

        controller.setSessionService(sessionService);

        when(productPriceQuotationFacade.getQuotationHistory("02"))
                                                                   .thenReturn(Collections.emptyList());

        ContentPageModel contentPage = mock(ContentPageModel.class);
        when(contentPage.getTitle())
                                    .thenReturn("PageTitle");
        when(cmsPageService.getPageForLabelOrId(anyString(), any()))
                                                                    .thenReturn(contentPage);

        controller.setCmsPageService(cmsPageService);

        Page mockPage = mock(Page.class);
        when(digitalDatalayer.getPage())
                                        .thenReturn(mockPage);
        PageInfo mockPageInfo = mock(PageInfo.class);
        when(mockPage.getPageInfo())
                                    .thenReturn(mockPageInfo);

        when(distCheckoutFacade.isAnonymousCheckout()).thenReturn(Boolean.FALSE);
    }

    @Override
    protected CustomerRegistrationPageController getController() {
        return controller;
    }

    @Test
    public void testDoRegister() throws Exception {

        MvcResult result = mockMvc.perform(get("/registration")
                                                               .flashAttr(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer))
                                  .andExpect(status().isOk())
                                  .andReturn();


        Map<String, Object> model = result.getModelAndView().getModel();

        assertThat(model.get("registerB2BForm"))
                                                .withFailMessage("Register B2B form is missing")
                                                .isNotNull();

        assertThat(model.get("registerForm"))
                                             .withFailMessage("Register B2C form is missing")
                                             .isNotNull();

        assertThat(result.getResponse().getForwardedUrl())
                                                          .withFailMessage("Wrong page displayed")
                                                          .isEqualTo("pages/account/accountRegisterConsolidatedPage");
    }

    @Test
    public void testDoRegisterPost() throws Exception {
        RegisterB2BForm registerB2BForm = new RegisterB2BForm();
        registerB2BForm.setCustomerId("test");
        registerB2BForm.setCompany("test");
        registerB2BForm.setTitleCode("mr");
        registerB2BForm.setFirstName("firstname");
        registerB2BForm.setLastName("LastNAme");
        registerB2BForm.setEmail("testmail@distrelec.com");
        registerB2BForm.setPwd("123456");
        registerB2BForm.setCheckPwd("123456");
        registerB2BForm.setPhoneNumber("+41968900231");
        registerB2BForm.setMobileNumber("+41968900231");

        mockMvc.perform(post("/registration/b2b")
                                                 .contentType(MediaType.TEXT_PLAIN)
                                                 .content(""))
               .andExpect(status().is2xxSuccessful());

    }
}
