package com.distrelec.fusionsearch.response;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.CATEGORY_CODES_FACET_CODE;
import static org.junit.Assert.assertEquals;
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
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.populator.response.helper.SearchQueryTransformer;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class CategoryResponsePopulatorTest extends AbstractSearchResponsePopulatorTest {

    @InjectMocks
    CategoryResponsePopulator categoryResponsePopulator;

    @Mock
    EnhanceSearchQueryStrategy enhanceSearchQueryStrategy;

    @Mock
    SearchQueryData searchQuery;

    @Mock
    SearchQueryTransformer queryTransformer;

    @Mock
    SearchResponseDTO searchResponseDTO;

    @Before
    public void setUp() {
        when(searchResponseTuple.getSearchResponseDTO()).thenReturn(searchResponseDTO);
        when(enhanceSearchQueryStrategy.enhanceSearchQuery(searchResponseTuple)).thenReturn(searchQuery);
    }

    @Test
    public void testPopulateFacetValues() {
        String facetCode = CATEGORY_CODES_FACET_CODE;
        String facetValue = "facetValue";
        String facetPath = "1||cat-L2-3D_525341|Optoelectronics||cat-L3D_525297|LEDs";
        String expectedFacetCode = "categoryCodePathROOT/cat-L2-3D_525341";
        long count = 12321L;

        SearchQueryData refinedSearchQuery = mock(SearchQueryData.class);
        FacetDTO facetDTO = mock(FacetDTO.class);
        FacetValueDTO facetValueDTO = mock(FacetValueDTO.class);

        when(searchResponseDTO.getFacets()).thenReturn(List.of(facetDTO));

        // facet DTO
        when(facetDTO.getCode()).thenReturn(facetCode);
        when(facetDTO.getValues()).thenReturn(List.of(facetValueDTO));

        // facet value DTO
        when(facetValueDTO.getPath()).thenReturn(facetPath);
        when(facetValueDTO.getCount()).thenReturn(count);
        when(facetValueDTO.getValue()).thenReturn(facetValue);

        when(queryTransformer.refineQueryAddFacet(searchQuery, expectedFacetCode, facetValue)).thenReturn(refinedSearchQuery);

        categoryResponsePopulator.populate(searchResponseTuple, searchPageData);

        FactFinderFacetData<SearchQueryData> categoryFacet = searchPageData.getCategories();
        List<FactFinderFacetValueData<SearchQueryData>> categoryFacetValues = categoryFacet.getValues();
        assertEquals(1, categoryFacetValues.size());

        FactFinderFacetValueData<SearchQueryData> actualFacetValue = categoryFacetValues.get(0);
        assertEquals(expectedFacetCode, actualFacetValue.getCode());
        assertEquals(facetValue, actualFacetValue.getName());
        assertEquals(count, actualFacetValue.getCount());
        assertEquals(refinedSearchQuery, actualFacetValue.getQuery());
    }
}
