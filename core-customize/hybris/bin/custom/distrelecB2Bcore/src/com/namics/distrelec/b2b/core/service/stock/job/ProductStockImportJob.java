package com.namics.distrelec.b2b.core.service.stock.job;

import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.model.jobs.ProductStockImportCronJobModel;
import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.product.model.StockLevelData;
import com.namics.distrelec.b2b.core.service.stock.DistStockNotificationService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductStockImportJob extends AbstractJobPerformable<ProductStockImportCronJobModel> {

    private static final Logger LOG = LogManager.getLogger(ProductStockImportJob.class);
    private static final String TWENTY_FOUR_HOURS = "24";
    private static final int ZERO = 0;

    @Autowired
    @Qualifier("erp.availabilityService")
    private AvailabilityService availabilityService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistStockNotificationService distStockNotificationService;

    @Override
    public PerformResult perform(ProductStockImportCronJobModel arg0) {

        boolean stockUpdated = false;
        boolean hasErrored = false;

        // Fetch the data and send it directly
        final List<DistStockNotificationModel> stockNotificationList = getDistStockNotificationService().getAllStockNotifications();
        if (CollectionUtils.isNotEmpty(stockNotificationList)) {
            for (DistStockNotificationModel stockNotificationModel : stockNotificationList) {

                final CMSSiteModel cmsSite = stockNotificationModel.getCurrentSite();

                // Set the CMS Site
                getCmsSiteService().setCurrentSite(cmsSite);

                try {
                    // Get current stocks
                    List<String> productCodeList = new ArrayList<>();
                    productCodeList.add(stockNotificationModel.getArticleNumber());
                    final List<ProductAvailabilityExtModel> productsAvailability = getAvailabilityService().getAvailability(productCodeList, true);

                    Integer availableStocks = ZERO;
                    if(CollectionUtils.isNotEmpty(productsAvailability)){
                        final ProductAvailabilityExtModel productAvailabilityExtModel = productsAvailability.stream()
                                .filter(stockLevels -> !stockLevels.getStockLevels().isEmpty())
                                .findAny()
                                .orElse(null);

                        Integer stockLevelSum = ZERO;
                        if(productAvailabilityExtModel != null){
                            stockLevelSum = productAvailabilityExtModel.getStockLevels().stream()
                                    .filter(Objects::nonNull)
                                    .mapToInt(StockLevelData::getAvailable)
                                    .sum();
                        }

                        if(productAvailabilityExtModel != null){
                             if(stockLevelSum > ZERO){
                                 availableStocks = stockLevelSum;
                             } else {
                                 availableStocks = productAvailabilityExtModel.getStockLevelTotal() == null ? Integer.valueOf(ZERO) : productAvailabilityExtModel.getStockLevelTotal();
                             }
                         }
                    }

                    // Check if the stocks are available.
                    if (availableStocks > ZERO) {
                        stockNotificationModel.setAvailableStocks(availableStocks);
                        stockNotificationModel.setCurrentSite(cmsSite);
                        // Save the updated stocks into Database.
                        getModelService().save(stockNotificationModel);
                        stockUpdated = true;
                    } else {
                        LOG.debug("There were no available stock to update for {}", stockNotificationModel.getArticleNumber());
                    }
                } catch (ModelSavingException modelSavingException) {
                    LOG.error("Data could not be saved in Database : " + modelSavingException.getLocalizedMessage());
                    hasErrored = true;
                } catch (WebServiceException webServiceException) {
                    LOG.error("Web Service Error : " + webServiceException.getLocalizedMessage());
                    hasErrored = true;
                } catch (Exception exception) {
                    LOG.error("Error occurred, please find details : " + exception.getLocalizedMessage());
                    hasErrored = true;
                }
            }
        } else {
            stockUpdated = true;
            LOG.warn("Performed Product Stock Import Job on {} list", null == stockNotificationList ? "a null" : "an empty");
        }

        if (!stockUpdated) {
            LOG.info("There were no available stock to update");
        }

        final CronJobResult cronJobResult = !hasErrored ? CronJobResult.SUCCESS : CronJobResult.ERROR;
        return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
    }

    public AvailabilityService getAvailabilityService() {
        return availabilityService;
    }

    public void setAvailabilityService(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public DistStockNotificationService getDistStockNotificationService() {
        return distStockNotificationService;
    }

    public void setDistStockNotificationService(DistStockNotificationService distStockNotificationService) {
        this.distStockNotificationService = distStockNotificationService;
    }
}
