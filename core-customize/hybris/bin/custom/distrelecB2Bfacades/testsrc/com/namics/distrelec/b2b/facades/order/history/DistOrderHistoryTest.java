/*
 * Copyright 2018 Distrelec. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.history;

import com.distrelec.webservice.if15.v1.*;
import com.distrelec.webservice.if19.v1.ItemList;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderRequest;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderResponse;
import com.distrelec.webservice.if19.v1.ReturnReasons;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.ReturnOnlineService;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapOrderService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.facades.order.impl.SapOrderHistoryFacade;
import com.namics.distrelec.b2b.facades.search.converter.ReadOrderResponseWraper;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.DistRMAEntryData;
import de.hybris.platform.commercefacades.order.data.DistReturnReasonData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;

import javax.annotation.Resource;
import javax.xml.bind.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@IntegrationTest
public class DistOrderHistoryTest extends ServicelayerTransactionalTest {

	@InjectMocks
	private SapOrderHistoryFacade sapOrderHistoryFacade;

	@Mock
	private SapOrderService sapOrderService;

	@Mock
	private OrderSearchResponseV2 orderSearchResponse;

	@Mock
	private B2BCustomerModel currentUserModel;

	@Mock
	private ReadOrderResponseV2 readOrderResponse;

	@Mock
	private Converter<ReadOrderResponseWraper, OrderData> sapReadOrderResponseOrderHistoryConverter;

	@Mock
	private Converter<OpenOrders, OrderHistoryData> sapReadAllOpenOrdersResponseConverter;

	@Mock
	private Converter<OrdersSearchLine, OrderHistoryData> sapSearchOrderHistoryConverter;

	@Mock
	private Converter<DistOrderHistoryPageableData, OrderSearchRequestV2> sapSearchOrderRequestConverter;

	@Mock
	private Converter<ItemList, DistRMAEntryData> defaultDistRMADataConverter;

	@Mock
	private Converter<ReturnReasons, DistReturnReasonData> defaultReturnReasonsConverter;

	@Mock
	private Converter<OpenOrders, OrderData> sapReadAllOpenOrdersDetailsResponse;

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
	private CMSSiteService cmsSiteService;

	@Resource
	private BaseSiteService baseSiteService;

	@Mock
	private DistSalesOrgModel mockedSalesOrg;

	@Resource(name = "distSalesOrgService")
	private DistSalesOrgService salesOrgService;

	@Mock
	private DistSalesOrgService distSalesOrgService;

	@Mock
	private SIHybrisIF15Out webServiceIF15Client;

	private final static Logger LOG = LogManager.getLogger(DistOrderHistoryTest.class);

	@Before
	public void initMocks() throws Exception {
		CacheManager.getInstance()
				.addCacheIfAbsent(new Cache(DistConstants.CacheName.ORDER_SEARCH, 100000, false, false, 300, 0));
		CacheManager.getInstance()
				.addCacheIfAbsent(new Cache(DistConstants.CacheName.RETURNS_ONLINE, 100000, false, false, 300, 0));

		MockitoAnnotations.initMocks(this);
		importCsv("/distrelecB2Bfacades/test/orderHistory/site.impex", "utf-8");
		importCsv("/distrelecB2Bfacades/test/orderHistory/b2bcustomer.impex", "utf-8");

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
		// set current cms site
		final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_CH", false);
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("CHF"));
		// sessionService.setAttribute(Europe1Constants.PARAMS.UPG,
		// currentCMSSite.getUserPriceGroup());
		sessionService.setAttribute(Europe1Constants.PARAMS.UTG, currentCMSSite.getUserTaxGroup());
		cmsSiteService.setCurrentSite(cmsSiteService.getSites().iterator().next());
		when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(salesOrg);
		when(b2buserService.getCurrentUser()).thenReturn(customer);
		sapOrderHistoryFacade.setUserService(userService);
		sapOrderHistoryFacade.setDistSalesOrgService(salesOrgService);
		cmsSiteService.setCurrentSite((CMSSiteModel) baseSiteService.getBaseSiteForUID("distrelec_CH"));
		final String responseResourcePath = "/distrelecB2Bfacades/test/orderHistory/response.xml";

		final JAXBElement<OrderSearchResponseV2> mockedResponse = (JAXBElement<OrderSearchResponseV2>) resourcePathToJaxbObject(
				responseResourcePath, ObjectFactory.class);
		when(sapOrderService.searchOrders(any())).thenReturn(mockedResponse.getValue());
		final RMAGetListForOrderRequest rmaGetListForOrderRequest = new RMAGetListForOrderRequest();
		rmaGetListForOrderRequest.setCustomerId(customer.getDefaultB2BUnit().getErpCustomerID());
		rmaGetListForOrderRequest.setSalesOrganization(salesOrg.getCode());
		rmaGetListForOrderRequest.setSessionLanguage(commonI18NService.getLanguage("EN").getIsocode().toUpperCase());
		final RMAGetListForOrderResponse rmaGetListForOrderResponse = new RMAGetListForOrderResponse();
		when(returnOnlineService.rmaListForOrder(rmaGetListForOrderRequest)).thenReturn(rmaGetListForOrderResponse);
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

	public static String jaxbObjectToString(final OrderSearchRequestV2 prototype,
			final Class<OrderSearchRequestV2> jaxbClass) throws JAXBException {
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

		final String requestResourcePath = "/distrelecB2Bfacades/test/orderHistory/request.xml";
		final String responseResourcePath = "/distrelecB2Bfacades/test/orderHistory/response.xml";

		// when
		final JAXBElement<OrderSearchRequestV2> request = (JAXBElement<OrderSearchRequestV2>) resourcePathToJaxbObject(
				requestResourcePath, ObjectFactory.class);
		final JAXBElement<OrderSearchResponseV2> mockedResponse = (JAXBElement<OrderSearchResponseV2>) resourcePathToJaxbObject(
				responseResourcePath, ObjectFactory.class);
		LOG.info("Requst:", request.getValue());
		if (validateRequest) {
			Mockito.when(webServiceIF15Client.if15SearchOrders(Matchers.eq(request.getValue())))
					.thenReturn(mockedResponse.getValue());
			Mockito.when(
					webServiceIF15Client.if15SearchOrders(AdditionalMatchers.not(Matchers.eq((request.getValue())))))
					.thenAnswer((final InvocationOnMock invocation) -> {
						final Object[] arguments = invocation.getArguments();
						final OrderSearchRequestV2 passedRequest = (OrderSearchRequestV2) arguments[0];
						final String expectedJaxbObjectToString = jaxbObjectToString(request.getValue(),
								OrderSearchRequestV2.class);
						final String actualJaxbObjectToString = jaxbObjectToString(passedRequest,
								OrderSearchRequestV2.class);
						LOG.info("expectedJaxbObjectToString: {}", expectedJaxbObjectToString);
						LOG.info("actualJaxbObjectToString: {}", actualJaxbObjectToString);
						assertEquals(expectedJaxbObjectToString, actualJaxbObjectToString);
						return mockedResponse;
					});
		} else {
			Mockito.when(webServiceIF15Client.if15SearchOrders(Mockito.any())).thenReturn(mockedResponse.getValue());
		}

		// do
		final DistOrderHistoryPageableData pageableDataIF15 = new DistOrderHistoryPageableData();
		pageableDataIF15.setContactId("0001296242");
		pageableDataIF15.setPageSize(10);
		pageableDataIF15.setSort("byDate"); // TODO Make DistInvoiceSearchRequestIF12Converter.BY_DATE public

		SearchPageData<OrderHistoryData> searchPageData;
		final String[] statuses = new String[2];
		statuses[0] = OrderStatus.ERP_STATUS_IN_PROGRESS.getCode();
		statuses[1] = OrderStatus.ERP_STATUS_SHIPPED.getCode();
		searchPageData = sapOrderHistoryFacade.getOrderHistory(pageableDataIF15, statuses);

		// verify
		assertEquals(mockedResponse.getValue().getResultTotalSize().intValue(), searchPageData.getResults().size());
		assertEquals(mockedResponse.getValue().getResultTotalSize().intValue(), searchPageData.getResults().size());
		// Mockito.verify(sapOrderHistoryFacade,
		// Mockito.times(1)).getOrderHistory(pageableDataIF15, statuses);
		// Mockito.verify(webServiceIF15Client,
		// Mockito.times(1)).if15SearchOrders(any());

	}

}
