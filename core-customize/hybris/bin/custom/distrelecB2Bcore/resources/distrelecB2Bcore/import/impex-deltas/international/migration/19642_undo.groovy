import com.namics.distrelec.b2b.core.model.DistSalesOrgModel
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService
import de.hybris.platform.catalog.model.CompanyModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)

String queryText = "SELECT {com.pk} FROM {Company AS com\n" +
        "JOIN CustomerType AS ct ON {com.customerType}={ct.pk}\n" +
        "JOIN DistSalesOrg AS so ON {com.salesOrg}={so.pk}\n" +
        "JOIN Country AS coun ON {com.country}={coun.pk}}\n" +
        "" +
        "WHERE {so.code}='7900'\n" +
        "AND {ct.code}='B2C'\n" +
        "AND {coun.isocode}='FR'"

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText)

SearchResult<CompanyModel> querySearchResult = flexibleSearchService.search(query)

DistSalesOrgService salesOrgService = spring.getBean(DistSalesOrgService.class)

DistSalesOrgModel frSalesOrg = salesOrgService.getSalesOrgForCode("7801")

ModelService modelService = spring.getBean(ModelService.class)

querySearchResult.result.stream().forEach { b2bUnit ->
    try {
        println(b2bUnit.uid)
        b2bUnit.setSalesOrg(frSalesOrg)
        modelService.save(b2bUnit)
    } catch (Exception e) {
        println(e.getMessage())
    }
}

return "Reassigned $querySearchResult.count B2C customers to 7801"