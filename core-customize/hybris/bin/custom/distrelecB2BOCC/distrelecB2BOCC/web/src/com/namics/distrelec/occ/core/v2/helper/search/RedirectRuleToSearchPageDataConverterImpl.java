package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import org.springframework.stereotype.Component;

@Component
class RedirectRuleToSearchPageDataConverterImpl implements RedirectRuleToSearchPageDataConverter {

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ProductData> convert(SearchQueryData searchQuery, SearchRedirectRule redirectRule) {
        FactFinderProductSearchPageData searchPageData = new FactFinderProductSearchPageData<>();
        searchPageData.setCode(searchQuery.getCode());
        searchPageData.setSearchType(searchQuery.getSearchType());
        searchPageData.setFreeTextSearch(searchQuery.getFreeTextSearch());
        searchPageData.setKeywordRedirectUrl(redirectRule.getRedirectUrl());

        switch (redirectRule.getStatus()) {
            case NOT_FOUND:
                searchPageData.setNotFound(true);
                break;
            case PUNCHED_OUT:
                searchPageData.setPunchedOut(true);
                break;
        }
        return searchPageData;
    }
}
