package com.distrelec.solrfacetsearch.indexer.cron;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.indexer.IndexerService;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrIndexerCronJobModel;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistSolrIndexerJobUnitTest {

    private static final String FACET_SEARCH_CONFIG_NAME = "solrFacetSearchConfigName";

    private static final String SITE_UID_SWEDEN = "distrelec_SE";

    private static final String SITE_UID_SWITZERLAND = "distrelec_CH";

    private static final String SITE_UID_INTERNATIONAL = "distrelec_EX";

    @InjectMocks
    private DistSolrIndexerJob distSolrIndexerJob;

    @Mock
    private FacetSearchConfigService facetSearchConfigService;

    @Mock
    private IndexerService indexerService;

    @Mock
    private ModelService modelService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private SolrIndexerCronJobModel solrIndexerCronJob;

    @Mock
    private CronJobModel cronJob;

    @Mock
    private SolrFacetSearchConfigModel solrFacetSearchConfig;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CMSSiteModel cmsSite;

    @Mock
    private BaseStoreModel baseStore;

    @Mock
    private LanguageModel language;

    @Mock
    private CurrencyModel currency;

    @Mock
    private IndexedType productIndexedType;

    @Before
    public void init() throws FacetConfigServiceException {
        MockitoAnnotations.openMocks(this);

        when(solrIndexerCronJob.getFacetSearchConfig()).thenReturn(solrFacetSearchConfig);
        when(solrFacetSearchConfig.getName()).thenReturn(FACET_SEARCH_CONFIG_NAME);
        when(facetSearchConfigService.getConfiguration(FACET_SEARCH_CONFIG_NAME)).thenReturn(facetSearchConfig);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(solrIndexerCronJob.getCmsSites()).thenReturn(List.of(cmsSite));
        when(solrIndexerCronJob.getIndexerOperation()).thenReturn(IndexerOperationValues.FULL);
        when(cmsSite.getStores()).thenReturn(List.of(baseStore));
        when(cmsSite.getUid()).thenReturn(SITE_UID_SWEDEN);
        when(baseStore.getChannel()).thenReturn(SiteChannel.B2B);
        when(baseStore.getLanguages()).thenReturn(Set.of(language));
        when(baseStore.getCurrencies()).thenReturn(Set.of(currency));
        when(baseStore.getDefaultCurrency()).thenReturn(currency);
        when(indexConfig.getStartTime()).thenReturn(LocalDateTime.now().minusHours(1));
        when(indexConfig.getEndTime()).thenReturn(LocalDateTime.now());
        when(solrIndexerCronJob.getUseAtomicUpdates()).thenReturn(FALSE);

        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getStringArray(any())).thenReturn(new String[0]);
    }

    @Test
    public void testPerform() throws IndexerException {
        PerformResult perform = distSolrIndexerJob.perform(solrIndexerCronJob);

        assertThat(perform.getResult(), is(CronJobResult.SUCCESS));
        assertThat(perform.getStatus(), is(CronJobStatus.FINISHED));

        verify(indexConfig, times(1)).setCurrencies(Set.of(currency));
        verify(indexConfig, times(1)).setLanguages(Set.of(language));
        verify(indexConfig, times(1)).setCmsSite(cmsSite);
        verify(indexConfig, times(1)).setAtomicUpdate(FALSE);
        verify(indexerService, times(1)).performFullIndex(facetSearchConfig, Map.of());
    }

    @Test
    public void testPerformException() throws IndexerException {
        doThrow(IndexerException.class).when(indexerService).performFullIndex(any(), any());

        PerformResult perform = distSolrIndexerJob.perform(solrIndexerCronJob);

        assertThat(perform.getResult(), is(CronJobResult.ERROR));
        assertThat(perform.getStatus(), is(CronJobStatus.ABORTED));

        verify(indexConfig, times(1)).setCurrencies(Set.of(currency));
        verify(indexConfig, times(1)).setLanguages(Set.of(language));
        verify(indexConfig, times(1)).setCmsSite(cmsSite);
        verify(indexConfig, times(1)).setAtomicUpdate(FALSE);
        verify(indexerService, times(1)).performFullIndex(facetSearchConfig, Map.of());
    }

    @Test
    public void testPerformAborted() throws IndexerException {
        when(solrIndexerCronJob.getRequestAbort()).thenReturn(TRUE);

        PerformResult perform = distSolrIndexerJob.perform(solrIndexerCronJob);

        assertThat(perform.getResult(), is(CronJobResult.SUCCESS));
        assertThat(perform.getStatus(), is(CronJobStatus.ABORTED));

        verify(indexConfig, times(0)).setCurrencies(any());
        verify(indexConfig, times(0)).setLanguages(any());
        verify(indexConfig, times(0)).setCmsSite(any());
        verify(indexConfig, times(0)).setAtomicUpdate(anyBoolean());
        verify(indexerService, times(0)).performFullIndex(any(), any());
    }

    @Test
    public void testPerformNoB2BChannel() throws IndexerException {
        when(baseStore.getChannel()).thenReturn(SiteChannel.B2C);

        PerformResult perform = distSolrIndexerJob.perform(solrIndexerCronJob);

        assertThat(perform.getResult(), is(CronJobResult.SUCCESS));
        assertThat(perform.getStatus(), is(CronJobStatus.FINISHED));

        verify(indexConfig, times(0)).setCurrencies(any());
        verify(indexConfig, times(0)).setLanguages(any());
        verify(indexConfig, times(0)).setCmsSite(any());
        verify(indexConfig, times(0)).setAtomicUpdate(anyBoolean());
        verify(indexerService, times(0)).performFullIndex(any(), any());
    }

    @Test
    public void testPerformNoCmsPages() throws FacetConfigServiceException, IndexerException {
        when(solrIndexerCronJob.getCmsSites()).thenReturn(List.of());

        PerformResult perform = distSolrIndexerJob.perform(solrIndexerCronJob);

        assertThat(perform.getResult(), is(CronJobResult.SUCCESS));
        assertThat(perform.getStatus(), is(CronJobStatus.FINISHED));
        verify(indexerService, times(0)).performFullIndex(any(), any());
    }

    @Test
    public void testPerformNoFacetConfig() throws FacetConfigServiceException {
        when(facetSearchConfigService.getConfiguration(FACET_SEARCH_CONFIG_NAME)).thenReturn(null);

        PerformResult perform = distSolrIndexerJob.perform(solrIndexerCronJob);

        assertThat(perform.getResult(), is(CronJobResult.ERROR));
        assertThat(perform.getStatus(), is(CronJobStatus.ABORTED));
        verify(facetSearchConfigService, times(1)).getConfiguration(FACET_SEARCH_CONFIG_NAME);
    }

    @Test
    public void testPerformWrongType() {
        PerformResult perform = distSolrIndexerJob.perform(cronJob);

        assertThat(perform.getResult(), is(CronJobResult.FAILURE));
        assertThat(perform.getStatus(), is(CronJobStatus.ABORTED));
    }

    @Test
    public void testLoadingOfAllCountriesSE() {
        CountryModel countrySE = Mockito.mock(CountryModel.class);

        when(cmsSite.getUid()).thenReturn(SITE_UID_SWEDEN);
        when(baseStore.getDeliveryCountries()).thenReturn(List.of(countrySE));
        when(baseStore.getRegisterCountries()).thenReturn(List.of());

        distSolrIndexerJob.perform(solrIndexerCronJob);

        // we expect only delivery countries for non distrelec_EX shops
        verify(indexConfig, times(1)).setAllCountries(List.of(countrySE));
    }

    @Test
    public void testLoadingOfAllCountriesCH() {
        CountryModel countryCH = Mockito.mock(CountryModel.class);
        CountryModel countryLI = Mockito.mock(CountryModel.class);

        when(cmsSite.getUid()).thenReturn(SITE_UID_SWITZERLAND);
        when(baseStore.getDeliveryCountries()).thenReturn(List.of(countryCH, countryLI));
        when(baseStore.getRegisterCountries()).thenReturn(List.of());

        distSolrIndexerJob.perform(solrIndexerCronJob);

        // we expect only delivery countries for non distrelec_EX shops
        verify(indexConfig, times(1)).setAllCountries(List.of(countryCH, countryLI));
    }

    @Test
    public void testLoadingOfAllCountriesEX() {
        CountryModel countryTH = Mockito.mock(CountryModel.class);
        CountryModel countryGR = Mockito.mock(CountryModel.class);
        CountryModel countryGB = Mockito.mock(CountryModel.class);
        CountryModel countryXI = Mockito.mock(CountryModel.class);
        CountryModel countryEX = Mockito.mock(CountryModel.class);

        when(cmsSite.getUid()).thenReturn(SITE_UID_INTERNATIONAL);
        when(cmsSite.getCountry()).thenReturn(countryEX);
        when(baseStore.getDeliveryCountries()).thenReturn(List.of(countryTH, countryGR, countryGB, countryXI, countryEX));
        when(baseStore.getRegisterCountries()).thenReturn(List.of(countryGR, countryGB, countryXI));

        distSolrIndexerJob.perform(solrIndexerCronJob);

        // we expect only register countries and the main country for distrelec_EX
        verify(indexConfig, times(1)).setAllCountries(List.of(countryGR, countryGB, countryXI, countryEX));
    }

}
