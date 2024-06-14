/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement;

import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService;
import com.namics.distrelec.distrelecoci.constants.DistrelecOciConstants;
import com.namics.distrelec.distrelecoci.exception.OciException;
import com.namics.distrelec.distrelecoci.utils.OutboundSection;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Test the {@link DistOciService} class.
 * 
 * @author pbueschi, Namics AG
 */
@IntegrationTest
public class DefaultDistOciServiceTest extends ServicelayerTransactionalTest {

    @Resource
    private ProductService productService;

    @Resource
    private CartService cartService;

    @Resource
    private CommerceCartService commerceCartService;

    @Resource
    private UserService userService;

    @Resource
    private SessionService sessionService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private DistOciService distOciService;

    @Resource
    private CMSSiteService cmsSiteService;

    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bcore/test/testDistEProcurement.impex", "utf-8");

        // set current cms site
        final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_CH", false);
        commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("CHF"));
        sessionService.setAttribute(Europe1Constants.PARAMS.UPG, currentCMSSite.getUserPriceGroup());
        sessionService.setAttribute(Europe1Constants.PARAMS.UTG, currentCMSSite.getUserTaxGroup());
        cmsSiteService.setCurrentSite(cmsSiteService.getSites().iterator().next());

        userService.setCurrentUser(userService.getUserForUID("ociuser"));

    }

    @Test
    public void testGenerateOciForm() throws CalculationException, OciException {
        setOciSessionParameters(MapUtils.EMPTY_MAP);
        final String ociForm = distOciService.generateOciForm();
        Assert.assertTrue(StringUtils.isNotBlank(ociForm));
    }

    @Test
    public void testGetOciRedirectUrl() throws OciException {
        final Map<String, String> param = new HashMap<String, String>();
        param.put(DistrelecOciConstants.FUNCTION, DistrelecOciConstants.DETAIL);
        param.put(DistrelecOciConstants.PRODUCTID, "14233524");
        setOciSessionParameters(param);
        final String ociRedirectUrl = distOciService.getOciRedirectUrl();
        Assert.assertTrue(StringUtils.contains(ociRedirectUrl, "14233524"));
    }

    @Test
    public void testGetOciRedirectUrlEmpty() throws OciException {
        setOciSessionParameters(MapUtils.EMPTY_MAP);
        final String ociRedirectUrl = distOciService.getOciRedirectUrl();
        Assert.assertTrue(StringUtils.equals(ociRedirectUrl, "/"));
    }

    @Test
    public void testGetOciFunctionForm() throws OciException {
        final Map<String, String> param = new HashMap<String, String>();
        param.put(DistrelecOciConstants.FUNCTION, DistrelecOciConstants.BACKGROUND_SEARCH);
        param.put(DistrelecOciConstants.SEARCHSTRING, "14233524");
        setOciSessionParameters(param);

        final String ociFunctionForm = distOciService.getOciFunctionForm(new ArrayList<ProductModel>());
        Assert.assertTrue(StringUtils.isNotBlank(ociFunctionForm));
    }

    @Test
    public void testGetOciFunctionFormEmpty() throws OciException {
        setOciSessionParameters(MapUtils.EMPTY_MAP);
        final String ociFunctionForm = distOciService.getOciFunctionForm(new ArrayList<ProductModel>());
        Assert.assertTrue(StringUtils.isBlank(ociFunctionForm));
    }

    /* Helper method to create OCI session with necessary attributes */
    private void setOciSessionParameters(final Map<String, String> additionalParams) throws OciException {
        final JaloSession jalosession = JaloSession.getCurrentSession();
        jalosession.setAttribute(DistrelecOciConstants.IS_OCI_LOGIN, Boolean.TRUE);

        final Map<String, String> param = new HashMap<String, String>();
        param.put(OutboundSection.Fields.CALLER, "~caller");
        param.put(OutboundSection.Fields.OK_CODE, "~okcode");
        param.put(OutboundSection.Fields.TARGET, "~target");
        param.put(DistrelecOciConstants.OCI_VERSION, "4.0");
        if (MapUtils.isNotEmpty(additionalParams)) {
            param.putAll(additionalParams);
        }

        final OutboundSection outboundSection = new OutboundSection(param, "hook_url");
        jalosession.setAttribute(DistrelecOciConstants.OUTBOUND_SECTION_DATA, outboundSection);
    }
}
