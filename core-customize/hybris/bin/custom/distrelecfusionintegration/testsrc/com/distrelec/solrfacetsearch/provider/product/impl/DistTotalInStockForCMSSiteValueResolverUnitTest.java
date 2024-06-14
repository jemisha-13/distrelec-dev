package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.STOCK_LEVELS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;

public class DistTotalInStockForCMSSiteValueResolverUnitTest extends DistAbstractValueResolverTest {

    @InjectMocks
    private DistTotalInStockForCMSSiteValueResolver distTotalInStockForCMSSiteValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    private Map<String, Object> indexDocumentContext = new HashMap<>();

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("stock");
        cloneableIndexedProperty.setExportId("stock");
        cloneableIndexedProperty.setLocalized(false);

        when(distSolrInputDocument.getIndexDocumentContext()).thenReturn(indexDocumentContext);
    }

    @Test
    public void testResolveWithStocks() throws FieldValueProviderException {
        ImmutablePair<Long, Long> stocklevels = new ImmutablePair<>(150L, 50L);
        indexDocumentContext.put(STOCK_LEVELS, stocklevels);

        distTotalInStockForCMSSiteValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(refEq(booleanIndexedProperty("availableForDelivery")), eq(Boolean.TRUE), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(booleanIndexedProperty("availableForPickup")), eq(Boolean.TRUE), any());
    }

    @Test
    public void testResolveWithZeroStocks() throws FieldValueProviderException {
        ImmutablePair<Long, Long> stocklevels = new ImmutablePair<>(0L, 0L);
        indexDocumentContext.put(STOCK_LEVELS, stocklevels);

        distTotalInStockForCMSSiteValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(refEq(booleanIndexedProperty("availableForDelivery")), eq(Boolean.FALSE), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(booleanIndexedProperty("availableForPickup")), eq(Boolean.FALSE), any());
    }

    @Test
    public void testResolveWithNullStocks() throws FieldValueProviderException {
        indexDocumentContext.put(STOCK_LEVELS, null);

        distTotalInStockForCMSSiteValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

    private IndexedProperty booleanIndexedProperty(String attributeName) {
        return distTotalInStockForCMSSiteValueResolver.createNewIndexedProperty(cloneableIndexedProperty, attributeName,
                                                                                SolrPropertiesTypes.BOOLEAN.getCode());
    }

}
