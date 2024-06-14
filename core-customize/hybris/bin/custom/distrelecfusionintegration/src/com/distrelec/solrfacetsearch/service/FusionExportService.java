package com.distrelec.solrfacetsearch.service;

import java.util.Collection;

import org.apache.solr.common.SolrInputDocument;

import com.distrelec.fusion.integration.dto.MigrationStatusRequestDTO;

import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;

public interface FusionExportService {
    void pushDocumentsToCollection(Collection<SolrInputDocument> solrDocuments, String indexName);

    void sendMigrationStatus(IndexerContext migrationStatus);

    void sendMigrationStatus(MigrationStatusRequestDTO migrationStatusRequestDTO);

    void deleteDocumentsByQuery(String indexName, String query);

}
