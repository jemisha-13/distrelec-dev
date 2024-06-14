import com.namics.distrelec.b2b.core.model.cms2.components.DistExtCarpetComponentModel
import com.namics.distrelec.b2b.core.model.cms2.components.DistHeroRotatingTeaserComponentModel
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserModel
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)

String contentCatalog = "distrelec_FRContentCatalog"

String emptyNavigationEntriesQueryText = "SELECT {e.pk} FROM {CMSNavigationEntry AS e\n" +
        "    JOIN CatalogVersion AS cv ON {e.catalogVersion}={cv.pk}\n" +
        "    JOIN Catalog AS c ON {cv.catalog}={c.pk}}\n" +
        "    \n" +
        "    WHERE {c.id}='$contentCatalog'\n" +
        "    AND {cv.version}='Staged'\n" +
        "    AND {e.item} IS NULL"
FlexibleSearchQuery emptyNavigationEntriesQuery = new FlexibleSearchQuery(emptyNavigationEntriesQueryText)
emptyNavigationEntriesQuery.setDisableCaching(true)

int totalItems = 0

SearchResult<CMSNavigationEntryModel> emptyNavigationEntriesResult = flexibleSearchService.search(emptyNavigationEntriesQuery)

totalItems += emptyNavigationEntriesResult.getCount()

ModelService modelService = spring.getBean(ModelService.class)

emptyNavigationEntriesResult.getResult().stream().forEach { modelService.remove(it) }

String distCarpetItemsWithoutSmallImageQueryText = "SELECT {dcct.pk} FROM {DistCarpetContentTeaser AS dcct\n" +
        "\tJOIN CatalogVersion AS cv ON {dcct.catalogVersion}={cv.pk}\n" +
        "    JOIN Catalog AS c ON {cv.catalog}={c.pk}}\n" +
        "    \n" +
        "    WHERE {c.id} = '$contentCatalog'\n" +
        "    AND {cv.version} = 'Staged'\n" +
        "    AND {dcct.imageSmall[en]:o} IS NULL\n" +
        "    AND {dcct.imageSmall[fr]:o} IS NULL"
FlexibleSearchQuery distCarpetItemsWithoutSmallImageQuery = new FlexibleSearchQuery(distCarpetItemsWithoutSmallImageQueryText)
distCarpetItemsWithoutSmallImageQuery.setDisableCaching(true)

SearchResult<DistCarpetContentTeaserModel> contentTeasersWithoutSmallImageResult = flexibleSearchService.search(distCarpetItemsWithoutSmallImageQuery)

totalItems += contentTeasersWithoutSmallImageResult.getCount()

contentTeasersWithoutSmallImageResult.getResult().stream().forEach {
    modelService.remove(it)
}

String distCarpetItemsWithoutFrenchSmallImageQueryText = "SELECT {dcct.pk} FROM {DistCarpetContentTeaser AS dcct\n" +
        "\tJOIN CatalogVersion AS cv ON {dcct.catalogVersion}={cv.pk}\n" +
        "    JOIN Catalog AS c ON {cv.catalog}={c.pk}}\n" +
        "    \n" +
        "    WHERE {c.id} = '$contentCatalog'\n" +
        "    AND {cv.version} = 'Staged'\n" +
        "    AND {dcct.imageSmall[en]:o} IS NOT NULL\n" +
        "    AND {dcct.imageSmall[fr]:o} IS NULL"
FlexibleSearchQuery distCarpetItemsWithoutFrenchSmallImageQuery = new FlexibleSearchQuery(distCarpetItemsWithoutFrenchSmallImageQueryText)
distCarpetItemsWithoutFrenchSmallImageQuery.setDisableCaching(true)

SearchResult<DistCarpetContentTeaserModel> contentTeasersWithoutFrenchSmallImageResult = flexibleSearchService.search(distCarpetItemsWithoutFrenchSmallImageQuery)

totalItems += contentTeasersWithoutFrenchSmallImageResult.getCount()

contentTeasersWithoutFrenchSmallImageResult.getResult().stream().forEach {
    it.setImageSmall(it.getImageSmall(Locale.ENGLISH), Locale.FRENCH)
    modelService.save(it)
}

