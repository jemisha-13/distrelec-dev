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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowModel;

import com.namics.distrelec.b2b.core.enums.DistChannel;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Context (velocity) for email order pending approval.
 */
public class OrderPendingAdminApprovalEmailContext extends CustomerEmailContext {
	
	private static final Logger LOG = LogManager.getLogger(OrderPendingAdminApprovalEmailContext.class);
	
    private static final String ORDER_URL = "/my-account/order-approval/order-details";
    private static final String BLANK_STRING = "";
    private Converter<OrderModel, OrderData> orderConverter;

    private OrderData orderData;

    private String rejectionMessage;

    private StringBuilder orderApprovalUrl;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.facades.process.email.context.CustomerEmailContext#init(de.hybris.platform.processengine.model.
     * BusinessProcessModel, de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel)
     */
    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof OrderProcessModel) {
            final CustomerData customerData = getCustomer();
            if (customerData != null && CollectionUtils.isNotEmpty(customerData.getApprovers())) {
                final String emails = customerData.getApprovers().stream() //
                                                  .filter(approver -> approver != null && StringUtils.isNotBlank(approver.getEmail())) //
                                                  .map(approver -> approver.getEmail()) //
                                                  .collect(Collectors.joining(";"));
                put(EMAIL, emails);
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
            if (workflow != null && CollectionUtils.isNotEmpty(workflow.getActions())) {
                workflow.getActions().stream() //
                        .filter(action -> WorkflowActionType.START.equals(action.getActionType())) //
                        .forEach(action -> {
                            orderApprovalUrl.append("/").append(order.getCode());
                            orderApprovalUrl.append("/workflow/").append(action.getCode());
                        });
            }
        }
    }
    
    public String getOrderApprovalUrlForSite(OrderModel order) throws UnsupportedEncodingException {
        return isStorefrontOrder(order) ? getOrderApprovalUrlForStorefront() : getOrderApprovalUrlForHeadless();
    }
    
    private String getOrderApprovalUrlForHeadless() throws UnsupportedEncodingException {
        return distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(getBaseSite(), true, ORDER_URL);
    }

    private String getOrderApprovalUrlForStorefront() throws UnsupportedEncodingException {
        return distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(getBaseSite(), true, ORDER_URL);
    }

    private boolean isStorefrontOrder(OrderModel order) {
    	return order.getDistChannel()!=null && order.getDistChannel().equals(DistChannel.STOREFRONT);
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

    public String getOrderApprovalUrl(OrderModel order) {
        try {
			return getOrderApprovalUrlForSite(order).toString();
		} catch (UnsupportedEncodingException e) {
			// YTODO Auto-generated catch block
			LOG.error("Couldn't get Approval URL");
		}
        return BLANK_STRING;
    }
    
    public String getOrderApprovalUrl() {
        return orderApprovalUrl!=null ? orderApprovalUrl.toString(): BLANK_STRING ;
    }

    public String getRejectionMessage() {
        return rejectionMessage;
    }

    public String getOrderDate() {
        return format.format(new Date());
    }
}
