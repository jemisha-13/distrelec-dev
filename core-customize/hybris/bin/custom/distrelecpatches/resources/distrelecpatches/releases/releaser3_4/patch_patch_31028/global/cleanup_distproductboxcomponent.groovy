import com.namics.distrelec.b2b.core.interceptor.DistProductBoxComponentValidateInterceptor
import com.namics.distrelec.b2b.core.model.cms2.components.DistProductBoxComponentModel
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.servicelayer.session.SessionService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("DistProductBoxComponentCleanup");

String queryText = "SELECT {pk} FROM {DistProductBoxComponent}"

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<DistProductBoxComponentModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

DistProductBoxComponentValidateInterceptor validateInterceptor = spring.getBean(DistProductBoxComponentValidateInterceptor.class)

int removedComponents = 0;

SessionService sessionService = spring.getBean(SessionService.class)

searchResult.result.forEach {
    try {
        validateInterceptor.onValidate(it, null)
    } catch (InterceptorException e) {
        sessionService.executeInLocalViewWithParams(Map.of(
                InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_BEANS,
                Set.of("cmsAbstractComponentRemoveInterceptor")), new SessionExecutionBody() {
            @Override
            void executeWithoutResult() {
                modelService.remove(it);
            }
        })
        removedComponents++;
    }
}

LOG.info("Removed $removedComponents invalid DistProductBoxComponent")

return "Removed $removedComponents invalid DistProductBoxComponent"