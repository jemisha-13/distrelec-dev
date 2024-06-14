package com.namics.distrelec.b2b.core.interceptor;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.interceptor.service.impl.DefaultItemModelPrepareInterceptorService;
import de.hybris.platform.core.model.ItemModel;

import java.util.function.Predicate;

public class DistItemModelPrepareInterceptorService extends DefaultItemModelPrepareInterceptorService {

    public DistItemModelPrepareInterceptorService(Predicate<ItemModel> cmsItemTypePredicate, Predicate<ItemModel> contentSlotForPageModelPredicate) {
        super(cmsItemTypePredicate, contentSlotForPageModelPredicate);
    }

    @Override
    protected boolean isActiveVersion(CatalogVersionModel itemCatalogVersion) {
        return itemCatalogVersion != null && itemCatalogVersion.equals(itemCatalogVersion.getCatalog().getActiveCatalogVersion());
    }

}
