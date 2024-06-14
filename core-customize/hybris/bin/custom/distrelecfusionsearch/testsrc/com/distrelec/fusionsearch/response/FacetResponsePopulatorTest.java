package com.distrelec.fusionsearch.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.fusion.search.dto.FacetDTO;
import com.distrelec.fusion.search.dto.FacetValueDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetType;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.populator.response.helper.SearchQueryTransformer;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class FacetResponsePopulatorTest extends AbstractSearchResponsePopulatorTest {

    @InjectMocks
    FacetResponsePopulator facetResponsePopulator;

    @Mock
    SearchQueryTransformer queryTransformer;

    @Mock
    SearchQueryData searchQuery;

    @Mock
    SearchResponseDTO searchResponseDTO;

    @Before
    public void setUp() {
        when(searchResponseTuple.getSearchQueryData()).thenReturn(searchQuery);
        when(searchResponseTuple.getSearchResponseDTO()).thenReturn(searchResponseDTO);
    }

    @Test
    public void testPopulateFacets() {
        String facetCode = "facetCode";
        String facetName = "facetName";

        FacetDTO facetDTO = mockFacetDTO(facetCode);

        when(searchResponseDTO.getFacets()).thenReturn(List.of(facetDTO));
        when(facetDTO.getName()).thenReturn(facetName);

        facetResponsePopulator.populate(searchResponseTuple, searchPageData);

        List<FactFinderFacetData<SearchQueryData>> otherFacets = searchPageData.getOtherFacets();
        assertEquals(1, otherFacets.size());

        FactFinderFacetData<SearchQueryData> actualFacetData = otherFacets.get(0);
        assertEquals(facetCode, actualFacetData.getCode());
        assertEquals(facetName, actualFacetData.getName());
        assertTrue(actualFacetData.getIsViable());
        assertEquals(FactFinderFacetType.CHECKBOX, actualFacetData.getType());
    }

    @Test
    public void testConvertCategoryFacet() {
        String facetCode = "categoryCodes";

        FacetDTO facetDTO = mockFacetDTO(facetCode);

        when(searchResponseDTO.getFacets()).thenReturn(List.of(facetDTO));

        facetResponsePopulator.populate(searchResponseTuple, searchPageData);

        List<FactFinderFacetData<SearchQueryData>> otherFacets = searchPageData.getOtherFacets();
        assertEquals(1, otherFacets.size());

        FactFinderFacetData<SearchQueryData> actualFacetData = otherFacets.get(0);
        assertEquals(facetCode, actualFacetData.getCode());
        assertFalse(actualFacetData.getIsViable());
    }

    @Test
    public void testPopulateFacetValuesForNotSelected() {
        String facetCode = "facetCode";
        String facetValue = "facetValue";
        String facetValueName = "facetValueName";
        long count = 12321L;
        String queryFilter = "filter_" + facetCode + "=" + facetValue;

        FacetDTO facetDTO = mockFacetDTO(facetCode);
        FacetValueDTO facetValueDTO = mockFacetValueDTO(facetValue);
        SearchQueryData refinedQueryData = mock(SearchQueryData.class);
        SearchQueryTermData filterTerm = mock(SearchQueryTermData.class);

        when(searchResponseDTO.getFacets()).thenReturn(List.of(facetDTO));
        when(facetDTO.getValues()).thenReturn(List.of(facetValueDTO));
        when(queryTransformer.refineQueryAddFacet(searchQuery, facetCode, facetValue)).thenReturn(refinedQueryData);

        // facet value DTO
        when(facetValueDTO.getCount()).thenReturn(count);
        when(facetValueDTO.getName()).thenReturn(facetValueName);

        facetResponsePopulator.populate(searchResponseTuple, searchPageData);

        List<FactFinderFacetData<SearchQueryData>> otherFacets = searchPageData.getOtherFacets();
        assertEquals(1, otherFacets.size());

        FactFinderFacetData<SearchQueryData> actualFacetData = otherFacets.get(0);
        List<FactFinderFacetValueData<SearchQueryData>> actualValues = actualFacetData.getValues();
        assertEquals(1, actualValues.size());

        FactFinderFacetValueData<SearchQueryData> actualFacetValue = actualValues.get(0);
        assertEquals(facetValueName, actualFacetValue.getName());
        assertEquals(count, actualFacetValue.getCount());
        assertEquals(queryFilter, actualFacetValue.getQueryFilter());
        assertFalse(actualFacetValue.isSelected());
    }

    @Test
    public void testPopulateSelectedFacetValue() {
        String facetCode = "facetCode";
        String facetValue = "facetValue";

        FacetDTO facetDTO = mockFacetDTO(facetCode);
        FacetValueDTO facetValueDTO = mockFacetValueDTO(facetValue);

        SearchQueryTermData filterTerm = mock(SearchQueryTermData.class); // active search query term

        when(searchQuery.getFilterTerms()).thenReturn(List.of(filterTerm));
        when(filterTerm.getKey()).thenReturn(facetCode);
        when(filterTerm.getValue()).thenReturn(facetValue);

        when(searchResponseDTO.getFacets()).thenReturn(List.of(facetDTO));
        when(facetDTO.getValues()).thenReturn(List.of(facetValueDTO));

        facetResponsePopulator.populate(searchResponseTuple, searchPageData);

        List<FactFinderFacetData<SearchQueryData>> otherFacets = searchPageData.getOtherFacets();
        assertEquals(1, otherFacets.size());

        FactFinderFacetData<SearchQueryData> actualFacetData = otherFacets.get(0);
        List<FactFinderFacetValueData<SearchQueryData>> actualValues = actualFacetData.getValues();
        assertEquals(1, actualValues.size());

        FactFinderFacetValueData<SearchQueryData> actualFacetValue = actualValues.get(0);
        assertTrue(actualFacetValue.isSelected());
    }

    private FacetDTO mockFacetDTO(String facetCode) {
        FacetDTO facetDTO = mock(FacetDTO.class);
        when(facetDTO.getCode()).thenReturn(facetCode);
        return facetDTO;
    }

    private FacetValueDTO mockFacetValueDTO(String value) {
        FacetValueDTO facetValueDTO = mock(FacetValueDTO.class);
        when(facetValueDTO.getValue()).thenReturn(value);
        return facetValueDTO;
    }
}
