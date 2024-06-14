package com.namics.distrelec.b2b.core.jalo.synchronization;

import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncCopyContext;
import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncCronJob;
import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncJob;
import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncWorker;
import de.hybris.platform.jalo.type.AttributeDescriptor;

/**
 * Adds attributes flagged with "sync" into an initial attribute list so their values will be copied to synchronised
 * cms components.
 */
public class DistCatalogVersionSyncCopyContext extends CatalogVersionSyncCopyContext {

    public static final String SYNC_PROP = "sync";

    public DistCatalogVersionSyncCopyContext(CatalogVersionSyncJob job, CatalogVersionSyncCronJob cronjob,
            CatalogVersionSyncWorker worker) {
        super(job, cronjob, worker);
    }

    @Override
    protected boolean isRequiredForCreation(AttributeDescriptor attributeDescriptor) {
        boolean isRequired = super.isRequiredForCreation(attributeDescriptor);
        if (isRequired) {
            return isRequired;
        }

        // parse sync prop
        Object syncProp = attributeDescriptor.getProperty(SYNC_PROP);
        if (syncProp instanceof Boolean) {
            return ((Boolean) syncProp).booleanValue();
        }

        return false;
    }
}
