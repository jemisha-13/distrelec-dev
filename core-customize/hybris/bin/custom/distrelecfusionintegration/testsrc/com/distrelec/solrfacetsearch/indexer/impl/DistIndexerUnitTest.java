package com.distrelec.solrfacetsearch.indexer.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;

import com.distrelec.solrfacetsearch.provider.impl.DistItemIdentityProvider;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContextFactory;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.IdentityProvider;
import de.hybris.platform.solrfacetsearch.provider.RangeNameProvider;
import de.hybris.platform.solrfacetsearch.provider.ValueProviderSelectionStrategy;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistIndexerUnitTest {

    private static final String ITEM_IDENTITY_PROVIDER = "itemIdentityProvider";

    private static final String ID = "PRODUCT_CODE-SE";

    private static final String PRODUCT_CODE = "Product";

    @InjectMocks
    private DistIndexer distIndexer;

    @Mock
    private IndexerBatchContextFactory indexerBatchContextFactory;

    @Mock
    private FieldNameProvider fieldNameProvider;

    @Mock
    private RangeNameProvider rangeNameProvider;

    @Mock
    private ValueProviderSelectionStrategy valueProviderSelectionStrategy;

    @Mock
    private BeanFactory beanFactory;

    @Mock
    private DistProductDocumentContextProvider distProductDocumentContextProvider;

    @Mock
    private Map<String, SolrDocumentContextProvider> contextProvidersByType;

    @Mock
    private DistItemIdentityProvider distItemIdentityProvider;

    @Mock
    private SolrInputDocument solrInputDocument;

    @Mock
    private IndexerBatchContext indexerBatchContext;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexedType indexedType;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private ProductModel product;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(indexerBatchContextFactory.getContext()).thenReturn(indexerBatchContext);
        when(contextProvidersByType.get(PRODUCT_CODE)).thenReturn(distProductDocumentContextProvider);

        when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
        when(indexerBatchContext.getIndexedType()).thenReturn(indexedType);
        when(indexerBatchContext.getInputDocuments()).thenReturn(new ArrayList<>());
        when(indexedType.getIdentityProvider()).thenReturn(ITEM_IDENTITY_PROVIDER);
        when(indexedType.getCode()).thenReturn(PRODUCT_CODE);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(distItemIdentityProvider.getIdentifier(indexConfig, product)).thenReturn(ID);
        when(beanFactory.getBean(ITEM_IDENTITY_PROVIDER, IdentityProvider.class)).thenReturn(distItemIdentityProvider);

    }

    @Test
    public void testCreateInputDocument() throws FieldValueProviderException {
        distIndexer.createInputDocument(product, indexConfig, indexedType);

        verify(distProductDocumentContextProvider, times(1)).addDocumentContext(any(), eq(product), eq(indexerBatchContext));
        assertThat(indexerBatchContext.getInputDocuments(), notNullValue());
        assertThat(indexerBatchContext.getInputDocuments().size(), is(1));
    }

    @Test
    public void testAddCommonFields() {
        distIndexer.addCommonFields(solrInputDocument, indexerBatchContext, product);

        verify(solrInputDocument, times(1)).addField("id", ID);
    }

    @Test
    public void testCreateWrappedDocument() {
        DistSolrInputDocument wrappedDocument = distIndexer.createWrappedDocument(indexerBatchContext, solrInputDocument);

        assertThat(wrappedDocument, notNullValue());
        assertThat(wrappedDocument.getDelegate(), is(solrInputDocument));
        assertThat(wrappedDocument.getBatchContext(), is(indexerBatchContext));
        assertThat(wrappedDocument.getFieldNameProvider(), is(fieldNameProvider));
        assertThat(wrappedDocument.getRangeNameProvider(), is(rangeNameProvider));
    }

}
