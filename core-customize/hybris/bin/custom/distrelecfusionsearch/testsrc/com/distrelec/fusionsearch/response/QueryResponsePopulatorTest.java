package com.distrelec.fusionsearch.response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.fusion.search.dto.QueryDTO;
import com.distrelec.fusion.search.dto.ResponseDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class QueryResponsePopulatorTest extends AbstractSearchResponsePopulatorTest {

    @InjectMocks
    QueryResponsePopulator responsePopulator;

    @Mock
    SearchResponseDTO searchResponseDTO;

    @Mock
    ResponseDTO responseDTO;

    @Mock
    QueryDTO queryDTO;

    @Before
    public void setUp() {
        when(searchResponseTuple.getSearchResponseDTO()).thenReturn(searchResponseDTO);
        when(searchResponseDTO.getResponse()).thenReturn(responseDTO);
        when(responseDTO.getQuery()).thenReturn(queryDTO);
    }

    @Test
    public void testPopulateQueryDataForNullOriginalQuery() {
        String query = "query";
        when(queryDTO.getQ()).thenReturn(query);
        responsePopulator.populate(searchResponseTuple, searchPageData);
        assertEquals(query, searchPageData.getFreeTextSearch());
    }

    @Test
    public void testPopulateQueryDataFromOriginalQuery() {
        String query = "query";
        when(queryDTO.getOriginalQuery()).thenReturn(query);
        responsePopulator.populate(searchResponseTuple, searchPageData);
        assertEquals(query, searchPageData.getFreeTextSearch());
    }
}
