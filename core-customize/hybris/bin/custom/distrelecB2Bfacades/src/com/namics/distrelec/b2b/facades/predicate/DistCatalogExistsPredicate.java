package com.namics.distrelec.b2b.facades.predicate;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.cmsfacades.common.predicate.CatalogExistsPredicate;

public class DistCatalogExistsPredicate extends CatalogExistsPredicate {

    @Override
    public boolean test(String catalogId) {
        boolean exists = super.test(catalogId);

        if (!exists && DistUtils.containsMinus(catalogId)) {
            String revertedCatalogId = DistUtils.revertCatalogIdMinusToUnderscore(catalogId);
            exists = super.test(revertedCatalogId);
        }

        return exists;
    }
}
