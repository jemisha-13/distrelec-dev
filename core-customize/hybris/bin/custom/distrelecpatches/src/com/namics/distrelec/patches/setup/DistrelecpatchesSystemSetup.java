/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.setup;

import com.namics.distrelec.patches.constants.DistrelecpatchesConstants;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.patches.AbstractPatchesSystemSetup;
import de.hybris.platform.patches.Patch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


/**
 * Example of SystemSetup that is used for creating patches data.
 */
@SystemSetup(extension = DistrelecpatchesConstants.EXTENSIONNAME)
public class DistrelecpatchesSystemSetup extends AbstractPatchesSystemSetup {

    private static final Logger LOG = LoggerFactory.getLogger(DistrelecpatchesSystemSetup.class);
    public static final String STAGED_CATALOG_VERSION = "Staged";
    public static final String ONLINE_CATALOG_VERSION = "Online";

    private List<Patch> patches = new ArrayList<>();

    private Set<String> cataloguesForSync;

    @Autowired
    private CatalogSynchronizationService catalogSynchronizationService;

    @Autowired
    private CatalogVersionService catalogVersionService;


    @Override
    @SystemSetup(type = Type.ESSENTIAL,
            process = Process.ALL)
    public void createEssentialData(final SystemSetupContext setupContext) {
        super.createEssentialData(setupContext);
    }

    /**
     * Implement this method to create data that is used in your project. This method will be called during the system initialization.
     *
     * @param setupContext the context provides the selected parameters and values
     */
    @Override
    @SystemSetup(type = Type.PROJECT,
            process = Process.ALL)
    public void createProjectData(final SystemSetupContext setupContext) {
        cataloguesForSync = new HashSet<>();
        super.createProjectData(setupContext);
        synchronizeCatalogues();
    }

    private void synchronizeCatalogues() {
        cataloguesForSync.stream()
                .map(catalogId -> {
                    try {
                        CatalogVersionModel stagedCatalog = catalogVersionService.getCatalogVersion(catalogId, STAGED_CATALOG_VERSION);
                        CatalogVersionModel onlineCatalog = catalogVersionService.getCatalogVersion(catalogId, ONLINE_CATALOG_VERSION);
                        return catalogSynchronizationService.getSyncJob(stagedCatalog, onlineCatalog, null);
                    } catch (Exception e) {
                        LOG.error("Failed to retrieve catalog sync job for catalog {}. Error: {}", catalogId, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(syncJob -> {
                    try {
                        catalogSynchronizationService.synchronize(syncJob, createSyncConfig());
                    } catch (Exception e) {
                        LOG.error("Synchronization job {} failed. Error: {}", syncJob.getCode(), e.getMessage());
                    }
                });
    }

    private SyncConfig createSyncConfig() {
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

    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        return super.getInitializationOptions();
    }

    public List<Patch> getPatches() {
        return patches;
    }

    @Override
    @Autowired
    public void setPatches(List<Patch> patches) {
        super.setPatches(patches);
        this.patches = patches;
    }

    @Override
    protected boolean isPatchRerunable(Patch patch) {
        return super.isPatchRerunable(patch) || isPatchFlaggedAsRerunnable(patch);
    }

    protected boolean isPatchFlaggedAsRerunnable(Patch patch) {
        String patchRerunProperty = String.join(".", "patches", patch.getPatchId(), "rerun.patch");
        Boolean isPatchRerunableEnabled = getBooleanValueForGivenKey(patchRerunProperty);
        return Boolean.TRUE.equals(isPatchRerunableEnabled);
    }

    @Override
    protected void executeVersionOnPatch(Patch patch) {
        super.executeVersionOnPatch(patch);
        if (patch instanceof SimpleDemoPatch) {
            cataloguesForSync.addAll(((SimpleDemoPatch) patch).getCatalogsForSyncronization());
        }
    }
}
