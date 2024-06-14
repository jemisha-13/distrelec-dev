package com.namics.distrelec.b2b.core.actions.stocknotification;

import com.namics.distrelec.b2b.core.model.process.DistStockNotificationProcessModel;
import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import com.namics.distrelec.b2b.core.service.stock.DistStockNotificationService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * {@code RemoveStockNotificationEntryAction}
 *
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler</a>, Distrelec
 * @since Distrelec 7.1
 */
public class RemoveStockNotificationEntryAction extends AbstractProceduralAction {

    private static final Logger LOG = LogManager.getLogger(RemoveStockNotificationEntryAction.class);

    @Autowired
    private DistStockNotificationService distStockNotificationService;

    @Autowired
    private ModelService modelService;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.processengine.action.AbstractProceduralAction#executeAction(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    public void executeAction(BusinessProcessModel businessProcessModel) throws RetryLaterException, Exception {

        final List<DistStockNotificationModel> stockNotificationList = ((DistStockNotificationProcessModel) businessProcessModel).getStockNotificationList();

        try {
            getModelService().removeAll(stockNotificationList);
        } catch (ModelRemovalException modelRemovalException) {
            LOG.error("Error occured in class : " + RemoveStockNotificationEntryAction.class + " while removing Stock Notification Entries. Details : "
                    + modelRemovalException.getMessage());
        }

    }

    public DistStockNotificationService getDistStockNotificationService() {
        return distStockNotificationService;
    }

    public void setDistStockNotificationService(DistStockNotificationService distStockNotificationService) {
        this.distStockNotificationService = distStockNotificationService;
    }

    @Override
    public ModelService getModelService() {
        return modelService;
    }

}
