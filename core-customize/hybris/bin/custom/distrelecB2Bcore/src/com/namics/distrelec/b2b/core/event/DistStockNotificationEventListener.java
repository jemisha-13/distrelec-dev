/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistStockNotificationProcessModel;
import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Numbers.ZERO;

/**
 * {@code DistNewProductsNewsLetterEventListener}
 *
 * @since Distrelec 5.10
 */
public class DistStockNotificationEventListener extends AbstractDistEventListener<DistStockNotificationEvent, DistStockNotificationProcessModel> {

    private static final String DISTRELEC_STOCK_NOTIFICATION_EMAIL_PROCESS = "distrelecStockNotificationEmailProcess";

    @Autowired
    private DistUserService distUserService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Override
    public DistStockNotificationProcessModel createTarget() {
        return getBusinessProcessService()
                .createProcess(DISTRELEC_STOCK_NOTIFICATION_EMAIL_PROCESS + System.currentTimeMillis(), DISTRELEC_STOCK_NOTIFICATION_EMAIL_PROCESS);
    }

    @Override
    protected boolean validate(final DistStockNotificationEvent event) {
        return event != null && CollectionUtils.isNotEmpty(event.getStockNotificationList());
    }

    @Override
    public void populate(final DistStockNotificationEvent event, final DistStockNotificationProcessModel target) {
        final List<DistStockNotificationModel> stockNotificationList = event.getStockNotificationList();
        DistStockNotificationModel stockNotification = stockNotificationList.get(ZERO);
        final BaseSiteModel baseSiteForUID = getBaseSiteServiceViaLookup().getBaseSiteForUID(stockNotification.getCurrentSite().getUid());
        target.setSite(baseSiteForUID);
        target.setLanguage(getLanguage(stockNotification));
        target.setStockNotificationList(stockNotificationList);
        target.setCustomer(distUserService.getAnonymousUser());
        getModelServiceViaLookup().save(target);
    }

    private LanguageModel getLanguage(DistStockNotificationModel stockNotification) {
        String languageIsocode = stockNotification.getLanguage() != null ? stockNotification.getLanguage().getIsocode() : "en";
        return commonI18NService.getLanguage(languageIsocode);
    }
}
