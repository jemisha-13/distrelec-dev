/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order;


import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;

/**
 * Customized version of the {@link B2BOrderFacade} facade.
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec CH
 * @since Namics Extensions 1.0
 */
public interface DistB2BOrderFacade extends B2BOrderFacade {
    
    /**
     * Retrieves the order list for approval dashboard
     *
     * @return All orders pending approval
     */
    SearchPageData<B2BOrderApprovalData> getPagedOrdersForApproval(WorkflowActionType[] actionTypes, WorkflowActionStatus[] status, PageableData pageableData);
    
    /**
     * Counts the number of order approval requests with the specified workflow action types and action status
     *
     * @return the number of order approval requests
     */
    int getApprovalRequestsCount();
    
    /**
     * Check if there is a generated SAP voucher during the order process and send an email to the customer.
     *
     * @param code
     *            the code of the order which caused the generation of the voucher.
     * @param pattern
     */
    void sendVoucherEmail(final String code, final String pattern);
    
    OrderModel getOrderModel(final String code);
    
    OrderData getOrderDetailsForCodeWithSubUsers(String orderCode, Set<PrincipalModel> members);
    
    void setCustomerClientID(final HttpServletRequest request, String cartCode);
    
    boolean isNumberOfGuestSuccessfulPurchasesExceeded(String email);

    void sendOrderCancellationPrepaymentMail(final DistCancelledOrderPrepayment cancelledOrderPrepayment);
}
