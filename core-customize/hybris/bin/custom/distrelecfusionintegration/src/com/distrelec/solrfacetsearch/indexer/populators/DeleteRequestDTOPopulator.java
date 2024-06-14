package com.distrelec.solrfacetsearch.indexer.populators;

import com.distrelec.fusion.integration.dto.DeleteRequestDTO;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DeleteRequestDTOPopulator implements Populator<String, DeleteRequestDTO> {

    @Override
    public void populate(String query, DeleteRequestDTO deleteRequestDTO) throws ConversionException {
        deleteRequestDTO.setQuery(query);
    }

}
