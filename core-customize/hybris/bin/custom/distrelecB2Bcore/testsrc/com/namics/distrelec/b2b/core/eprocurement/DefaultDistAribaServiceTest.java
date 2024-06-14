/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapAvailabilityService;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapOrderCalculationService;
import com.namics.distrelec.b2b.core.inout.erp.mock.MockSIHybrisIF11V1Out;
import com.namics.distrelec.b2b.core.inout.erp.mock.MockSIHybrisV1Out;
import com.namics.distrelec.b2b.core.model.eprocurement.AribaCartModel;
import com.namics.distrelec.b2b.cxml.generated.CXML;
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
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Test the {@link DistAribaService} class.
 * 
 * @author pbueschi, Namics AG
 */
@IntegrationTest
public class DefaultDistAribaServiceTest extends ServicelayerTransactionalTest {

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
    private DistAribaService distAribaService;

    @Resource(name = "sap.orderCalculationService")
    private SapOrderCalculationService sapOrderCalculationService;

    @Resource(name = "sap.availabilityService")
    private SapAvailabilityService sapAvailabilityService;

    @Resource
    private BaseStoreService baseStoreService;

    private MockSIHybrisV1Out mockSIHybrisV1Out = new MockSIHybrisV1Out();
    private MockSIHybrisIF11V1Out mockSIHybrisIF11V1Out = new MockSIHybrisIF11V1Out();

    @Before
    public void setUp() throws Exception {
        sapOrderCalculationService.setWebServiceClient(mockSIHybrisV1Out);
        sapOrderCalculationService.setWebServiceClientIF11(mockSIHybrisIF11V1Out);
        sapAvailabilityService.setWebServiceClient(mockSIHybrisV1Out);

        importCsv("/distrelecB2Bcore/test/testDistEProcurement.impex", "utf-8");
        // set current cms site
        final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_CH", false);
        commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("CHF"));
        sessionService.setAttribute(Europe1Constants.PARAMS.UPG, currentCMSSite.getUserPriceGroup());
        sessionService.setAttribute(Europe1Constants.PARAMS.UTG, currentCMSSite.getUserTaxGroup());

