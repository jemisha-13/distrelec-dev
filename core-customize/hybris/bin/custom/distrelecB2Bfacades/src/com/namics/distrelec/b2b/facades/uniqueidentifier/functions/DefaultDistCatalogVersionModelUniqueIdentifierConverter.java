package com.namics.distrelec.b2b.facades.uniqueidentifier.functions;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.functions.DefaultCatalogVersionModelUniqueIdentifierConverter;

public class DefaultDistCatalogVersionModelUniqueIdentifierConverter extends DefaultCatalogVersionModelUniqueIdentifierConverter {

    @Override
    public ItemData convert(CatalogVersionModel catalogVersion) throws IllegalArgumentException {
        ItemData itemData = super.convert(catalogVersion);

        String catalogId = catalogVersion.getCatalog().getId();
        if (DistUtils.containsUnderscore(catalogId)) {
            String convertedCatalogId = DistUtils.convertCatalogIdUnderscoreToMinus(catalogId);
            itemData.setItemId(convertedCatalogId + SEPARATOR + catalogVersion.getVersion());
        }

        return itemData;
    }
}
