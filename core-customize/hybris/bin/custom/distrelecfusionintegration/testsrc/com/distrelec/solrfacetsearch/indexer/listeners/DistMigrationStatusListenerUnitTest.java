package com.distrelec.solrfacetsearch.indexer.listeners;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.solrfacetsearch.service.impl.DistFusionExportService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistMigrationStatusListenerUnitTest {

    @InjectMocks
    private DistMigrationStatusListener distMigrationStatusListener;

    @Mock
    private DistFusionExportService distFusionExportService;

    @Mock
    private IndexerContext indexerContext;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAfterIndex() throws IndexerException {
        distMigrationStatusListener.afterIndex(indexerContext);

        verify(distFusionExportService, times(1)).sendMigrationStatus(indexerContext);
    }

    @Test
    public void testAfterIndexError() throws IndexerException {
        distMigrationStatusListener.afterIndexError(indexerContext);

        verify(distFusionExportService, times(1)).sendMigrationStatus(indexerContext);
    }
}
