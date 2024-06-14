/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.eprocurement.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapOrderCalculationService;
import com.namics.distrelec.b2b.core.inout.erp.mock.MockSIHybrisV1Out;
import com.namics.distrelec.b2b.cxml.generated.CXML;
import com.namics.distrelec.b2b.facades.eprocurement.DistAribaFacade;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the {@link DistAribaFacade} class.
 *
 * @author pbueschi, Namics AG
 */
@IntegrationTest
public class DefaultDistAribaFacadeTest extends ServicelayerTransactionalTest {

    @Resource
    private CartService cartService;

    @Resource
    private UserService userService;

    @Resource
    private CMSSiteService cmsSiteService;

    @Resource
    private SessionService sessionService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private DistAribaFacade distAribaFacade;

    @Resource
    private DistAribaService distAribaService;

    @Resource(name="sap.orderCalculationService" )
    private SapOrderCalculationService sapOrderCalculationService;

    private final MockSIHybrisV1Out mockSIHybrisV1Out = new MockSIHybrisV1Out();

    @Before
    public void setUp() throws Exception {
        sapOrderCalculationService.setWebServiceClient(mockSIHybrisV1Out);
        importCsv("/distrelecB2Bcore/test/testDistEProcurement.impex", "utf-8");

        // set current cms site
        final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_CH", false);
        commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("CHF"));
        sessionService.setAttribute(Europe1Constants.PARAMS.UPG, currentCMSSite.getUserPriceGroup());
        sessionService.setAttribute(Europe1Constants.PARAMS.UTG, currentCMSSite.getUserTaxGroup());
        cmsSiteService.setCurrentSite(cmsSiteService.getSites().iterator().next());

        // login ariba customer
        final String aribaSetupRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cXML payloadID=\"958075346970@www.bigbuyer.com\" timestamp=\"2005-06-14T12:57:09-07:00\" version=\"1.1.010\"><Header><From><Credential domain=\"NetworkID\"><Identity>AN01000002792</Identity></Credential></From><To><Credential domain=\"DUNS\"><Identity>12345678</Identity></Credential></To><Sender><Credential domain=\"AribaNetworkUserId\"><Identity>aribaUser</Identity><SharedSecret>12341234</SharedSecret></Credential><UserAgent>Distrelec</UserAgent></Sender></Header><Request deploymentMode=\"test\"><PunchOutSetupRequest operation=\"create\"><BuyerCookie>1J3YVWU9QWMTB</BuyerCookie><Extrinsic name=\"CartCode\"/><BrowserFormPost><URL>http://www.ariba.service/</URL></BrowserFormPost><ShipTo><Address addressID=\"001\"><Name xml:lang=\"de\">Ariba User</Name><PostalAddress><DeliverTo>Ariba Unit</DeliverTo><Street>Teststrassse 42</Street><City>Zürich</City><State/><Country isoCountryCode=\"CH\">Schweiz</Country></PostalAddress></Address></ShipTo><ItemOut quantity=\"2\" lineNumber=\"0000000001\" requestedDeliveryDate=\"2009-03-12T10-03-39+01:00\"><ItemID><SupplierPartID>14233524</SupplierPartID></ItemID><ItemDetail><UnitPrice><Money currency=\"EUR\">157.95</Money></UnitPrice><Description xml:lang=\"de\">asdf</Description><UnitOfMeasure>PC</UnitOfMeasure><Classification domain=\"domain\">value</Classification><ManufacturerName xml:lang=\"de\">manufacturer name</ManufacturerName></ItemDetail></ItemOut></PunchOutSetupRequest></Request></cXML>";
        final String token = distAribaFacade.getAribaToken("aribaUser", "12341234", aribaSetupRequest);
        final UserModel aribaUser = distAribaService.aribaLogin(token);
        userService.setCurrentUser(aribaUser);
        Assert.assertNotNull(aribaUser);

        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        ServletContext mockContext = mock(ServletContext.class);
        when(servletRequest.getServletContext())
                .thenReturn(mockContext);
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(servletRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @Test
    public void testGetCredentialsFromDistAribaOutSetupRequest() {
        final String aribaSetupRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cXML payloadID=\"958075346970@www.bigbuyer.com\" timestamp=\"2005-06-14T12:57:09-07:00\" version=\"1.1.010\"><Header><From><Credential domain=\"NetworkID\"><Identity>AN01000002792</Identity></Credential></From><To><Credential domain=\"DUNS\"><Identity>12345678</Identity></Credential></To><Sender><Credential domain=\"AribaNetworkUserId\"><Identity>aribaUser</Identity><SharedSecret>12341234</SharedSecret></Credential><UserAgent>Distrelec</UserAgent></Sender></Header><Request deploymentMode=\"test\"><PunchOutSetupRequest operation=\"create\"><BuyerCookie>1J3YVWU9QWMTB</BuyerCookie><Extrinsic name=\"CartCode\"/><BrowserFormPost><URL>http://www.ariba.service/</URL></BrowserFormPost><ShipTo><Address addressID=\"001\"><Name xml:lang=\"de\">Ariba User</Name><PostalAddress><DeliverTo>Ariba Unit</DeliverTo><Street>Teststrassse 42</Street><City>Zürich</City><State/><Country isoCountryCode=\"CH\">Schweiz</Country></PostalAddress></Address></ShipTo><ItemOut quantity=\"2\" lineNumber=\"0000000001\" requestedDeliveryDate=\"2009-03-12T10-03-39+01:00\"><ItemID><SupplierPartID>14233524</SupplierPartID></ItemID><ItemDetail><UnitPrice><Money currency=\"EUR\">157.95</Money></UnitPrice><Description xml:lang=\"de\">asdf</Description><UnitOfMeasure>PC</UnitOfMeasure><Classification domain=\"domain\">value</Classification><ManufacturerName xml:lang=\"de\">manufacturer name</ManufacturerName></ItemDetail></ItemOut></PunchOutSetupRequest></Request></cXML>";
        final Map<String, String> credentials = distAribaFacade.getCredentialsFromDistAribaOutSetupRequest(aribaSetupRequest);
        Assert.assertTrue(MapUtils.isNotEmpty(credentials));
        Assert.assertEquals(credentials.get(DistConstants.Ariba.URL_PARAM_KEY_USERNAME), "AN01000002792");
        Assert.assertEquals(credentials.get(DistConstants.Ariba.URL_PARAM_KEY_PASSWORD), "12341234");
    }

    @Test
    public void testSetUpAribaCart() {
        final CXML cXmlPunchOutSetupRequest = distAribaFacade.parseAribaSetupRequest();
        final String aribaCartCode = distAribaFacade.setUpAribaCart(cXmlPunchOutSetupRequest, false, "");
        Assert.assertTrue(StringUtils.isBlank(aribaCartCode));
        Assert.assertTrue(CollectionUtils.isEmpty(cartService.getSessionCart().getEntries()));
    }

    @Test
    @Ignore
    public void testSetUpAribaCartBasketFromCustomer() {
        final CXML cXmlPunchOutSetupRequest = distAribaFacade.parseAribaSetupRequest();
        final String aribaCartCode = distAribaFacade.setUpAribaCart(cXmlPunchOutSetupRequest, true, "");
        Assert.assertTrue(StringUtils.isNotBlank(aribaCartCode));
        Assert.assertTrue(CollectionUtils.isNotEmpty(cartService.getSessionCart().getEntries()));
    }
}
