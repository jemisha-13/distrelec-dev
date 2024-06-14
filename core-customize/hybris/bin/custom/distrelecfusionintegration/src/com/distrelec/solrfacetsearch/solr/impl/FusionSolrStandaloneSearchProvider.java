package com.distrelec.solrfacetsearch.solr.impl;

import com.distrelec.solrfacetsearch.indexer.strategies.IndexNameStrategy;
import com.distrelec.solrfacetsearch.service.FusionExportService;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrStandaloneSearchProvider;

public class FusionSolrStandaloneSearchProvider extends SolrStandaloneSearchProvider {

    private FusionExportService fusionExportService;

    private IndexNameStrategy indexNameStrategy;

    public FusionSolrStandaloneSearchProvider(final FusionExportService fusionExportService, final IndexNameStrategy indexNameStrategy) {
        this.fusionExportService = fusionExportService;
        this.indexNameStrategy = indexNameStrategy;
    }

    @Override
    public void createIndex(Index index) throws SolrServiceException {
        // do not create an index
    }

    @Override
    public void deleteIndex(Index index) throws SolrServiceException {
        // do not delete the index
    }

    @Override
    public void exportConfig(Index index) throws SolrServiceException {
        // do not exportConfig the index
    }

    @Override
    public void deleteOldDocuments(Index index, long indexOperationId) {
        String indexName = indexNameStrategy.createIndexName(index.getFacetSearchConfig(), index.getIndexedType());
        String query = "-(indexOperationId:[" + indexOperationId + " TO *])";

        CMSSiteModel cmsSite = index.getFacetSearchConfig().getIndexConfig().getCmsSite();
        if (cmsSite != null && cmsSite.getCountry() != null) {
            query = "(" + query + " AND country:" + cmsSite.getCountry().getIsocode() + ")";
        }

        fusionExportService.deleteDocumentsByQuery(indexName, query);
    }

}
