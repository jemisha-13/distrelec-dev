import com.namics.distrelec.b2b.core.model.cms2.components.DistExtCarpetComponentModel
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.servicelayer.session.SessionService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.stream.Collectors

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty
import static org.apache.commons.lang3.StringUtils.isNotBlank

Logger LOG = LoggerFactory.getLogger("DistExtCarpetComponentCleanup");

String queryText = "SELECT {c.pk} FROM {DistExtCarpetComponent AS c}"

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<DistExtCarpetComponentModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

def hasSearchQuery(DistExtCarpetComponentModel carpetComponent) {
    return isNotBlank(carpetComponent.getSearchQuery());
}

def hasColumnItems(DistExtCarpetComponentModel carpetComponent) {
    return isNotEmpty(carpetComponent.carpetColumn1Items)
            && isNotEmpty(carpetComponent.carpetColumn2Items)
            && isNotEmpty(carpetComponent.carpetColumn3Items)
            && isNotEmpty(carpetComponent.carpetColumn4Items)
}

def hasSameColumnSizes(DistExtCarpetComponentModel carpetComponent) {
    def columnSize = carpetComponent.carpetColumn1Items.size();
    return carpetComponent.carpetColumn2Items.size() == columnSize
        && carpetComponent.carpetColumn3Items.size() == columnSize
        && carpetComponent.carpetColumn4Items.size() == columnSize
}

def isCarpetComponentValid(DistExtCarpetComponentModel carpetComponent) {
    return hasSearchQuery(carpetComponent) ^ ( hasColumnItems(carpetComponent) && hasSameColumnSizes(carpetComponent));
}

List<DistExtCarpetComponentModel> invalidCarpetComponents = searchResult.result.stream()
        .filter { !isCarpetComponentValid(it) }
        .collect(Collectors.toUnmodifiableList());

int count = invalidCarpetComponents.size();

LOG.info("Found {} invalid carpet components", count);

SessionService sessionService = spring.getBean(SessionService.class)

sessionService.executeInLocalViewWithParams(Map.of(
        InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_BEANS,
        Set.of("cmsAbstractComponentRemoveInterceptor")), new SessionExecutionBody() {
    @Override
    void executeWithoutResult() {
        modelService.removeAll(invalidCarpetComponents);
    }
})

return "Removed ${count} DistExtCarpetComponents";