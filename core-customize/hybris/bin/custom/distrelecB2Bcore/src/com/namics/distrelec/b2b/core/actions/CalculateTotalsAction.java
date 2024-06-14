package com.namics.distrelec.b2b.core.actions;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import de.hybris.platform.b2b.process.approval.actions.AbstractProceduralB2BOrderApproveAction;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.task.RetryLaterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class CalculateTotalsAction extends AbstractProceduralB2BOrderApproveAction {

    private static final Logger LOG = LogManager.getLogger(CalculateTotalsAction.class);

    private CalculationService calculationService;
    private CMSSiteService cmsSiteService;

    @Override
    public void executeAction(B2BApprovalProcessModel process) throws RetryLaterException {
        OrderModel orderModel = process.getOrder();

        // Calculate the order now that it has been copied
        try {
            getCmsSiteService().setCurrentSite((CMSSiteModel) orderModel.getSite());
            getCalculationService().calculateTotals(orderModel, false);
        } catch (final CalculationException ex) {
            DistLogUtils.logError(LOG, "{} {} Failed to calculate order [{}]", ex, ErrorLogCode.PLACEORDER_ERROR, ErrorSource.HYBRIS, orderModel);
        }
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }

    @Required
    public void setCalculationService(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    @Required
    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }
}
