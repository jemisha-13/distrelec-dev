package com.distrelec.solrfacetsearch.provider.category.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistCategoryShopVisibilityValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String CATEGORY_CODE_A = "A";

    private static final String CATEGORY_CODE_B = "B";

    private static final String CATEGORY_CODE_C = "C";

    @InjectMocks
    private DistCategoryShopVisibilityValueResolver distCategoryShopVisibilityValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("visibleInShop");
        cloneableIndexedProperty.setExportId("visibleInShop");
        cloneableIndexedProperty.setLocalized(false);

        when(indexConfig.getVisibleCategoryCodes()).thenReturn(Set.of(CATEGORY_CODE_A,
                                                                      CATEGORY_CODE_B));
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
    }

    @Test
    public void testResolveVisibleInShop() throws FieldValueProviderException {
        when(category.getCode()).thenReturn(CATEGORY_CODE_A);

        distCategoryShopVisibilityValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(TRUE), any());
    }

    @Test
    public void testResolveVisibleInShopNotVisible() throws FieldValueProviderException {
        when(category.getCode()).thenReturn(CATEGORY_CODE_C);

        distCategoryShopVisibilityValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(FALSE), any());
    }
}
