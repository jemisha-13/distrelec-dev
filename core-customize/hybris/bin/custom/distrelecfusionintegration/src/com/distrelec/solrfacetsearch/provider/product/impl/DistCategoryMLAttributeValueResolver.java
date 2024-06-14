package com.distrelec.solrfacetsearch.provider.product.impl;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.List;
import java.util.Map;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistCategoryMLAttributeValueResolver extends AbstractDistProductValueResolver {

    private static final int CATEGORY_LEVEL_1 = 1;

    private static final int CATEGORY_LEVEL_2 = 2;

    private static final int CATEGORY_LEVEL_3 = 3;

    private static final int CATEGORY_LEVEL_4 = 4;

    private final Gson gson;

    protected DistCategoryMLAttributeValueResolver(final DistCMSSiteDao distCMSSiteDao, final DistProductSearchExportDAO distProductSearchExportDAO,
                                                   final EnumerationService enumerationService, final Gson gson) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
        this.gson = gson;
    }

    @Override
    protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext, final IndexedProperty indexedProperty,
                                  final ProductModel product, final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        CategoryModel primarySuperCategory = product.getPrimarySuperCategory();
        if (primarySuperCategory == null) {
            return;
        }
        // fallthrough
        switch (primarySuperCategory.getLevel()) {
            case CATEGORY_LEVEL_4:
                addFieldValue(document, indexerBatchContext, createNewIndexedProperty(indexedProperty, "category4"),
                              formatValue(primarySuperCategory.getCat4code(), primarySuperCategory.getCat4name()), resolverContext.getFieldQualifier());
                addFieldValue(document, indexerBatchContext, createNewIndexedProperty(indexedProperty, "category4Name"), primarySuperCategory.getCat4name(),
                              resolverContext.getFieldQualifier());
            case CATEGORY_LEVEL_3:
                addFieldValue(document, indexerBatchContext, createNewIndexedProperty(indexedProperty, "category3"),
                              formatValue(primarySuperCategory.getCat3code(), primarySuperCategory.getCat3name()), resolverContext.getFieldQualifier());
                addFieldValue(document, indexerBatchContext, createNewIndexedProperty(indexedProperty, "category3Name"), primarySuperCategory.getCat3name(),
                              resolverContext.getFieldQualifier());
            case CATEGORY_LEVEL_2:
                addFieldValue(document, indexerBatchContext, createNewIndexedProperty(indexedProperty, "category2"),
                              formatValue(primarySuperCategory.getCat2code(), primarySuperCategory.getCat2name()), resolverContext.getFieldQualifier());
                addFieldValue(document, indexerBatchContext, createNewIndexedProperty(indexedProperty, "category2Name"), primarySuperCategory.getCat2name(),
                              resolverContext.getFieldQualifier());
            case CATEGORY_LEVEL_1:
                addFieldValue(document, indexerBatchContext, createNewIndexedProperty(indexedProperty, "category1"),
                              formatValue(primarySuperCategory.getCat1code(), primarySuperCategory.getCat1name()), resolverContext.getFieldQualifier());
                addFieldValue(document, indexerBatchContext, createNewIndexedProperty(indexedProperty, "category1Name"), primarySuperCategory.getCat1name(),
                              resolverContext.getFieldQualifier());
            default:
                // Do nothing
        }

        String catPathExtensions = primarySuperCategory.getCatPathExtensions();
        if (isBlank(catPathExtensions)) {
            return;
        }
        List<Map<String, String>> map = gson.fromJson(catPathExtensions, List.class);

        for (int i = 0; i < map.size(); i++) {
            document.addField(createNewIndexedProperty(indexedProperty, format("category%sUrl", i + 1)),
                              map.get(i).get("url"),
                              resolverContext.getFieldQualifier());
        }
    }

    private String formatValue(String code, String name) {
        if (isNotBlank(code)) {
            return isNotBlank(name) ? code + "|" + name : code;
        }
        return EMPTY;
    }
}
