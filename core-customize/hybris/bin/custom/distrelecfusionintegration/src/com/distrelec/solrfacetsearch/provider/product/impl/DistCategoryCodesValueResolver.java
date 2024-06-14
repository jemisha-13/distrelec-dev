package com.distrelec.solrfacetsearch.provider.product.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistCategoryCodesValueResolver extends AbstractDistProductValueResolver {

    private static final int CATEGORY_LEVEL_1 = 1;

    private static final int CATEGORY_LEVEL_2 = 2;

    private static final int CATEGORY_LEVEL_3 = 3;

    private static final int CATEGORY_LEVEL_4 = 4;

    protected DistCategoryCodesValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
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

        List<String> categoryCodes = new ArrayList<>();

        switch (primarySuperCategory.getLevel()) {
            case CATEGORY_LEVEL_4:
                addToListIfNotNull(categoryCodes, primarySuperCategory.getCat4code());
            case CATEGORY_LEVEL_3:
                addToListIfNotNull(categoryCodes, primarySuperCategory.getCat3code());
            case CATEGORY_LEVEL_2:
                addToListIfNotNull(categoryCodes, primarySuperCategory.getCat2code());
            case CATEGORY_LEVEL_1:
                addToListIfNotNull(categoryCodes, primarySuperCategory.getCat1code());
            default:
                // Do nothing
        }
        Collections.reverse(categoryCodes);
        addFieldValue(document, indexerBatchContext, indexedProperty, categoryCodes, resolverContext.getFieldQualifier());
    }

    private void addToListIfNotNull(List<String> catCodes, String catCode) {
        if (StringUtils.isNotBlank(catCode)) {
            catCodes.add(catCode);
        }
    }
}
