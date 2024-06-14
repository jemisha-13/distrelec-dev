package com.namics.distrelec.b2b.facades.customer.impl;

import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.InvoiceHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistUserDashboardContentFacadeUnitTest {

    @Mock
    private DistB2BOrderFacade orderFacade;

    @Mock
    private OrderHistoryFacade orderHistoryFacade;

    @Mock
    private InvoiceHistoryFacade invoiceHistoryFacade;

    @Mock
    private SessionService sessionService;

    @Mock
    private UserService userService;

    @Mock
    private DistAribaService distAribaService;

    @Mock
    private DistOciService distOciService;

    @InjectMocks
    private DistUserDashboardContentFacade distUserDashboardContentFacade;

    @Test
    public void testGetOpenOrdersCountWithNoOrders() {
        // when
        when(orderFacade.getApprovalRequestsCount()).thenReturn(0);
        when(orderHistoryFacade.getOrdersForStatuses(any(DistOrderHistoryPageableData.class))).thenReturn(Collections.emptyList());

        int result = distUserDashboardContentFacade.getOpenOrdersCount();

        // then
        assertThat(result, equalTo(0));
    }

    @Test
    public void testGetOpenOrdersCountWithOrdersAndApprovalRequests() {
        // given
        List<OrderHistoryData> orders = Collections.nCopies(3, mock(OrderHistoryData.class));

        // when
        when(orderFacade.getApprovalRequestsCount()).thenReturn(5);
        when(orderHistoryFacade.getOrdersForStatuses(any(DistOrderHistoryPageableData.class))).thenReturn(orders);

        int result = distUserDashboardContentFacade.getOpenOrdersCount();

        // then
        assertThat(result, equalTo(8));
    }

    @Test
    public void testGetNewInvoicesCountWithNoInvoices() {
        // given
        SearchPageData<DistB2BInvoiceHistoryData> emptyPageData = new SearchPageData<>();
        emptyPageData.setResults(Collections.emptyList());
        CustomerData customerData = mock(CustomerData.class);

        // when
        when(sessionService.getOrLoadAttribute(eq("currentUserData"), any(SessionService.SessionAttributeLoader.class))).thenReturn(customerData);
        when(customerData.getUnit()).thenReturn(mock(B2BUnitData.class));
        when(invoiceHistoryFacade.getInvoiceSearchHistory(any(DistOnlineInvoiceHistoryPageableData.class))).thenReturn(emptyPageData);

        int result = distUserDashboardContentFacade.getNewInvoicesCount();

        // then
        assertThat(result, equalTo(0));
    }

    @Test
    public void testGetNewInvoicesCountWithInvoices() {
        // given
        CustomerData customerData = mock(CustomerData.class);
        SearchPageData<DistB2BInvoiceHistoryData> pageData = new SearchPageData<>();
        pageData.setResults(Collections.nCopies(5, mock(DistB2BInvoiceHistoryData.class)));

        // when
        when(sessionService.getOrLoadAttribute(eq("currentUserData"), any(SessionService.SessionAttributeLoader.class))).thenReturn(customerData);
        when(customerData.getUnit()).thenReturn(mock(B2BUnitData.class));
        when(invoiceHistoryFacade.getInvoiceSearchHistory(any(DistOnlineInvoiceHistoryPageableData.class))).thenReturn(pageData);

        int result = distUserDashboardContentFacade.getNewInvoicesCount();

        // then
        assertThat(result, equalTo(5));
    }

    @Test
    public void testGetUserWhenUserNotInSession() {
        // given
        CustomerData customerData = mock(CustomerData.class);

        // when
        when(sessionService.getAttribute("currentUserData")).thenReturn(null);
        when(sessionService.getOrLoadAttribute(eq("currentUserData"), any(SessionService.SessionAttributeLoader.class))).thenReturn(customerData);

        CustomerData result = distUserDashboardContentFacade.getUser();

        // then
        assertThat(result, equalTo(customerData));
    }

    @Test
    public void testGetUserWhenSessionUserDifferentFromCurrentUser() {
        // given
        UserModel currentUserModel = mock(UserModel.class);
        CustomerData sessionUser = mock(CustomerData.class);
        CustomerData currentUser = mock(CustomerData.class);

        // when
        when(sessionUser.getUid()).thenReturn("user123");
        when(sessionService.getAttribute("currentUserData")).thenReturn(sessionUser);
        when(userService.getCurrentUser()).thenReturn(currentUserModel);
        when(currentUserModel.getUid()).thenReturn("user456");
        when(sessionService.getOrLoadAttribute(eq("currentUserData"), any(SessionService.SessionAttributeLoader.class))).thenReturn(currentUser);

        CustomerData result = distUserDashboardContentFacade.getUser();

        // then
        assertThat(result, equalTo(currentUser));
        verify(sessionService).removeAttribute("currentUserData");
    }

    @Test
    public void testGetApprovalRequestsCount() {
        // when
        when(orderFacade.getApprovalRequestsCount()).thenReturn(5);

        int result = distUserDashboardContentFacade.getApprovalRequestsCount();

        // then
        assertThat(result, equalTo(5));
    }

    @Test
    public void testGetOfferedQuoteCountAnonymousUser() {
        // when
        when(userService.isAnonymousUser(any())).thenReturn(true);

        int result = distUserDashboardContentFacade.getOfferedQuoteCount();

        // then
        assertThat(result, equalTo(0));
    }

    @Test
    public void testGetOfferedQuoteCountRegularUser() {
        // when
        when(userService.isAnonymousUser(any())).thenReturn(false);
        when(sessionService.getOrLoadAttribute(eq("offeredQuoteCount"), any())).thenReturn(3);

        int result = distUserDashboardContentFacade.getOfferedQuoteCount();

        // then
        assertThat(result, equalTo(3));
    }

    @Test
    public void testIsOciCustomer() {
        // when
        when(distOciService.isOciCustomer()).thenReturn(true);

        boolean result = distUserDashboardContentFacade.isOciCustomer();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsAribaCustomer() {
        // when
        when(distAribaService.isAribaCustomer()).thenReturn(false);

        boolean result = distUserDashboardContentFacade.isAribaCustomer();

        // then
        assertThat(result, is(false));
    }
}
