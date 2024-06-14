package com.distrelec.solrfacetsearch.indexer.strategies.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexerStrategy;
import de.hybris.platform.solrfacetsearch.indexer.strategies.impl.DistributedIndexerStrategy;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistIndexerStrategyFactorUnitTest {

    private static final String DIST_FUSION_INDEXER_STRATEGY_BEANID = "distFusionIndexerStrategy";

    private static final String DISTRIBUTED_INDEXER_STRATEGY_BEANID = "distributedIndexerStrategy";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    private DistIndexerStrategyFactor distIndexerStrategyFactor;

    @Mock
    private DistIndexerStrategy distIndexerStrategy;

    @Mock
    private DistributedIndexerStrategy distributedIndexerStrategy;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private SolrConfig solrConfig;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        distIndexerStrategyFactor.setDistFusionIndexerStrategy(DIST_FUSION_INDEXER_STRATEGY_BEANID);
        distIndexerStrategyFactor.setDistributedIndexerStrategyBeanId(DISTRIBUTED_INDEXER_STRATEGY_BEANID);

        when(facetSearchConfig.getSolrConfig()).thenReturn(solrConfig);
        when(solrConfig.getMode()).thenReturn(SolrServerMode.FUSION);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexConfig.isDistributedIndexing()).thenReturn(true);

    }

    @Test
    public void testCreateIndexerStrategy() throws IndexerException {
        when(applicationContext.getBean(DIST_FUSION_INDEXER_STRATEGY_BEANID, IndexerStrategy.class))
                                                                                                    .thenReturn(distIndexerStrategy);

        IndexerStrategy indexerStrategy = distIndexerStrategyFactor.createIndexerStrategy(facetSearchConfig);

        assertThat(indexerStrategy, is(distIndexerStrategy));
    }

    @Test
    public void testCreateIndexerStrategyNoBean() throws IndexerException {
        thrown.expect(IndexerException.class);
        when(applicationContext.getBean(DIST_FUSION_INDEXER_STRATEGY_BEANID, IndexerStrategy.class))
                                                                                                    .thenThrow(NoSuchBeanDefinitionException.class);

        distIndexerStrategyFactor.createIndexerStrategy(facetSearchConfig);
    }

    @Test
    public void testCreateIndexerStrategyNotFusion() throws IndexerException {
        when(solrConfig.getMode()).thenReturn(SolrServerMode.STANDALONE);
        when(applicationContext.getBean(DISTRIBUTED_INDEXER_STRATEGY_BEANID, IndexerStrategy.class))
                                                                                                    .thenReturn(distributedIndexerStrategy);

        IndexerStrategy indexerStrategy = distIndexerStrategyFactor.createIndexerStrategy(facetSearchConfig);

        assertThat(indexerStrategy, is(distributedIndexerStrategy));
    }

}
