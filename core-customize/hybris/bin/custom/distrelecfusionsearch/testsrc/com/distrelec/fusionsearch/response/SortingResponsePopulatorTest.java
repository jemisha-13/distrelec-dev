package com.distrelec.fusionsearch.response;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.RELEVANCE_SORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.namics.hybris.ffsearch.data.paging.FactFinderSortData;
import com.namics.hybris.ffsearch.data.paging.SortType;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

@UnitTest
public class SortingResponsePopulatorTest extends AbstractSearchResponsePopulatorTest {

    @InjectMocks
    SortingResponsePopulator sortingResponsePopulator;

    @Mock
    PageableData pageableData;

    @Before
    public void setUp() {
        when(searchResponseTuple.getPageableData()).thenReturn(pageableData);
    }

    @Test
    public void testPopulateBestMatchSortForEmptyString() {
        testPopulateBestMatchForSort("");
    }

    @Test
    public void testPopulateBestMatchSortForNullString() {
        testPopulateBestMatchForSort(null);
    }

    private void testPopulateBestMatchForSort(String sort) {
        when(pageableData.getSort()).thenReturn(sort);

        sortingResponsePopulator.populate(searchResponseTuple, searchPageData);

        List<FactFinderSortData> sorting = searchPageData.getSorting();
        assertEquals(5, sorting.size());

        FactFinderSortData relevance = sorting.get(0);
        assertEquals(RELEVANCE_SORT, relevance.getCode());
        assertEquals(RELEVANCE_SORT, relevance.getName());
        assertTrue(relevance.isRelevanceSort());
        assertTrue(relevance.isSelected());
        assertEquals(SortType.DSC, relevance.getSortType());
    }
}
