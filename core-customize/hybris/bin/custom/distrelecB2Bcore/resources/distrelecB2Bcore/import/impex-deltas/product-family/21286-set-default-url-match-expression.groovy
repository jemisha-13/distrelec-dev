import com.namics.distrelec.b2b.core.enums.UrlMatchExpression
import com.namics.distrelec.b2b.core.model.DistContentPageMappingModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService")

SearchResult<DistContentPageMappingModel> result = flexibleSearchService.search("SELECT {pk} FROM {DistContentPageMapping} WHERE {urlMatchExpression} is null")

ModelService modelService = spring.getBean("modelService")

int updatedCount = 0

result.result.forEach { dbItem ->
    dbItem.urlMatchExpression = UrlMatchExpression.EQUALS
    dbItem.permanent = false
    modelService.save(dbItem)
    updatedCount++
}

return "Updated $updatedCount entries"
