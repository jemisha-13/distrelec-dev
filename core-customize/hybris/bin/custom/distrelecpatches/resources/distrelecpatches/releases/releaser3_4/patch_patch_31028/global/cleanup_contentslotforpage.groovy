import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("ContentSlotForPageCleanup");

String queryText = """
SELECT {csp.pk}
FROM {
           ContentSlotForPage AS csp
      JOIN CatalogVersion     AS cspcv ON {csp.catalogVersion} = {cspcv.pk}
      JOIN ContentSlot        AS cs    ON {csp.contentSlot} = {cs.pk}
      JOIN CatalogVersion     AS cscv  ON {cs.catalogVersion} = {cscv.pk}
      JOIN Catalog            AS csc   ON {cscv.catalog} = {csc.pk}
     }
WHERE {cspcv.pk} != {cscv.pk}
      AND {csc.id} != 'distrelec_IntContentCatalog'
"""

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<ContentSlotForPageModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

modelService.removeAll(searchResult.result);

int removedComponents = searchResult.count;

LOG.info("Removed $removedComponents invalid ContentSlotForPage")

return "Removed $removedComponents invalid ContentSlotForPage"