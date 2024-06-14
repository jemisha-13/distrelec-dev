import de.hybris.platform.cms2.model.pages.PageTemplateModel
import de.hybris.platform.cms2.servicelayer.daos.PageTemplateDao
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

final String sourceCatalog = "distrelec_CHContentCatalog"
final String targetCatalog = "distrelec_FRContentCatalog"

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)

String findPageTemplatesInContentCatalogQueryText = "SELECT {t.pk} FROM {PageTemplate AS t " +
        "JOIN CatalogVersion AS cv ON {t.catalogVersion}={cv.pk} " +
        "JOIN Catalog AS c ON {c.pk}={cv.catalog}}" +
        "WHERE {c.id} = ?catalogId " +
        "AND {cv.version} = 'Staged'"

FlexibleSearchQuery findPageTemplatesInSourceCatalogQuery = new FlexibleSearchQuery(findPageTemplatesInContentCatalogQueryText)
findPageTemplatesInSourceCatalogQuery.addQueryParameter("catalogId", sourceCatalog)

SearchResult<PageTemplateModel> findPageTemplatesInSourceCatalogQueryResult = flexibleSearchService.search(findPageTemplatesInSourceCatalogQuery)

String findPageTemplateByUidInContentCatalogQueryText = "SELECT {t.pk} FROM {PageTemplate AS t " +
        "JOIN CatalogVersion AS cv ON {t.catalogVersion}={cv.pk} " +
        "JOIN Catalog AS c ON {c.pk}={cv.catalog}}" +
        "WHERE {c.id} = ?catalogId " +
        "AND {cv.version} = 'Staged' " +
        "AND {t.uid} = ?templateUid"

ModelService modelService = spring.getBean(ModelService.class)

for(PageTemplateModel sourcePageTemplate : findPageTemplatesInSourceCatalogQueryResult.result){
    String templateCode = sourcePageTemplate.getUid()

    FlexibleSearchQuery findPageTemplateByUidInContentCatalogQuery = new FlexibleSearchQuery(findPageTemplateByUidInContentCatalogQueryText)
    findPageTemplateByUidInContentCatalogQuery.addQueryParameter("catalogId", targetCatalog)
    findPageTemplateByUidInContentCatalogQuery.addQueryParameter("templateUid", templateCode)

    PageTemplateModel targetPageTemplate = flexibleSearchService.searchUnique(findPageTemplateByUidInContentCatalogQuery);
    targetPageTemplate.setRestrictedPageTypes(sourcePageTemplate.getRestrictedPageTypes())

    modelService.save(targetPageTemplate)
}

return "Updated ${findPageTemplatesInSourceCatalogQueryResult.count} page templates"