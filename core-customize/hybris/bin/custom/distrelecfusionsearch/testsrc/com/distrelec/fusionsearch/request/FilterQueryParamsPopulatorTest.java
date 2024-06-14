package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.FQ_PARAM;
import static com.distrelec.fusionsearch.request.FilterQueryParamsPopulator.FQ_TEMPLATE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilterValue;
import de.factfinder.webservice.ws71.FFsearch.Filter;
import de.factfinder.webservice.ws71.FFsearch.FilterValue;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class FilterQueryParamsPopulatorTest extends AbstractParamsPopulatorTest {

    @InjectMocks
    FilterQueryParamsPopulator filterQueryParamsPopulator;

    @Mock
    Params searchParams;

    @Mock
    SearchRequest searchRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        params = new HashSetValuedHashMap<>();

        when(searchRequestTuple.getSearchRequest()).thenReturn(searchRequest);
        when(searchRequest.getSearchParams()).thenReturn(searchParams);
    }

    @Test
    public void testNotPopulateFilterQueriesIfNullArrayOfFilter() {
        filterQueryParamsPopulator.populate(searchRequestTuple, params);
    }

    @Test
    public void testPopulateFilterQuery() {
        String filterName = "filterName";
        String filterValue = "filterValue";
        String filterQuery = String.format(FQ_TEMPLATE, filterName, filterValue);

        ArrayOfFilter arrayOfFilter = mock(ArrayOfFilter.class);
        Filter filter = mockFilter(filterName, filterValue);

        when(searchRequest.getSearchType()).thenReturn(DistSearchType.CATEGORY);
        when(searchParams.getFilters()).thenReturn(arrayOfFilter);
        when(arrayOfFilter.getFilter()).thenReturn(List.of(filter));

        filterQueryParamsPopulator.populate(searchRequestTuple, params);

        Collection<String> values = params.get(FQ_PARAM);
        assertEquals(1, values.size());
        assertEquals(filterQuery, values.iterator().next());
    }

    @Test
    public void testPopulateCategoryFilterQuery() {
        String filter1Name = "categoryCodePathROOT";
        String filter1Value = "cat-L2-3D_525341";
        String filter2Name = "categoryCodePathROOT/cat-L2-3D_525341";
        String filter2Value = "cat-L3D_525297";
        String filterQuery = "categoryCodes:(\"cat-L3D_525297\")";

        ArrayOfFilter arrayOfFilter = mock(ArrayOfFilter.class);
        Filter filter1 = mockFilter(filter1Name, filter1Value);
        Filter filter2 = mockFilter(filter2Name, filter2Value);

        when(searchRequest.getSearchType()).thenReturn(DistSearchType.CATEGORY);
        when(searchParams.getFilters()).thenReturn(arrayOfFilter);
        when(arrayOfFilter.getFilter()).thenReturn(List.of(filter1, filter2));

        filterQueryParamsPopulator.populate(searchRequestTuple, params);

        Collection<String> values = params.get(FQ_PARAM);
        assertEquals(1, values.size());
        assertEquals(filterQuery, values.iterator().next());
    }

    private Filter mockFilter(String filterName, String filterValue) {
        Filter filter = mock(Filter.class);
        ArrayOfFilterValue valueList = mock(ArrayOfFilterValue.class);
        FilterValue fv = mock(FilterValue.class);

        when(filter.getName()).thenReturn(filterName);
        when(filter.getValueList()).thenReturn(valueList);
        when(valueList.getFilterValue()).thenReturn(List.of(fv));
        when(fv.getValue()).thenReturn(filterValue);

        return filter;
    }
}
