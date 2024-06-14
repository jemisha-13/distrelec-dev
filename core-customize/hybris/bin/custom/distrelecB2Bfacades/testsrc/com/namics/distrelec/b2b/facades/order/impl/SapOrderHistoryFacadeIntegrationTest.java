/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.impl;

import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistSubUserData;
import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ManualTest
public class SapOrderHistoryFacadeIntegrationTest extends ServicelayerTransactionalTest {

    private static final String CUSTOMER_ID = "0003147227";
    private static final String COMPANY_NAME = "Becker AG";
    private static final String MASTER_EMAIL = "master103@example.com";
    private static final String SUB_EMAIL = "sub103@example.com";

    private static final Logger LOG = LogManager.getLogger(SapOrderHistoryFacadeIntegrationTest.class);

    @Resource(name = "sap.orderHistoryFacade")
    private SapOrderHistoryFacade sapOrderHistoryFacade;

    @Resource(name = "customerFacade")
    private DistCustomerFacade distCustomerFacade;

    @Resource(name = "cmsSiteService")
    private CMSSiteService cmsSiteService;

    @Resource
    private UserService userService;

    @Resource
    private ModelService modelService;

    @Before
    public void setUp() throws Exception {
        createCoreData();
        createDefaultCatalog();
        importCsv("/distrelecB2Bfacades/test/testCatalogAndSalesOrg.impex", "utf-8");

        // set current cms site
        final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_DE", false);
        assertNotNull(currentCMSSite);

        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletContext mockContext = mock(ServletContext.class);
        when(request.getServletContext())
                .thenReturn(mockContext);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Test
    public void subUserHasNoOrdersOnSapAndCanBeDeleted() {

        try {
            deleteCustomer(MASTER_EMAIL);
            deleteCustomer(SUB_EMAIL);
        } catch (final Exception e) {
            // OK
        }

        // 1 Create Master User
        createNewCustomer(MASTER_EMAIL, "myFirst", "myLast", "myPassword", CUSTOMER_ID, COMPANY_NAME);
        final B2BCustomerModel master = userService.getUserForUID(MASTER_EMAIL, B2BCustomerModel.class);
        checkCustomer(master);
        userService.setCurrentUser(master);
        assertEquals(master, userService.getCurrentUser());

        // 2 Create Sub User
        createB2BEmployee(SUB_EMAIL, "myFirst", "myLast", "myPassword", MASTER_EMAIL);
        final B2BCustomerModel sub = userService.getUserForUID(SUB_EMAIL, B2BCustomerModel.class);
        checkCustomer(sub);

        // 3 Get Order History For Sub
        final DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData();
        final String[] statuses = null;
        final SearchPageData<OrderHistoryData> result = sapOrderHistoryFacade.getOrderHistory(SUB_EMAIL, pageableData, statuses);
        assertEquals(0, result.getResults().size());

        // 4 Delete Sub User
        assertEquals(true, deleteSubUser(SUB_EMAIL));
        try {
            userService.getUserForUID(SUB_EMAIL);
            fail(SUB_EMAIL + "should have been deleted, since there it has no orders on SAP nor locally");
        } catch (final UnknownIdentifierException e) {
            // OK
        }

        // 5 Re-Create Sub User
        createB2BEmployee(SUB_EMAIL, "myFirst", "myLast", "myPassword", MASTER_EMAIL);
        final B2BCustomerModel subAgain = userService.getUserForUID(SUB_EMAIL, B2BCustomerModel.class);
        checkCustomer(subAgain);

        userService.setCurrentUser(userService.getAdminUser());
    }

    @Test
    public void subUserHasOrdersOnSapAndWillBeDeactivated() {

        try {
            deleteCustomer(MASTER_EMAIL);
            deleteCustomer(SUB_EMAIL);
        } catch (final Exception e) {
            // OK
        }

        // 1 Create Master User
        createNewCustomer(MASTER_EMAIL, "myFirst", "myLast", "myPassword", CUSTOMER_ID, COMPANY_NAME);
        final B2BCustomerModel master = userService.getUserForUID(MASTER_EMAIL, B2BCustomerModel.class);
        checkCustomer(master);
        userService.setCurrentUser(master);
        assertEquals(master, userService.getCurrentUser());

        // 2 Create Sub User
        createB2BEmployee(SUB_EMAIL, "myFirst", "myLast", "myPassword", MASTER_EMAIL);
        final B2BCustomerModel sub = userService.getUserForUID(SUB_EMAIL, B2BCustomerModel.class);
        checkCustomer(sub);

        // 2.5 Create Order for Sub User
        createLocalOrder(sub);

        // 3 Get Order History For Sub
        final DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData();
        final String[] statuses = null;
        final SearchPageData<OrderHistoryData> result = sapOrderHistoryFacade.getOrderHistory(SUB_EMAIL, pageableData, statuses);
        assertEquals(0, result.getResults().size());

        // 4 Try To Delete Sub User
        assertEquals(true, deleteSubUser(SUB_EMAIL));
        try {
            final B2BCustomerModel subAfterDelete = userService.getUserForUID(SUB_EMAIL, B2BCustomerModel.class);
            assertFalse(subAfterDelete.getActive());
        } catch (final UnknownIdentifierException e) {
            fail(SUB_EMAIL + "should NOT have been deleted, since there it has orders either on SAP or locally");
        }

        // 5 Re-Create Sub User
        // createB2BEmployee(SUB_EMAIL, "myFirst", "myLast", "myPassword", MASTER_EMAIL);
        // final B2BCustomerModel subAgain = userService.getUserForUID(SUB_EMAIL, B2BCustomerModel.class);
        // checkCustomer(subAgain);

        userService.setCurrentUser(userService.getAdminUser());
    }

    public void createLocalOrder(final B2BCustomerModel sub) {
        final OrderModel order = modelService.create(OrderModel.class);
        order.setCode("orderCode0001");
        order.setUser(sub);
        order.setDate(new Date());
        order.setCurrency(createCurrency());
        modelService.save(order);
    }

    public CurrencyModel createCurrency() {
        final CurrencyModel currency = modelService.create(CurrencyModel.class);
        currency.setIsocode("EUR1");
        currency.setSymbol("EUR1");
        currency.setBase(Boolean.TRUE);
        currency.setActive(Boolean.TRUE);
        currency.setConversion(Double.valueOf(1));
        return currency;
    }

    protected void checkCustomer(final B2BCustomerModel customer) {
        assertNotNull(customer);
        assertThat(customer.getErpContactID(), is(not(Matchers.isEmptyOrNullString())));
        final B2BUnitModel defaultB2BUnit = customer.getDefaultB2BUnit();
        assertNotNull(defaultB2BUnit);
        assertThat(defaultB2BUnit.getErpCustomerID(), is(not(Matchers.isEmptyOrNullString())));
        assertTrue(customer.getActive());
    }

    protected void createNewCustomer(final String email, final String firstName, final String lastName, final String password,
            final String customerId, final String companyName) {
        try {
            final DistRegisterData registerData = new DistRegisterData();
            registerData.setCustomerType(CustomerType.B2B_KEY_ACCOUNT);
            registerData.setCompanyName(companyName);
            registerData.setTitleCode("mr");
            registerData.setFirstName(firstName);
            registerData.setLastName(lastName);
            registerData.setFunctionCode("20");
            registerData.setEmail(email);
            registerData.setLogin(email);
            registerData.setPassword(password);
            registerData.setCountryCode("DE");
            registerData.setStreetName("street name sample");
            registerData.setStreetNumber("123");
            registerData.setTown("town sample");
            registerData.setPostalCode("63145");
            // registerData.setRegionCode("01");
            registerData.setPhoneNumber("1234545353535");
            registerData.setVatId("DE123456789");
            // registerData.setContactId(contactId);
            registerData.setCustomerId(customerId);
            distCustomerFacade.registerNewCustomer(registerData, CustomerType.B2B);
        } catch (final Exception e) {
            LOG.error(e, e);
            fail(e.getMessage());
        }
    }

    protected void createB2BEmployee(final String email, final String firstName, final String lastName, final String password, final String approverUid) {
        try {
            final DistSubUserData registerData = new DistSubUserData();
            registerData.setTitleCode("mr");
            registerData.setFirstName(firstName);
            registerData.setLastName(lastName);
            registerData.setFunctionCode("20");
            registerData.setEmail(email);
            registerData.setLogin(email);
            registerData.setPassword(password);
            registerData.setPhoneNumber("1234545353535");
            registerData.setBudgetPerOrder(BigDecimal.ZERO);
            distCustomerFacade.createB2BEmployee(registerData, approverUid);
        } catch (final Exception e) {
            LOG.error(e, e);
            fail(e.getMessage());
        }
    }

    protected void deleteCustomer(final String customerUidToDelete) {
        try {
            final B2BCustomerModel customer = userService.getUserForUID(customerUidToDelete, B2BCustomerModel.class);
            modelService.removeAll(customer.getOrders());
            modelService.remove(customer);
        } catch (final UnknownIdentifierException e) {
            //OK
        }
    }

    protected boolean deleteSubUser(final String customerUidToDelete) {
        return distCustomerFacade.deleteSubUser(customerUidToDelete);
    }

    @After
    public void tearDown() {
        try {
            deleteCustomer(MASTER_EMAIL);
            deleteCustomer(SUB_EMAIL);
        } catch (final Exception e) {
            fail(e.getMessage());
        }

    }

}
