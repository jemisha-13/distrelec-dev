package com.namics.distrelec.b2b.core.setup;

import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public interface DistSetupSyncJobService extends SetupSyncJobService {

    PerformResult executeCatalogSyncJob(String catalogId, boolean fullSync);
}
