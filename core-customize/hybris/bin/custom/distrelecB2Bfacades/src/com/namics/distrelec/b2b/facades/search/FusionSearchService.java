package com.namics.distrelec.b2b.facades.search;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

public interface FusionSearchService<ITEM extends ProductData> {

    FactFinderProductSearchPageData<SearchStateData, ITEM> search(SearchQueryData searchQuery, PageableData pageableData);
}
