package com.namics.distrelec.b2b.facades.product.impl;

import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import com.namics.distrelec.b2b.core.service.stock.DistStockNotificationService;
import com.namics.distrelec.b2b.facades.product.DistrelecOutOfStockNotificationFacade;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultDistOutOfStockNotificationFacade implements DistrelecOutOfStockNotificationFacade {

    @Autowired
    private DistStockNotificationService distStockNotificationService;
    @Autowired
    private CMSSiteService cmsSiteService;

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.product.DistrelecProductFacade#saveStockNotificationDetails(java.lang.String, java.lang.String)
     */
    @Override
    public boolean saveStockNotificationDetails(final String customerEmail, final List<String> articleNumbers) {
        final CMSSiteModel site = cmsSiteService.getCurrentSite();
        boolean wasOosNotificationSaved = false;
        if (CollectionUtils.isEmpty(getDistStockNotificationService().getStockNotificationDetails(customerEmail, articleNumbers, site))) {
            wasOosNotificationSaved = getDistStockNotificationService().addStockNotificationDetails(customerEmail, articleNumbers, site);
        }
        return wasOosNotificationSaved;
    }

    @Override
    public boolean saveBackOrderOutOfStock(final String customerEmail, final List<OrderEntryData> outOfStockCartItems) {
        final CMSSiteModel site = cmsSiteService.getCurrentSite();
        boolean wasNotificationSaved = false;

        final Map<String, OrderEntryData> productAndQuantityMap = outOfStockCartItems.stream()
                .collect(Collectors.toMap(key -> key.getProduct().getCode(), entryData -> entryData));

        final List<DistStockNotificationModel> existingNotificationModels = getDistStockNotificationService().getStockNotificationDetails(customerEmail, new ArrayList<>(productAndQuantityMap.keySet()), site);
        for(DistStockNotificationModel existingModel: existingNotificationModels){
            //Remove duplicates DISTRELEC-17257
            productAndQuantityMap.remove(existingModel.getArticleNumber());
        }

        if(MapUtils.isNotEmpty(productAndQuantityMap)) {
            wasNotificationSaved = getDistStockNotificationService().processOutOfStockNotification(customerEmail, productAndQuantityMap, site);
        }
        return wasNotificationSaved;
    }


    public DistStockNotificationService getDistStockNotificationService() {
        return distStockNotificationService;
    }

    public void setDistStockNotificationService(DistStockNotificationService distStockNotificationService) {
        this.distStockNotificationService = distStockNotificationService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }
}
