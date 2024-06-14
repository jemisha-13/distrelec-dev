package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.MODE_PARAM;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.QUERY_PARAM;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.PRODUCT_FAMILY_CODE;

import org.apache.commons.collections4.MultiValuedMap;

import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class SearchQueryParamsPopulator implements Populator<SearchRequestTuple, MultiValuedMap<String, String>> {

    public static final String NEW_MODE = "new";

    static final String PRODUCT_FAMILY_MODE = "family";

    static final String OFFER_MODE = "offer";

    @Override
    public void populate(SearchRequestTuple searchRequestTuple, MultiValuedMap<String, String> params) throws ConversionException {
        SearchQueryData searchQuery = searchRequestTuple.getSearchQueryData();

        switch (searchQuery.getSearchType()) {
            case MANUFACTURER -> {
                params.put(QUERY_PARAM, "*");
                params.put(MODE_PARAM, DistSearchType.MANUFACTURER.name().toLowerCase());
            }
            case CATEGORY -> {
                params.put(QUERY_PARAM, "*");
                if (isProductFamilySearch(searchRequestTuple.getSearchRequest().getSearchParams().getFilters())) {
                    params.put(MODE_PARAM, PRODUCT_FAMILY_MODE);
                } else {
                    params.put(MODE_PARAM, DistSearchType.CATEGORY.name().toLowerCase());
                }
            }
            case OUTLET -> {
                params.put(QUERY_PARAM, "*");
                params.put(MODE_PARAM, OFFER_MODE);
            }
            case NEW -> {
                params.put(QUERY_PARAM, "*");
                params.put(MODE_PARAM, NEW_MODE);
            }
            default -> params.put(QUERY_PARAM, searchQuery.getFreeTextSearch());
        }
    }

    private boolean isProductFamilySearch(ArrayOfFilter arrayOfFilter) {
        return arrayOfFilter.getFilter().stream()
                            .anyMatch(filter -> filter.getName().equals(PRODUCT_FAMILY_CODE));
    }
}
