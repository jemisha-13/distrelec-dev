package com.distrelec.fusionsearch.response;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.MANUFACTURER_SORT;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.PRICE_SORT;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.RELEVANCE_SORT;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderSortData;
import com.namics.hybris.ffsearch.data.paging.SortType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class SortingResponsePopulator implements Populator<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    @Override
    public void populate(SearchResponseTuple searchResponseTuple,
                         FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) throws ConversionException {
        PageableData pageableData = searchResponseTuple.getPageableData();
        String actualSort = StringUtils.trimToEmpty(pageableData.getSort());

        List<FactFinderSortData> sorting = new ArrayList<>();
        sorting.add(createSortData(StringUtils.EMPTY, actualSort, true, SortType.DSC));
        sorting.add(createSortData(MANUFACTURER_SORT, actualSort, false, SortType.ASC));
        sorting.add(createSortData(MANUFACTURER_SORT, actualSort, false, SortType.DSC));
        sorting.add(createSortData(PRICE_SORT, actualSort, false, SortType.ASC));
        sorting.add(createSortData(PRICE_SORT, actualSort, false, SortType.DSC));

        searchPageData.setSorting(sorting);
    }

    private FactFinderSortData createSortData(String code, String actualSort, boolean relevanceSort, SortType sortType) {
        String matchingSort;
        if (RELEVANCE_SORT.equals(code)) {
            matchingSort = code;
        } else {
            matchingSort = code + ":" + sortType.getValue();
        }

        FactFinderSortData sortData = new FactFinderSortData();
        sortData.setCode(code);
        sortData.setName(code);
        sortData.setSelected(matchingSort.equals(actualSort));
        sortData.setRelevanceSort(relevanceSort);
        sortData.setSortType(sortType);
        return sortData;
    }
}
