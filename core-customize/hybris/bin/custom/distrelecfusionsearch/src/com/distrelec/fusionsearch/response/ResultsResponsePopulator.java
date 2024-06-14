package com.distrelec.fusionsearch.response;

import static com.distrelec.fusionsearch.response.QueryFilterUtil.shouldRedirect;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.distrelec.fusion.search.dto.FusionDTO;
import com.distrelec.fusion.search.dto.ResponseDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

class ResultsResponsePopulator implements Populator<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    private Converter<Map<String, Object>, SearchResultValueData> docToSearchResultConverter;

    @Override
    public void populate(SearchResponseTuple searchResponseTuple,
                         FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) throws ConversionException {
        SearchResponseDTO searchResponseDTO = searchResponseTuple.getSearchResponseDTO();
        FusionDTO fusionDTO = searchResponseDTO.getFusion();
        boolean shouldRedirect = shouldRedirect(fusionDTO);
        if (shouldRedirect) {
            // do not convert docs if redirect
            return;
        }

        ResponseDTO responseDTO = searchResponseDTO.getResponse();
        List<Map<String, Object>> docs = responseDTO.getDocs();
        if (CollectionUtils.isNotEmpty(docs)) {
            List<SearchResultValueData> results = docToSearchResultConverter.convertAll(docs);
            searchPageData.setResults(results);
        }
    }

    public void setDocToSearchResultConverter(Converter<Map<String, Object>, SearchResultValueData> docToSearchResultConverter) {
        this.docToSearchResultConverter = docToSearchResultConverter;
    }
}
