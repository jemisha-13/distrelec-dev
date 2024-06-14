import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("ContentSlotForTemplateCleanup");

String queryText = """
SELECT {cst.pk}
FROM {
           ContentSlotForTemplate AS cst
      JOIN CatalogVersion         AS cstcv ON {cst.catalogVersion} = {cstcv.pk}
      JOIN ContentSlot            AS cs    ON {cst.contentSlot} = {cs.pk}
      JOIN CatalogVersion         AS cscv  ON {cs.catalogVersion} = {cscv.pk}
      JOIN Catalog                AS csc   ON {cscv.catalog} = {csc.pk}
     }
WHERE {cstcv.pk} != {cscv.pk}
AND {csc.id} != 'distrelec_IntContentCatalog'
"""

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<ContentSlotForTemplateModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

modelService.removeAll(searchResult.result);

int removedComponents = searchResult.count;

LOG.info("Removed $removedComponents invalid ContentSlotForTemplate")

return "Removed $removedComponents invalid ContentSlotForTemplate"