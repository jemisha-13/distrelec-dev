package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.apache.commons.collections4.CollectionUtils.disjunction;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;

import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistVisibleInChannelsValueResolver extends AbstractDistProductValueResolver {

    private static final Logger LOG = LogManager.getLogger(DistVisibleInChannelsValueResolver.class);

    private static final List<String> AVAILABLE_CHANNELS = List.of(SiteChannel.B2B.getCode(), SiteChannel.B2C.getCode());

    protected DistVisibleInChannelsValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                                 EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        long t1 = System.currentTimeMillis();

        List<String> channels = getDistProductSearchExportDAO().getChannelsWithPunchOutFilters(product)
                                                               .stream()
                                                               .map(EnumerationValueModel::getCode)
                                                               .toList();

        List<String> availableInChannels = disjunction(channels, AVAILABLE_CHANNELS)
                                                                                    .stream()
                                                                                    .toList();

        addFieldValue(document, indexerBatchContext, indexedProperty, availableInChannels, resolverContext.getFieldQualifier());

        LOG.debug("Time taken to determine channels (B2C, B2B) the product is visible in for product {} is {}ms",
                  product.getCode(),
                  System.currentTimeMillis() - t1);
    }

}
