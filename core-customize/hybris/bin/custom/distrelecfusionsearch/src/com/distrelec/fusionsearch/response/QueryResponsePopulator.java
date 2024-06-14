package com.distrelec.fusionsearch.response;

import com.distrelec.fusion.search.dto.QueryDTO;
import com.distrelec.fusion.search.dto.ResponseDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class QueryResponsePopulator implements Populator<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    @Override
    public void populate(SearchResponseTuple searchResponseTuple,
                         FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) throws ConversionException {
        SearchResponseDTO searchResponseDTO = searchResponseTuple.getSearchResponseDTO();
        ResponseDTO responseDTO = searchResponseDTO.getResponse();
        QueryDTO queryDTO = responseDTO.getQuery();

        String freeTextSearch = queryDTO.getOriginalQuery();
        if (freeTextSearch == null) {
            freeTextSearch = queryDTO.getQ();
        }
        searchPageData.setFreeTextSearch(freeTextSearch);
    }
}
