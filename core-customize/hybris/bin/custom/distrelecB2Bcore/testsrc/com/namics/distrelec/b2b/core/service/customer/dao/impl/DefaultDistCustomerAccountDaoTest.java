/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.dao.impl;

import com.namics.distrelec.b2b.core.service.customer.dao.DistCustomerAccountDao;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Test class for {@link DefaultDistCustomerAccountDao}.
 * 
 * @author daebenothmn, Distrelec
 * 
 */

@IntegrationTest
public class DefaultDistCustomerAccountDaoTest extends ServicelayerTransactionalTest {

    private static final long ORDER_CODE = 9834759L;
    private static final OrderStatus[] ORDER_STATUS_ARR = { OrderStatus.ERP_STATUS_CANCELLED, OrderStatus.ERP_STATUS_IN_PROGRESS,
            OrderStatus.ERP_STATUS_PARTIALLY_SHIPPED, OrderStatus.ERP_STATUS_RECIEVED, OrderStatus.ERP_STATUS_SHIPPED };
    private static final String ORDER_BY[] = { "byDate", "byStatus", "byTotalPrice" };
    public static final Random RAND = new Random();

    @Resource
    private ModelService modelService;

    @Resource
    private DistCustomerAccountDao customerAccountDao;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private UserService userService;

    private B2BCustomerModel customer;
    private BaseStoreModel store;

    @Before
    public void prepare() throws Exception {
        importCsv("/distrelecB2Bcore/test/testOrganizations.impex", "utf-8");

        final List<Object> models = new ArrayList<Object>();

        final B2BUnitModel b2bUnit = modelService.create(B2BUnitModel.class);
        b2bUnit.setActive(Boolean.TRUE);
        b2bUnit.setUid("b2bunit");
        b2bUnit.setBuyer(Boolean.TRUE);
        b2bUnit.setCustomerType(CustomerType.B2B);

        models.add(b2bUnit);

        // Initialize the customer
        customer = modelService.create(B2BCustomerModel.class);
        customer.setCreationtime(new Date());
        customer.setUid("NB-ABCD-WXYZ");
        customer.setEmail("toto.tata@titi.com");
        customer.setActive(Boolean.TRUE);
        customer.setAuthorizedToUnlockPages(false);
        customer.setDoubleOptInActivated(true);
        customer.setLoginDisabled(false);
        
        customer.setDefaultB2BUnit(b2bUnit);
        b2bUnit.setErpCustomerID(customer.getUid());
        models.add(customer);

        // Initialize the store
        store = modelService.create(BaseStoreModel.class);
        store.setChannel(SiteChannel.B2C);
        store.setName("Distrelec CH");
        store.setNet(true);
        store.setUid("NB-EFGH-IJKL");
        models.add(store);

        // Initialize the currency model
        final CurrencyModel currency = commonI18NService.getCurrency("CHF");
        Assert.notNull(currency);

        for (int i = 0; i < 10; i++) {
            final OrderModel order = modelService.create(OrderModel.class);
            order.setCode(String.valueOf(ORDER_CODE + i));
            order.setStore(store);
            order.setUser(customer);
            order.setTotalPrice(Double.valueOf(1000 * RAND.nextDouble()));
            order.setCurrency(currency);
            final Date date = new Date(System.currentTimeMillis() - RAND.nextInt());
            order.setDate(date);
            order.setCreationtime(date);
            order.setDiscountsIncludeDeliveryCost(false);
            order.setDiscountsIncludePaymentCost(false);
            order.setNet(Boolean.FALSE);
            order.setStatus(ORDER_STATUS_ARR[RAND.nextInt(ORDER_STATUS_ARR.length)]);
            models.add(order);
        }

        modelService.saveAll(models);
    }

    @Test
    public void testCutomerAccountDaoType() {
        assertEquals("The object cutomerAccountDao must be of type " + DefaultDistCustomerAccountDao.class.getName(), customerAccountDao.getClass(),
                DefaultDistCustomerAccountDao.class);
    }

    @Test
    public final void testFindOrdersByCustomerAndStoreCustomerModelBaseStoreModelOrderStatusArrayPageableData() {
        final DefaultDistCustomerAccountDao ddcad = (DefaultDistCustomerAccountDao) customerAccountDao;
        // Prepare the pageable data
        final DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData();
        pageableData.setPageSize(10);
        pageableData.setSort(ORDER_BY[RAND.nextInt(ORDER_BY.length)]);
        pageableData.setCurrentPage(0);

        // Execute query
        final SearchPageData<OrderModel> searchData = ddcad.findOrdersByCustomerAndStore(customer, store, ORDER_STATUS_ARR, pageableData);
        assertNotNull("The search page data should be not null", searchData);
        assertNotNull("The list must be not null", searchData.getResults());
        assertFalse("The list should not be empty", searchData.getResults().isEmpty());
    }

    @Test
    public final void testFindCreditCardPaymentInfosByCustomer() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/testDistCustomerAccountDao.impex", "utf-8");

        final UserModel user = userService.getUserForUID("b2cuser");
        final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;

        final List<CreditCardPaymentInfoModel> creditCardsPaymentInfos = ((DefaultDistCustomerAccountDao) customerAccountDao)
                .findCreditCardPaymentInfosByCustomer(b2bCustomer, true);

        // We recived only 2 CreditCardPaymentInfo (for visa and mastercard).
        // The CreditCardPaymentInfo amex has no movexCode
        // 1st card
        CreditCardPaymentInfoModel creditCardPaymentInfo = creditCardsPaymentInfos.get(0);
        assertEquals("creditCardPaymentInfo : wrong code", "ccid3", creditCardPaymentInfo.getCode());

        // 2nd card
        creditCardPaymentInfo = creditCardsPaymentInfos.get(1);
        assertEquals("creditCardPaymentInfo : wrong code", "ccid4", creditCardPaymentInfo.getCode());
    }

    @Test
    public final void testFindCreditCardPaymentInfoByCustomer() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/testDistCustomerAccountDao.impex", "utf-8");

        final UserModel user = userService.getUserForUID("b2cuser");
        final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;

        final List<CreditCardPaymentInfoModel> creditCardsPaymentInfos = ((DefaultDistCustomerAccountDao) customerAccountDao)
                .findCreditCardPaymentInfosByCustomer(b2bCustomer, true);

        // We recived only 2 CreditCardPaymentInfo (for visa and mastercard).
        // The CreditCardPaymentInfo amex has no movexCode
        // 1st card
        CreditCardPaymentInfoModel creditCardPaymentInfo = ((DefaultDistCustomerAccountDao) customerAccountDao).findCreditCardPaymentInfoByCustomer(
                b2bCustomer, creditCardsPaymentInfos.get(0).getPk().toString());
        assertEquals("creditCardPaymentInfo : wrong code", "ccid3", creditCardPaymentInfo.getCode());

        // 2nd card
        creditCardPaymentInfo = ((DefaultDistCustomerAccountDao) customerAccountDao).findCreditCardPaymentInfoByCustomer(b2bCustomer, creditCardsPaymentInfos
                .get(1).getPk().toString());
        assertEquals("creditCardPaymentInfo : wrong code", "ccid4", creditCardPaymentInfo.getCode());
    }

}
