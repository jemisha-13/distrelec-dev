import de.hybris.platform.cms2lib.model.components.AbstractBannerComponentModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("ItemSyncTimestampCleanup");

String queryText = """
SELECT {b.pk} FROM {AbstractBannerComponent AS b}
WHERE {b.priority} IS NULL
"""

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<AbstractBannerComponentModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

LOG.info("Found {} invalid AbstractBannerComponents", searchResult.count);

searchResult.result.stream()
        .forEach {
            it.setPriority(false);
            modelService.save(it);
        }