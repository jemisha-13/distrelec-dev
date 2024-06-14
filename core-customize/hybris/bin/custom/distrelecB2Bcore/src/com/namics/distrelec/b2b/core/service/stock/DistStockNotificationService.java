package com.namics.distrelec.b2b.core.service.stock;

import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;

/**
 * {@code StockNotificationEmailJob}
 * 
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler</a>, Distrelec
 * @since Distrelec 7.1
 */
public interface DistStockNotificationService {

    /**
     * @param customerEmail
     * @param articleNumbers
     * @param site
     * @return DistStockNotificationModel
     */
    List<DistStockNotificationModel> getStockNotificationDetails(final String customerEmail, final List<String> articleNumbers, final CMSSiteModel site);

    /**
     * @param customerEmail
     * @param articleNumbers
     * @param site
     * @return boolean
     */
    boolean addStockNotificationDetails(final String customerEmail, final List<String> articleNumbers, final CMSSiteModel site);

    /**
     * @return List
     */
    List<DistStockNotificationModel> getAllStockNotifications();
    
    /**
     * @return List
     */
    List<DistStockNotificationModel> getStockNotificationsForAvailableStocks();

    /**
     *
     * @param value
     */
    void removeStockNotificationDetails(List<DistStockNotificationModel> value);

    /**
     *
     * @param customerEmail
     * @param productAndQuantityMap
     * @param site
     * @return
     */
    boolean processOutOfStockNotification(final String customerEmail, final Map<String, OrderEntryData> productAndQuantityMap, final CMSSiteModel site);
}
