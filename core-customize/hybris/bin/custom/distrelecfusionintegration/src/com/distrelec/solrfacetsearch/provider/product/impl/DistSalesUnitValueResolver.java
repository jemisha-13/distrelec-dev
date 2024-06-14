package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistSalesUnitValueResolver extends AbstractDistProductValueResolver {

    protected DistSalesUnitValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                         EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext batchContext,
                                  IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        String salesUnitField = getSalesUnitField(product);
        addFieldValue(document, batchContext, indexedProperty, salesUnitField, resolverContext.getFieldQualifier());
    }

    private String getSalesUnitField(ProductModel product) {
        DistSalesUnitModel productSalesUnit = product.getSalesUnit();
        if (productSalesUnit != null) {
            return isNotBlank(productSalesUnit.getName()) ? productSalesUnit.getName() : productSalesUnit.getNameErp();
        }

        if (product.getUnit() != null) {
            return product.getUnit().getName();
        }
        return null;
    }
}
