package com.namics.distrelec.b2b.core.missingorders.dao.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class DefaultDistMissingOrdersDaoTest extends ServicelayerTransactionalTest {

    @Resource
    private DefaultDistMissingOrdersDao distMissingOrdersDao;

    @Resource
    private ModelService modelService;

    @Resource
    private SessionService sessionService;

    @Resource
    private UserService userService;

    private CurrencyModel euro;

    @Before
    public void setUp(){
        euro = modelService.create(CurrencyModel.class);
        euro.setIsocode("EUR");
        euro.setSymbol("€");
        modelService.save(euro);
    }

    @Test
    public void testFindMissingOrdersByDateRange() throws ParseException {
        Date beforeDateWithoutErpOrderCode = createDate("01-01-2020");
        Date beforeDateWithErpOrderCode = createDate("02-01-2020");
        Date inDateWithoutErpOrderCode = createDate("10-01-2020");
        Date inDateWithErpOrderCode = createDate("11-01-2020");
        Date afterDateWithoutErpOrderCode = createDate("20-01-2020");
        Date afterDateWithErpOrderCode = createDate("20-01-2020");

        createOrder("BEFORE_WITHOUT_ID", beforeDateWithoutErpOrderCode, null);
        createOrder("BEFORE_WITH_ID", beforeDateWithErpOrderCode, "BEFORE_ERP_ID");
        createOrder("IN_WITHOUT_ID", inDateWithoutErpOrderCode, null);
        createOrder("IN_WITH_ID", inDateWithErpOrderCode, "IN_ERP_ID");
        createOrder("AFTER_WITHOUT_ID", afterDateWithoutErpOrderCode, null);
        createOrder("AFTER_WITH_ID", afterDateWithErpOrderCode, "AFTER_ERP_ID");

        Date searchFromDate = createDate("09-01-2020");
        Date searchToDate = createDate("12-01-2020");
        List<OrderModel> missingOrders = distMissingOrdersDao.findMissingOrders(searchFromDate, searchToDate);

        assertThat(missingOrders)
                .hasSize(1);
        assertThat(missingOrders.get(0).getCode())
                .isEqualTo("IN_WITHOUT_ID");
    }

    @Test
    public void testFindMissingOrdersByNumnerOfDaysAgo(){
        Date before1day = createDateNumberOfDaysAgo(1);
        Date before5days = createDateNumberOfDaysAgo(5);

        createOrder("BEFORE_5_DAYS_WITHOUT_ID", before5days, null);
        createOrder("BEFORE_5_DAYS_WITH_ID", before5days, "BEFORE_5_DAYS");
        createOrder("BEFORE_1_DAY_WITHOUT_ID", before1day, null);
        createOrder("BEFORE_1_DAY_WITH_ID", before1day, "BEFORE_1_DAY");

        List<OrderModel> missingOrders = distMissingOrdersDao.findMissingOrders(2);

        assertThat(missingOrders)
                .hasSize(1);
        assertThat(missingOrders.get(0).getCode())
                .isEqualTo("BEFORE_1_DAY_WITHOUT_ID");
    }

    private Date createDateNumberOfDaysAgo(int numberOfDays){
        Instant now = Instant.now();
        return Date.from(now.minus(numberOfDays, ChronoUnit.DAYS));
    }

    private Date createDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.parse(date);
    }

    private void createOrder(String orderCode, Date creationDate, String erpOrderCode) {
        Map<String, Object> params = ImmutableMap.of(InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_TYPES,
                ImmutableSet.of(InterceptorExecutionPolicy.InterceptorType.PREPARE,
                        InterceptorExecutionPolicy.InterceptorType.VALIDATE));
        sessionService.executeInLocalViewWithParams(params, new SessionExecutionBody() {
            @Override
            public void executeWithoutResult() {
                OrderModel order = modelService.create(OrderModel.class);
                order.setCode(orderCode);
                order.setDate(creationDate);
                order.setErpOrderCode(erpOrderCode);
                order.setCurrency(euro);
                order.setUser(userService.getAnonymousUser());
                modelService.save(order);
            }
        });
    }



}

