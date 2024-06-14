package com.namics.hybris.ffsearch.category;

import com.namics.hybris.ffsearch.model.category.UpdateCategoryTraversalDataCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.springframework.beans.factory.annotation.Required;

public class UpdateCategoryTraversalDataJob extends AbstractJobPerformable<UpdateCategoryTraversalDataCronJobModel> {

    private UpdateCategoryTraversalDataService updateCategoryTraversalDataService;

    @Override
    public PerformResult perform(UpdateCategoryTraversalDataCronJobModel updateCategoryTraversalDataCronJobModel) {
        getUpdateCategoryTraversalDataService().updateCategoryTraversalData();
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public UpdateCategoryTraversalDataService getUpdateCategoryTraversalDataService() {
        return updateCategoryTraversalDataService;
    }

    @Required
    public void setUpdateCategoryTraversalDataService(UpdateCategoryTraversalDataService updateCategoryTraversalDataService) {
        this.updateCategoryTraversalDataService = updateCategoryTraversalDataService;
    }
}