        // login ariba customer
        final String aribaSetupRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cXML payloadID=\"958075346970@www.bigbuyer.com\" timestamp=\"2005-06-14T12:57:09-07:00\" version=\"1.1.010\"><Header><From><Credential domain=\"NetworkID\"><Identity>AN01000002792</Identity></Credential></From><To><Credential domain=\"DUNS\"><Identity>12345678</Identity></Credential></To><Sender><Credential domain=\"AribaNetworkUserId\"><Identity>aribaUser</Identity><SharedSecret>12341234</SharedSecret></Credential><UserAgent>Distrelec</UserAgent></Sender></Header><Request deploymentMode=\"test\"><PunchOutSetupRequest operation=\"create\"><BuyerCookie>1J3YVWU9QWMTB</BuyerCookie><Extrinsic name=\"CartCode\"/><BrowserFormPost><URL>http://www.ariba.service/</URL></BrowserFormPost><ShipTo><Address addressID=\"001\"><Name xml:lang=\"de\">Ariba User</Name><PostalAddress><DeliverTo>Ariba Unit</DeliverTo><Street>Teststrassse 42</Street><City>Zürich</City><State/><Country isoCountryCode=\"CH\">Schweiz</Country></PostalAddress></Address></ShipTo><ItemOut quantity=\"2\" lineNumber=\"0000000001\" requestedDeliveryDate=\"2009-03-12T10-03-39+01:00\"><ItemID><SupplierPartID>14233524</SupplierPartID></ItemID><ItemDetail><UnitPrice><Money currency=\"CHF\">53.5</Money></UnitPrice><Description xml:lang=\"de\">asdf</Description><UnitOfMeasure>PC</UnitOfMeasure><Classification domain=\"domain\">value</Classification><ManufacturerName xml:lang=\"de\">manufacturer name</ManufacturerName></ItemDetail></ItemOut></PunchOutSetupRequest></Request></cXML>";
        final String token = distAribaService.getAribaToken("aribauser", "12341234", aribaSetupRequest);
        final UserModel aribaUser = distAribaService.aribaLogin(token);
        userService.setCurrentUser(aribaUser);
        Assert.assertNotNull(aribaUser);

        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockServletResponse = mock(HttpServletResponse.class);
        ServletRequestAttributes mockRequestAttributes = new ServletRequestAttributes((mockServletRequest));
        RequestContextHolder.setRequestAttributes(mockRequestAttributes);
    }

    @Test
    public void testParseAribaSetupRequest() {
        final String aribaSetupRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cXML payloadID=\"958075346970@www.bigbuyer.com\" timestamp=\"2005-06-14T12:57:09-07:00\" version=\"1.1.010\"><Header><From><Credential domain=\"NetworkID\"><Identity>AN01000002792</Identity></Credential></From><To><Credential domain=\"DUNS\"><Identity>12345678</Identity></Credential></To><Sender><Credential domain=\"AribaNetworkUserId\"><Identity>aribaUser</Identity><SharedSecret>12341234</SharedSecret></Credential><UserAgent>Distrelec</UserAgent></Sender></Header><Request deploymentMode=\"test\"><PunchOutSetupRequest operation=\"create\"><BuyerCookie>1J3YVWU9QWMTB</BuyerCookie><Extrinsic name=\"CartCode\"/><BrowserFormPost><URL>http://www.ariba.service/</URL></BrowserFormPost><ShipTo><Address addressID=\"001\"><Name xml:lang=\"de\">Ariba User</Name><PostalAddress><DeliverTo>Ariba Unit</DeliverTo><Street>Teststrassse 42</Street><City>Zürich</City><State/><Country isoCountryCode=\"CH\">Schweiz</Country></PostalAddress></Address></ShipTo><ItemOut quantity=\"2\" lineNumber=\"0000000001\" requestedDeliveryDate=\"2009-03-12T10-03-39+01:00\"><ItemID><SupplierPartID>14233524</SupplierPartID></ItemID><ItemDetail><UnitPrice><Money currency=\"CHF\">53.5</Money></UnitPrice><Description xml:lang=\"de\">asdf</Description><UnitOfMeasure>PC</UnitOfMeasure><Classification domain=\"domain\">value</Classification><ManufacturerName xml:lang=\"de\">manufacturer name</ManufacturerName></ItemDetail></ItemOut></PunchOutSetupRequest></Request></cXML>";
        final CXML distAriabSetupRequest = distAribaService.parseAribaSetupRequest(aribaSetupRequest);
        Assert.assertNotNull(distAriabSetupRequest);
    }

    @Test
    public void testParseAribaSetupResponse() throws Exception {
        final File file = new File("response");
        final Writer writer = new PrintWriter(file);
        distAribaService.parseAribaSetupResponse(HttpStatus.OK, "https://ariba.service.com", "1234567890myPayloadId", writer);
        writer.flush();
        writer.close();
        final FileInputStream fileInputStream = new FileInputStream(file);
        final String setupResponse = IOUtils.toString(fileInputStream);
        Assert.assertTrue(StringUtils.isNotBlank(setupResponse));
    }

    @Test
    public void testGetAribaSetupRequestParameters() {
        final String aribaSetupRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cXML payloadID=\"958075346970@www.bigbuyer.com\" timestamp=\"2005-06-14T12:57:09-07:00\" version=\"1.1.010\"><Header><From><Credential domain=\"NetworkID\"><Identity>AN01000002792</Identity></Credential></From><To><Credential domain=\"DUNS\"><Identity>12345678</Identity></Credential></To><Sender><Credential domain=\"AribaNetworkUserId\"><Identity>aribaUser</Identity><SharedSecret>12341234</SharedSecret></Credential><UserAgent>Distrelec</UserAgent></Sender></Header><Request deploymentMode=\"test\"><PunchOutSetupRequest operation=\"create\"><BuyerCookie>1J3YVWU9QWMTB</BuyerCookie><Extrinsic name=\"CartCode\"/><BrowserFormPost><URL>http://www.ariba.service/</URL></BrowserFormPost><ShipTo><Address addressID=\"001\"><Name xml:lang=\"de\">Ariba User</Name><PostalAddress><DeliverTo>Ariba Unit</DeliverTo><Street>Teststrassse 42</Street><City>Zürich</City><State/><Country isoCountryCode=\"CH\">Schweiz</Country></PostalAddress></Address></ShipTo><ItemOut quantity=\"2\" lineNumber=\"0000000001\" requestedDeliveryDate=\"2009-03-12T10-03-39+01:00\"><ItemID><SupplierPartID>14233524</SupplierPartID></ItemID><ItemDetail><UnitPrice><Money currency=\"CHF\">53.5</Money></UnitPrice><Description xml:lang=\"de\">asdf</Description><UnitOfMeasure>PC</UnitOfMeasure><Classification domain=\"domain\">value</Classification><ManufacturerName xml:lang=\"de\">manufacturer name</ManufacturerName></ItemDetail></ItemOut></PunchOutSetupRequest></Request></cXML>";
        final CXML distAriabSetupRequest = distAribaService.parseAribaSetupRequest(aribaSetupRequest);
        final Map<String, String> setupRequestParameters = distAribaService.getAribaSetupRequestParameters(distAriabSetupRequest);
        Assert.assertNotNull(setupRequestParameters.get(DistConstants.Ariba.SetupRequestParams.PRODUCT_CODE));
    }

    @Test
    public void testSetUpAribaCart() {
        final String aribaCartCode = distAribaService.setUpAribaCart();
        Assert.assertTrue(StringUtils.isNotBlank(aribaCartCode));
        Assert.assertTrue(cartService.getSessionCart() instanceof AribaCartModel);
        Assert.assertTrue(CollectionUtils.isEmpty(cartService.getSessionCart().getEntries()));
    }

    @Test
    public void testSetUpAribaCartWithProducts() {
        final String aribaCartCode = setUpAribaCartWithProducts();
        Assert.assertTrue(StringUtils.isNotBlank(aribaCartCode));
        Assert.assertTrue(cartService.getSessionCart() instanceof AribaCartModel);
        Assert.assertTrue(CollectionUtils.isNotEmpty(cartService.getSessionCart().getEntries()));
    }

    @Test
    public void testParseAribaOrderMessage() {
        final String aribaCartCode = setUpAribaCartWithProducts();
        Assert.assertTrue(StringUtils.isNotBlank(aribaCartCode));
        final String aribaOrderMessage = distAribaService.parseAribaOrderMessage();
        Assert.assertTrue(StringUtils.isNotBlank(aribaOrderMessage));
    }

    /* Helper method to create ariba cart containing product(s) */
    private String setUpAribaCartWithProducts() {
        final String aribaSetupRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cXML payloadID=\"958075346970@www.bigbuyer.com\" timestamp=\"2005-06-14T12:57:09-07:00\" version=\"1.1.010\"><Header><From><Credential domain=\"NetworkID\"><Identity>AN01000002792</Identity></Credential></From><To><Credential domain=\"DUNS\"><Identity>12345678</Identity></Credential></To><Sender><Credential domain=\"AribaNetworkUserId\"><Identity>aribaUser</Identity><SharedSecret>12341234</SharedSecret></Credential><UserAgent>Distrelec</UserAgent></Sender></Header><Request deploymentMode=\"test\"><PunchOutSetupRequest operation=\"create\"><BuyerCookie>1J3YVWU9QWMTB</BuyerCookie><Extrinsic name=\"CartCode\"/><BrowserFormPost><URL>http://www.ariba.service/</URL></BrowserFormPost><ShipTo><Address addressID=\"001\"><Name xml:lang=\"de\">Ariba User</Name><PostalAddress><DeliverTo>Ariba Unit</DeliverTo><Street>Teststrassse 42</Street><City>Zürich</City><State/><Country isoCountryCode=\"CH\">Schweiz</Country></PostalAddress></Address></ShipTo><ItemOut quantity=\"2\" lineNumber=\"0000000001\" requestedDeliveryDate=\"2009-03-12T10-03-39+01:00\"><ItemID><SupplierPartID>14233524</SupplierPartID></ItemID><ItemDetail><UnitPrice><Money currency=\"CHF\">53.5</Money></UnitPrice><Description xml:lang=\"de\">asdf</Description><UnitOfMeasure>PC</UnitOfMeasure><Classification domain=\"domain\">value</Classification><ManufacturerName xml:lang=\"de\">manufacturer name</ManufacturerName></ItemDetail></ItemOut></PunchOutSetupRequest></Request></cXML>";
        final CXML distAriabSetupRequest = distAribaService.parseAribaSetupRequest(aribaSetupRequest);
        return distAribaService.setUpAribaCart(distAriabSetupRequest);
    }
}
