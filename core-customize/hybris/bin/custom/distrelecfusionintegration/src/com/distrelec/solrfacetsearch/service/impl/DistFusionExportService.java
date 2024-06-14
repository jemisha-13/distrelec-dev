package com.distrelec.solrfacetsearch.service.impl;

import static java.lang.Boolean.FALSE;

import java.util.Collection;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.distrelec.fusion.integration.dto.DeleteRequestDTO;
import com.distrelec.fusion.integration.dto.FusionDocumentRequestDTO;
import com.distrelec.fusion.integration.dto.MigrationStatusRequestDTO;
import com.distrelec.solrfacetsearch.exception.FusionIntegrationException;
import com.distrelec.solrfacetsearch.service.FusionExportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.constants.DistConstants;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;

public class DistFusionExportService implements FusionExportService {

    private static final Logger LOG = LoggerFactory.getLogger(DistFusionExportService.class);

    public static final String HEADER_API_KEY = "x-api-key";

    private static final String API_KEY_INDEX = "distrelecfusionintegration.fusion.api.key.index";

    private static final String API_KEY_DELETE = "distrelecfusionintegration.fusion.api.key.delete";

    private static final String FUSION_INDEX_URL = "distrelecfusionintegration.fusion.index.url";

    private static final String SIMULATE_UPDATE = "distrelecfusionintegration.fusion.simulateupdate";

    private static final String MIGRATION_STATUS_INDEX_PROFILE = "distrelecfusionintegration.fusion.index.profile.migration.status";

    private static final String DELETE_PROFILE_PREFIX = "delete_";

    private static final String DELETE_REQUEST_QUERY_PARAM = "?echo=false&stats=false";

    private static final ObjectMapper mapper = new ObjectMapper();

    private RestTemplate restTemplate;

    private ConfigurationService configurationService;

    private Converter<Object, FusionDocumentRequestDTO> fusionDocumentRequestDTOConverter;

    private Converter<String, DeleteRequestDTO> deleteRequestDTOConverter;

    private Converter<IndexerContext, MigrationStatusRequestDTO> migrationStatusRequestDTOConverter;

    @Override
    public void pushDocumentsToCollection(Collection<SolrInputDocument> solrDocuments, String collectionName) {
        List<FusionDocumentRequestDTO> fusionDTOs = fusionDocumentRequestDTOConverter.convertAll(solrDocuments);
        logPayload(fusionDTOs);
        if (isSimulation()) {
            return;
        }

        long t0 = System.currentTimeMillis();
        try {
            HttpEntity<Collection<FusionDocumentRequestDTO>> request = new HttpEntity<>(fusionDTOs, createIndexHttpHeader());
            restTemplate.postForEntity(createURL(collectionName),
                                       request,
                                       FusionDocumentRequestDTO.class);
        } catch (Exception e) {
            LOG.error("Error sending indexing request to Fusion!", e);
            throw new FusionIntegrationException("Error sending indexing request to Fusion!", e);
        } finally {
            LOG.info("Submitting {} documents to fusion took: {} ms", solrDocuments.size(), System.currentTimeMillis() - t0);
        }
    }

    @Override
    public void deleteDocumentsByQuery(String indexName, String deleteQuery) {
        DeleteRequestDTO deleteRequestDTO = deleteRequestDTOConverter.convert(deleteQuery);
        logPayload(deleteRequestDTO);

        if (isSimulation()) {
            return;
        }

        LOG.info("Deleting documents on index {} with query:{}", indexName, deleteQuery);

        try {
            HttpEntity<DeleteRequestDTO> request = new HttpEntity<>(deleteRequestDTO, createDeleteHttpHeader());
            restTemplate.postForEntity(createDeleteURL(indexName),
                                       request,
                                       String.class);
        } catch (Exception e) {
            LOG.error("Error sending delete request to Fusion!", e);
            throw new FusionIntegrationException("Error sending delete request to Fusion!", e);
        }
    }

    @Override
    public void sendMigrationStatus(IndexerContext indexerContext) {
        MigrationStatusRequestDTO migrationStatusRequestDTO = migrationStatusRequestDTOConverter.convert(indexerContext);
        sendMigrationStatus(migrationStatusRequestDTO);
    }

    @Override
    public void sendMigrationStatus(MigrationStatusRequestDTO migrationStatusRequestDTO) {
        logPayload(migrationStatusRequestDTO);
        if (isSimulation()) {
            return;
        }

        long t0 = System.currentTimeMillis();
        try {
            HttpEntity<MigrationStatusRequestDTO> request = new HttpEntity<>(migrationStatusRequestDTO, createIndexHttpHeader());
            restTemplate.postForEntity(createURL(getMigrationStatusIndexProfile()),
                                       request,
                                       String.class);
        } catch (Exception e) {
            LOG.error("Error sending migration status request to Fusion!", e);
            throw new FusionIntegrationException("Error sending migration status request to Fusion!", e);
        } finally {
            LOG.info("Sending migration status to fusion took: {} ms", System.currentTimeMillis() - t0);
        }
    }

    private void logPayload(Object fusionDTOs) {
        if (LOG.isDebugEnabled()) {
            try {
                String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fusionDTOs);
                LOG.debug("JSON-payload is:\n {}", jsonInString);
            } catch (JsonProcessingException e) {
                LOG.error("Error deserializing JSON Object", e);
            }
        }
    }

    private boolean isSimulation() {
        return configurationService.getConfiguration().getBoolean(SIMULATE_UPDATE, FALSE);
    }

    private HttpHeaders createIndexHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_API_KEY, configurationService.getConfiguration().getString(API_KEY_INDEX));
        return headers;
    }

    private HttpHeaders createDeleteHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_API_KEY, configurationService.getConfiguration().getString(API_KEY_DELETE));
        return headers;
    }

    private String createURL(String collection) {
        return configurationService.getConfiguration().getString(FUSION_INDEX_URL)
               + DistConstants.Punctuation.FORWARD_SLASH
               + collection;
    }

    private String createDeleteURL(String indexName) {
        return configurationService.getConfiguration().getString(FUSION_INDEX_URL)
               + DistConstants.Punctuation.FORWARD_SLASH
               + DELETE_PROFILE_PREFIX + indexName
               + DELETE_REQUEST_QUERY_PARAM;
    }

    private String getMigrationStatusIndexProfile() {
        return configurationService.getConfiguration().getString(MIGRATION_STATUS_INDEX_PROFILE);
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setFusionDocumentRequestDTOConverter(Converter<Object, FusionDocumentRequestDTO> fusionDocumentRequestDTOConverter) {
        this.fusionDocumentRequestDTOConverter = fusionDocumentRequestDTOConverter;
    }

    public void setDeleteRequestDTOConverter(Converter<String, DeleteRequestDTO> deleteRequestDTOConverter) {
        this.deleteRequestDTOConverter = deleteRequestDTOConverter;
    }

    public void setMigrationStatusRequestDTOConverter(Converter<IndexerContext, MigrationStatusRequestDTO> migrationStatusRequestDTOConverter) {
        this.migrationStatusRequestDTOConverter = migrationStatusRequestDTOConverter;
    }
}
