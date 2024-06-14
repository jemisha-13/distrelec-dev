import de.hybris.platform.catalog.model.ItemSyncTimestampModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("ItemSyncTimestampCleanup");

String queryText = """
SELECT {ts.pk}

  FROM {
    ItemSyncTimestamp AS ts  
    JOIN CmsItem AS si ON {ts.sourceItem}={si.pk}
    JOIN CatalogVersion AS scv ON {si.catalogVersion}={scv.pk}
    JOIN Catalog AS sc ON {scv.catalog}={sc.pk}
    JOIN CmsItem AS ti ON {ts.targetItem}={ti.pk}
    }

  WHERE (NOT ({sc.id} = 'distrelec_IntContentCatalog' AND {scv.version}='Online'))
  AND ({si.catalogVersion} != {ts.sourceVersion}
  OR {ti.catalogVersion} != {ts.targetVersion})
"""

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<ItemSyncTimestampModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

int count = searchResult.count;

LOG.info("Found {} invalid item sync timestamps", count);

searchResult.result.stream()
        .forEach { modelService.remove(it); }

return "Removed ${count} timestamps";