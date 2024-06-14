package com.namics.distrelec.b2b.facades.customer.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.InvoiceHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

public class DistUserDashboardContentFacade implements DistUserDashboardFacade {
    private static final int START_DATE = 3650;

    private static final String IN_PROGRESS_ORDER_STATUS = "ERP_STATUS_IN_PROGRESS";

    protected static final String OFFERED_QUOTES_SESSION_ATTR = "offeredQuoteCount";

    protected static final String OFFERED_QUOTES_STATUS_CODE = "02";

    protected static final int DEFAULT_SEARCH_MAX_SIZE = 100;

    @Autowired
    private DistB2BOrderFacade orderFacade;

    @Autowired
    private DistAribaService distAribaService;

    @Autowired
    private DistOciService distOciService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DistCustomerFacade defaultDistCustomerFacade;

    @Autowired
    private DistProductPriceQuotationFacade distProductPriceQuotationFacade;

    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Resource(name = "erp.orderHistoryFacade")
    private OrderHistoryFacade orderHistoryFacade;

    @Resource(name = "erp.invoiceHistoryFacade")
    private InvoiceHistoryFacade invoiceHistoryFacade;

    @Override
    public Integer getOpenOrdersCount() {
        int approvalRequest = getOrderFacade().getApprovalRequestsCount();
        final DistOrderHistoryPageableData pageableData = createPageableData();
        pageableData.setStatus(IN_PROGRESS_ORDER_STATUS);
        final List<OrderHistoryData> orderList = orderHistoryFacade.getOrdersForStatuses(pageableData);
        return CollectionUtils.isNotEmpty(orderList) ? orderList.size() + approvalRequest : 0;
    }

    @Override
    public Integer getNewInvoicesCount() {
        DistOnlineInvoiceHistoryPageableData invoiceHistoryPageableData = new DistOnlineInvoiceHistoryPageableData();
        invoiceHistoryPageableData.setSort("byDate");

        CustomerData customer = getUser();

        invoiceHistoryPageableData.setCustomerID(customer.getUnit().getErpCustomerId());
        invoiceHistoryPageableData.setPageSize(DEFAULT_SEARCH_MAX_SIZE);

        LocalDate now = LocalDate.now();
        LocalDate before30days = now.minusDays(30);

        invoiceHistoryPageableData.setInvoiceDateFrom(convertLocalDate2Date(before30days));
        invoiceHistoryPageableData.setInvoiceDateTo(convertLocalDate2Date(now));

        final SearchPageData<DistB2BInvoiceHistoryData> searchPageData = invoiceHistoryFacade.getInvoiceSearchHistory(invoiceHistoryPageableData);
        return searchPageData.getResults().size();
    }

    @Override
    public Integer getApprovalRequestsCount() {
        return getOrderFacade().getApprovalRequestsCount();
    }

    @Override
    public Integer getOfferedQuoteCount() {
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            return 0;
        }

        return getSessionService().getOrLoadAttribute(OFFERED_QUOTES_SESSION_ATTR,
                                                      () -> getDistProductPriceQuotationFacade().getQuotationHistory(OFFERED_QUOTES_STATUS_CODE).size());
    }

    @Override
    public boolean isOciCustomer() {
        return distOciService.isOciCustomer();
    }

    @Override
    public boolean isAribaCustomer() {
        return distAribaService.isAribaCustomer();
    }

    private DistOrderHistoryPageableData createPageableData() {
        DistOrderHistoryPageableData data = new DistOrderHistoryPageableData();
        LocalDate endDate = LocalDate.now();
        LocalDate fromDate = endDate.minusDays(START_DATE);
        data.setFromDate(convertLocalDate2Date(fromDate));
        data.setToDate(convertLocalDate2Date(endDate));
        return data;
    }

    private Date convertLocalDate2Date(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    protected CustomerData getUser() {
        // The user does not change during the session, then we store it in the
        // session. When a user logs in, then a new session is created,
        // which means that the 'currentUser' attribute will be recalculated.
        final Object obj = getSessionService().getAttribute("currentUserData");
        if (obj != null && !((CustomerData) obj).getUid().equals(getUserService().getCurrentUser().getUid())) {
            getSessionService().removeAttribute("currentUserData");
        }

        return getSessionService().getOrLoadAttribute("currentUserData", () -> getDefaultDistCustomerFacade().getCurrentCustomer());
    }

    public DistB2BOrderFacade getOrderFacade() {
        return orderFacade;
    }

    public DistAribaService getDistAribaService() {
        return distAribaService;
    }

    public DistOciService getDistOciService() {
        return distOciService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public DistCustomerFacade getDefaultDistCustomerFacade() {
        return defaultDistCustomerFacade;
    }

    public DistProductPriceQuotationFacade getDistProductPriceQuotationFacade() {
        return distProductPriceQuotationFacade;
    }

    public UserService getUserService() {
        return userService;
    }

    public OrderHistoryFacade getOrderHistoryFacade() {
        return orderHistoryFacade;
    }

    public InvoiceHistoryFacade getInvoiceHistoryFacade() {
        return invoiceHistoryFacade;
    }

    public void setOrderHistoryFacade(final OrderHistoryFacade orderHistoryFacade) {
        this.orderHistoryFacade = orderHistoryFacade;
    }

    public void setInvoiceHistoryFacade(final InvoiceHistoryFacade invoiceHistoryFacade) {
        this.invoiceHistoryFacade = invoiceHistoryFacade;
    }
}
