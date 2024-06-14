package com.namics.distrelec.b2b.facades.order.impl;

import com.namics.distrelec.b2b.core.event.OrderCancellationEvent;
import com.namics.distrelec.b2b.core.event.SapVoucherEvent;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.promotion.SapGeneratedVoucherModel;
import com.namics.distrelec.b2b.core.service.customer.dao.DistPagedB2BWorkflowActionDao;
import com.namics.distrelec.b2b.core.service.order.DistOrderService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.facades.order.DistCancelledOrderPrepayment;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistB2BOrderFacadeUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private DistPagedB2BWorkflowActionDao distPagedB2BWorkflowActionDao;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private DistOrderService distOrderService;

    @Mock
    private Converter<WorkflowActionModel, B2BOrderApprovalData> b2bOrderApprovalDataConverter;

    @Mock
    private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;

    @Mock
    private EventService eventService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private BaseStoreService baseStoreService;

    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Mock
    private CustomerAccountService customerAccountService;

    @Mock
    private B2BOrderService b2bOrderService;

    @Mock
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Mock
    private Converter<OrderModel, OrderData> orderConverter;

    @Mock
    private CommonI18NService commonI18NService;

    @InjectMocks
    private DefaultDistB2BOrderFacade defaultDistB2BOrderFacade;

    @Test
    public void testGetPagedOrdersForApprovalUserIsAdmin() {
        // given
        UserModel currentUser = mock(UserModel.class);
        UserGroupModel userGroupModel = mock(UserGroupModel.class);
        WorkflowActionType[] actionTypes = { WorkflowActionType.START };
        WorkflowActionStatus[] status = { WorkflowActionStatus.IN_PROGRESS };
        PageableData pageableData = new DistOrderHistoryPageableData();
        de.hybris.platform.commerceservices.search.pagedata.SearchPageData<WorkflowActionModel> actionPageData = mock(
                de.hybris.platform.commerceservices.search.pagedata.SearchPageData.class);
        PaginationData paginationPage = mock(PaginationData.class);

        // when
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(userGroupModel);
        when(userService.isMemberOfGroup(currentUser, userGroupModel)).thenReturn(true);
        when(distPagedB2BWorkflowActionDao.findPagedWorkflowActionsByUserAndActionTypes(currentUser, actionTypes, status, pageableData))
                .thenReturn(actionPageData);
        when(actionPageData.getPagination()).thenReturn(paginationPage);

        SearchPageData<B2BOrderApprovalData> result = defaultDistB2BOrderFacade.getPagedOrdersForApproval(actionTypes, status, pageableData);

        // then
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testIsNumberOfGuestSuccessfulPurchasesExceededLessThanMax() {
        // given
        String email = "luka.modric@distrelec.com";
        int maxPurchasesNumber = 3;
        Configuration configuration = mock(Configuration.class);

        // when
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getInt("distrelec.guest.maxPurchases.number", 3)).thenReturn(maxPurchasesNumber);
        when(distOrderService.findAllOrdersForGivenUserEmail(email)).thenReturn(createMockOrders(2));

        boolean result = defaultDistB2BOrderFacade.isNumberOfGuestSuccessfulPurchasesExceeded(email);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsNumberOfGuestSuccessfulPurchasesExceededEqualToMax() {
        // given
        String email = "luka.modric@distrelec.com";
        int maxPurchasesNumber = 3;
        Configuration configuration = mock(Configuration.class);

        // when
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getInt("distrelec.guest.maxPurchases.number", 3)).thenReturn(maxPurchasesNumber);
        when(distOrderService.findAllOrdersForGivenUserEmail(email)).thenReturn(createMockOrders(3));

        boolean result = defaultDistB2BOrderFacade.isNumberOfGuestSuccessfulPurchasesExceeded(email);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testGetApprovalRequestsCountForAnonymousUser() {
        UserModel currentUser = mock(UserModel.class);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.isAnonymousUser(currentUser)).thenReturn(true);

        int result = defaultDistB2BOrderFacade.getApprovalRequestsCount();

        assertThat(result, is(0));
    }

    @Test
    public void testGetApprovalRequestsCountForUser() {
        // given
        UserModel currentUser = mock(UserModel.class);
        UserGroupModel userGroupModel = mock(UserGroupModel.class);
        List<WorkflowActionStatus> workflowActionStatusList = new ArrayList<>(Arrays.asList(WorkflowActionStatus.values()));
        workflowActionStatusList.remove(WorkflowActionStatus.COMPLETED);
        WorkflowActionStatus[] wfActionStatus = workflowActionStatusList.toArray(new WorkflowActionStatus[workflowActionStatusList.size()]);

        // when
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.isAnonymousUser(currentUser)).thenReturn(false);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(userGroupModel);
        when(userService.isMemberOfGroup(currentUser, userGroupModel)).thenReturn(true);
        when(distPagedB2BWorkflowActionDao.getApprovalRequestsCountForApprover(currentUser, new WorkflowActionType[] { WorkflowActionType.START }, wfActionStatus)).thenReturn(5);

        int result = defaultDistB2BOrderFacade.getApprovalRequestsCount();

        // then
        assertThat(result, is(5));
    }

    @Test
    public void testGetApprovalRequestsCountForSubUser() {
        // given
        UserModel currentUser = mock(UserModel.class);
        UserGroupModel userGroupModel = mock(UserGroupModel.class);
        WorkflowActionStatus[] statuses = new WorkflowActionStatus[] { WorkflowActionStatus.ENDED_THROUGH_END_OF_WORKFLOW, WorkflowActionStatus.IN_PROGRESS,
                                                                       WorkflowActionStatus.PENDING };

        // when
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.isAnonymousUser(currentUser)).thenReturn(false);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(userGroupModel);
        when(userService.isMemberOfGroup(currentUser, userGroupModel)).thenReturn(false);
        when(distPagedB2BWorkflowActionDao.getApprovalRequestsCountForSubUser(currentUser, new WorkflowActionType[] { WorkflowActionType.START }, statuses)).thenReturn(3);

        int result = defaultDistB2BOrderFacade.getApprovalRequestsCount();

        // then
        assertThat(result, is(3));
    }

    @Test
    public void testGetOrderApprovalDetailsForCode() {
        // given
        String code = "testCode";
        WorkflowActionModel workflowActionModel = mock(WorkflowActionModel.class);
        B2BOrderApprovalData expectedData = mock(B2BOrderApprovalData.class);

        when(b2bWorkflowIntegrationService.getActionForCode(code)).thenReturn(workflowActionModel);
        when(b2bOrderApprovalDataConverter.convert(workflowActionModel)).thenReturn(expectedData);

        // when
        B2BOrderApprovalData result = defaultDistB2BOrderFacade.getOrderApprovalDetailsForCode(code);

        // then
        assertThat(result, equalTo(expectedData));
    }

    @Test(expected = UnknownIdentifierException.class)
    public void testGetOrderApprovalDetailsForCodeExceptionThrown() {
        // given
        String code = "invalidCode";
        when(b2bWorkflowIntegrationService.getActionForCode(code)).thenReturn(null);

        // when
        defaultDistB2BOrderFacade.getOrderApprovalDetailsForCode(code);
    }

    @Test
    public void testSendVoucherEmailSuccessful() {
        // given
        String code = "validCode";
        String pattern = "emailPattern";
        OrderModel orderModel = mock(OrderModel.class);

        // when
        when(b2bOrderService.getOrderForCode(code)).thenReturn(orderModel);
        when(orderModel.getGeneratedVoucher()).thenReturn(mock(SapGeneratedVoucherModel.class));

        defaultDistB2BOrderFacade.sendVoucherEmail(code, pattern);

        // then
        verify(eventService).publishEvent(any(SapVoucherEvent.class));
    }

    @Test
    public void testGetOrderModelForAnonymousCheckout() {
        // given
        String code = "validGUID";
        OrderModel expectedOrder = mock(OrderModel.class);
        BaseStoreModel currentBaseStore = mock(BaseStoreModel.class);

        // when
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(true);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(currentBaseStore);
        when(customerAccountService.getOrderDetailsForGUID(code, currentBaseStore)).thenReturn(expectedOrder);

        OrderModel result = defaultDistB2BOrderFacade.getOrderModel(code);

        // then
        assertThat(result, equalTo(expectedOrder));
    }

    @Test
    public void testGetOrderModelForNonAnonymousCheckout() {
        // given
        String code = "validCode";
        OrderModel expectedOrder = mock(OrderModel.class);

        // when
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(false);
        when(b2bOrderService.getOrderForCode(code)).thenReturn(expectedOrder);

        OrderModel result = defaultDistB2BOrderFacade.getOrderModel(code);

        // then
        assertThat(result, equalTo(expectedOrder));
    }

    @Test(expected = UnknownIdentifierException.class)
    public void testGetOrderModelOrderNotFound() {
        // given
        String code = "invalidCode";

        // when
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(false);
        when(b2bOrderService.getOrderForCode(code)).thenReturn(null);

        defaultDistB2BOrderFacade.getOrderModel(code);
    }

    @Test
    public void testComparatorByTotalPriceAscending() {
        // given
        Comparator<B2BOrderApprovalData> comparator = new DefaultDistB2BOrderFacade().getComparator("byTotalPrice", true);

        // when
        B2BOrderApprovalData order1 = createMockB2BOrderApprovalDataWithTotalPrice(100);
        B2BOrderApprovalData order2 = createMockB2BOrderApprovalDataWithTotalPrice(200);

        // then
        assertThat(comparator.compare(order1, order2) < 0, is(true));
    }

    @Test
    public void testComparatorByTotalPriceDescending() {
        // given
        Comparator<B2BOrderApprovalData> comparator = new DefaultDistB2BOrderFacade().getComparator("byTotalPrice", false);

        // when
        B2BOrderApprovalData order1 = createMockB2BOrderApprovalDataWithTotalPrice(100);
        B2BOrderApprovalData order2 = createMockB2BOrderApprovalDataWithTotalPrice(200);

        // then
        assertThat(comparator.compare(order1, order2) > 0, is(true));
    }

    @Test
    public void testComparatorByStatusAscending() {
        // given
        Comparator<B2BOrderApprovalData> comparator = new DefaultDistB2BOrderFacade().getComparator("byStatus", true);

        // when
        B2BOrderApprovalData order1 = createMockB2BOrderApprovalDataWithStatus(OrderStatus.APPROVED);
        B2BOrderApprovalData order2 = createMockB2BOrderApprovalDataWithStatus(OrderStatus.CANCELLED);

        // then
        assertThat(comparator.compare(order1, order2) < 0, is(true));
    }

    @Test
    public void testComparatorByStatusDescending() {
        // given
        Comparator<B2BOrderApprovalData> comparator = new DefaultDistB2BOrderFacade().getComparator("byStatus", false);

        // when
        B2BOrderApprovalData order1 = createMockB2BOrderApprovalDataWithStatus(OrderStatus.APPROVED);
        B2BOrderApprovalData order2 = createMockB2BOrderApprovalDataWithStatus(OrderStatus.CANCELLED);

        // given
        assertThat(comparator.compare(order1, order2) > 0, is(true));
    }

    @Test
    public void testComparatorByCreatedAscending() {
        // given
        Comparator<B2BOrderApprovalData> comparator = new DefaultDistB2BOrderFacade().getComparator("byCreated", true);

        // when
        B2BOrderApprovalData order1 = createMockB2BOrderApprovalDataWithCreated(new Date(1000));
        B2BOrderApprovalData order2 = createMockB2BOrderApprovalDataWithCreated(new Date(2000));

        // then
        assertThat(comparator.compare(order1, order2) < 0, is(true));
    }

    @Test
    public void testComparatorByCreatedDescending() {
        // given
        Comparator<B2BOrderApprovalData> comparator = new DefaultDistB2BOrderFacade().getComparator("byCreated", false);

        // when
        B2BOrderApprovalData order1 = createMockB2BOrderApprovalDataWithCreated(new Date(1000));
        B2BOrderApprovalData order2 = createMockB2BOrderApprovalDataWithCreated(new Date(2000));

        // then
        assertThat(comparator.compare(order1, order2) > 0, is(true));
    }

    @Test
    public void testGetOrderDetailsForCodeNonAnonymousCheckoutManagingSubUsers() {
        // given
        String orderCode = "testCode";
        B2BCustomerModel user = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        DistSalesOrgModel salesOrg = mock(DistSalesOrgModel.class);
        Set<PrincipalModel> members = new HashSet<>();
        members.add(mock(UserModel.class));
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        OrderModel orderModel = mock(OrderModel.class);
        OrderData orderData = mock(OrderData.class);

        // when
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(false);
        when(userService.getCurrentUser()).thenReturn(user);
        when(b2bUnitService.getParent(user)).thenReturn(b2bUnit);
        when(b2bUnit.getSalesOrg()).thenReturn(salesOrg);
        when(salesOrg.isAdminManagingSubUsers()).thenReturn(true);
        when(b2bUnit.getMembers()).thenReturn(members);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
        when(distOrderService.getOrderForCodeWithSubUsers(anyString(), any(BaseStoreModel.class), anyList())).thenReturn(orderModel);
        when(orderConverter.convert(orderModel)).thenReturn(orderData);

        OrderData result = defaultDistB2BOrderFacade.getOrderDetailsForCode(orderCode);

        // then
        assertThat(result, equalTo(orderData));
    }

    @Test
    public void testSendOrderCancellationPrepaymentMail() {
        // given
        DistCancelledOrderPrepayment cancelledOrderPrepayment = new DistCancelledOrderPrepayment();

        // when
        defaultDistB2BOrderFacade.sendOrderCancellationPrepaymentMail(cancelledOrderPrepayment);

        // then
        verify(eventService, times(1)).publishEvent(any(OrderCancellationEvent.class));
    }

    private B2BOrderApprovalData createMockB2BOrderApprovalDataWithTotalPrice(double price) {
        B2BOrderApprovalData data = mock(B2BOrderApprovalData.class);
        OrderData orderData = mock(OrderData.class);
        PriceData priceData = mock(PriceData.class);

        when(priceData.getValue()).thenReturn(BigDecimal.valueOf(price));
        when(orderData.getTotalPrice()).thenReturn(priceData);
        when(data.getB2bOrderData()).thenReturn(orderData);

        return data;
    }

    private B2BOrderApprovalData createMockB2BOrderApprovalDataWithStatus(OrderStatus orderStatus) {
        B2BOrderApprovalData data = mock(B2BOrderApprovalData.class);
        OrderData orderData = mock(OrderData.class);

        when(orderData.getStatus()).thenReturn(orderStatus);
        when(data.getB2bOrderData()).thenReturn(orderData);

        return data;
    }

    private B2BOrderApprovalData createMockB2BOrderApprovalDataWithCreated(Date createdDate) {
        B2BOrderApprovalData data = mock(B2BOrderApprovalData.class);
        OrderData orderData = mock(OrderData.class);

        when(data.getB2bOrderData()).thenReturn(orderData);
        when(orderData.getCreated()).thenReturn(createdDate);

        return data;
    }

    private List<OrderModel> createMockOrders(int numberOfOrders) {
        return IntStream.range(0, numberOfOrders)
                        .mapToObj(i -> mock(OrderModel.class))
                        .collect(Collectors.toList());
    }
}