String distExtCarpetContentTeaserComponentWithoutValidItemsQueryText = "SELECT {decc.pk} FROM {DistExtCarpetComponent AS decc\n" +
        "\tJOIN CatalogVersion AS cv ON {decc.catalogVersion}={cv.pk}\n" +
        "    JOIN Catalog AS c ON {cv.catalog}={c.pk}}\n" +
        "    \n" +
        "    WHERE {c.id} = '$contentCatalog'\n" +
        "    AND {cv.version} = 'Staged'\n" +
        "    AND NOT EXISTS ({{\n" +
        "                SELECT {ci.pk} FROM {DistExtCarpetItem AS ci}\n" +
        "                WHERE {decc.carpetColumn1Items} LIKE '%'||{ci.pk}||'%'\n" +
        "                OR {decc.carpetColumn2Items} LIKE '%'||{ci.pk}||'%'\n" +
        "                OR {decc.carpetColumn3Items} LIKE '%'||{ci.pk}||'%'\n" +
        "                OR {decc.carpetColumn4Items} LIKE '%'||{ci.pk}||'%'\n" +
        "                }})"
FlexibleSearchQuery distExtCarpetContentTeaserComponentWithoutValidItemsQuery = new FlexibleSearchQuery(distExtCarpetContentTeaserComponentWithoutValidItemsQueryText)
distExtCarpetContentTeaserComponentWithoutValidItemsQuery.setDisableCaching(true)

SearchResult<DistCarpetContentTeaserModel> distExtCarpetContentTeaserComponentWithoutValidItemsResult = flexibleSearchService.search(distExtCarpetContentTeaserComponentWithoutValidItemsQuery)

totalItems += distExtCarpetContentTeaserComponentWithoutValidItemsResult.getCount()

distExtCarpetContentTeaserComponentWithoutValidItemsResult.getResult().stream().forEach {
    modelService.remove(it)
}

String unassignedExtCarpetItemsQueryText = "SELECT {deci.pk} FROM {DistExtCarpetItem AS deci\n" +
        "        JOIN CatalogVersion AS cv ON {deci.catalogVersion}={cv.pk}\n" +
        "        JOIN Catalog AS c ON {cv.catalog}={c.pk}}\n" +
        "        \n" +
        "        WHERE {c.id} = '$contentCatalog'\n" +
        "        AND {cv.version} = 'Staged'\n" +
        "        AND {deci.product} IS NULL\n" +
        "        AND ({deci.contentTeaser} IS NULL\n" +
        "        OR NOT EXISTS ({{\n" +
        "                    SELECT {t.pk} FROM {AbstractDistCarpetContentTeaser AS t}\n" +
        "                    WHERE {t.pk}={deci.contentTeaser}\n" +
        "                    }}))"

FlexibleSearchQuery unassignedExtCarpetItemsQuery = new FlexibleSearchQuery(unassignedExtCarpetItemsQueryText)
unassignedExtCarpetItemsQuery.setDisableCaching(true)

SearchResult<DistCarpetContentTeaserModel> unassignedExtCarpetItemsResult = flexibleSearchService.search(unassignedExtCarpetItemsQuery)

totalItems += unassignedExtCarpetItemsResult.getCount()

unassignedExtCarpetItemsResult.getResult().stream().forEach {
    modelService.remove(it)
}

String distExtCarpetComponentsWithItemsQueryText = "SELECT {decc.pk} FROM {DistExtCarpetComponent AS decc\n" +
        "\tJOIN CatalogVersion AS cv ON {decc.catalogVersion}={cv.pk}\n" +
        "    JOIN Catalog AS c ON {cv.catalog}={c.pk}}\n" +
        "WHERE {c.id} = '$contentCatalog'\n" +
        "AND {cv.version} = 'Staged'\n" +
        "AND (\n" +
        "  {decc.carpetColumn1Items} IS NOT NULL\n" +
        "\tOR {decc.carpetColumn2Items} IS NOT NULL\n" +
        "\tOR {decc.carpetColumn3Items} IS NOT NULL\n" +
        "\tOR {decc.carpetColumn4Items} IS NOT NULL\n" +
        "  )"
