/*
 * Copyright 2018 Distrelec. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.history;

import com.distrelec.webservice.if15.v1.*;
import com.distrelec.webservice.if19.v1.ItemList;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsRequest;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsResponse;
import com.distrelec.webservice.if19.v1.ReturnReasons;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.ReturnOnlineService;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapOrderService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.order.DistOrderService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.order.impl.SapOrderHistoryFacade;
import com.namics.distrelec.b2b.facades.search.converter.ReadOrderResponseWraper;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.DistRMAEntryData;
import de.hybris.platform.commercefacades.order.data.DistReturnReasonData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@IntegrationTest
public class DistOrderHistoryReadOrderTest extends ServicelayerTransactionalTest {

    @InjectMocks
    private SapOrderHistoryFacade sapOrderHistoryFacade;

    @Mock
    private SapOrderService sapOrderService;

    @Mock
    private B2BCustomerModel currentUserModel;

    @Mock
    private ReadOrderResponseV2 readOrderResponse;

    @Resource
    private Converter<ReadOrderResponseWraper, OrderData> sapReadOrderResponseOrderHistoryConverter;

    @Mock
    private Converter<ItemList, DistRMAEntryData> defaultDistRMADataConverter;

    @Mock
    private Converter<ReturnReasons, DistReturnReasonData> defaultReturnReasonsConverter;

    @Mock
    private ReturnOnlineService returnOnlineService;

    @Resource
    private SessionService sessionService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private UserService userService;

    @Mock
    private UserService b2buserService;

    @Resource
    private DistrelecCMSSiteService cmsSiteService;

    @Resource
    private BaseSiteService baseSiteService;
    @Mock
    private OrderData orderData;
    @Mock
    private RMAGetOrderItemsResponse rmaGetOrderItemsResponse;

    @Mock
    private ReadAllOpenOrdersResponseV2 readAllOpenOrdersResponse;

    @Mock
    private DistSalesOrgModel mockedSalesOrg;

    @Resource(name = "distSalesOrgService")
    private DistSalesOrgService salesOrgService;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private SIHybrisIF15Out webServiceIF15Client;

    @Resource
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private DistOrderService distOrderService;

    private final static Logger LOG = LogManager.getLogger(DistOrderHistoryReadOrderTest.class);

    @Before
    public void initMocks() throws Exception {
        CacheManager.getInstance()
                .addCacheIfAbsent(new Cache(DistConstants.CacheName.ORDER_SEARCH, 100000, false, false, 300, 0));
        CacheManager.getInstance()
                .addCacheIfAbsent(new Cache(DistConstants.CacheName.RETURNS_ONLINE, 100000, false, false, 300, 0));

        MockitoAnnotations.initMocks(this);

        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletContext mockContext = mock(ServletContext.class);
        when(request.getServletContext())
                .thenReturn(mockContext);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        importCsv("/distrelecB2Bfacades/test/orderHistory/site.impex", "utf-8");

        // set current cms site
        final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_CH", false);
        commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("CHF"));
        sessionService.setAttribute(Europe1Constants.PARAMS.UPG, currentCMSSite.getUserPriceGroup());
        sessionService.setAttribute(Europe1Constants.PARAMS.UTG, currentCMSSite.getUserTaxGroup());
        cmsSiteService.setCurrentSite(cmsSiteService.getSites().iterator().next());
        importCsv("/distrelecB2Bfacades/test/orderHistory/b2bcustomer.impex", "utf-8");
        importCsv("/distrelecB2Bfacades/test/orderHistory/testData.impex", "utf-8");

        final DistSalesOrgModel salesOrg = salesOrgService.getSalesOrgForCode("7310");

        salesOrg.setCode("7310");
        salesOrg.setName("Distrelec CH");
        final B2BCustomerModel customer = (B2BCustomerModel) userService.getUserForUID("hans.muster@test.ch");
        customer.setSessionLanguage(commonI18NService.getLanguage("EN"));
        customer.setSessionCurrency(commonI18NService.getCurrency("CHF"));
        b2buserService.setCurrentUser(customer);
        sessionService.setAttribute("user", customer);
        sessionService.setAttribute(UserConstants.USER_SESSION_ATTR_KEY, customer);
        sessionService.setAttribute("sessionLanguage", commonI18NService.getLanguage("EN"));
        b2bUnitService.updateBranchInSession(sessionService.getCurrentSession(), customer);

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(salesOrg);
        when(b2buserService.getCurrentUser()).thenReturn(customer);
        sapOrderHistoryFacade.setUserService(userService);
        sapOrderHistoryFacade.setDistSalesOrgService(distSalesOrgService);
        sapOrderHistoryFacade.setSapReadOrderResponseOrderHistoryConverter(sapReadOrderResponseOrderHistoryConverter);
        cmsSiteService.setCurrentSite((CMSSiteModel) baseSiteService.getBaseSiteForUID("distrelec_CH"));
        final String responseResourcePath = "/distrelecB2Bfacades/test/orderHistory/readOrderResponse.xml";

        final JAXBElement<ReadOrderResponseV2> mockedResponse = (JAXBElement<ReadOrderResponseV2>) resourcePathToJaxbObject(
                responseResourcePath, ObjectFactory.class);
        when(sapOrderService.readOrder(any())).thenReturn(mockedResponse.getValue());
        final RMAGetOrderItemsRequest rmaGetOrderItemsRequest = new RMAGetOrderItemsRequest();
        rmaGetOrderItemsRequest.setCustomerId(customer.getDefaultB2BUnit().getErpCustomerID());
        rmaGetOrderItemsRequest.setSalesOrganization(salesOrg.getCode());
        rmaGetOrderItemsRequest.setSessionLanguage(commonI18NService.getLanguage("EN").getIsocode().toUpperCase());
        final RMAGetOrderItemsResponse rmaGetListForOrderResponse = new RMAGetOrderItemsResponse();
        when(returnOnlineService.rmaGetOrderItems(rmaGetOrderItemsRequest)).thenReturn(rmaGetListForOrderResponse);

        sapOrderHistoryFacade.setDistrelecCMSSiteService(cmsSiteService);
        sapOrderHistoryFacade.setDistOrderService(distOrderService);
        Configuration mockConfig = mock(Configuration.class);
        when(configurationService.getConfiguration())
                .thenReturn(mockConfig);
    }

    @After
    public void clearAllCaches() {
        CacheManager.getInstance().clearAll();
    }

    // Move an Utility class, taking classloader into account
    public Object resourcePathToJaxbObject(final String resourcePath, final Class resultClass) throws JAXBException {
        final InputStream resource = getClass().getResourceAsStream(resourcePath);
        final JAXBContext jaxbContext = JAXBContext.newInstance(resultClass);
        final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(resource);
    }

    public static String jaxbObjectToString(final ReadOrderRequestV2 prototype,
                                            final Class<ReadOrderRequestV2> jaxbClass) throws JAXBException {
        if (prototype == null) {
            return StringUtils.EMPTY;
        }
        final JAXBContext jaxbRequestContext = JAXBContext.newInstance(jaxbClass);
        final Marshaller jaxbRequestMarshaller = jaxbRequestContext.createMarshaller();
        final StringWriter w = new StringWriter();
        jaxbRequestMarshaller.marshal(prototype, w);
        final String marshalledString = w.toString();
        return marshalledString;
    }

    @Ignore // Ignored because of the cache.
    @Test
    public void onlyTest() throws IOException, JAXBException, P2FaultMessage {
        test(false);
    }

    @Test
    public void testAndVerify() throws IOException, JAXBException, P2FaultMessage {
        test(true);
    }

    private void test(final boolean validateRequest) throws IOException, JAXBException, P2FaultMessage {

        final String requestResourcePath = "/distrelecB2Bfacades/test/orderHistory/readOrderRequest.xml";
        final String responseResourcePath = "/distrelecB2Bfacades/test/orderHistory/readOrderResponse.xml";

        // when
        final JAXBElement<ReadOrderRequestV2> request = (JAXBElement<ReadOrderRequestV2>) resourcePathToJaxbObject(
                requestResourcePath, ObjectFactory.class);
        final JAXBElement<ReadOrderResponseV2> mockedResponse = (JAXBElement<ReadOrderResponseV2>) resourcePathToJaxbObject(
                responseResourcePath, ObjectFactory.class);

        if (validateRequest) {
            Mockito.when(webServiceIF15Client.if15ReadOrder(Matchers.eq(request.getValue())))
                    .thenReturn(mockedResponse.getValue());
            Mockito.when(webServiceIF15Client.if15ReadOrder(AdditionalMatchers.not(Matchers.eq((request.getValue())))))
                    .thenAnswer((final InvocationOnMock invocation) -> {
                        final Object[] arguments = invocation.getArguments();
                        final ReadOrderRequestV2 passedRequest = (ReadOrderRequestV2) arguments[0];
                        final String expectedJaxbObjectToString = jaxbObjectToString(request.getValue(),
                                ReadOrderRequestV2.class);
                        final String actualJaxbObjectToString = jaxbObjectToString(passedRequest,
                                ReadOrderRequestV2.class);
                        LOG.info("expectedJaxbObjectToString: {}", expectedJaxbObjectToString);
                        LOG.info("actualJaxbObjectToString: {}", actualJaxbObjectToString);
                        assertEquals(expectedJaxbObjectToString, actualJaxbObjectToString);
                        return mockedResponse;
                    });
        } else {
            Mockito.when(webServiceIF15Client.if15ReadOrder(Mockito.any())).thenReturn(mockedResponse.getValue());
        }

        final OrderData orderData = sapOrderHistoryFacade.getOrderForCode(request.getValue().getOrderId());

        assertEquals(mockedResponse.getValue().getOrderId(), request.getValue().getOrderId());
        assertEquals(orderData.getCode(), request.getValue().getOrderId());

    }

}
