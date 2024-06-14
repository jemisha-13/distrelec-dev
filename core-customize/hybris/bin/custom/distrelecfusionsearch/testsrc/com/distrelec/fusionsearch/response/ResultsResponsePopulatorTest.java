package com.distrelec.fusionsearch.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.fusion.search.dto.FusionDTO;
import com.distrelec.fusion.search.dto.ResponseDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
public class ResultsResponsePopulatorTest extends AbstractSearchResponsePopulatorTest {

    @InjectMocks
    ResultsResponsePopulator responsePopulator;

    @Mock
    Converter<Map<String, Object>, SearchResultValueData> docToSearchResultValueDataConverter;

    @Mock
    ResponseDTO responseDTO;

    @Mock
    SearchResponseDTO searchResponseDTO;

    @Before
    public void setUp() {
        when(searchResponseTuple.getSearchResponseDTO()).thenReturn(searchResponseDTO);
        when(searchResponseDTO.getResponse()).thenReturn(responseDTO);
    }

    @Test
    public void testPopulateResults() {
        String productNumberKey = "productNumber";
        String productNumberValue = "13412332";
        Map<String, Object> doc = Map.of(productNumberKey, productNumberValue);
        List<Map<String, Object>> docs = List.of(doc);

        SearchResultValueData searchResultValueData = mock(SearchResultValueData.class);

        when(responseDTO.getDocs()).thenReturn(docs);
        when(docToSearchResultValueDataConverter.convertAll(eq(docs))).thenReturn(List.of(searchResultValueData));

        responsePopulator.populate(searchResponseTuple, searchPageData);

        List<SearchResultValueData> results = searchPageData.getResults();
        assertEquals(1, results.size());
        assertEquals(searchResultValueData, results.get(0));
    }

    @Test
    public void testNotPopulateResultsForNullDocs() {
        when(responseDTO.getDocs()).thenReturn(null);

        responsePopulator.populate(searchResponseTuple, searchPageData);

        assertNull(searchPageData.getResults());
    }

    @Test
    public void testNotPopulateResultsIfRedirect() {
        FusionDTO fusionDTO = mock(FusionDTO.class);

        when(searchResponseDTO.getFusion()).thenReturn(fusionDTO);
        when(fusionDTO.getRedirect()).thenReturn(List.of("/redirect"));

        responsePopulator.populate(searchResponseTuple, searchPageData);

        assertNull(searchPageData.getResults());
        verify(responseDTO, never()).getDocs();
    }
}