FlexibleSearchQuery distExtCarpetComponentsWithItemsQuery = new FlexibleSearchQuery(distExtCarpetComponentsWithItemsQueryText)
distExtCarpetComponentsWithItemsQuery.setDisableCaching(true)

SearchResult<DistExtCarpetComponentModel> distExtCarpetComponentsWithItemsResult = flexibleSearchService.search(distExtCarpetComponentsWithItemsQuery)

distExtCarpetComponentsWithItemsResult.getResult().stream().forEach {
    int columnSize = it.getCarpetColumn1Items().size()

    if (it.getCarpetColumn2Items().size() != columnSize || it.getCarpetColumn3Items().size() != columnSize || it.getCarpetColumn4Items().size() != columnSize) {
        totalItems += it.getCarpetColumn1Items().size()
        it.getCarpetColumn1Items().stream().forEach { item -> modelService.remove(item) }

        totalItems += it.getCarpetColumn2Items().size()
        it.getCarpetColumn2Items().stream().forEach { item -> modelService.remove(item) }

        totalItems += it.getCarpetColumn3Items().size()
        it.getCarpetColumn3Items().stream().forEach { item -> modelService.remove(item) }

        totalItems += it.getCarpetColumn4Items().size()
        it.getCarpetColumn4Items().stream().forEach { item -> modelService.remove(item) }
        totalItems++
        modelService.remove(it)
    }
}

String distHeroRotationTeaserComponentsQueryText = "SELECT {com.pk} FROM {DistHeroRotatingTeaserComponent AS com\n" +
        "\tJOIN CatalogVersion AS cv ON {com.catalogVersion}={cv.pk}\n" +
        "    JOIN Catalog AS c ON {cv.catalog}={c.pk}}\n" +
        "    \n" +
        "    WHERE {c.id}='$contentCatalog'\n" +
        "    AND {cv.version}='Staged'"
FlexibleSearchQuery distHeroRotationTeaserComponentsQuery = new FlexibleSearchQuery(distHeroRotationTeaserComponentsQueryText)
distHeroRotationTeaserComponentsQuery.setDisableCaching(true)

SearchResult<DistHeroRotatingTeaserComponentModel> distHeroRotationTeaserComponentsResult = flexibleSearchService.search(distHeroRotationTeaserComponentsQueryText)

distHeroRotationTeaserComponentsResult.getResult().stream().forEach {
    if (it.heroRotatingTeaserItems.size() < 5) {
        totalItems += it.heroRotatingTeaserItems.size()
        it.heroRotatingTeaserItems.stream().forEach { item -> modelService.remove(item) }
        totalItems++
        modelService.remove(it)
    }
}

String productBoxComponentsWithoutItemsQueryText = "SELECT {com.pk} FROM {DistProductBoxComponent AS com\n" +
        "\tJOIN CatalogVersion AS cv ON {com.catalogVersion}={cv.pk}\n" +
        "    JOIN Catalog AS c ON {cv.catalog}={c.pk}}\n" +
        "    \n" +
        "    WHERE {c.id} = 'distrelec_FRContentCatalog'\n" +
        "    AND {cv.version} = 'Staged'\n" +
        "    AND NOT EXISTS ({{\n" +
        "                    SELECT {i.pk} FROM {DistCarpetItem AS i}\n" +
        "                    WHERE {com.items} LIKE '%'||{i.pk}||'%'\n" +
        "                    }})"
FlexibleSearchQuery productBoxComponentsWithoutItemsQuery = new FlexibleSearchQuery(productBoxComponentsWithoutItemsQueryText)
productBoxComponentsWithoutItemsQuery.setDisableCaching(true)

SearchResult<DistHeroRotatingTeaserComponentModel> productBoxComponentsWithoutItemsResult = flexibleSearchService.search(productBoxComponentsWithoutItemsQuery)

totalItems += productBoxComponentsWithoutItemsResult.getCount()

productBoxComponentsWithoutItemsResult.getResult().stream().forEach {
    modelService.remove(it)
}

return "Removed $totalItems items"