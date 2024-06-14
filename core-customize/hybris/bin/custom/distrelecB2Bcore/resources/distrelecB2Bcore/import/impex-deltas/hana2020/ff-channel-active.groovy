import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel
import com.namics.hybris.ffsearch.model.export.DistFactFinderExportCronJobModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.apache.logging.log4j.LogManager

LOG = LogManager.getLogger("ff-channel-active")

String FF_COMPOSITE_JOB_CODE = "distFACTFinderExportCronJob"

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)

String findChannelsQueryText = "SELECT {c.pk} FROM {DistFactFinderExportChannel AS c} " +
        "WHERE {c.active} is null"

FlexibleSearchQuery findChannelsQuery = new FlexibleSearchQuery(findChannelsQueryText)

SearchResult<DistFactFinderExportChannelModel> findChannelsQueryResult = flexibleSearchService.search(findChannelsQuery)

String getActiveCronjobText = "SELECT {cron.pk}" +
        " FROM {DistFactFinderExportCronJob as cron}" +
        " WHERE {cron.active}=1" +
        "   AND EXISTS({{" +
        "     SELECT 1" +
        "     FROM {DistFactFinderExportChannel AS channel}" +
        "     WHERE {channel.code}=?channel" +
        "       AND {cron.channel}={channel.pk}" +
        "   }})" +
        "   AND EXISTS({{" +
        "     SELECT 1" +
        "     FROM {CompositeEntry AS ce}" +
        "     WHERE {ce.executableCronJob}={cron.pk}" +
        "       AND EXISTS({{" +
        "         SELECT 1" +
        "         FROM {CompositeCronJob AS comp}" +
        "         WHERE {ce.compositeCronJob}={comp.pk}" +
        "           AND {comp.code}='" + FF_COMPOSITE_JOB_CODE + "'" +
        "       }})" +
        "   }})"


ModelService modelService = spring.getBean(ModelService.class)

for(DistFactFinderExportChannelModel factFinderExportChannelModel : findChannelsQueryResult.result){
    LOG.info("Update " + factFinderExportChannelModel.code)

    String channelCode = factFinderExportChannelModel.code
    FlexibleSearchQuery getActiveCronjobQuery = new FlexibleSearchQuery(getActiveCronjobText)
    getActiveCronjobQuery.addQueryParameter("channel", channelCode)

    SearchResult<DistFactFinderExportCronJobModel> activeCronjobs = flexibleSearchService.search(getActiveCronjobQuery)
    boolean isChannelActive = !activeCronjobs.getResult().isEmpty()

    factFinderExportChannelModel.active = isChannelActive
    modelService.save(factFinderExportChannelModel)
}

String returnMsg = "Updated ${findChannelsQueryResult.count} fact finder export channels"
LOG.info(returnMsg)
return returnMsg
