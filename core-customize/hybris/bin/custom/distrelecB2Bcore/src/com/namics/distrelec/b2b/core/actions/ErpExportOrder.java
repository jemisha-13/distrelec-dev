/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.erp.CheckoutService;

import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.session.SessionService;
/**
 * Exports an order to the corresponding ERP system.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class ErpExportOrder extends AbstractSimpleDecisionAction<OrderProcessModel> {

    private static final Logger LOG = LogManager.getLogger(ErpExportOrder.class);

    @Autowired
    @Qualifier("erp.checkoutService")
    private CheckoutService checkoutService;

    @Autowired
    private SessionService sessionService;

    @Override
    public Transition executeAction(final OrderProcessModel orderProcess) throws Exception {
        final OrderModel orderModel = orderProcess.getOrder();
        if(null==orderModel || null==orderModel.getSite()) {
            LOG.error("{} Site doesn't exist in order model:",(null!=orderModel)?orderModel.getCode():"Order model is null");
        }
        getSessionService().setAttribute("currentSite", orderModel.getSite());
        final boolean exportSuccessful = getCheckoutService().exportOrder(orderModel);
        if (exportSuccessful) {
            orderModel.setExportStatus(ExportStatus.EXPORTED);
        } else {
            orderModel.setExportStatus(ExportStatus.NOTEXPORTED);
            LOG.error("{} Can not export order to ERP.",ErrorLogCode.ERP_EXPORT_ERROR.getCode());
        }
        getModelService().save(orderModel);

        return exportSuccessful ? Transition.OK : Transition.NOK;
    }

    public CheckoutService getCheckoutService() {
        return checkoutService;
    }

    public void setCheckoutService(final CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
