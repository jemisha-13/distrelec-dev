package com.namics.distrelec.b2b.storefront.controllers.pages;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.util.CookieGenerator;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.storefront.controllers.pages.checkout.DistCheckoutOrderConfirmationController;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.session.SessionService;

public class DistCheckoutOrderConfirmationControllerTest extends AbstractPageControllerTest<DistCheckoutOrderConfirmationController> {

    @InjectMocks
    private DistCheckoutOrderConfirmationController distCheckoutOrderConfirmationController;

    @Mock
    private DistB2BOrderFacade mockDistB2BOrderFacade;

    @Mock
    private CookieGenerator mockGuidCookieGenerator;

    @Mock
    private DistUserFacade mockUserFacade;

    @Mock
    private DistCustomerFacade mockCustomerFacade;

    @Mock
    private DistNewsletterFacade mockDistNewsletterFacade;

    @Mock
    private SessionService mockSessionService;

    @Mock
    private DistCheckoutFacade mockDistCheckoutFacade;

    @Override
    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        super.setUp();

        // Call to setup the controller
        setUp(getController());
    }

    @Override
    public void setUp(final DistCheckoutOrderConfirmationController distCheckoutOrderConfirmationController) throws Exception {
        super.setUp(distCheckoutOrderConfirmationController);
        // distCheckoutOrderConfirmationController.setCustomerFacade(mockCustomerFacade);
        // distCheckoutOrderConfirmationController.setDistB2BOrderFacade(mockDistB2BOrderFacade);
        // distCheckoutOrderConfirmationController.setGuidCookieGenerator(mockGuidCookieGenerator);
        // distCheckoutOrderConfirmationController.setUserFacade(mockUserFacade);
    }

    @Override
    protected DistCheckoutOrderConfirmationController getController() {
        return distCheckoutOrderConfirmationController;
    }

}
