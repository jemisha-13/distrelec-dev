package distrelecpatches.releases.releaser1_8.patch_patch_31028.global

import com.namics.distrelec.b2b.core.enums.CmsComponentWidth
import com.namics.distrelec.b2b.core.model.cms2.components.DistHeroRotatingTeaserComponentModel
import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserItemModel
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

Logger LOG = LoggerFactory.getLogger("DistHeroRotatingTeaserComponentCleanup")

String queryText = "SELECT {pk} FROM {DistHeroRotatingTeaserComponent}"

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText)

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)

SearchResult<DistHeroRotatingTeaserComponentModel> searchResult = flexibleSearchService.search(query)

ModelService modelService = spring.getBean(ModelService.class)

int fixedComponents = 0

SessionService sessionService = spring.getBean(SessionService.class)

searchResult.result.stream()
        .forEach { component ->
            boolean markForDelete = false;
            List<DistHeroRotatingTeaserItemModel> items = component.heroRotatingTeaserItems.stream()
                    .filter { (it.catalogVersion != component.catalogVersion) }
                    .collect(Collectors.toList())

            if(items.size() != component.heroRotatingTeaserItems.size()){
                fixedComponents++
            }

            if(items.size() < 7 && component.getComponentWidth().equals(CmsComponentWidth.FULLWIDTH)){
                markForDelete = true;
            }

            if(items.size() < 5 && (component.getComponentWidth().equals(CmsComponentWidth.ONETHIRD) || component.getComponentWidth().equals(CmsComponentWidth.TWOTHIRD))){
                markForDelete = true;
            }

            if(markForDelete){
                if(items.size() == component.heroRotatingTeaserItems.size()){
                    fixedComponents++;
                }

                sessionService.executeInLocalViewWithParams(Map.of(
                        InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_BEANS,
                        Set.of("cmsAbstractComponentRemoveInterceptor")), new SessionExecutionBody() {
                    @Override
                    void executeWithoutResult() {
                        modelService.remove(component);
                    }
                })
            }else{
                if(items.size() != component.heroRotatingTeaserItems.size()){
                    component.setHeroRotatingTeaserItems(items)
                    modelService.save(component)
                }
            }

        }

LOG.info("Fixed $fixedComponents components")

return "Fixed $fixedComponents components"