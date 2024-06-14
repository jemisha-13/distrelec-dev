import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetItemModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("DistCarpetItemCleanup");

String queryText = """
SELECT {ci.pk} FROM {DistCarpetItem AS ci
    LEFT JOIN AbstractDistCarpetContentTeaser AS t ON {ci.contentTeaser}={t.pk}
    LEFT JOIN Product AS p ON {ci.product}={p.pk}}
    WHERE ({t.pk} IS NULL
    AND {p.pk} IS NULL)
    OR ({t.pk} IS NOT NULL
    AND {p.pk} IS NOT NULL)
"""

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class);

SearchResult<DistCarpetItemModel> searchResult = flexibleSearchService.search(query);

ModelService modelService = spring.getBean(ModelService.class);

int count = searchResult.count;

LOG.info("Found {} invalid DistCarpetItems", count);

modelService.removeAll(searchResult.result)

return "Removed ${count} DistCarpetItems";