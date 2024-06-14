package com.namics.hybris.ffsearch.populator.response;

import java.util.*;

import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;
import de.factfinder.webservice.ws71.FFsearch.SortMethods;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderSortData;
import com.namics.hybris.ffsearch.data.paging.SortType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import com.namics.hybris.ffsearch.populator.common.SortCodeTranslator;

import de.factfinder.webservice.ws71.FFsearch.SortItem;
import de.hybris.platform.converters.Populator;

public class ResponsePageDataSortPopulator<STATE, RESULT> implements Populator<SearchResponse, FactFinderProductSearchPageData<SearchQueryData, RESULT>> {

    private Populator<SearchResponse, SearchQueryData> responseQueryDataSortPopulator;

    @Override
    public void populate(final SearchResponse source, final FactFinderProductSearchPageData<SearchQueryData, RESULT> target) {
        final List<FactFinderSortData> sortData = populateSortData(source);
        target.setSorting(sortData);
        getResponseQueryDataSortPopulator().populate(source, target.getCurrentQuery());
        target.getPagination().setSort(target.getCurrentQuery().getSort());
    }

    protected List<FactFinderSortData> populateSortData(final SearchResponse source) {
        if ((source.getSearchResult().getSortsList() == null) || (CollectionUtils.isEmpty(source.getSearchResult().getSortsList().getSortItem()))) {
            return Collections.emptyList();
        }
        List<FactFinderSortData> sortData = getSortData(source.getSearchResult().getSortsList().getSortItem(), source.getSearchRequest());
        sortData.sort(Comparator.comparing(FactFinderSortData::isRelevanceSort).reversed());
        return sortData;
    }

    private List<FactFinderSortData> getSortData(List<SortItem> sortItems, SearchRequest searchRequest) {
        List<FactFinderSortData> sortData = new ArrayList<>();
        for(SortItem item : sortItems) {
            if (isItemBestsellerInAscendingOrder(item)) {
                continue;
            }
            sortData.add(createFactFinderSortData(searchRequest, item));
        }
        return sortData;
    }

    private boolean isItemBestsellerInAscendingOrder(SortItem item) {
        return DistFactFinderExportColumns.BESTSELLER.getValue().equals(item.getName()) && SortMethods.ASC.equals(item.getOrder());
    }

    private FactFinderSortData createFactFinderSortData(SearchRequest searchRequest, SortItem item) {
        final FactFinderSortData sortData = new FactFinderSortData();
        String sortCode = item.isRelevanceSortItem() ? "" : SortCodeTranslator.getSortCode(item.getName(), searchRequest);
        String sortName = item.isRelevanceSortItem() ? "" : item.getName();
        sortData.setCode(sortCode);
        sortData.setName(sortName);
        sortData.setSelected(SortCodeTranslator.isSelectedItem(item, searchRequest));
        sortData.setRelevanceSort(item.isRelevanceSortItem());
        sortData.setSortType(SortType.fromValue(item.getOrder().value()));
        return sortData;
    }

    protected Populator<SearchResponse, SearchQueryData> getResponseQueryDataSortPopulator() {
        return responseQueryDataSortPopulator;
    }

    @Required
    public void setResponseQueryDataSortPopulator(final Populator<SearchResponse, SearchQueryData> responseQueryDataSortPopulator) {
        this.responseQueryDataSortPopulator = responseQueryDataSortPopulator;
    }
}
