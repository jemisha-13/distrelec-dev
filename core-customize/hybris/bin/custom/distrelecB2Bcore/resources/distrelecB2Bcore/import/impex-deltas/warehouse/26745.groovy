import de.hybris.platform.ordersplitting.model.StockLevelModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import groovy.transform.Field

import static java.util.Collections.singletonList

@Field ModelService modelService = spring.getBean 'modelService'
@Field FlexibleSearchService flexibleSearchService = spring.getBean 'flexibleSearchService'

List<StockLevelModel> emptyStockLevels = getStockLevelsWithoutWarehouse()
modelService.removeAll(emptyStockLevels)

List<StockLevelModel> getStockLevelsWithoutWarehouse() {
    String query = "SELECT {sl.pk} from {StockLevel as sl JOIN Warehouse as w on {sl.warehouse}={w.pk} } " +
            "WHERE {w.code} IN ('ES31','ES71','ES72','ES51','ES11','ESIT','EF71','EF72','EF11','EFIT','EF51','EF31','PTCH','7375','7171','7711','7373','7372')"
    FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query)
    flexibleSearchQuery.setLocale(null)
    flexibleSearchQuery.setCount(10000)
    flexibleSearchQuery.setResultClassList(singletonList(StockLevelModel.class))
    SearchResult searchResult = flexibleSearchService.search(flexibleSearchQuery)

    printf "Total count of stocklevels without warehouse is: %s \n", searchResult.getTotalCount()

    return (List<StockLevelModel>) (Object) searchResult.getResult()
}