package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.PRICEINFO_CONTEXT_FIELD;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistShopVisibilityValueResolver extends AbstractDistProductValueResolver {
    private static final Logger LOG = LogManager.getLogger(DistShopVisibilityValueResolver.class);

    protected DistShopVisibilityValueResolver(final DistCMSSiteDao distCMSSiteDao, final DistProductSearchExportDAO distProductSearchExportDAO,
                                              final EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
    }

    @Override
    protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext, final IndexedProperty indexedProperty,
                                  final ProductModel product, final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        CMSSiteModel cmsSite = indexerBatchContext.getFacetSearchConfig().getIndexConfig().getCmsSite();

        long t1 = System.currentTimeMillis();
        boolean isProductVisible = isProductVisible(document, product, cmsSite);
        LOG.debug("Time taken to determine common shop-visibility for product {} is {} ms", product.getCode(), System.currentTimeMillis() - t1);

        addProductVisibilityForMainCountry(document, indexerBatchContext, indexedProperty, product, resolverContext, cmsSite, isProductVisible);
        addProductVisibilityForAllCountries(document, indexerBatchContext, indexedProperty, product, resolverContext, cmsSite, isProductVisible);
        LOG.debug("Time taken to determine full shop-visibility for product {} is {} ms", product.getCode(), System.currentTimeMillis() - t1);
    }

    private boolean isProductVisible(InputDocument document, ProductModel product, CMSSiteModel cmsSite) {
        List<PriceInformation> priceInfos = getDocumentContextAttribute(document, PRICEINFO_CONTEXT_FIELD);
        return getDistProductSearchExportDAO().isVisibleInSalesOrg(product, cmsSite.getSalesOrg()) &&
                isNotEmpty(priceInfos) &&
                isNotEmpty(product.getSupercategories());
    }

    private void addProductVisibilityForMainCountry(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
                                                    ProductModel product, ValueResolverContext<Object, Object> resolverContext, CMSSiteModel cmsSite,
                                                    boolean isProductVisible) throws FieldValueProviderException {
        boolean visibilityForMainCountry = isProductVisible && isProductNotPunchOutForCountry(product, cmsSite, cmsSite.getCountry());
        addFieldValue(document, indexerBatchContext, indexedProperty, visibilityForMainCountry, resolverContext.getFieldQualifier());
    }

    private void addProductVisibilityForAllCountries(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
                                                     ProductModel product, ValueResolverContext<Object, Object> resolverContext, CMSSiteModel cmsSite,
                                                     boolean isProductVisible) throws FieldValueProviderException {
        for (CountryModel country : indexerBatchContext.getFacetSearchConfig().getIndexConfig().getAllCountries()) {
            boolean visibilityForCountry = isProductVisible && isProductNotPunchOutForCountry(product, cmsSite, country);
            document.addField(createNewIndexedProperty(indexedProperty,
                                                       "visibleInShop_" + country.getIsocode().toUpperCase(),
                                                       SolrPropertiesTypes.BOOLEAN.toString()),
                              visibilityForCountry,
                              resolverContext.getFieldQualifier());
        }
    }

    private boolean isProductNotPunchOutForCountry(ProductModel product, CMSSiteModel cmsSite, CountryModel country) {
        return !getDistProductSearchExportDAO().hasActivePunchOutFilter(cmsSite, country, product);
    }

}
