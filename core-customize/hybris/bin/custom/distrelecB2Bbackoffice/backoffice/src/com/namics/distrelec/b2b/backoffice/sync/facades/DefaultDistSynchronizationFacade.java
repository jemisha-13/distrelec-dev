package com.namics.distrelec.b2b.backoffice.sync.facades;

import com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Optional;

public class DefaultDistSynchronizationFacade extends DefaultSynchronizationFacade {

    @Override
    public Optional<CatalogVersionModel> getSyncCatalogVersion(Collection<ItemModel> items) {
        if (CollectionUtils.isNotEmpty(items) && items.size() > 1) {
            CatalogVersionModel.class.getClass();
            if (items.stream().allMatch(CatalogVersionModel.class::isInstance)) {
                return Optional.of((CatalogVersionModel) items.iterator().next());
            }
        }
        return super.getSyncCatalogVersion(items);
    }
}
