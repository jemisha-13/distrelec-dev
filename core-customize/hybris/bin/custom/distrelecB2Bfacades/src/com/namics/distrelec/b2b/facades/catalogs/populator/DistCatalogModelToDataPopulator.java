package com.namics.distrelec.b2b.facades.catalogs.populator;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cmsfacades.catalogs.populator.CatalogModelToDataPopulator;
import de.hybris.platform.cmsfacades.data.CatalogData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistCatalogModelToDataPopulator extends CatalogModelToDataPopulator {

    @Override
    public void populate(CatalogModel source, CatalogData target) throws ConversionException {
        super.populate(source, target);

        String catalogId = source.getId();
        if (DistUtils.containsUnderscore(catalogId)) {
            target.setCatalogId(DistUtils.convertCatalogIdUnderscoreToMinus(catalogId));
        }
    }
}
