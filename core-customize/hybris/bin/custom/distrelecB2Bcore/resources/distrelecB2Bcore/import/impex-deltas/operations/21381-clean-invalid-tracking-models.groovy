import com.namics.distrelec.b2b.core.jalo.tracking.DistrelecYmktTracking
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService")

SearchResult<DistrelecYmktTracking> result = flexibleSearchService.search("SELECT {pk} FROM {DistrelecYmktTracking} where {cookieId}='' or {cookieId} is null")

ModelService modelService = spring.getBean("modelService")

int removedCount = 0

result.result.forEach { dbItem ->
    modelService.remove(dbItem)
    removedCount++
}

return "Removed $removedCount entries"