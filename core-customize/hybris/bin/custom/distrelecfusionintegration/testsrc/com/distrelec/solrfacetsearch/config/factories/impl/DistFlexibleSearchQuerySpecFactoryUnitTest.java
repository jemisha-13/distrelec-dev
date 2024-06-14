package com.distrelec.solrfacetsearch.config.factories.impl;

import java.time.LocalDate;
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

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import static com.distrelec.solrfacetsearch.config.factories.impl.DistFlexibleSearchQuerySpecFactory.CMS_SITE;
import static com.distrelec.solrfacetsearch.config.factories.impl.DistFlexibleSearchQuerySpecFactory.COUNTRY;
import static com.distrelec.solrfacetsearch.config.factories.impl.DistFlexibleSearchQuerySpecFactory.LAST_INDEX_TIME;
import static com.distrelec.solrfacetsearch.config.factories.impl.DistFlexibleSearchQuerySpecFactory.PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER;
import static com.distrelec.solrfacetsearch.config.factories.impl.DistFlexibleSearchQuerySpecFactory.PRODUCT_INDEX_TYPE_IDENTIFIER;
import static com.distrelec.solrfacetsearch.config.factories.impl.DistFlexibleSearchQuerySpecFactory.SALES_ORG;
import static com.distrelec.solrfacetsearch.config.factories.impl.DistFlexibleSearchQuerySpecFactory.VISIBLE;
import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistFlexibleSearchQuerySpecFactoryUnitTest {

    @InjectMocks
    private DistFlexibleSearchQuerySpecFactory distFlexibleSearchQuerySpecFactory;

    @Mock
    private IndexedTypeFlexibleSearchQuery queryData;

    @Mock
    private IndexedType indexedType;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CMSSiteModel cmsSite;

    @Mock
    private CountryModel country;

    @Mock
    private DistSalesOrgModel salesOrg;

    @Mock
    private Map<String, Object> parameters;

    @Mock
    private Map<String, Date> lastFusionIndexUpdates;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(queryData.getParameters()).thenReturn(parameters);
        when(indexedType.getIdentifier()).thenReturn(PRODUCT_INDEX_TYPE_IDENTIFIER);

        when(cmsSite.getCountry()).thenReturn(country);
        when(cmsSite.getSalesOrg()).thenReturn(salesOrg);
        when(cmsSite.getLastFusionIndexUpdates()).thenReturn(lastFusionIndexUpdates);

        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);

    }

    @Test
    public void testPopulateRuntimeParametersInitialDate() throws SolrServiceException {
        when(cmsSite.getLastFusionIndexUpdates()).thenReturn(null);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, getInitialDate());
    }

    @Test
    public void testPopulateRuntimeParametersFull() throws SolrServiceException {
        Date lastFullIndexDate = Date.from(LocalDate.now()
                                                    .minusDays(2)
                                                    .atStartOfDay(ZoneId.systemDefault())
                                                    .toInstant());

        when(queryData.getType()).thenReturn(IndexOperation.FULL);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(lastFullIndexDate);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, lastFullIndexDate);
    }

    @Test
    public void testPopulateRuntimeParametersFullNoLastDate() throws SolrServiceException {
        when(queryData.getType()).thenReturn(IndexOperation.FULL);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(null);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, getInitialDate());
    }

    @Test
    public void testPopulateRuntimeParametersUpdateNoDates() throws SolrServiceException {
        when(queryData.getType()).thenReturn(IndexOperation.UPDATE);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(null);
        when(lastFusionIndexUpdates.get("UPDATE_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(null);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, getInitialDate());
    }

    @Test
    public void testPopulateRuntimeParametersUpdateFullAfterUpdate() throws SolrServiceException {
        Date lastFullIndexDate = Date.from(LocalDate.now()
                                                    .minusDays(1)
                                                    .atStartOfDay(ZoneId.systemDefault())
                                                    .toInstant());
        Date lastUpdateIndexDate = Date.from(LocalDate.now()
                                                      .minusDays(2)
                                                      .atStartOfDay(ZoneId.systemDefault())
                                                      .toInstant());

        when(queryData.getType()).thenReturn(IndexOperation.UPDATE);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(lastFullIndexDate);
        when(lastFusionIndexUpdates.get("UPDATE_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(lastUpdateIndexDate);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, lastFullIndexDate);
    }

    @Test
    public void testPopulateRuntimeParametersUpdateUpdateAfterFull() throws SolrServiceException {
        Date lastFullIndexDate = Date.from(LocalDate.now()
                                                    .minusDays(2)
                                                    .atStartOfDay(ZoneId.systemDefault())
                                                    .toInstant());
        Date lastUpdateIndexDate = Date.from(LocalDate.now()
                                                      .minusDays(1)
                                                      .atStartOfDay(ZoneId.systemDefault())
                                                      .toInstant());

        when(queryData.getType()).thenReturn(IndexOperation.UPDATE);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(lastFullIndexDate);
        when(lastFusionIndexUpdates.get("UPDATE_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(lastUpdateIndexDate);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, lastUpdateIndexDate);
    }

    @Test
    public void testPopulateRuntimeParametersUpdateFullExists() throws SolrServiceException {
        Date lastFullIndexDate = Date.from(LocalDate.now()
                                                    .minusDays(2)
                                                    .atStartOfDay(ZoneId.systemDefault())
                                                    .toInstant());

        when(queryData.getType()).thenReturn(IndexOperation.UPDATE);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(lastFullIndexDate);
        when(lastFusionIndexUpdates.get("UPDATE_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(null);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, lastFullIndexDate);
    }

    @Test
    public void testPopulateRuntimeParametersAtomicUpdateNoDates() throws SolrServiceException {
        when(queryData.getType()).thenReturn(IndexOperation.UPDATE);
        when(indexConfig.isAtomicUpdate()).thenReturn(TRUE);
        when(indexedType.getIdentifier()).thenReturn(PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(null);
        when(lastFusionIndexUpdates.get("UPDATE_" + PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER)).thenReturn(null);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, getInitialDate());
    }

    @Test
    public void testPopulateRuntimeParametersAtomicUpdateFullAfterAtomicUpdate() throws SolrServiceException {
        Date lastFullIndexDate = Date.from(LocalDate.now()
                                                    .minusDays(1)
                                                    .atStartOfDay(ZoneId.systemDefault())
                                                    .toInstant());
        Date lastAtomicUpdateIndexDate = Date.from(LocalDate.now()
                                                            .minusDays(2)
                                                            .atStartOfDay(ZoneId.systemDefault())
                                                            .toInstant());

        when(queryData.getType()).thenReturn(IndexOperation.UPDATE);
        when(indexConfig.isAtomicUpdate()).thenReturn(TRUE);
        when(indexedType.getIdentifier()).thenReturn(PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(lastFullIndexDate);
        when(lastFusionIndexUpdates.get("UPDATE_" + PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER)).thenReturn(lastAtomicUpdateIndexDate);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, lastFullIndexDate);
    }

    @Test
    public void testPopulateRuntimeParametersAtomicUpdateUpdateAfterFull() throws SolrServiceException {
        Date lastFullIndexDate = Date.from(LocalDate.now()
                                                    .minusDays(2)
                                                    .atStartOfDay(ZoneId.systemDefault())
                                                    .toInstant());
        Date lastAtomicUpdateIndexDate = Date.from(LocalDate.now()
                                                            .minusDays(1)
                                                            .atStartOfDay(ZoneId.systemDefault())
                                                            .toInstant());

        when(queryData.getType()).thenReturn(IndexOperation.UPDATE);
        when(indexConfig.isAtomicUpdate()).thenReturn(TRUE);
        when(indexedType.getIdentifier()).thenReturn(PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(lastFullIndexDate);
        when(lastFusionIndexUpdates.get("UPDATE_" + PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER)).thenReturn(lastAtomicUpdateIndexDate);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, lastAtomicUpdateIndexDate);
    }

    @Test
    public void testPopulateRuntimeParametersAtomicUpdateFullExists() throws SolrServiceException {
        Date lastFullIndexDate = Date.from(LocalDate.now()
                                                    .minusDays(2)
                                                    .atStartOfDay(ZoneId.systemDefault())
                                                    .toInstant());

        when(queryData.getType()).thenReturn(IndexOperation.UPDATE);
        when(indexConfig.isAtomicUpdate()).thenReturn(TRUE);
        when(indexedType.getIdentifier()).thenReturn(PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER);
        when(lastFusionIndexUpdates.get("FULL_" + PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(lastFullIndexDate);
        when(lastFusionIndexUpdates.get("UPDATE_" + PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER)).thenReturn(null);

        distFlexibleSearchQuerySpecFactory.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        verifyCommonBehaviour();
        verify(parameters, times(1)).put(LAST_INDEX_TIME, lastFullIndexDate);
    }

    private Date getInitialDate() {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate localDate = LocalDate.of(2000, 1, 1);

        return Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
    }

    private void verifyCommonBehaviour() {
        verify(parameters, times(1)).put(VISIBLE, true);
        verify(parameters, times(1)).put(CMS_SITE, cmsSite);
        verify(parameters, times(1)).put(COUNTRY, country);
        verify(parameters, times(1)).put(SALES_ORG, salesOrg);
    }
}
