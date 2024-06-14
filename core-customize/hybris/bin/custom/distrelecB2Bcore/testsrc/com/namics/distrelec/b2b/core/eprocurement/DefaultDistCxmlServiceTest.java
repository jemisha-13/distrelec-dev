/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement;

import com.namics.distrelec.b2b.core.eprocurement.service.cxml.CxmlException;
import com.namics.distrelec.b2b.core.eprocurement.service.cxml.DistCxmlService;
import com.namics.distrelec.b2b.core.eprocurement.service.cxml.impl.CxmlManager;
import com.namics.distrelec.b2b.core.eprocurement.service.cxml.impl.CxmlOutboundSection;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapAvailabilityService;
import com.namics.distrelec.b2b.core.inout.erp.mock.MockSIHybrisV1Out;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCartService;
import com.namics.distrelec.distrelecoci.utils.OutboundSection;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Test the {@link DistCxmlService} class.
 * 
 * @author pbueschi, Namics AG
 */
@IntegrationTest
public class DefaultDistCxmlServiceTest extends ServicelayerTransactionalTest {

    @Resource
    private ProductService productService;

    @Resource
    private CartService cartService;

    @Resource
    private DistCommerceCartService commerceCartService;

    @Resource
    private UserService userService;

    @Resource
    private SessionService sessionService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private DistCxmlService distCxmlService;

    @Resource
    private CMSSiteService cmsSiteService;

    @Resource(name = "sap.availabilityService")
    private SapAvailabilityService sapAvailabilityService;

    private MockSIHybrisV1Out mockSIHybrisV1Out = new MockSIHybrisV1Out();
    
    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bcore/test/testDistEProcurement.impex", "utf-8");
        sapAvailabilityService.setWebServiceClient(mockSIHybrisV1Out);

        // set current cms site
        final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_CH", false);
        commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("CHF"));
        sessionService.setAttribute(Europe1Constants.PARAMS.UPG, currentCMSSite.getUserPriceGroup());
        sessionService.setAttribute(Europe1Constants.PARAMS.UTG, currentCMSSite.getUserTaxGroup());
        cmsSiteService.setCurrentSite(cmsSiteService.getSites().iterator().next());

        userService.setCurrentUser(userService.getUserForUID("cxmluser"));

        final ProductModel product = productService.getProductForCode("14233524");
        final CartModel cart = cartService.getSessionCart();
        commerceCartService.addToCart(cart, product, 1, product.getUnit(), true, false, false, "custref1", false);

    }

    @Test
    public void testCxmlLogin() throws CalculationException, CxmlException {
        setCxmlSessionParameters(MapUtils.EMPTY_MAP);

        MockHttpServletRequest dummyRequest = new MockHttpServletRequest();

        dummyRequest.setParameter(CxmlManager.PARAM_HOOK_URL, "http://dummy.org");
        dummyRequest.setParameter("USERNAME", "cxmluser");
        dummyRequest.setParameter("PASSWORD", "12341234");

        dummyRequest.getSession().setAttribute("jalosession", JaloSession.getCurrentSession());

        distCxmlService.doCxmlLogin(dummyRequest);

        Assert.assertTrue(distCxmlService.isCxmlCustomer());


    }

    @Test
    public void testGenerateCxmlForm() throws CalculationException, CxmlException {
        setCxmlSessionParameters(MapUtils.EMPTY_MAP);
        final String ociForm = distCxmlService.createCxmlOrderMessage();
        Assert.assertTrue(StringUtils.isNotBlank(ociForm));
    }

    @Test
    public void testGetCxmlRedirectUrlEmpty() throws CxmlException {
        setCxmlSessionParameters(MapUtils.EMPTY_MAP);
        final String ociRedirectUrl = distCxmlService.getCxmlRedirectUrl();
        Assert.assertTrue(StringUtils.equals(ociRedirectUrl, "/"));
    }

    /* Helper method to create OCI session with necessary attributes */
    private void setCxmlSessionParameters(final Map<String, String> additionalParams) throws CxmlException {
        final JaloSession jalosession = JaloSession.getCurrentSession();
        jalosession.setAttribute(CxmlManager.ATTR_IS_CXML_LOGIN, Boolean.TRUE);

        final Map<String, String> param = new HashMap<String, String>();
        param.put(OutboundSection.Fields.CALLER, "~caller");
        param.put(OutboundSection.Fields.OK_CODE, "~okcode");
        param.put(OutboundSection.Fields.TARGET, "~target");
        if (MapUtils.isNotEmpty(additionalParams)) {
            param.putAll(additionalParams);
        }

        CxmlOutboundSection outboundSection;
        outboundSection = new CxmlOutboundSection(param, CxmlManager.PARAM_HOOK_URL);
        jalosession.setAttribute(CxmlManager.ATTR_OUTBOUND_SECTION_DATA, outboundSection);

    }
}
