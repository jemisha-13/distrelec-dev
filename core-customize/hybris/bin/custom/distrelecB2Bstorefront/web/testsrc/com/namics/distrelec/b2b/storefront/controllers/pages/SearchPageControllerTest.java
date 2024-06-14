package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BCustomerService;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import static com.namics.distrelec.b2b.storefront.controllers.pages.AbstractSearchPageController.PAGE_PARAMETER_NAME;
import static com.namics.distrelec.b2b.storefront.controllers.pages.AbstractSearchPageController.SEARCH_QUERY_PARAMETER_NAME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SearchPageControllerTest extends AbstractPageControllerTest<SearchPageController> {

    @Mock
    private ProductSearchFacade<ProductData> productSearchFacade;
    @Mock
    private DefaultB2BCustomerService b2BCustomerService;
    @InjectMocks
    private SearchPageController controller;

    private static final String SEARCH_QUERY_URL = "/search?q=MDO4024C";
	
    @Before
    @Override
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        super.setUp();
        
    }
	@Override
	protected SearchPageController getController() {
		return controller;
	}
	

    @Test
    public void testGlobalMessage() throws Exception {
    	mockMvc.perform(get(SEARCH_QUERY_URL)) 
    	.andExpect(status().isOk());
    }

    @Test
    public void testExtraCharactersAreFilteredOut() throws Exception {
        final MockHttpServletRequestBuilder builder = createMockServletRequest().flashAttr("cmsPage", new CMSPageTypeModel());
        final B2BCustomerModel nullCustomer = null;
         when( b2BCustomerService.getCurrentB2BCustomer()).thenReturn(nullCustomer);

        final MvcResult result = mockMvc.perform(builder).andExpect(status().isOk()).andReturn();
        final ModelAndView modelAndView = result.getModelAndView();

        assertNotNull(modelAndView);
        assertFalse(modelAndView.getViewName().equalsIgnoreCase("forward:unknownError"));

    }

    private MockHttpServletRequestBuilder createMockServletRequest() {
        final String bug = "*,robotics";
        final String url = "/search?" + bug;
        final int zero = 0;
        final int one = 1;
        final String showMode = "Page";

        final MockHttpSession session = new MockHttpSession();
        session.setAttribute(SEARCH_QUERY_PARAMETER_NAME, bug);
        session.setAttribute(PAGE_PARAMETER_NAME, one);
        session.setAttribute("pageSize", zero);
        session.setAttribute("show", showMode);
        return MockMvcRequestBuilders.get(url).session(session).requestAttr("cmsPage", "csmPage");
    }
}
