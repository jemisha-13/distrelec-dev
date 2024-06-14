import com.namics.distrelec.b2b.core.interceptor.DistCarpetContentTeaserValidateInterceptor
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserModel
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("DistCarpetContentTeaserCleanup");

String queryText = "SELECT {pk} FROM {DistCarpetContentTeaser}"

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<DistCarpetContentTeaserModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

DistCarpetContentTeaserValidateInterceptor validateInterceptor = spring.getBean(DistCarpetContentTeaserValidateInterceptor.class)

int removedComponents = 0;

searchResult.result.forEach {
    try {
        validateInterceptor.onValidate(it, null)
    } catch (InterceptorException e) {
        modelService.remove(it)
        removedComponents++;
    }
}

LOG.info("Removed $removedComponents invalid DistCarpetContentTeaserCleanup")

return "Removed $removedComponents invalid DistCarpetContentTeaserCleanup"