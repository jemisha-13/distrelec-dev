package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.CATEGORY_CODES_FACET_CODE;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.FQ_PARAM;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_ROOT_PATH_PATTERN;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;

import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilterValue;
import de.factfinder.webservice.ws71.FFsearch.Filter;
import de.factfinder.webservice.ws71.FFsearch.FilterValue;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class FilterQueryParamsPopulator implements Populator<SearchRequestTuple, MultiValuedMap<String, String>> {

    static final String FQ_TEMPLATE = "%s:(\"%s\")";

    static final String FQ_MANUFACTURER = "manufacturerCode";

    static final String MANUFACTURER_FILTER_NAME = "Manufacturer";

    static final String PRODUCT_FAMILY_CODE = "productFamilyCode";

    static final String PRODUCT_STATUS = "productStatus";

    static final String OUTLET_PRODUCT_STATUS = "OFFER";

    static final String NEW_STATUS = "NEW";

    static final String PROMOTION_LABELS = "PromotionLabels";

    static final String CATEGORY_CODES = "categoryCodes";

    @Override
    public void populate(SearchRequestTuple searchRequestTuple, MultiValuedMap<String, String> params) throws ConversionException {
        SearchRequest searchRequest = searchRequestTuple.getSearchRequest();
        Params searchParams = searchRequest.getSearchParams();
        ArrayOfFilter arrayOfFilter = searchParams.getFilters();
        if (arrayOfFilter != null) {
            populateFilters(searchRequest, arrayOfFilter, params);
        }
    }

    private void populateFilters(SearchRequest searchRequest, ArrayOfFilter arrayOfFilter, MultiValuedMap<String, String> params) {
        switch (searchRequest.getSearchType()) {
            case MANUFACTURER -> populateManufacturerFilters(searchRequest.getCode(), arrayOfFilter, params);
            case CATEGORY, CATEGORY_AND_TEXT, TEXT -> {
                if (isProductFamilySearch(arrayOfFilter)) {
                    populateProductFamilyFilters(arrayOfFilter, params);
                } else {
                    List<Filter> filters = arrayOfFilter.getFilter();
                    populateCategoryFilters(filters, params);
                }
            }
            case OUTLET -> {
                populateOutletFilters(arrayOfFilter, params);
            }
            case NEW -> {
                populateNewFilters(arrayOfFilter, params);
            }
        }
    }

    private void populateNewFilters(ArrayOfFilter arrayOfFilter, MultiValuedMap<String, String> params) {
        params.put(FQ_PARAM, formatFQParam(PRODUCT_STATUS, NEW_STATUS));
        List<Filter> filteredFilters = arrayOfFilter.getFilter().stream()
                                                    .filter(filter -> !PROMOTION_LABELS.equals(filter.getName()))
                                                    .collect(Collectors.toList());
        populateCategoryFilters(filteredFilters, params);
    }

    private void populateOutletFilters(ArrayOfFilter arrayOfFilter, MultiValuedMap<String, String> params) {
        params.put(FQ_PARAM, formatFQParam(PRODUCT_STATUS, OUTLET_PRODUCT_STATUS));
        List<Filter> filteredFilters = arrayOfFilter.getFilter().stream()
                                                    .filter(filter -> !PROMOTION_LABELS.equals(filter.getName()))
                                                    .collect(Collectors.toList());
        populateCategoryFilters(filteredFilters, params);
    }

    private boolean isProductFamilySearch(ArrayOfFilter arrayOfFilter) {
        return arrayOfFilter.getFilter().stream()
                            .anyMatch(filter -> filter.getName().equals(PRODUCT_FAMILY_CODE));
    }

    private void populateManufacturerFilters(String manufacturerCode, ArrayOfFilter arrayOfFilter, MultiValuedMap<String, String> params) {
        for (Filter filter : arrayOfFilter.getFilter()) {
            String filterName = filter.getName();
            if (filterName.equals(MANUFACTURER_FILTER_NAME)) {
                params.put(FQ_PARAM, formatFQParam(FQ_MANUFACTURER, manufacturerCode));
            } else {
                populateFilter(params, filter, filterName);
            }
        }
    }

    private void populateCategoryFilters(List<Filter> filters, MultiValuedMap<String, String> params) {
        int categoryLevel = 0;
        Filter categoryCodeFilter = null;
        for (Filter filter : filters) {
            String filterName = filter.getName();
            if (CATEGORY_CODE_ROOT_PATH_PATTERN.matcher(filterName).matches()) {
                // populate category filter
                int currentLevel = StringUtils.countMatches(filterName, '/');
                if (currentLevel >= categoryLevel) {
                    categoryLevel = currentLevel;
                    categoryCodeFilter = filter;
                }
            } else {
                populateFilter(params, filter, filterName);
            }
        }

        if (categoryCodeFilter != null) {
            populateFilter(params, categoryCodeFilter, CATEGORY_CODES_FACET_CODE);
        }
    }

    private void populateProductFamilyFilters(ArrayOfFilter arrayOfFilter, MultiValuedMap<String, String> params) {
        for (Filter filter : arrayOfFilter.getFilter()) {
            String filterName = filter.getName();
            if (!CATEGORY_CODE_ROOT_PATH_PATTERN.matcher(filterName).matches()) {
                populateFilter(params, filter, filterName);
            }
        }
    }

    private void populateFilter(MultiValuedMap<String, String> params, Filter filter, String filterName) {
        ArrayOfFilterValue valueList = filter.getValueList();
        String filterQueryString = valueList.getFilterValue()
                                            .stream()
                                            .map(FilterValue::getValue)
                                            .filter(StringUtils::isNotBlank)
                                            .collect(Collectors.joining("\"OR\""));

        String filterQuery = formatFQParam(getFilterName(filterName), filterQueryString);
        params.put(FQ_PARAM, filterQuery);
    }

    private String formatFQParam(String filterName, String filterValue) {
        String fqParam = String.format(FQ_TEMPLATE, filterName, filterValue);
        return fqParam;
    }

    private String getFilterName(String filterName) {
        if (CATEGORY_CODE_ROOT_PATH_PATTERN.matcher(filterName).matches()) {
            return CATEGORY_CODES;
        }
        return filterName;
    }
}
