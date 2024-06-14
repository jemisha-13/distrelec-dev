import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService")

String catalog = "distrelec_BEContentCatalog"
String catalogVersion = "Staged"

String query = "SELECT {link.pk} FROM {CMSLinkComponent AS link} " +
        " WHERE {link.localizedUrl[nl]} is not null " +
        " AND {link.localizedUrl[en]:o} is null" +
        " AND EXISTS ({{ " +
        "   SELECT 1 " +
        "   FROM {CatalogVersion AS cv}" +
        "   WHERE {link.catalogVersion} = {cv.pk}" +
        "     AND {cv.version} = '" + catalogVersion + "' " +
        "     AND EXISTS ({{ " +
        "       SELECT 1" +
        "       FROM {Catalog AS c}" +
        "       WHERE {cv.catalog} = {c.pk}" +
        "         AND {c.id} = '" + catalog + "' " +
        "     }}) " +
        " }}) "

SearchResult<CMSLinkComponentModel> result = flexibleSearchService.search(query)

ModelService modelService = spring.getBean("modelService")

int updatedCount = 0

Locale nlLocale = new Locale("NL")

result.result.forEach { dbItem ->
    String localizedUrl = dbItem.getLocalizedUrl(nlLocale)
    dbItem.setLocalizedUrl(localizedUrl, Locale.ENGLISH)
    modelService.save(dbItem)
    updatedCount++
}

return "Updated $updatedCount entries"
