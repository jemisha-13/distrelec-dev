package distrelecpatches.releases.releaser1_7.patch_patch_30552.global

import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.catalog.model.SyncItemJobModel
import de.hybris.platform.catalog.synchronization.SyncConfig
import de.hybris.platform.cronjob.enums.JobLogLevel
import de.hybris.platform.tx.Transaction
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Field Logger LOG = LoggerFactory.getLogger("rpatch_30552_030-sync-content-catalogs.groovy")

@Field def catalogSynchronizationService = spring.getBean("catalogSynchronizationService")

@Field def catalogVersionService = spring.getBean("catalogVersionService")

@Field String STAGED_CATALOG_VERSION = "Staged"
@Field String ONLINE_CATALOG_VERSION = "Online"

@Field Set<String> cataloguesForSync = Set.of('distrelec_ATContentCatalog', 'distrelec_BEContentCatalog',
        'distrelec_CHContentCatalog', 'distrelec_CZContentCatalog', 'distrelec_DEContentCatalog',
        'distrelec_DKContentCatalog', 'distrelec_EEContentCatalog', 'distrelec_EXContentCatalog',
        'distrelec_FIContentCatalog', 'distrelec_FRContentCatalog', 'distrelec_HUContentCatalog',
        'distrelec_ITContentCatalog', 'distrelec_LTContentCatalog', 'distrelec_LVContentCatalog',
        'distrelec_NLContentCatalog', 'distrelec_NOContentCatalog', 'distrelec_PLContentCatalog',
        'distrelec_ROContentCatalog', 'distrelec_SEContentCatalog', 'distrelec_SKContentCatalog'
)

// sync all Site Catalogs so we can add Online components versions to International Catalog

List<SyncItemJobModel> syncJobs = new ArrayList<>();

for (String catalogId : cataloguesForSync) {
    SyncItemJobModel catalogSyncJob = getSyncJobForCatalog(catalogId)
    if (catalogSyncJob == null) {
        break;
    }
    syncJobs.add(catalogSyncJob);
}

// https://answers.sap.com/questions/12765139/entity-not-found-catalogversionsyncworker-processi.html
// https://answers.sap.com/questions/12760183/sync-a-product-from-groovy.html

// groovy script in HAC runs in an inner transaction, so everything that's saved through the modelService
// won't actually be persisted to the database until the transaction is committed (end of script),
// hence the entity not found in the database.

Transaction.current().commit();

for (SyncItemJobModel catalogSyncJob : syncJobs) {
    runSyncJobForCatalog(catalogSyncJob)
}

SyncItemJobModel getSyncJobForCatalog(String catalogId) {
    try {
        CatalogVersionModel stagedCatalog = catalogVersionService.getCatalogVersion(catalogId, STAGED_CATALOG_VERSION)
        CatalogVersionModel onlineCatalog = catalogVersionService.getCatalogVersion(catalogId, ONLINE_CATALOG_VERSION)
        LOG.info("Starting sync job for catalog", catalogId)
        return catalogSynchronizationService.getSyncJob(stagedCatalog, onlineCatalog, null)
    } catch (Exception e) {
        LOG.error("Failed to retrieve catalog sync job for catalog {}. Error: {}", catalogId, e.getMessage())
        return null
    }
}

void runSyncJobForCatalog(SyncItemJobModel catalogSyncJob) {
    try {
        catalogSynchronizationService.synchronize(catalogSyncJob, createSyncConfig());
    } catch (Exception e) {
        LOG.error("Synchronization job {} failed. Error: {}", catalogSyncJob.getCode(), e.getMessage());
    }
}

SyncConfig createSyncConfig() {
    SyncConfig syncConfig = new SyncConfig();
    syncConfig.setSynchronous(true);
    syncConfig.setCreateSavedValues(false);
    syncConfig.setForceUpdate(false);
    syncConfig.setLogLevelDatabase(JobLogLevel.ERROR);
    syncConfig.setLogLevelFile(JobLogLevel.ERROR);
    syncConfig.setLogToDatabase(false);
    syncConfig.setLogToFile(true);
    syncConfig.setAbortWhenCollidingSyncIsRunning(true);
    return syncConfig;
}

