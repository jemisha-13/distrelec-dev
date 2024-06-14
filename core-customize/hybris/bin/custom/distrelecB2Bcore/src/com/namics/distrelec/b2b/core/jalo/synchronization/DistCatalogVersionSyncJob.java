package com.namics.distrelec.b2b.core.jalo.synchronization;

import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncCopyContext;
import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncCronJob;
import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncJob;
import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncWorker;

public class DistCatalogVersionSyncJob extends CatalogVersionSyncJob {

    @Override
    protected CatalogVersionSyncCopyContext createCopyContext(CatalogVersionSyncCronJob catalogVersionSyncCronJob, CatalogVersionSyncWorker worker) {
        return new DistCatalogVersionSyncCopyContext(this, catalogVersionSyncCronJob, worker);
    }
}
