package com.namics.distrelec.b2b.facades.sites.impl;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.cmsfacades.data.SiteData;
import de.hybris.platform.cmsfacades.sites.impl.DefaultSiteFacade;

import java.util.ArrayList;
import java.util.List;

public class DefaultDistSiteFacade extends DefaultSiteFacade {

    @Override
    public List<SiteData> getSitesForCatalogs(final List<String> catalogIds) {
        List<String> allCatalogIds = new ArrayList<>(catalogIds);

        catalogIds.stream()
            .filter(DistUtils::containsMinus)
            .map(DistUtils::revertCatalogIdMinusToUnderscore)
            .forEach(allCatalogIds::add);

        return super.getSitesForCatalogs(allCatalogIds);
    }
}
