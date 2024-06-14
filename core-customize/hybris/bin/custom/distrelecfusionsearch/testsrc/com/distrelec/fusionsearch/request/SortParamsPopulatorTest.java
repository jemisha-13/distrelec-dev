package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.RELEVANCE_SORT;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.SORT_PARAM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

@UnitTest
public class SortParamsPopulatorTest extends AbstractParamsPopulatorTest {

    @InjectMocks
    SortParamsPopulator sortParamsPopulator;

    @Mock
    PageableData pageableData;

    @Before
    public void setUp() {
        when(searchRequestTuple.getPageableData()).thenReturn(pageableData);
    }

    @Test
    public void testPopulateRelevanceSort() {
        assertConvertSort(RELEVANCE_SORT, RELEVANCE_SORT);
    }

    @Test
    public void testPopulateNullSortAsRelevanceSort() {
        assertConvertSort(null, RELEVANCE_SORT);
    }

    @Test
    public void testConvertManufacturerAscSort() {
        assertConvertSort("Manufacturer:asc", "manufacturerName_s asc");
    }

    @Test
    public void testConvertManufacturerDescSort() {
        assertConvertSort("Manufacturer:desc", "manufacturerName_s desc");
    }

    @Test
    public void testConvertPriceAscSort() {
        assertConvertSort("Price:asc", "singleMinPriceGross_d asc");
    }

    @Test
    public void testConvertPriceDescSort() {
        assertConvertSort("Price:desc", "singleMinPriceGross_d desc");
    }

    private void assertConvertSort(String inputSort, String expectedConvertedSort) {
        String sort = "Manufacturer:asc";
        when(pageableData.getSort()).thenReturn(inputSort);
        sortParamsPopulator.populate(searchRequestTuple, params);
        assertContainsParam(SORT_PARAM, expectedConvertedSort);
    }

    private void assertContainsParam(String paramName, String paramValue) {
        Collection<String> values = params.get(paramName);
        assertEquals(1, values.size());
        assertEquals(paramValue, values.iterator().next());
    }
}
