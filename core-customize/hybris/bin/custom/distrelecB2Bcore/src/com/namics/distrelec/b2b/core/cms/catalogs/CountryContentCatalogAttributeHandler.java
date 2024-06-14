package com.namics.distrelec.b2b.core.cms.catalogs;

import java.util.Optional;

import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryContentCatalogAttributeHandler extends AbstractDynamicAttributeHandler<ContentCatalogModel, CMSSiteModel> {

    private final Logger LOG = LoggerFactory.getLogger(CountryContentCatalogAttributeHandler.class);

    @Override
    public ContentCatalogModel get(CMSSiteModel cmsSiteModel) {
        if (cmsSiteModel == null) {
            throw new IllegalArgumentException("consent must not be null");
        }

        Optional<ContentCatalogModel> catalogOptional = cmsSiteModel.getContentCatalogs().stream().filter(catalog -> null!=catalog.getSuperCatalog()).findAny();

        if (catalogOptional.isPresent()) {
            return catalogOptional.get();
        } else {
            LOG.warn("Country specific content catalog is not found for site " + cmsSiteModel.getUid());
            return null;
        }
    }
}
