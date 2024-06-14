package com.namics.distrelec.b2b.core.catalog.daos.impl;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.catalog.daos.impl.DefaultCatalogDao;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class DefaultDistCatalogDao extends DefaultCatalogDao {

    @Override
    public CatalogModel findCatalogById(final String catalogId) {
        try {
            return super.findCatalogById(catalogId);
        } catch (UnknownIdentifierException e) {
            if (DistUtils.containsMinus(catalogId)) {
                String revertedCatalogId = DistUtils.revertCatalogIdMinusToUnderscore(catalogId);
                return super.findCatalogById(revertedCatalogId);
            }
            throw e;
        }
    }
}
