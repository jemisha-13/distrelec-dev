package com.namics.distrelec.b2b.core.service.stock.job;

import com.namics.distrelec.b2b.core.event.DistStockNotificationEvent;
import com.namics.distrelec.b2b.core.mail.internal.AbstractDistInternalMailJob;
import com.namics.distrelec.b2b.core.model.jobs.StockNotificationEmailCronJobModel;
import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import com.namics.distrelec.b2b.core.service.site.impl.DefaultDistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.service.stock.DistStockNotificationService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.distrelecB2BCore.stockNotification.data.StockNotificationProductResult;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Numbers.ONE;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Numbers.ZERO;

/**
 * {@code StockNotificationEmailJob}
 *
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler</a>, Distrelec
 * @since Distrelec 7.1
 */
public class StockNotificationEmailJob extends AbstractDistInternalMailJob<StockNotificationEmailCronJobModel, List<String>, StockNotificationProductResult> {

    private static final Logger LOG = LogManager.getLogger(StockNotificationEmailJob.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private DistStockNotificationService distStockNotificationService;

    @Autowired
    private DefaultDistrelecBaseStoreService baseStoreService;

    @Override
    public PerformResult perform(final StockNotificationEmailCronJobModel stockNotificationEmailCronJob) {
       boolean hasEMailBeenSent = true;

        try {
            // Fetch the data and send it directly
            final List<DistStockNotificationModel> stockListForProcessing = new ArrayList<>();
            final List<DistStockNotificationModel> stockNotificationList = getDistStockNotificationService().getStockNotificationsForAvailableStocks();

            for(DistStockNotificationModel model: stockNotificationList){
                final Integer availableStocks = model.getAvailableStocks();
                if(availableStocks == null){
                    LOG.warn("Skipping Product {} for OOS {} as availableStocks is null", model.getArticleNumber(), model.getPk());
                    continue;
                }

                Integer quantityRequired = model.getQuantityRequiredToSendEmail();

                if(quantityRequired == null){
                    //coerce nulls to one, these are old records from the PDP.
                    quantityRequired = ONE;
                }

                if(availableStocks - quantityRequired >= ZERO){
                    stockListForProcessing.add(model);
                } else {
                    LOG.info("Product {} has quantityRequired [{}] more than availableStock [{}] - skipping. No records deleted", model.getArticleNumber(), model.getQuantityRequiredToSendEmail(), model.getAvailableStocks());
                }
            }

            if (CollectionUtils.isEmpty(stockListForProcessing)) {
                // The List is empty
                LOG.error("No emails sent on date: '{}' as Stock Notifications were empty.", new Date());
            } else {
                final Map<String, List<DistStockNotificationModel>> stockNotificationsByEmail = stockListForProcessing.stream().collect(Collectors.groupingBy(DistStockNotificationModel::getCustomerEmailId));

                for (Map.Entry<String, List<DistStockNotificationModel>> emailDataByEmailAddress : stockNotificationsByEmail.entrySet()) {
                    try {
                        final Map<CMSSiteModel, List<DistStockNotificationModel>> customerStockNotificationsBySite = emailDataByEmailAddress.getValue()
                                .stream()
                                .collect(Collectors.groupingBy(DistStockNotificationModel::getCurrentSite));

                        for (final Map.Entry<CMSSiteModel, List<DistStockNotificationModel>> emailDataBySite : customerStockNotificationsBySite.entrySet()) {

                            final List<DistStockNotificationModel> emailData = new ArrayList<>(emailDataBySite.getValue());
                            getEventService().publishEvent(createEvent(emailData));
                        }
                    } catch (Exception e) {
                        LOG.error("Stock Notification Email was not sent: " + e.getMessage());
                        hasEMailBeenSent = false;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Cronjob Failed : " + e.getMessage());
            hasEMailBeenSent = false;
        }
        return new PerformResult(hasEMailBeenSent ? CronJobResult.SUCCESS : CronJobResult.FAILURE, CronJobStatus.FINISHED);

    }

    private DistStockNotificationEvent createEvent(final List<DistStockNotificationModel> emailData) {
        final DistStockNotificationEvent event = new DistStockNotificationEvent(emailData);

        final DistStockNotificationModel model = emailData.get(ZERO);
        event.setSite(emailData.get(ZERO).getCurrentSite());
        event.setLanguage(model.getLanguage());
        return event;
    }

    @Override
    protected StockNotificationProductResult convert(final List<String> source) {
        LOG.warn("Attempting to convert and return StockNotificationProductResult, will return null");
        return null;
    }

    @Override
    protected FlexibleSearchQuery getQuery() {
        LOG.warn("Attempting to getQuery, will return null");
        return null;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

    public DistStockNotificationService getDistStockNotificationService() {
        return distStockNotificationService;
    }

    public void setDistStockNotificationService(final DistStockNotificationService distStockNotificationService) {
        this.distStockNotificationService = distStockNotificationService;
    }

    public DefaultDistrelecBaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(DefaultDistrelecBaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }
}
