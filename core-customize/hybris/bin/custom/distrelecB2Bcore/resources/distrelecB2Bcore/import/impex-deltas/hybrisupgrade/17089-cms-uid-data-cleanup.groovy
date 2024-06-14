import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import de.hybris.platform.cms2.model.contents.CMSItemModel
import de.hybris.platform.servicelayer.exceptions.ModelSavingException
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.servicelayer.session.SessionService

FlexibleSearchService searchService = spring.getBean(FlexibleSearchService.class)

String findAllQuery = "SELECT {pk} FROM {CMSItem} WHERE {uid} LIKE '% %'"

SearchResult<CMSItemModel> cmsItems = searchService.search(findAllQuery)

ModelService modelService = spring.getBean(ModelService.class)

String findByUidQuery = "SELECT {pk} FROM {CMSItem} WHERE {uid}='?uid'"

int modifiedItems = 0;

SessionService sessionService = spring.getBean(SessionService.class)

def saveItemWithNewUid(CMSItemModel cmsItem) {
    final Map<String, Object> params = ImmutableMap.of(InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_BEANS, ImmutableSet.of(
            "distExtCarpetComponentValidateInterceptor",
            "distProductBoxComponentValidateInterceptor",
            "distCarpetContentTeaserWithTextValidateInterceptor",
            "distHeroRotatingTeaserValidateInterceptor",
            "distrelecCategoryGridItemComponentValidateInterceptor"
    ))

    sessionService.executeInLocalViewWithParams(params, new SessionExecutionBody() {
        void executeWithoutResult() {
            try {
                modelService.save(cmsItem)
            } catch (ModelSavingException e) {
                cmsItem.setUid(cmsItem.getUid() + "2")
                modelService.save(cmsItem)
            }
        }
    })
}

cmsItems.result.forEach { cmsItem ->
    cmsItem.setUid(cmsItem.getUid().replace(" ", ""))
    saveItemWithNewUid(cmsItem);
    modifiedItems++
}

return "Modified ${modifiedItems} items"