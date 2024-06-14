import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("CMSNavigationEntryCleanup");

String queryText = "SELECT {pk} FROM {CMSNavigationEntry} WHERE {item} IS NULL"

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<CMSNavigationEntryModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

modelService.removeAll(searchResult.result);

int removedComponents = searchResult.count;

LOG.info("Removed $removedComponents invalid CMSNavigationEntry")

return "Removed $removedComponents invalid CMSNavigationEntry"