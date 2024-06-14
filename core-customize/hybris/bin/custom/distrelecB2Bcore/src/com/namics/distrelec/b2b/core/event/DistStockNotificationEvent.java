/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.util.List;

/**
 * {@code DistStockNotificationEvent}
 *
 * @since Distrelec 5.10
 */
public class DistStockNotificationEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    private List<DistStockNotificationModel> stockNotificationList;

    public DistStockNotificationEvent(final List<DistStockNotificationModel> stockNotificationList){
        super();
        this.stockNotificationList = stockNotificationList;
    }

    public List<DistStockNotificationModel> getStockNotificationList() {
        return stockNotificationList;
    }

    public final void setStockNotificationList(final List<DistStockNotificationModel> stockNotificationList) {
        this.stockNotificationList = stockNotificationList;
    }
}
