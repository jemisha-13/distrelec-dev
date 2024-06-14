package com.distrelec.solrfacetsearch.provider.product.impl;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistCategoryAttributeValueResolver extends AbstractDistProductValueResolver {

    private static final int CATEGORY_LEVEL_1 = 1;

    private static final int CATEGORY_LEVEL_2 = 2;

    private static final int CATEGORY_LEVEL_3 = 3;

    private static final int CATEGORY_LEVEL_4 = 4;

    protected DistCategoryAttributeValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                                 EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        CategoryModel primarySuperCategory = product.getPrimarySuperCategory();
        if (primarySuperCategory == null) {
            return;
        }
        switch (primarySuperCategory.getLevel()) {
            case CATEGORY_LEVEL_4:
                document.addField(createNewIndexedProperty(indexedProperty, "category4Code"), primarySuperCategory.getCat4code(),
                                  resolverContext.getFieldQualifier());
            case CATEGORY_LEVEL_3:
                document.addField(createNewIndexedProperty(indexedProperty, "category3Code"), primarySuperCategory.getCat3code(),
                                  resolverContext.getFieldQualifier());
            case CATEGORY_LEVEL_2:
                document.addField(createNewIndexedProperty(indexedProperty, "category2Code"), primarySuperCategory.getCat2code(),
                                  resolverContext.getFieldQualifier());
            case CATEGORY_LEVEL_1:
                document.addField(createNewIndexedProperty(indexedProperty, "category1Code"), primarySuperCategory.getCat1code(),
                                  resolverContext.getFieldQualifier());
            default:
                // Do nothing
        }

        document.addField(createNewIndexedProperty(indexedProperty, "categoryCodePath"), primarySuperCategory.getCatPathSelectCode(),
                          resolverContext.getFieldQualifier());
    }
}
