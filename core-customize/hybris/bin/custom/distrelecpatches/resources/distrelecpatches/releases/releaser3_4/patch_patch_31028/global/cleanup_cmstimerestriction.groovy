import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("CMSTimeRestrictionCleanup");

String queryText = "SELECT {pk} FROM {CMSTimeRestriction} WHERE {useStoreTimeZone} IS NULL"

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<CMSTimeRestrictionModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

modelService.removeAll(searchResult.result);

int removedComponents = searchResult.count;

LOG.info("Removed $removedComponents invalid CMSTimeRestriction")

return "Removed $removedComponents invalid CMSTimeRestriction"