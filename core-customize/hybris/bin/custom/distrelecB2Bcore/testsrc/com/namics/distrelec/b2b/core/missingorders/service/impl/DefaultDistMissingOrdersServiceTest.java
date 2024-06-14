package com.namics.distrelec.b2b.core.missingorders.service.impl;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.distrelec.webservice.if15.v1.OrderReadEntry;
import com.distrelec.webservice.if15.v1.ReadOrderRequestV2;
import com.distrelec.webservice.if15.v1.ReadOrderResponseV2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.namics.distrelec.b2b.core.inout.erp.CheckoutService;
import com.namics.distrelec.b2b.core.inout.erp.OrderService;
import com.namics.distrelec.b2b.core.missingorders.service.MissingOrdersMatchResult;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

@IntegrationTest
public class DefaultDistMissingOrdersServiceTest extends ServicelayerTransactionalTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Resource
    private DefaultDistMissingOrdersService distMissingOrdersService;

    @Resource
    private SessionService sessionService;

    @Resource
    private ModelService modelService;

    @Resource
    FlexibleSearchService flexibleSearchService;

    @Mock
    private OrderService orderErpService;

    private CurrencyModel euro;

    private UnitModel piece;

    private B2BCustomerModel testCustomer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        euro = modelService.create(CurrencyModel.class);
        euro.setIsocode("EUR");
        euro.setSymbol("€");
        modelService.save(euro);

        piece = modelService.create(UnitModel.class);
        piece.setCode("PC");
        piece.setUnitType("Quantity");
        modelService.save(piece);

        testCustomer = createTestCustomer();

        distMissingOrdersService.setSapOrderService(orderErpService);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        when(mockRequest.getServletContext())
                .thenReturn(mockServletContext);
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        ReadOrderResponseV2 mockReadOrderResponse = mock(ReadOrderResponseV2.class);
        OrderReadEntry mockOrderEntry1 = mock(OrderReadEntry.class);
        when(mockOrderEntry1.getMaterialNumber())
                .thenReturn("MATCH1");
        when(mockOrderEntry1.getOrderQuantity())
                .thenReturn(1L);
        OrderReadEntry mockOrderEntry2 = mock(OrderReadEntry.class);
        when(mockOrderEntry2.getMaterialNumber())
                .thenReturn("MATCH2");
        when(mockOrderEntry2.getOrderQuantity())
                .thenReturn(2L);
        when(mockReadOrderResponse.getOrderEntries()).thenReturn(asList(mockOrderEntry1, mockOrderEntry2));
        when(mockReadOrderResponse.getOrderId())
                .thenReturn("ERP_ORDER_ID");
        when(orderErpService.readOrder(any()))
                .thenReturn(mockReadOrderResponse);
    }

    @Test
    public void testFindMissingOrdersMissingNumberOfDays() {
        thrown.expect(IllegalArgumentException.class);
        distMissingOrdersService.matchMissingOrders(null, true, new Date(), new Date());
    }

    @Test
    public void testFindMissingOrdersMissingStartAndEndDate() {
        thrown.expect(IllegalArgumentException.class);
        distMissingOrdersService.matchMissingOrders(3, false, null, null);
    }

    @Test
    public void testFindMissingOrdersInDateRange() throws ParseException {
        Date beforeDateWithoutErpOrderCode = createDate("01-01-2020");
        Date beforeDateWithErpOrderCode = createDate("02-01-2020");
        Date inDateWithoutErpOrderCode = createDate("10-01-2020");
        Date inDateWithErpOrderCode = createDate("11-01-2020");
        Date afterDateWithoutErpOrderCode = createDate("20-01-2020");
        Date afterDateWithErpOrderCode = createDate("20-01-2020");

        createOrder("BEFORE_WITHOUT_ID", beforeDateWithoutErpOrderCode, null);
        createOrder("BEFORE_WITH_ID", beforeDateWithErpOrderCode, "BEFORE_ERP_ID");
        createOrder("IN_WITHOUT_ID", inDateWithoutErpOrderCode, null);
        createOrder("IN_WITHOUT_ID_EXISTS_IN_ERP", inDateWithoutErpOrderCode, null);
        createOrder("IN_WITH_ID", inDateWithErpOrderCode, "IN_ERP_ID");
        createOrder("AFTER_WITHOUT_ID", afterDateWithoutErpOrderCode, null);
        createOrder("AFTER_WITH_ID", afterDateWithErpOrderCode, "AFTER_ERP_ID");

        ReadOrderRequestV2 readOrderNotExists = new ReadOrderRequestV2();
        readOrderNotExists.setOrderId("IN_WITHOUT_ID");
        readOrderNotExists.setWebshopOrderFlag(true);
        readOrderNotExists.setCustomerId("TEST_ERP_CUSTOMER");
        readOrderNotExists.setSalesOrganization("TEST");
        when(orderErpService.readOrder(eq(readOrderNotExists)))
                .thenReturn(null);

        ReadOrderRequestV2 readOrderExists = new ReadOrderRequestV2();
        readOrderExists.setOrderId("IN_WITHOUT_ID_EXISTS_IN_ERP");
        readOrderExists.setWebshopOrderFlag(true);
        readOrderExists.setCustomerId("TEST_ERP_CUSTOMER");
        readOrderExists.setSalesOrganization("TEST");

        ReadOrderResponseV2 readOrderResponse = new ReadOrderResponseV2();
        readOrderResponse.setOrderId("ERP_ORDER_ID");
        when(orderErpService.readOrder(argThat(new ReadOrderRequestV2Matcher(readOrderNotExists))))
                .thenReturn(null);
        when(orderErpService.readOrder(argThat(new ReadOrderRequestV2Matcher(readOrderExists))))
                .thenReturn(readOrderResponse);

        Date searchFromDate = createDate("09-01-2020");
        Date searchToDate = createDate("12-01-2020");
        MissingOrdersMatchResult matchResult = distMissingOrdersService.matchMissingOrders(null, false, searchFromDate, searchToDate);

        assertThat(matchResult.getOrdersFoundInErp())
                .hasSize(1);

        OrderModel orderWithoutId = matchResult.getOrdersFoundInErp().stream()
                .filter(order -> order.getCode().equals("IN_WITHOUT_ID_EXISTS_IN_ERP"))
                .findFirst().orElse(null);
        assertThat(orderWithoutId)
                .isNotNull();
        assertThat(orderWithoutId.getErpOrderCode())
                .isEqualTo("ERP_ORDER_ID");

        assertThat(matchResult.getOrdersMissingInErp())
                .hasSize(1);
        assertThat(matchResult.getOrdersMissingInErp().get(0).getCode())
                .isEqualTo("IN_WITHOUT_ID");
        assertThat(matchResult.getOrdersMissingInErp().get(0).getErpOrderCode())
                .isNull();
    }

    @Test
    public void testFindMissingOrdersInPreviousDays() {
        Date before1day = createDateNumberOfDaysAgo(1);
        Date before5days = createDateNumberOfDaysAgo(5);

        createOrder("BEFORE_5_DAYS_WITHOUT_ID", before5days, null);
        createOrder("BEFORE_5_DAYS_WITH_ID", before5days, "BEFORE_5_DAYS");
        createOrder("BEFORE_1_DAY_WITHOUT_ID", before1day, null);
        createOrder("BEFORE_1_DAY_WITHOUT_ID_EXIST_IN_ERP", before1day, null);
        createOrder("BEFORE_1_DAY_WITH_ID", before1day, "BEFORE_1_DAY");

        ReadOrderRequestV2 readOrderNotExists = new ReadOrderRequestV2();
        readOrderNotExists.setOrderId("BEFORE_1_DAY_WITHOUT_ID");
        readOrderNotExists.setWebshopOrderFlag(true);
        readOrderNotExists.setCustomerId("TEST_ERP_CUSTOMER");
        readOrderNotExists.setSalesOrganization("TEST");
        when(orderErpService.readOrder(eq(readOrderNotExists)))
                .thenReturn(null);

        ReadOrderRequestV2 readOrderExists = new ReadOrderRequestV2();
        readOrderExists.setOrderId("BEFORE_1_DAY_WITHOUT_ID_EXIST_IN_ERP");
        readOrderExists.setWebshopOrderFlag(true);
        readOrderExists.setCustomerId("TEST_ERP_CUSTOMER");
        readOrderExists.setSalesOrganization("TEST");

        ReadOrderResponseV2 readOrderResponse = new ReadOrderResponseV2();
        readOrderResponse.setOrderId("ERP_ORDER_ID");
        when(orderErpService.readOrder(argThat(new ReadOrderRequestV2Matcher(readOrderNotExists))))
                .thenReturn(null);
        when(orderErpService.readOrder(argThat(new ReadOrderRequestV2Matcher(readOrderExists))))
                .thenReturn(readOrderResponse);

        MissingOrdersMatchResult matchResult = distMissingOrdersService.matchMissingOrders(2, true, null, null);

        assertThat(matchResult.getOrdersFoundInErp())
                .hasSize(1);
        assertThat(matchResult.getOrdersFoundInErp().get(0).getCode())
                .isEqualTo("BEFORE_1_DAY_WITHOUT_ID_EXIST_IN_ERP");
        assertThat(matchResult.getOrdersFoundInErp().get(0).getErpOrderCode())
                .isEqualTo("ERP_ORDER_ID");

        assertThat(matchResult.getOrdersMissingInErp())
                .hasSize(1);
        assertThat(matchResult.getOrdersMissingInErp().get(0).getCode())
                .isEqualTo("BEFORE_1_DAY_WITHOUT_ID");
        assertThat(matchResult.getOrdersMissingInErp().get(0).getErpOrderCode())
                .isNull();

        OrderModel updatedExistingOrder = findOrderByCode("BEFORE_1_DAY_WITHOUT_ID_EXIST_IN_ERP");
        assertThat(updatedExistingOrder.getErpOrderCode())
                .isEqualTo("ERP_ORDER_ID");
    }

    @Test
    public void testCreateOrders() {
        CheckoutService checkoutService = mock(CheckoutService.class);
        distMissingOrdersService.setCheckoutService(checkoutService);

        List<OrderModel> orders = asList(createOrder("ORDER1", new Date(), null),
                createOrder("ORDER2", new Date(), null),
                createOrder("ORDER3", new Date(), null));

        distMissingOrdersService.createSapOrders(orders);
        verify(checkoutService, times(3)).exportOrder(any());
    }

    private OrderModel findOrderByCode(String orderCode) {
        return executeWithoutInterceptors(() -> {
            String queryText = "SELECT {pk} FROM {Order AS} WHERE {code}=?orderCode";
            FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);
            query.addQueryParameter("orderCode", orderCode);
            return flexibleSearchService.searchUnique(query);
        });
    }

    private Date createDateNumberOfDaysAgo(int numberOfDays) {
        Instant now = Instant.now();
        return Date.from(now.minus(numberOfDays, ChronoUnit.DAYS));
    }

    private Date createDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.parse(date);
    }

    private OrderModel createOrder(String orderCode, Date creationDate, String erpOrderCode) {
        return executeWithoutInterceptors(() -> {
            OrderModel order = modelService.create(OrderModel.class);
            order.setCode(orderCode);
            order.setDate(creationDate);
            order.setErpOrderCode(erpOrderCode);
            order.setCurrency(euro);
            order.setUser(testCustomer);
            modelService.save(order);
            return order;
        });
    }

    private B2BCustomerModel createTestCustomer() {
        return executeWithoutInterceptors(() -> {
            B2BCustomerModel customer = modelService.create(B2BCustomerModel.class);
            customer.setUid("testCustomer");
            customer.setDefaultB2BUnit(createTestUnit());
            modelService.save(customer);
            return customer;
        });
    }

    private B2BUnitModel createTestUnit() {
        return executeWithoutInterceptors(() -> {
            B2BUnitModel unit = modelService.create(B2BUnitModel.class);
            unit.setUid("testCustomer");
            unit.setErpCustomerID("TEST_ERP_CUSTOMER");
            unit.setSalesOrg(createTestSalesOrg());
            modelService.save(unit);
            return unit;
        });
    }


    private DistSalesOrgModel createTestSalesOrg() {
        return executeWithoutInterceptors(() -> {
            DistSalesOrgModel salesOrg = modelService.create(DistSalesOrgModel.class);
            salesOrg.setCode("TEST");
            modelService.save(salesOrg);
            return salesOrg;
        });
    }

    private <T> T executeWithoutInterceptors(Callable<T> callable) {
        Map<String, Object> params = ImmutableMap.of(InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_TYPES,
                ImmutableSet.of(InterceptorExecutionPolicy.InterceptorType.PREPARE,
                        InterceptorExecutionPolicy.InterceptorType.VALIDATE,
                        InterceptorExecutionPolicy.InterceptorType.INIT_DEFAULTS,
                        InterceptorExecutionPolicy.InterceptorType.LOAD,
                        InterceptorExecutionPolicy.InterceptorType.REMOVE));
        return sessionService.executeInLocalViewWithParams(params, new SessionExecutionBody() {
            @Override
            public Object execute() {
                try {
                    return callable.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static class ReadOrderRequestV2Matcher implements ArgumentMatcher<ReadOrderRequestV2> {

        private final ReadOrderRequestV2 original;

        public ReadOrderRequestV2Matcher(ReadOrderRequestV2 original) {
            this.original = original;
        }

        @Override
        public boolean matches(ReadOrderRequestV2 compareTo) {
            if (compareTo != null) {
                return Objects.equals(original.getCustomerId(), compareTo.getCustomerId())
                        && Objects.equals(original.getOrderId(), compareTo.getOrderId())
                        && Objects.equals(original.getSalesOrganization(), compareTo.getSalesOrganization())
                        && Objects.equals(original.isWebshopOrderFlag(), compareTo.isWebshopOrderFlag());
            }
            return false;
        }
    }

}
