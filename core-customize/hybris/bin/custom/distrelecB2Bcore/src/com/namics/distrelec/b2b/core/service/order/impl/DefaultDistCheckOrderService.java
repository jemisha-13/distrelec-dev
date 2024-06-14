/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.event.OrderConfirmationEventListener;
import com.namics.distrelec.b2b.fulfilmentprocess.impl.DefaultCheckOrderService;

import de.hybris.platform.core.model.order.OrderModel;

public class DefaultDistCheckOrderService extends DefaultCheckOrderService {

    private static final Logger LOG = LogManager.getLogger(OrderConfirmationEventListener.class);

    @Override
    public boolean check(final OrderModel order) {
        if (!order.getCalculated().booleanValue()) {
            LOG.error("{} {} Order with code {} is not calculated!", ErrorLogCode.ORDER_RELATED_ERROR, ErrorSource.HYBRIS, order.getCode());
            return false;
        }
        if (order.getEntries().isEmpty()) {
            LOG.error("{} {} Order with code {} has no entries!", ErrorLogCode.ORDER_RELATED_ERROR, ErrorSource.HYBRIS, order.getCode());
            return false;
        } else if (isPaymentAddressInvalid(order)) {
            LOG.error("{} {} Order with code {} has no payment address!", ErrorLogCode.ORDER_RELATED_ERROR, ErrorSource.HYBRIS, order.getCode());
            return false;
        } else if (isPaymentModeInvalid(order)) {
            LOG.error("{} {} Order with code {} has no payment mode selected!", ErrorLogCode.PAYMENT_ERROR, ErrorSource.HYBRIS, order.getCode());
            return false;
        } else if (isDeliveryModeInvalid(order)) {
            LOG.error("{} {} Order with code {} has no delivery mode selected!", ErrorLogCode.DELIVERY_MODE_ERROR, ErrorSource.HYBRIS, order.getCode());
            return false;
        } else if (isDeliveryAddressInvalid(order)) {
            if (order.getPickupLocation() == null) {
                LOG.error("{} {} Order with code {} has pickup selected as delivery mode, but no pickup location!", ErrorLogCode.DELIVERY_MODE_ERROR,
                        ErrorSource.HYBRIS, order.getCode());
                return false;
            } else if (order.getPickupLocation() == null && order.getDeliveryAddress() == null) {
                LOG.error("{} {} Order with code {} has no delivery address!", ErrorLogCode.DELIVERY_MODE_ERROR, ErrorSource.HYBRIS, order.getCode());
                return false;
            }
        }
        return true;
    }

    private boolean isDeliveryAddressInvalid(final OrderModel order) {
        return order.getDeliveryAddress() == null && isNormalOrderOrNewOpenOrder(order);
    }

    private boolean isNormalOrderOrNewOpenOrder(final OrderModel order) {
        return !isOpenOrder(order) || isNewOpenOrder(order);
    }

    private boolean isDeliveryModeInvalid(final OrderModel order) {
        return order.getDeliveryMode() == null && isNormalOrderOrNewOpenOrder(order);
    }

    private boolean isPaymentModeInvalid(final OrderModel order) {
        return order.getPaymentMode() == null && isNormalOrderOrNewOpenOrder(order);
    }

    private boolean isNewOpenOrder(final OrderModel order) {
        return isOpenOrder(order) && StringUtils.isBlank(order.getErpOpenOrderCode());
    }

    private boolean isOpenOrder(final OrderModel order) {
        return order.isOpenOrder();
    }

    private boolean isPaymentAddressInvalid(final OrderModel order) {
        return order.getPaymentAddress() == null && isNormalOrderOrNewOpenOrder(order);

    }

}
