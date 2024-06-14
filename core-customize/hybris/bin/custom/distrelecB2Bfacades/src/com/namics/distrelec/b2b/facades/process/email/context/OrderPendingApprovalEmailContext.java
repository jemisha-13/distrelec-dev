/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.facades.process.email.context;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import com.namics.distrelec.b2b.core.enums.DistChannel;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * Context (velocity) for email order pending approval.
 */
public class OrderPendingApprovalEmailContext extends CustomerEmailContext {
    private static final String ORDER_URL = "/my-account/order-approval-requests/order-details";
    private static final String BLANK_STRING = "";
    private Converter<OrderModel, OrderData> orderConverter;

    private OrderData orderData;

    private String rejectionMessage;

    private StringBuilder orderApprovalUrl;
    
    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof OrderProcessModel) {
            final CustomerData customerData = getCustomer();
            if (customerData != null && CollectionUtils.isNotEmpty(customerData.getApprovers())) {
                final StringBuilder email_builder = new StringBuilder(customerData.getEmail());
                put(EMAIL, email_builder.toString());
            }

            final OrderModel order = ((OrderProcessModel) businessProcessModel).getOrder();
            orderData = getOrderConverter().convert(order);
            try {
				orderApprovalUrl = new StringBuilder(getOrderApprovalUrlForSite(order));
			} catch (UnsupportedEncodingException e) {
				// YTODO Auto-generated catch block
				orderApprovalUrl=new StringBuilder(BLANK_STRING);
				e.printStackTrace();
			}

            final WorkflowModel workflow = order.getWorkflow();
            if (workflow != null) {
                for (final WorkflowActionModel action : workflow.getActions()) {
                    if (action.getActionType().equals(WorkflowActionType.START)) {
                        orderApprovalUrl.append("/").append(order.getCode());
                        orderApprovalUrl.append("/workflow/").append(action.getCode());
                    }
                }
            }
        }
    }
    
    public String getOrderApprovalUrlForSite(OrderModel order) throws UnsupportedEncodingException {
        return isStorefrontOrder(order) ? getOrderApprovalUrlForStorefront() : getOrderApprovalUrlForHeadless();
    }
    
    private boolean isStorefrontOrder(OrderModel order) {
    	return order.getDistChannel()!=null && order.getDistChannel().equals(DistChannel.STOREFRONT);
    }
    
    private String getOrderApprovalUrlForHeadless() throws UnsupportedEncodingException {
        return distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(getBaseSite(), true, ORDER_URL);
    }

    private String getOrderApprovalUrlForStorefront() throws UnsupportedEncodingException {
        return distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(getBaseSite(), true, ORDER_URL);
    }

    protected Converter<OrderModel, OrderData> getOrderConverter() {
        return orderConverter;
    }

    @Required
    public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter) {
        this.orderConverter = orderConverter;
    }

    public OrderData getOrder() {
        return orderData;
    }

    public String getOrderApprovalUrl() {
        return orderApprovalUrl.toString();
    }

    public String getRejectionMessage() {
        return rejectionMessage;
    }

}
