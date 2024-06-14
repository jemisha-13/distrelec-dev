package com.distrelec.fusionsearch.response;

import static com.distrelec.fusionsearch.response.QueryFilterUtil.shouldRedirect;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.distrelec.fusion.search.dto.FusionDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;

class KeywordRedirectUrlResponsePopulator implements Populator<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    @Override
    public void populate(SearchResponseTuple searchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) {
        SearchResponseDTO searchResponseDTO = searchResponseTuple.getSearchResponseDTO();
        FusionDTO fusionDTO = searchResponseDTO.getFusion();
        if (shouldRedirect(fusionDTO)) {
            List<String> redirects = fusionDTO.getRedirect();
            searchPageData.setKeywordRedirectUrl(redirects.get(0));
        }
    }
}
