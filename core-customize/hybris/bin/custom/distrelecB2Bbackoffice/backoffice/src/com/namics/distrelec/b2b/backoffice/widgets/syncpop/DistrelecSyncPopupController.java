package com.namics.distrelec.b2b.backoffice.widgets.syncpop;

import com.hybris.backoffice.sync.SyncTask;
import com.hybris.backoffice.sync.SyncTaskExecutionInfo;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.syncpopup.SyncPopupController;
import com.hybris.cockpitng.annotations.SocketEvent;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.catalog.synchronization.SyncResult;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import org.zkoss.zul.Messagebox;

import java.util.List;

public class DistrelecSyncPopupController extends SyncPopupController {

    private CatalogSynchronizationService catalogSynchronizationService;

    @SocketEvent(
            socketId = "inputObjects"
    )
    public void showSyncJobsForInputObjects(List<ItemModel> items) {
        this.setValue("modelSyncItems", items);
        if (items.size() == 1) {
            this.prepareView(items);
        } else {
            triggerMultipleSynchronizationJobs(items);
        }
    }

    private void triggerMultipleSynchronizationJobs(List<ItemModel> syncItems) {
        syncItems.stream().map(CatalogVersionModel.class::cast)
                .map(CatalogVersionModel::getCatalog)
                .flatMap(catalogModel -> catalogModel.getCatalogVersions().stream())
                .flatMap(catalogVersionModel -> catalogVersionModel.getSynchronizations().stream())
                .distinct()
                .forEach(this::startSynchronizationJob);
        showInfoBox("sync.jobs.multiplesynchronization.ok", "title.window");
        closeSyncPopup();
    }

    private void showInfoBox(String msgKey, String titleKey) {
        Messagebox.show(this.getLabel(msgKey), this.getLabel(titleKey), 1, "z-messagebox-icon z-messagebox-information");
    }

    private void startSynchronizationJob(SyncItemJobModel syncItemJob) {
        SyncTask syncTask = new SyncTask(syncItemJob);
        SyncConfig syncConfig = createSyncConfig();
        SyncResult syncResult = catalogSynchronizationService.synchronize(syncItemJob, syncConfig);
        String cronJobCode = syncResult.getCronJob().getCode();
        if (cronJobCode != null) {
            sendOutput("startedSyncCronJob", cronJobCode);
            sendOutput("syncTaskExecutionInfo", new SyncTaskExecutionInfo(syncTask, cronJobCode));
        } else {
            getNotificationService().notifyUser("syncPopup", "syncCannotRun", NotificationEvent.Level.FAILURE, new Object[0]);
        }
    }

    private SyncConfig createSyncConfig(){
        SyncConfig syncConfig = new SyncConfig();
        syncConfig.setSynchronous(false);
        syncConfig.setCreateSavedValues(false);
        syncConfig.setForceUpdate(false);
        syncConfig.setLogLevelDatabase(JobLogLevel.ERROR);
        syncConfig.setLogLevelFile(JobLogLevel.ERROR);
        syncConfig.setLogToDatabase(false);
        syncConfig.setLogToFile(false);
        syncConfig.setAbortWhenCollidingSyncIsRunning(true);
        return syncConfig;
    }


}
