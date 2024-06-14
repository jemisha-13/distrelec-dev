package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@UnitTest
public class LoginPageControllerTest extends AbstractLoginPageControllerTest<LoginPageController> {

    private static final String LOGIN_URL = "/login";
    private static final String REFERER_URL= "distrelec-local.ch:9002/login";
    private static final String ERROR = "error";
    private static final String QD = "qd";
    private static final String PENDING_APPROVAL_USER_DISTRELEC_COM = "pending-approval-user@distrelec.com";
    private static final String REFERER = "referer";
    private static final String ACTIVATED_USER_UID = "enabled-user@distrelec.com";
    private static final String REDIRECT_WELCOME_NO_CACHE_TRUE = "redirect:/welcome?no-cache=true";
    private static final String LOGINPAGE = "loginpage";
    private static final String LOGIN = "login";
    private static final String CONTENT_PAGE = "ContentPage";

    @Mock
    private DistCategoryFacade distCategoryFacade;
    @Mock
    private HttpSessionRequestCache httpSessionRequestCache;
    @Mock
    private ContentPageModel contentPageModel;
    @InjectMocks
    private LoginPageController controller;

    @Override
    protected LoginPageController getController() {
        return controller;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        super.setUp();
        prepareMocks();
    }

    private void prepareMocks() throws CMSItemNotFoundException {
        final ContentPageModel contentPage = createTestContentPageModel();
        when(userService.getCurrentUser()).thenReturn(new CustomerModel());
        when(userService.isAnonymousUser(any(UserModel.class))).thenReturn(true);
        when(distCmsPageService.getHomepage()).thenReturn(contentPage);
        when(distCmsPageService.getPageForLabel(LOGIN)).thenReturn(contentPage);

        doNothing().when(sessionService).setAttribute(anyString(), anyString());
        when(distCmsPageService.getPageForLabel(anyString())).thenReturn(new ContentPageModel());

    }

    private ContentPageModel createTestContentPageModel() {
        final ContentPageModel contentPage = mock(ContentPageModel.class);
        contentPage.setKeywords(LOGIN);
        contentPage.setDescription(CONTENT_PAGE, Locale.ENGLISH);
        contentPage.setName(LOGINPAGE);
        contentPage.setUid(LOGINPAGE);
        contentPage.setLabel(LOGINPAGE);
        contentPage.setTitle(LOGINPAGE, Locale.ENGLISH);
        return contentPage;
    }

    @Test
    @Ignore
    public void testUnsuccessfulLoginAsUnapprovedUser() throws Exception {
        mockMvc.perform(get(LOGIN_URL)
                .sessionAttr(ERROR, true)
                .sessionAttr(QD, PENDING_APPROVAL_USER_DISTRELEC_COM)
                .sessionAttr(WebConstants.ACCOUNT_NOT_ACTIVE, Boolean.TRUE))
                .andExpect(status().isOk())
                .andExpect(view().name(ControllerConstants.Views.Pages.Account.AccountLoginPage))
                .andReturn();
    }

    @Test
    @Ignore
    public void testSuccessfulLogin() throws Exception {
        doNothing().when(httpSessionRequestCache).saveRequest(any(HttpServletRequest.class), any(HttpServletResponse.class));
        mockMvc.perform(get(LOGIN_URL)
                .header(REFERER, REFERER_URL)
                .sessionAttr(ERROR, Boolean.FALSE)
                .sessionAttr(QD, ACTIVATED_USER_UID)
                .sessionAttr(WebConstants.DO_NOT_STORE_REFERRER, Boolean.TRUE))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_WELCOME_NO_CACHE_TRUE))
                .andReturn();
    }
}
