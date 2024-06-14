package com.distrelec.fusionsearch.response;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;

interface EnhanceSearchQueryStrategy {

    SearchQueryData enhanceSearchQuery(SearchResponseTuple searchResponseTuple);
}
