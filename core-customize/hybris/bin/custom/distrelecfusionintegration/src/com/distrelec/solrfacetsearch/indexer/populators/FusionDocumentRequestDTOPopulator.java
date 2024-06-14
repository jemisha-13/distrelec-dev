package com.distrelec.solrfacetsearch.indexer.populators;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;

import com.distrelec.fusion.integration.dto.FusionDocumentRequestDTO;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class FusionDocumentRequestDTOPopulator implements Populator<SolrInputDocument, FusionDocumentRequestDTO> {

    @Override
    public void populate(SolrInputDocument solrDocument, FusionDocumentRequestDTO fusionDocumentRequestDTO) throws ConversionException {
        fusionDocumentRequestDTO.setFields(createFusionDocumentFields(solrDocument));
    }

    private Map<String, Object> createFusionDocumentFields(SolrInputDocument solrDocument) {
        Map<String, Object> fields = new HashMap<>();
        for (String fieldName : solrDocument.getFieldNames()) {
            Object fieldValue = solrDocument.getField(fieldName).getValue();
            if (fieldValue != null) {
                fields.put(fieldName, fieldValue);
            }
        }
        return fields;
    }

}
