package com.namics.distrelec.b2b.core.service.stock.dao;

import java.util.List;

import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;

/**
 * {@code StockNotificationEmailJob}
 * 
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler</a>, Distrelec
 * @since Distrelec 7.1
 */
public interface DistStockNotificationDao {

    /**
     * @param customerEmail
     * @param articleNumber
     * @param site
     * @return DistStockNotificationModel
     */
    DistStockNotificationModel getStockNotificationDetails(final String customerEmail, final String articleNumber, final CMSSiteModel site);

    /**
     *
     * @param customerEmail
     * @param articleNumber
     * @return
     */
    List<DistStockNotificationModel> getDuplicateStockNotificationRecords();

    /**
     * @return List
     */
    List<DistStockNotificationModel> getAllStockNotifications();

    /**
     * @return List
     */
    List<DistStockNotificationModel> getStockNotificationsForAvailableStocks();

}
