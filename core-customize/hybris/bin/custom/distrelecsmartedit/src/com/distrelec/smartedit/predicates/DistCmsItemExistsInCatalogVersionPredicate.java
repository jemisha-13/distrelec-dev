package com.distrelec.smartedit.predicates;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cmsfacades.common.predicate.CmsItemExistsInCatalogVersionPredicate;

import java.util.Optional;

public class DistCmsItemExistsInCatalogVersionPredicate extends CmsItemExistsInCatalogVersionPredicate {

    @Override
    public boolean test(final String itemUuid, final CatalogVersionModel catalogVersionModel) {
        boolean result = super.test(itemUuid, catalogVersionModel);

        if (!result) {
            // check if a cms item is from a super catalog

            CatalogModel catalogModel = catalogVersionModel.getCatalog();
            if (catalogModel instanceof ContentCatalogModel) {
                ContentCatalogModel contentCatalogModel = (ContentCatalogModel) catalogModel;
                CatalogModel superCatalog = contentCatalogModel.getSuperCatalog();

                if (superCatalog != null) {
                    final Optional<CMSItemModel> item = getUniqueItemIdentifierService().getItemModel(itemUuid, CMSItemModel.class);
                    if (item.isPresent()) {
                        CatalogVersionModel itemCatVersion = item.get().getCatalogVersion();

                        result = superCatalog.getId().equals(itemCatVersion.getCatalog().getId());
                    }
                }
            }
        }

        return result;
    }
}
