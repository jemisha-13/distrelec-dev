package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;

public interface RedirectRuleToSearchPageDataConverter {

    FactFinderProductSearchPageData<SearchStateData, ProductData> convert(SearchQueryData searchQuery, SearchRedirectRule redirectRule);
}
