package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.AccountBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
public class CompanyPageControllerTest extends AbstractPageControllerTest<CompanyPageController> {
    @Mock
    private DistCustomerAccountService distCustomerAccountService;

    @Mock
    private B2BUnitFacade b2bUnitFacade;

    @Mock
    private AccountBreadcrumbBuilder accountBreadcrumbBuilder;

    @Mock
    private DistCheckoutFacade checkoutFacade;

    @Mock
    private DistCustomerFacade distCustomerFacade;

    @Mock
    private Converter<B2BCustomerModel, CustomerData> b2bCustomerConverter;

    @InjectMocks
    private CompanyPageController controller;

    @Override
    protected CompanyPageController getController() {
        return controller;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        super.setUp();
    }

    @Test
    public void testSuccessfulReactivation() throws Exception {
        doNothing().when(b2bCustomerFacade).resendSetInitialPasswordEmail(Matchers.anyString());
        final String requestUrl = "/my-account/company/resend/activation/{customerUid}";
        final String customerUid = "id-001";
        final String expectedRedirectionUrl = "/my-account/company/edit/employee/" + customerUid + AbstractController.QUESTION_MARK
                                              + AbstractController.NO_CACHE_EQUALS_TRUE;

        mockMvc.perform(get(requestUrl, customerUid))
               .andExpect(status().is3xxRedirection())
               .andExpect(flash().attributeExists(GlobalMessages.INFO_MESSAGES_HOLDER))
               .andExpect(redirectedUrl(expectedRedirectionUrl))
               .andReturn();
    }
}
