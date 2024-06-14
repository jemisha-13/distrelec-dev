package com.namics.distrelec.b2b.core.catalog.daos.impl;

import java.util.Collection;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.catalog.daos.impl.DefaultCatalogVersionDao;
import de.hybris.platform.catalog.model.CatalogVersionModel;

public class DefaultDistCatalogVersionDao extends DefaultCatalogVersionDao {

    @Override
    public Collection<CatalogVersionModel> findCatalogVersions(String catalogId, String catalogVersionName) {
        Collection<CatalogVersionModel> catalogVersions = super.findCatalogVersions(catalogId, catalogVersionName);

        if (catalogVersions.isEmpty() && DistUtils.containsMinus(catalogId)) {
            String convertedCatalogId = DistUtils.revertCatalogIdMinusToUnderscore(catalogId);
            catalogVersions = super.findCatalogVersions(convertedCatalogId, catalogVersionName);
        }

        return catalogVersions;
    }
}
