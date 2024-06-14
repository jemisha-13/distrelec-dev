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

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.Locale;

/**
 * Context (velocity) for email order approval rejection.
 */
public class OrderApprovalRejectionEmailContext extends CustomerEmailContext {
    private static final String ORDER_URL = "/my-account/order-approval-requests/order-details";

    private Converter<OrderModel, OrderData> orderConverter;
    private OrderData orderData;
    private String rejectionMessage;
    private StringBuilder orderApprovalUrl;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof OrderProcessModel) {
            final OrderModel order = ((OrderProcessModel) businessProcessModel).getOrder();
            orderData = getOrderConverter().convert(order);
            orderApprovalUrl = new StringBuilder(getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), false, ORDER_URL));

            final B2BPermissionResultModel permissionResultModel = order.getPermissionResults().iterator().next();
            if (permissionResultModel != null) {
                rejectionMessage = permissionResultModel.getNote(new Locale(getCommonI18NService().getLocaleForLanguage(
                        getCommonI18NService().getCurrentLanguage()
                ).getLanguage()));
            }

            final WorkflowModel workflow = order.getWorkflow();
            if (workflow != null) {
                workflow.getActions().stream().filter(action -> WorkflowActionType.START.equals(action.getActionType())) //
                        .findFirst() //
                        .ifPresent(action -> {
                            orderApprovalUrl.append("/").append(order.getCode());
                            orderApprovalUrl.append("/workflow/").append(action.getCode());
                        });
            }
        }
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
