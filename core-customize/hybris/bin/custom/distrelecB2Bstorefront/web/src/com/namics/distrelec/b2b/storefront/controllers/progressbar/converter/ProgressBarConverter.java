/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.progressbar.converter;

import java.util.Arrays;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.storefront.controllers.progressbar.data.ProgressBarData;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public class ProgressBarConverter extends AbstractPopulatingConverter<String, ProgressBarData> {

    @Autowired
    private ConfigurationService configurationService;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractPopulatingConverter#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final String baseCode, final ProgressBarData progressBar) {
        final Configuration configuration = configurationService.getConfiguration();
        progressBar.setCode(baseCode);
        progressBar.setMessageCode(configuration.getString(baseCode + ".message"));
        progressBar.setStepClass(configuration.getString(baseCode + ".class"));
        progressBar.setUrl(configuration.getString(baseCode + ".url"));
        progressBar.setPageUrl(configuration.getString(baseCode + ".pageUrl", progressBar.getUrl()));

        final String pageUrls = configuration.getString(baseCode + ".pageUrls", StringUtils.EMPTY);
        if (StringUtils.isNotBlank(pageUrls)) {
            progressBar.setPageUrls(Arrays.asList(pageUrls.split(";")));
        }

        super.populate(baseCode, progressBar);
    }
}
