import com.namics.distrelec.b2b.core.interceptor.DistHeroRotatingTeaserItemValidateInterceptor
import com.namics.distrelec.b2b.core.interceptor.exceptions.DistValidationInterceptorException
import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserItemModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("DistHeroRotatingTeaserItemCleanup")

String queryText = "SELECT {pk} FROM {DistHeroRotatingTeaserItem}"

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText)

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)

SearchResult<DistHeroRotatingTeaserItemModel> searchResult = flexibleSearchService.search(query)

DistHeroRotatingTeaserItemValidateInterceptor validateInterceptor = spring.getBean(DistHeroRotatingTeaserItemValidateInterceptor.class)
ModelService modelService = spring.getBean(ModelService.class)

int removedItems = 0;

searchResult.result.stream().forEach { item ->
    try {
        validateInterceptor.onValidate(item, null)
    } catch (DistValidationInterceptorException e) {
        modelService.remove(item)
        removedItems++
    }
}

LOG.info("Removed $removedItems invalid DistHeroRotatingTeaserItem")

return "Removed $removedItems invalid DistHeroRotatingTeaserItem"