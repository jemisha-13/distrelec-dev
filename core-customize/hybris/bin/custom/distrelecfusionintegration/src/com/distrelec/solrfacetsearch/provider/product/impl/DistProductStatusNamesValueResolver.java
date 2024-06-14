package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.PRODUCT_STATUS_CODES;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.enums.ProductStatus;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistProductStatusNamesValueResolver extends AbstractDistProductValueResolver {

    private static final String PRODUCT_STATUS = "ProductStatus";

    public DistProductStatusNamesValueResolver(DistProductSearchExportDAO distProductSearchExportDAO,
                                               DistCMSSiteDao distCMSSiteDao,
                                               EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
    }

    @Override
    protected void addFieldValues(InputDocument document,
                                  IndexerBatchContext batchContext,
                                  IndexedProperty indexedProperty,
                                  ProductModel product,
                                  ValueResolverContext<Object, Object> valueResolverContext) throws FieldValueProviderException {
        List<String> productStatusList = getDocumentContextAttribute(document, PRODUCT_STATUS_CODES);

        if (isNotEmpty(productStatusList)) {
            List<String> statusNames = productStatusList.stream().map(this::getEnumNameByCode).toList();
            addFieldValue(document, batchContext, indexedProperty, statusNames, valueResolverContext.getFieldQualifier());
        }
    }

    private String getEnumNameByCode(String code) {
        return getEnumerationService().getEnumerationName(getEnumerationService().<ProductStatus> getEnumerationValue(PRODUCT_STATUS, code));
    }
}
