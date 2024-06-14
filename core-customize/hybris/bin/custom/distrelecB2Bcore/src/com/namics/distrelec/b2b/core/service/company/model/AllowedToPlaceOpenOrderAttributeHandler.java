/*
 * Copyright 2000-2014 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.company.model;

import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * There is the possibility to disable open orders from the Customer point of view.<br>
 * If you want to deactivate the open order functionality please set this parameter to false<br>
 * <b>distrelec.openorders.enabled=false<b>
 * 
 * If the open order functionality is active the attribute is calculated on the base of the value of openOrderMaxKeepOpenDays field.<br>
 * If this value is 0 it means the company is not allowed to place open orders.
 * 
 * See specifications:<br>
 * https://wiki.namics.com/display/distrelint/IF-08+Customer+Service
 * 
 * 
 * @author DAEBERSANIF, Namics AG
 * @since Distrelec 2.0
 * 
 */
public class AllowedToPlaceOpenOrderAttributeHandler extends AbstractDynamicAttributeHandler<Boolean, CompanyModel> {

    private static final String IS_OPEN_ORDER_ENABLED_KEY = "distrelec.openorders.enabled";

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public Boolean get(final CompanyModel model) {
        boolean isOpenOrdersEnabled = true;

        if (getConfigurationService() != null && getConfigurationService().getConfiguration() != null) {
            isOpenOrdersEnabled = getConfigurationService().getConfiguration().getBoolean(IS_OPEN_ORDER_ENABLED_KEY, true);
        }

        return Boolean.valueOf(isOpenOrdersEnabled && model.getOpenOrderMaxKeepOpenDays() > 0);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
