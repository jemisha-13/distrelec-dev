/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.catplus;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Import;
import com.namics.distrelec.b2b.core.inout.catplus.parser.CatalogPlusImportParser;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * {@code CatalogPlusImportTask}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class CatalogPlusImportTask {

    private static final Logger LOG = Logger.getLogger(CatalogPlusImportTask.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CatalogVersionService catalogVersionService;

    private CatalogPlusImportParser catalogPlusImportParser;
    private CleanupHelper cleanupHelper;

    /**
     * Start parsing CatalogPlus file
     * 
     * @param file
     */
    public void execute(final File file) {
        final long startTimeMillis = System.currentTimeMillis();
        LOG.info("Start importing of file [" + file + "]");
        Assert.notNull(file);

        if (!Registry.hasCurrentTenant()) {
            Registry.activateMasterTenant();
        }

        final User user = UserManager.getInstance().getUserByLogin("admin");

        SessionContext ctx = null;
        try {
            ctx = JaloSession.getCurrentSession().createLocalSessionContext();
            ctx.setUser(user);

            final Configuration configuration = configurationService.getConfiguration();
            catalogVersionService.setSessionCatalogVersion(configuration.getString(Import.PRODUCT_CATALOG_PLUS_ID),
                    configuration.getString(Import.PRODUCT_CATALOG_PLUS_VERSION));

            final boolean successful = catalogPlusImportParser.parse(file);

            cleanup(file, !successful);
            LOG.info("Import of file [" + file + "] took " + ((System.currentTimeMillis() - startTimeMillis) / 1000) + "s and "
                    + (successful ? "succeeded" : "failed"));
        } finally {
            if (ctx != null) {
                JaloSession.getCurrentSession().removeLocalSessionContext();
            }
        }
    }

    /**
     * cleanup after processing
     * 
     * @param file
     * @param error
     */
    private void cleanup(final File file, final boolean error) {

        final BatchHeader batchHeader = new BatchHeader();
        batchHeader.setFile(file);
        cleanupHelper.cleanup(batchHeader, error);
    }

    /* Getters & Setters */

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    public void setCatalogVersionService(final CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public CleanupHelper getCleanupHelper() {
        return cleanupHelper;
    }

    @Required
    public void setCleanupHelper(final CleanupHelper cleanupHelper) {
        this.cleanupHelper = cleanupHelper;
    }

    public CatalogPlusImportParser getCatalogPlusImportParser() {
        return catalogPlusImportParser;
    }

    @Required
    public void setCatalogPlusImportParser(final CatalogPlusImportParser catalogPlusImportParser) {
        this.catalogPlusImportParser = catalogPlusImportParser;
    }

}
