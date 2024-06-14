package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * {@code ErrorPageControllerTest}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.2
 */
@UnitTest
public class ErrorPageControllerTest extends AbstractPageControllerTest<ErrorPageController> {

    private static final String P_NOT_FOUND_URL = "/notFound";

    @Autowired
    private WebApplicationContext context;

    @InjectMocks
    protected ErrorPageController controller;

    @Mock
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Mock
    private DistUserDashboardFacade distUserDashboardFacade;

    @Mock
    private DistCheckoutFacade distCheckoutFacade;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = createMockMvc();
        super.setUp();
    }

    /**
     * Test method for
     * {@link com.namics.distrelec.b2b.storefront.controllers.pages.ErrorPageController#notFound(org.springframework.ui.Model, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     * 
     * @throws Exception
     */
    @Test
    public final void testNotFound() throws Exception {
        mockMvc.perform(get("/notFound")).andExpect(status().isNotFound());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest#getController()
     */
    @Override
    protected ErrorPageController getController() {
        return controller;
    }
}
