package com.distrelec.solrfacetsearch.service.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.distrelec.fusion.integration.dto.DeleteRequestDTO;
import com.distrelec.fusion.integration.dto.FusionDocumentRequestDTO;
import com.distrelec.fusion.integration.dto.MigrationStatusRequestDTO;
import com.distrelec.solrfacetsearch.exception.FusionIntegrationException;
import com.namics.distrelec.b2b.core.constants.DistConstants;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistFusionExportServiceUnitTest {

    private static final String HEADER_API_KEY = "x-api-key";

    private static final String API_KEY_INDEX = "distrelecfusionintegration.fusion.api.key.index";

    private static final String API_KEY_INDEX_VALUE = "indexing-api-key";

    private static final String API_KEY_DELETE = "distrelecfusionintegration.fusion.api.key.delete";

    private static final String API_KEY_DELETE_VALUE = "delete-api-key";

    private static final String FUSION_INDEX_URL = "distrelecfusionintegration.fusion.index.url";

    private static final String FUSION_INDEX_URL_VALUE = "/apps/webshop/index";

    private static final String DELETE_PROFILE_PREFIX = "delete_";

    private static final String DELETE_REQUEST_QUERY_PARAM = "?echo=false&stats=false";

    private static final String SIMULATE_UPDATE = "distrelecfusionintegration.fusion.simulateupdate";

    private static final String MIGRATION_STATUS_INDEX_PROFILE = "distrelecfusionintegration.fusion.index.profile.migration.status";

    private static final String MIGRATION_STATUS_INDEX_PROFILE_VALUE = "migration_status";

    private static final String PRODUCT_COLLECTION_NAME = "webshop_product";

    private static final String DELETE_QUERY = "(-(indexOperationId:[110633459257114649 TO *]) AND country_s:SE)";

    private static final String EXCEPTION_MESSAGE_COLLECTION = "Error sending indexing request to Fusion!";

    private static final String EXCEPTION_MESSAGE_DELETE = "Error sending delete request to Fusion!";

    private static final String EXCEPTION_MESSAGE_MIGRATION = "Error sending migration status request to Fusion!";

    @InjectMocks
    private DistFusionExportService distFusionExportService = new DistFusionExportService();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Converter<Object, FusionDocumentRequestDTO> fusionDocumentRequestDTOConverter;

    @Mock
    private Converter<String, DeleteRequestDTO> deleteRequestDTOConverter;

    @Mock
    private Converter<IndexerContext, MigrationStatusRequestDTO> migrationStatusDTOConverter;

    @Mock
    private Configuration configuration;

    @Mock
    private IndexerContext indexerContext;

    @Mock
    private MigrationStatusRequestDTO migrationStatusDTO;

    @Mock
    private DeleteRequestDTO deleteRequestDTO;

    @Mock
    private SolrInputDocument solrInputDocument;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getBoolean(SIMULATE_UPDATE, FALSE)).thenReturn(FALSE);
        when(configuration.getString(FUSION_INDEX_URL)).thenReturn(FUSION_INDEX_URL_VALUE);
        when(configuration.getString(API_KEY_INDEX)).thenReturn(API_KEY_INDEX_VALUE);
        when(configuration.getString(API_KEY_DELETE)).thenReturn(API_KEY_DELETE_VALUE);
        when(deleteRequestDTOConverter.convert(DELETE_QUERY)).thenReturn(deleteRequestDTO);
        when(migrationStatusDTOConverter.convert(indexerContext)).thenReturn(migrationStatusDTO);
    }

    @Test
    public void testPushDocumentsToCollection() {
        distFusionExportService.pushDocumentsToCollection(List.of(solrInputDocument), PRODUCT_COLLECTION_NAME);

        String expectedUrl = FUSION_INDEX_URL_VALUE
                             + DistConstants.Punctuation.FORWARD_SLASH
                             + PRODUCT_COLLECTION_NAME;
        verify(restTemplate, times(1)).postForEntity(eq(expectedUrl),
                                                     any(),
                                                     eq(FusionDocumentRequestDTO.class));
    }

    @Test
    public void testPushDocumentsToCollectionSimulation() {
        when(configuration.getBoolean(SIMULATE_UPDATE, FALSE)).thenReturn(TRUE);

        distFusionExportService.pushDocumentsToCollection(List.of(solrInputDocument), PRODUCT_COLLECTION_NAME);

        verify(restTemplate, times(0)).postForEntity(any(), any(), any());
    }

    @Test
    public void testPushDocumentsToCollectionException() {
        thrown.expect(FusionIntegrationException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE_COLLECTION);

        when(restTemplate.postForEntity(any(String.class), any(), any())).thenThrow(ResourceAccessException.class);

        distFusionExportService.pushDocumentsToCollection(List.of(solrInputDocument), PRODUCT_COLLECTION_NAME);
    }

    @Test
    public void testDeleteDocumentsByQuery() {
        distFusionExportService.deleteDocumentsByQuery(PRODUCT_COLLECTION_NAME, DELETE_QUERY);

        String expectedUrl = FUSION_INDEX_URL_VALUE
                             + DistConstants.Punctuation.FORWARD_SLASH
                             + DELETE_PROFILE_PREFIX + PRODUCT_COLLECTION_NAME
                             + DELETE_REQUEST_QUERY_PARAM;
        HttpEntity expectedRequest = createExpectedDeleteRequest(deleteRequestDTO);
        verify(restTemplate, times(1)).postForEntity(eq(expectedUrl),
                                                     refEq(expectedRequest),
                                                     eq(String.class));
    }

    @Test
    public void testDeleteDocumentsByQueryIsSimulation() {
        when(configuration.getBoolean(SIMULATE_UPDATE, FALSE)).thenReturn(TRUE);

        distFusionExportService.deleteDocumentsByQuery(PRODUCT_COLLECTION_NAME, DELETE_QUERY);

        verify(restTemplate, times(0)).postForEntity(any(), any(), any());
    }

    @Test
    public void testDeleteDocumentsByQueryException() {
        thrown.expect(FusionIntegrationException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE_DELETE);

        when(restTemplate.postForEntity(any(String.class), any(), any())).thenThrow(ResourceAccessException.class);

        distFusionExportService.deleteDocumentsByQuery(PRODUCT_COLLECTION_NAME, DELETE_QUERY);
    }

    @Test
    public void testSendMigrationStatus() {
        when(configuration.getString(MIGRATION_STATUS_INDEX_PROFILE)).thenReturn(MIGRATION_STATUS_INDEX_PROFILE_VALUE);

        distFusionExportService.sendMigrationStatus(indexerContext);

        verify(migrationStatusDTOConverter, times(1)).convert(indexerContext);
        String expectedUrl = FUSION_INDEX_URL_VALUE + DistConstants.Punctuation.FORWARD_SLASH + MIGRATION_STATUS_INDEX_PROFILE_VALUE;
        HttpEntity expectedRequest = createExpectedRequest(migrationStatusDTO);
        verify(restTemplate, times(1)).postForEntity(eq(expectedUrl),
                                                     refEq(expectedRequest),
                                                     eq(String.class));
    }

    @Test
    public void testSendMigrationStatusIsSimulation() {
        when(configuration.getBoolean(SIMULATE_UPDATE, FALSE)).thenReturn(TRUE);

        distFusionExportService.sendMigrationStatus(indexerContext);

        verify(migrationStatusDTOConverter, times(1)).convert(indexerContext);
        verify(restTemplate, times(0)).postForEntity(any(), any(), any());
    }

    @Test
    public void testSendMigrationStatusException() {
        thrown.expect(FusionIntegrationException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE_MIGRATION);

        when(restTemplate.postForEntity(any(String.class), any(), any())).thenThrow(ResourceAccessException.class);

        distFusionExportService.sendMigrationStatus(indexerContext);
    }

    @Test
    public void testSendMigrationStatusDirectDTO() {
        when(configuration.getString(MIGRATION_STATUS_INDEX_PROFILE)).thenReturn(MIGRATION_STATUS_INDEX_PROFILE_VALUE);

        distFusionExportService.sendMigrationStatus(migrationStatusDTO);

        String expectedUrl = FUSION_INDEX_URL_VALUE + DistConstants.Punctuation.FORWARD_SLASH + MIGRATION_STATUS_INDEX_PROFILE_VALUE;
        HttpEntity expectedRequest = createExpectedRequest(migrationStatusDTO);
        verify(restTemplate, times(1)).postForEntity(eq(expectedUrl),
                                                     refEq(expectedRequest),
                                                     eq(String.class));
    }

    @Test
    public void testSendMigrationStatusDirectDTOIsSimulation() {
        when(configuration.getBoolean(SIMULATE_UPDATE, FALSE)).thenReturn(TRUE);

        distFusionExportService.sendMigrationStatus(migrationStatusDTO);

        verify(restTemplate, times(0)).postForEntity(any(), any(), any());
    }

    @Test
    public void testSendMigrationStatusDirectDTOException() {
        thrown.expect(FusionIntegrationException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE_MIGRATION);

        when(restTemplate.postForEntity(any(String.class), any(), any())).thenThrow(ResourceAccessException.class);

        distFusionExportService.sendMigrationStatus(migrationStatusDTO);
    }


    private HttpEntity createExpectedRequest(Object request) {
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add(HEADER_API_KEY, API_KEY_INDEX_VALUE);
        return new HttpEntity<>(request, expectedHeaders);
    }

    private HttpEntity createExpectedDeleteRequest(Object request) {
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add(HEADER_API_KEY, API_KEY_DELETE_VALUE);
        return new HttpEntity<>(request, expectedHeaders);
    }

}
