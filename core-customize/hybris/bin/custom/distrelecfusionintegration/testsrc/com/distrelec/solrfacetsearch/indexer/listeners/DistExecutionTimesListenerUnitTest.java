package com.distrelec.solrfacetsearch.indexer.listeners;

import static de.hybris.platform.solrfacetsearch.config.IndexOperation.FULL;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistExecutionTimesListenerUnitTest {

    private static final String PRODUCT_INDEX_TYPE_IDENTIFIER = "distProduct";

    @InjectMocks
    private DistExecutionTimesListener distExecutionTimesListener;

    @Mock
    private ModelService modelService;

    @Mock
    private IndexerContext indexerContext;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CMSSiteModel cmsSite;

    @Mock
    private IndexedType productIndexedType;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexerContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
    }

    @Test
    public void testAfterPrepareContext() throws IndexerException {
        distExecutionTimesListener.afterPrepareContext(indexerContext);

        verify(indexConfig, times(1)).setStartTime(any());
    }

    @Test
    public void testAfterIndex() throws IndexerException {
        LocalDateTime startTime = LocalDateTime.now();
        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(indexConfig.getStartTime()).thenReturn(startTime);
        when(productIndexedType.getIdentifier()).thenReturn(PRODUCT_INDEX_TYPE_IDENTIFIER);
        when(indexConfig.getIndexedTypes()).thenReturn(Map.of("product", productIndexedType));
        when(indexerContext.getIndexOperation()).thenReturn(FULL);
        when(indexerContext.getIndexedType()).thenReturn(productIndexedType);
        when(cmsSite.getLastFusionIndexUpdates()).thenReturn(Map.of());

        distExecutionTimesListener.afterIndex(indexerContext);

        Map<String, Date> expectedFusionIndexUpdates = Map.of(FULL.name() + "_" + PRODUCT_INDEX_TYPE_IDENTIFIER,
                                                              Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
        verify(cmsSite, times(1)).setLastFusionIndexUpdates(expectedFusionIndexUpdates);
        verify(modelService, times(1)).save(cmsSite);
    }

    @Test
    public void testAfterIndexNoCmsSite() throws IndexerException {
        when(indexConfig.getCmsSite()).thenReturn(null);

        distExecutionTimesListener.afterIndex(indexerContext);

        verify(modelService, times(0)).save(any());
    }
}
