import de.hybris.platform.cms2.model.pages.ContentPageModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

Logger LOG = LoggerFactory.getLogger("FIX PAGE NAME")

String query = "SELECT {p.pk} FROM {ContentPage AS p} WHERE {p.name} IS NULL"

ApplicationContext context = (ApplicationContext)spring
FlexibleSearchService flexibleSearchService = context.getBean(FlexibleSearchService.class)

SearchResult<ContentPageModel> searchResult = flexibleSearchService.search(query)

LOG.info("Found ${searchResult.count} pages without name")

ModelService modelService = context.getBean(ModelService.class)

searchResult.result.stream()
        .forEach {
            it.setName(it.uid)
            modelService.save(it)
        }

return "Fixed ${searchResult.count} pages"