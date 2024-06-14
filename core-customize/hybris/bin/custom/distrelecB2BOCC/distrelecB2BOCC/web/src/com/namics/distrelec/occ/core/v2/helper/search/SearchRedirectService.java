package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

public interface SearchRedirectService {

    /**
     * Returns a search redirect rule if a search query should be redirected or it is punched out, or null if it
     * is valid.
     */
    SearchRedirectRule shouldRedirect(SearchQueryData searchQuery);

    /**
     * Returns true if this service supports a search type.
     */
    boolean supportsSearchType(DistSearchType searchType);
}
