package com.distrelec.smartedit.type;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.services.ContentCatalogService;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

public class AbstractPageDisplayOnSitesComponentTypeDataPopulator implements ComponentTypeDataPopulator {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPageDisplayOnSitesComponentTypeDataPopulator.class);

    @Autowired
    private ContentCatalogService contentCatalogService;

    @Override
    public void populate(ComponentTypeData componentTypeData, DistrelecComponentTypeFacade.ComponentSearchData componentSearchData) {
        ContentCatalogModel catalog = getCatalogFromSearchData(componentSearchData);
        if (catalog != null && isChildCatalog(catalog)) {
            componentTypeData.setAttributes(
                    componentTypeData.getAttributes().stream()
                    .filter(componentTypeAttributeData -> !componentTypeAttributeData.getQualifier().equals("displayOnSites"))
                    .collect(Collectors.toList())
            );
        }
    }

    private boolean isChildCatalog(ContentCatalogModel catalog) {
        return catalog.getSuperCatalog() != null;
    }

    private ContentCatalogModel getCatalogFromSearchData(DistrelecComponentTypeFacade.ComponentSearchData searchData) {
        String catalogId = searchData.getCatalogId();
        if (!StringUtils.isEmpty(catalogId)) {
            String restoredCatalogId = DistUtils.revertCatalogIdMinusToUnderscore(catalogId);
            return contentCatalogService.getContentCatalogs()
                    .stream()
                    .filter(contentCatalog -> contentCatalog.getId().equals(restoredCatalogId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

}
