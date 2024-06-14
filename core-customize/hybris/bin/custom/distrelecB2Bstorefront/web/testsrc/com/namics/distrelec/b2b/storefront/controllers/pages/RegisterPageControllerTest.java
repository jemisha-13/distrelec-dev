package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UnitTest
public class RegisterPageControllerTest extends AbstractPageControllerTest<RegisterPageController> {

    @InjectMocks
    private RegisterPageController controller;

    @Mock
    private DistCheckoutFacade checkoutFacade;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        super.setUp();

        prepareMocks();
    }

    private void prepareMocks() throws Exception{
        ContentPageModel pageModel = mock(ContentPageModel.class);

        when(pageModel.getTitle())
                .thenReturn("register");

        when(distCmsPageService.getPageForLabelOrId("register"))
                .thenReturn(pageModel);
    }

    @Override
    protected RegisterPageController getController() {
        return controller;
    }

    @Test
    public void testGetPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/register?emailToRegister=test@email.com"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletRequest request = result.getRequest();
        HttpSession session = request.getSession();

        assertThat(session.getAttribute("emailToRegister"))
                .withFailMessage("Registration email is not set in session")
                .isEqualTo("test@email.com");
    }


}