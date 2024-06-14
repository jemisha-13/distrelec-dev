package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;
import com.namics.distrelec.b2b.core.reevoo.productfeed.dao.RevooProductFeedExportDao;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistProductEligibleForReevooValueResolverUnitTest extends DistAbstractValueResolverTest {

    @InjectMocks
    private DistProductEligibleForReevooValueResolver distProductEligibleForReevooValueResolver;

    @Mock
    private RevooProductFeedExportDao revooProductFeedExportDao;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CMSSiteModel cmsSite;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("eligibleForReevoo");
        cloneableIndexedProperty.setExportId("eligibleForReevoo");
        cloneableIndexedProperty.setLocalized(false);

        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
    }

    @Test
    public void testResolveEligibleForReevoo() throws FieldValueProviderException {
        when(revooProductFeedExportDao.isProductEligible(cmsSite, product)).thenReturn(Boolean.TRUE);

        distProductEligibleForReevooValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(true), any());
    }

    @Test
    public void testResolveEligibleForReevooNull() throws FieldValueProviderException {
        when(revooProductFeedExportDao.isProductEligible(cmsSite, product)).thenReturn(null);

        distProductEligibleForReevooValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(false), any());
    }
}
