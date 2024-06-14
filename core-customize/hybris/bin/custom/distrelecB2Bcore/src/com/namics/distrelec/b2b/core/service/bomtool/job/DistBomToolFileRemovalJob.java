package com.namics.distrelec.b2b.core.service.bomtool.job;

import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class DistBomToolFileRemovalJob extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistBomToolFileRemovalJob.class);

    private static final String BOM_TOOL_FILES_OLDER_THAN = "SELECT {m." + MediaModel.PK + "} "
            + "FROM {" + MediaModel._TYPECODE + " AS m JOIN " + MediaFolderModel._TYPECODE + " AS mf ON {m." + MediaModel.FOLDER + "} = {mf." + MediaFolderModel.PK + "}} "
            + "WHERE {mf." + MediaFolderModel.QUALIFIER + "} = 'bom-tool' AND {m." + MediaModel.CREATIONTIME + "} < ?inputDate";

    @Autowired
    private ModelService modelService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public PerformResult perform(CronJobModel cronJobModel) {
        try {
            LOG.info("Starting bomToolFileRemovalJob");
            Date currentDate = new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
            Date inputDate = Date.from(localDateTime.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());

            FlexibleSearchQuery query = new FlexibleSearchQuery(BOM_TOOL_FILES_OLDER_THAN);
            query.addQueryParameter("inputDate", inputDate);
            List<MediaModel> result = flexibleSearchService.<MediaModel>search(query).getResult();

            if (CollectionUtils.isNotEmpty(result)) {
                modelService.removeAll(result);
            }
        } catch (Exception e) {
            LOG.error("An error ocurred while performing bom tool files removal job", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }

        LOG.info("Finished bomToolFileRemovalJob");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
}
