package com.distrelec.fusionsearch.response;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSessionParams.LAST_PAGE_SIZE;
import static com.namics.hybris.ffsearch.populator.paging.ProductsPerPageOptionPopulator.DEFAULT_VALUES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.fusion.search.dto.ResponseDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.paging.ProductsPerPageOption;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.session.SessionService;

@UnitTest
public class PaginationResponsePopulatorTest extends AbstractSearchResponsePopulatorTest {

    @InjectMocks
    PaginationResponsePopulator responsePopulator;

    @Mock
    SessionService sessionService;

    @Test
    public void testPopulatePagination() {
        int currentPage = 18;
        int start = 850;
        int numFound = 10299;
        int pageSize = 50;
        int totalPages = 206;

        // prerequisites
        assertTrue(Arrays.stream(DEFAULT_VALUES).anyMatch(value -> value == pageSize));

        PageableData pageableData = new PageableData();
        pageableData.setPageSize(pageSize);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setStart(start);
        responseDTO.setNumFound(numFound);
        responseDTO.setTotalPages(totalPages);

        SearchResponseDTO searchResponseDTO = new SearchResponseDTO();
        searchResponseDTO.setResponse(responseDTO);

        when(searchResponseTuple.getPageableData()).thenReturn(pageableData);
        when(searchResponseTuple.getSearchResponseDTO()).thenReturn(searchResponseDTO);

        responsePopulator.populate(searchResponseTuple, searchPageData);

        FactFinderPaginationData paginationData = (FactFinderPaginationData) searchPageData.getPagination();
        assertEquals(currentPage, paginationData.getCurrentPage());
        assertEquals(numFound, paginationData.getTotalNumberOfResults());
        assertEquals(pageSize, paginationData.getPageSize());
        assertEquals(totalPages, paginationData.getNumberOfPages());

        assertPopulatedOptions(paginationData, pageSize);

        verify(sessionService).setAttribute(LAST_PAGE_SIZE, pageSize);
    }

    private void assertPopulatedOptions(FactFinderPaginationData paginationData, int pageSize) {
        List<ProductsPerPageOption> options = paginationData.getProductsPerPageOptions();
        assertEquals(DEFAULT_VALUES.length, options.size());

        for (int optionId = 0; optionId < options.size(); optionId++) {
            ProductsPerPageOption option = options.get(optionId);
            assertEquals(DEFAULT_VALUES[optionId], option.getValue());
            assertEquals(optionId == 0, option.isDefault());
            assertEquals(DEFAULT_VALUES[optionId] == pageSize, option.isSelected());
        }
    }
}
