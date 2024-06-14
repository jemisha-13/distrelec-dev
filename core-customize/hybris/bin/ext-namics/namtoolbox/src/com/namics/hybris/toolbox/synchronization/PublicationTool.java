package com.namics.hybris.toolbox.synchronization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.jalo.SyncItemCronJob;
import de.hybris.platform.catalog.jalo.SyncItemJob;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;

public class PublicationTool {

    /**
     * Performs a publication on a catalog version.
     * 
     * @param catalogID
     *            The catalog id like 'axiBase'
     * @param stagedVersion
     *            The name of the version like 'service_staged'.
     * @param publicationName
     *            The name of the publication in the stageVersion like 'AXI Services stage -> live Full'
     * @throws Exception
     *             If an exception occured.
     */
    public static void syncFromStagedToOnlineVersion(final String catalogID, final String stagedVersion, final String publicationName) throws Exception {
        // get the cataloge (Step 1)
        final Catalog catalog = CatalogManager.getInstance().getCatalog(catalogID);
        if (catalog != null) {
            final CatalogVersion offlineVersion = catalog.getCatalogVersion(stagedVersion);

            // the catalog version has an attribute which holds the synchronisation process from staged to online
            final List<SyncItemJob> jobs = offlineVersion.getSynchronizations();
            SyncItemJob job = null;

            for (final SyncItemJob syncItemJob : jobs) {
                if (syncItemJob.getCode().equals(publicationName)) {
                    job = syncItemJob;
                }
            }

            if (job == null) {
                throw new JaloSystemException("Couldn't find SyncItemJob '" + publicationName + "' for version '" + offlineVersion + "'");
            }

            final EnumerationValue ev = EnumerationManager.getInstance().getEnumerationValue("JobLogLevel", "WARNING");

            // get the cronjob instance and configure it (Step 3)
            final SyncItemCronJob cronjob = job.newExecution();
            cronjob.setLogToDatabase(true);
            cronjob.setLogToFile(true);
            cronjob.setLogLevelDatabase(ev); // Enum JronJobLoglevel INFO
            cronjob.setLogLevelFile(ev); // Enum JronJobLoglevel INFO
            cronjob.setForceUpdate(false);

            // configure the job
            job.configureFullVersionSync(cronjob);

            // start the job (Step 4)
            job.perform(cronjob, true); // true, hier wird synchron ausgef�hrt

            // rebuild index (Step 5)
            // Cause this step is very time expensive, we leave it out.
            // LucenesearchManager.getInstance().rebuildAllIndexes();
        }
    }

    /**
     * Performs a publication on a catalog version.
     * 
     * @param publicationName
     *            The name of the publication in the stageVersion like 'AXI Services stage -> live Full'
     * @throws Exception
     *             If an exception occured.
     */
    @SuppressWarnings("unchecked")
    public static void syncFromStagedToOnlineVersion(final String publicationName) throws Exception {
        if (publicationName == null) {
            throw new IllegalArgumentException("publicationName must not be null.");
        }
        final String flexisearch = new StringBuilder().append("SELECT {").append(Item.PK).append("} FROM {")
                .append(de.hybris.platform.catalog.constants.GeneratedCatalogConstants.TC.SYNCITEMJOB).append("} ").append("WHERE {").append(SyncItemJob.CODE)
                .append("}=?code").toString();

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("code", publicationName);
        final List<SyncItemJob> jobs = FlexibleSearch.getInstance().search(flexisearch, params, SyncItemJob.class).getResult();

        if (jobs.isEmpty()) {
            throw new IllegalStateException("No synchronisation job with qualifier '" + publicationName + "' were found.");
        }

        if (jobs.size() > 1) {
            throw new IllegalStateException("More than one job with qualifier '" + publicationName + "' were found.");
        }

        final SyncItemJob job = jobs.get(0);
        final CatalogVersion stagedVersion = job.getSourceVersion();
        final Catalog catalog = stagedVersion.getCatalog();
        PublicationTool.syncFromStagedToOnlineVersion(catalog.getId(), stagedVersion.getVersion(), publicationName);

    }

}
