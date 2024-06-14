package com.namics.distrelec.b2b.core.catalog.daos.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.impl.DefaultCMSCatalogVersionService;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;

import java.util.List;

public class DistCMSCatalogVersionService extends DefaultCMSCatalogVersionService {

    @Override
    public List<CatalogVersionModel> getFullHierarchyForCatalogVersion(CatalogVersionModel catalogVersion, CMSSiteModel cmsSiteModel) {
        if(catalogVersion.getCatalog() instanceof ContentCatalogModel) {
            return super.getFullHierarchyForCatalogVersion(catalogVersion, cmsSiteModel);
        }
        return List.of(catalogVersion);
    }
}
