package com.namics.distrelec.b2b.core.service.stock.dao.impl;

import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import com.namics.distrelec.b2b.core.service.stock.dao.DistStockNotificationDao;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code StockNotificationEmailJob}
 * 
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler</a>, Distrelec
 * @since Distrelec 7.1
 */
public class DefaultDistStockNotificationDao implements DistStockNotificationDao {

    // Search query for all entries in Stock Notification table
    private static final String GET_ALL_STOCK_NOTIFICATION_DETAILS_QUERY = "SELECT {" + DistStockNotificationModel.PK + "} FROM {" + DistStockNotificationModel._TYPECODE + "}";

    // Search query for entries in Stock Notification table which has available stock
    private static final String STOCK_NOTIFICATION_FOR_AVAILABLE_STOCK = "SELECT {" + DistStockNotificationModel.PK + "} FROM {"
            + DistStockNotificationModel._TYPECODE + "} WHERE {" + DistStockNotificationModel.AVAILABLESTOCKS +"} IS NOT NULL";

    private static final String FIND_DUPLICATED_STOCK_RECORDS = "SELECT MIN({"+ DistStockNotificationModel.PK+"}) FROM {"
            + DistStockNotificationModel._TYPECODE +  "} GROUP BY {" + DistStockNotificationModel.CUSTOMEREMAILID + "}, {" + DistStockNotificationModel.ARTICLENUMBER + "}, {"
            + DistStockNotificationModel.CURRENTSITE + "}, {" + DistStockNotificationModel.LANGUAGE + "}, {" + DistStockNotificationModel.CURRENCY + "} HAVING COUNT({"
            + DistStockNotificationModel.PK + "}) >= 2";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    private static final int ZERO = 0;

    /**
     * DistStockNotification query
     */
    private static final String GET_STOCK_NOTIFICATIONS_BY_EMAIL_ARTICLE_NUMBER_AND_SITE_QUERY = "SELECT {pk} FROM {" + DistStockNotificationModel._TYPECODE + "} WHERE {"
            + DistStockNotificationModel.CUSTOMEREMAILID + "}=?customerEmailId AND {" + DistStockNotificationModel.ARTICLENUMBER + "}=?articleNumber "
            + "AND {" + DistStockNotificationModel.CURRENTSITE + "}=?currentSite";

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.product.dao.DistProductDao#getStockNotificationDetails(java.lang.String, java.lang.String)
     */
    @Override
    public DistStockNotificationModel getStockNotificationDetails(final String customerEmail, final String articleNumber, final CMSSiteModel site) {
        final List<DistStockNotificationModel> results = getDistStockNotificationModels(customerEmail, articleNumber, GET_STOCK_NOTIFICATIONS_BY_EMAIL_ARTICLE_NUMBER_AND_SITE_QUERY, site);
        return CollectionUtils.isEmpty(results) ? null : results.get(ZERO);
    }

    private List<DistStockNotificationModel> getDistStockNotificationModels(final String customerEmail, final String articleNumber, final String query, final CMSSiteModel site) {
        final Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(DistStockNotificationModel.CUSTOMEREMAILID, customerEmail);
        parameterMap.put(DistStockNotificationModel.ARTICLENUMBER, articleNumber);
        parameterMap.put(DistStockNotificationModel.CURRENTSITE, site);
        return executeStockNotificationQuery(query, parameterMap);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.stock.dao.DistStockNotificationDao#getAllStockNotifications()
     */
    @Override
    public List<DistStockNotificationModel> getAllStockNotifications() {
        return executeStockNotificationQuery(GET_ALL_STOCK_NOTIFICATION_DETAILS_QUERY, null);
    }

    @Override
    public List<DistStockNotificationModel> getStockNotificationsForAvailableStocks() {
        return executeStockNotificationQuery(STOCK_NOTIFICATION_FOR_AVAILABLE_STOCK, null);
    }

    private List<DistStockNotificationModel> executeStockNotificationQuery(final String queryType, final Map<String, Object> parameterMap) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryType);
        if(parameterMap != null){
            searchQuery.addQueryParameters(parameterMap);
        }

        final SearchResult<DistStockNotificationModel> searchResult = flexibleSearchService.search(searchQuery);
        return searchResult.getCount() > ZERO ? searchResult.getResult() : Collections.emptyList();
    }

    @Override
    public List<DistStockNotificationModel> getDuplicateStockNotificationRecords(){
        final List<DistStockNotificationModel> results = executeStockNotificationQuery(FIND_DUPLICATED_STOCK_RECORDS, null);
        return CollectionUtils.isEmpty(results) ? null : results;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}
