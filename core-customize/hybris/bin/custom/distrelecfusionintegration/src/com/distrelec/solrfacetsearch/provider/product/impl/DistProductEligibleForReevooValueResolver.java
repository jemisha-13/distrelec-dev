package com.distrelec.solrfacetsearch.provider.product.impl;

import org.apache.commons.lang3.BooleanUtils;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.reevoo.productfeed.dao.RevooProductFeedExportDao;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistProductEligibleForReevooValueResolver extends AbstractDistProductValueResolver {

    private RevooProductFeedExportDao revooProductFeedExportDao;

    public DistProductEligibleForReevooValueResolver(DistProductSearchExportDAO distProductSearchExportDAO, DistCMSSiteDao distCMSSiteDao,
                                                     EnumerationService enumerationService, RevooProductFeedExportDao revooProductFeedExportDao) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
        this.revooProductFeedExportDao = revooProductFeedExportDao;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext batchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> valueResolverContext) throws FieldValueProviderException {
        CMSSiteModel site = batchContext.getFacetSearchConfig().getIndexConfig().getCmsSite();
        Boolean productEligible = revooProductFeedExportDao.isProductEligible(site, product);
        addFieldValue(document, batchContext, indexedProperty, BooleanUtils.isTrue(productEligible), valueResolverContext.getFieldQualifier());
    }
}
