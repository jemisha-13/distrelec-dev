package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ResendAccountActivationTokenPageControllerTest extends AbstractPageControllerTest<ResendAccountActivationTokenPageController>{

    @Mock
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;
    @Mock
    private DistCustomerAccountService distCustomerAccountService;
    @Mock
    private DistB2BCartFacade b2bCartFacade;
    @InjectMocks
    private ResendAccountActivationTokenPageController controller;

    private static final String DOUBLE_OPT_IN_URL = "/register/doubleoptin/request";

    @Override
    protected ResendAccountActivationTokenPageController getController() {
        return controller;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = createMockMvc();

        ContentPageModel contentPage = mock(ContentPageModel.class);
        when(distCmsPageService.getPageForLabelOrId(anyString(), any()))
                .thenReturn(contentPage);

        super.setUp();
    }

    @Test
    public void testUnapprovedCustomerIsRedirectedToLoginPage() throws Exception {
        final List<B2BCustomerModel> fakeCustomers = createNotYetApprovedCustomers();
        when(distCustomerAccountService.getCustomersByEmail(Mockito.anyString())).thenReturn(fakeCustomers);

        when(configuration.getInt(eq("distrelec.maxTokenResend.attempts"), anyInt()))
            .thenReturn(3);

        final MvcResult result = mockMvc.perform(post(DOUBLE_OPT_IN_URL)
                .param("email", "neil.clarke@distrelec.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andReturn();
    }

    private List<B2BCustomerModel> createNotYetApprovedCustomers() {
        B2BCustomerModel model = new B2BCustomerModel();
        model.setDoubleOptInActivated(false);
        model.setUid("neil.clarke@distrelec.com");
        return Collections.singletonList(model);
    }
}
