package com.distrelec.solrfacetsearch.indexer.strategies.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistIndexNameStrategyUnitTest {

    private static final String FUSION_COLLECTION_PREFIX = "webshop_";

    private static final String COLLECTION_SUFFIX = "distrelecfusionintegration.fusion.collectionSuffix";

    private static final String COLLECTION_SUFFIX_VALUE = "staging";

    private static final String INDEX_NAME = "product_atomic";

    @InjectMocks
    private DistIndexNameStrategy distIndexNameStrategy;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private IndexedType indexedType;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(COLLECTION_SUFFIX)).thenReturn(null);

        when(indexedType.getIndexName()).thenReturn(INDEX_NAME);
    }

    @Test
    public void testCreateIndexName() {
        String indexName = distIndexNameStrategy.createIndexName(null, indexedType);

        assertThat(indexName, notNullValue());
        assertThat(indexName, is(FUSION_COLLECTION_PREFIX + INDEX_NAME));
    }

    @Test
    public void testCreateIndexNameSuffix() {
        when(configuration.getString(COLLECTION_SUFFIX)).thenReturn(COLLECTION_SUFFIX_VALUE);

        String indexName = distIndexNameStrategy.createIndexName(null, indexedType);

        assertThat(indexName, notNullValue());
        assertThat(indexName, is(FUSION_COLLECTION_PREFIX + INDEX_NAME + "_" + COLLECTION_SUFFIX_VALUE));
    }
}
