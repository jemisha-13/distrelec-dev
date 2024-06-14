/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.task.before;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.dataimport.batch.task.BeforeImpexImport;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.impex.ImportConfig;

/**
 * {@code LegacyModeBeforeImpexImport}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.9
 */
public class LegacyModeBeforeImpexImport implements BeforeImpexImport {

    private static final Logger LOG = Logger.getLogger(LegacyModeBeforeImpexImport.class);

    private ConfigurationService configurationService;
    private String legacyModePropertyName;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.dataimport.batch.task.BeforeImpexImport#before(de.hybris.platform.servicelayer.impex.ImportConfig,
     * de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader, java.io.File, java.io.File)
     */
    @Override
    public void before(final ImportConfig importConfig, final BatchHeader batchHeader, final File originalFile, final File impexFile) {
        if (getLegacyModePropertyName() != null) {
            importConfig.setLegacyMode(Boolean.valueOf(getConfigurationService().getConfiguration().getBoolean(getLegacyModePropertyName(), false)));
        } else {
            LOG.warn("The field legacyModePropertyName is not set !!");
        }
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public String getLegacyModePropertyName() {
        return legacyModePropertyName;
    }

    @Required
    public void setLegacyModePropertyName(final String legacyModePropertyName) {
        this.legacyModePropertyName = legacyModePropertyName;
    }
}
