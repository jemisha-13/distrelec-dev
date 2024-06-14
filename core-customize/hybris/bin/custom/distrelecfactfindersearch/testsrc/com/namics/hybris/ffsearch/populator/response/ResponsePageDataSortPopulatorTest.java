package com.namics.hybris.ffsearch.populator.response;

import com.namics.hybris.ffsearch.data.paging.FactFinderSortData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import de.factfinder.webservice.ws71.FFsearch.*;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class ResponsePageDataSortPopulatorTest {

    private final ResponsePageDataSortPopulator populator = new ResponsePageDataSortPopulator();

    @Test
    public void populateSortData_EmptySortItemsListIsGiven_EmptyListReturned() {
        SearchResponse source = getSearchResponseWithEmptySortItems();

        List<FactFinderSortData> sortData =  populator.populateSortData(source);

        assertThat(sortData).isEmpty();
    }

    @Test
    public void populateSortData_SortItemsListIsGiven_AllSortOptionsReturned() {
        SearchResponse source = getSearchResponse();

        List<FactFinderSortData> sortData =  populator.populateSortData(source);

        assertThat(sortData).hasSize(5);
    }

    @Test
    public void populateSortData_SortItemsListIsGiven_RelevanceSortOptionIdFirst() {
        SearchResponse source = getSearchResponse();

        List<FactFinderSortData> sortData =  populator.populateSortData(source);

        assertThat(sortData.get(0).isRelevanceSort()).isTrue();
    }

    private SearchResponse getSearchResponseWithEmptySortItems() {
        SearchResponse source = new SearchResponse();
        Result result = new Result();
        source.setSearchResult(result);
        return source;
    }

    private SearchResponse getSearchResponse() {
        SearchResponse source = new SearchResponse();
        Result result = new Result();
        List<SortItem> sortItems = Arrays.asList(createSortItem(true, SortMethods.DESC, true, null),
                createSortItem(false, SortMethods.ASC, false, "Manufacturer"),
                createSortItem(false, SortMethods.DESC, false, "Manufacturer"),
                createSortItem(false, SortMethods.ASC, false, "Price"),
                createSortItem(false, SortMethods.DESC, false, "Price"));
        ArrayOfSortItem items = new ArrayOfSortItem();
        items.getSortItem().addAll(sortItems);
        result.setSortsList(items);
        source.setSearchResult(result);
        SearchRequest searchRequest = new SearchRequest();
        Params searchParams = new Params();
        searchRequest.setSearchParams(searchParams);
        source.setSearchRequest(searchRequest);
        return source;
    }

    private SortItem createSortItem(boolean relevanceSortItem, SortMethods order, boolean isSelected, String name) {
        SortItem item = new SortItem();
        item.setRelevanceSortItem(relevanceSortItem);
        item.setOrder(order);
        item.setSelected(isSelected);
        item.setName(name);
        return item;
    }
}