package com.distrelec.fusionsearch.response;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSessionParams.LAST_PAGE_SIZE;
import static com.namics.hybris.ffsearch.populator.paging.ProductsPerPageOptionPopulator.DEFAULT_VALUES;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.fusion.search.dto.ResponseDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.paging.ProductsPerPageOption;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;

class PaginationResponsePopulator implements Populator<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    @Autowired
    private SessionService sessionService;

    @Override
    public void populate(SearchResponseTuple searchResponseTuple,
                         FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) throws ConversionException {

        FactFinderPaginationData paginationData = initializePaginationData(searchPageData);

        PageableData pageableData = searchResponseTuple.getPageableData();
        int pageSize = pageableData.getPageSize();
        paginationData.setPageSize(pageSize);
        populateDefaultOptions(pageSize, paginationData);
        sessionService.setAttribute(LAST_PAGE_SIZE, pageSize);

        SearchResponseDTO searchResponseDTO = searchResponseTuple.getSearchResponseDTO();
        ResponseDTO responseDTO = searchResponseDTO.getResponse();

        int currentPage = Math.floorDiv(responseDTO.getStart(), pageSize) + 1;
        paginationData.setCurrentPage(currentPage);
        paginationData.setNumberOfPages(responseDTO.getTotalPages());
        paginationData.setTotalNumberOfResults(responseDTO.getNumFound());
    }

    private FactFinderPaginationData initializePaginationData(FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) {
        FactFinderPaginationData paginationData = (FactFinderPaginationData) searchPageData.getPagination();
        if (paginationData == null) {
            paginationData = new FactFinderPaginationData();
            searchPageData.setPagination(paginationData);
        }
        return paginationData;
    }

    private void populateDefaultOptions(int pageSize, FactFinderPaginationData paginationData) {
        List<ProductsPerPageOption> options = paginationData.getProductsPerPageOptions();
        for (int value : DEFAULT_VALUES) {
            boolean isDefault = value == DEFAULT_VALUES[0];
            boolean isSelected = value == pageSize;
            ProductsPerPageOption option = new ProductsPerPageOption(value, isDefault, isSelected);
            options.add(option);
        }
    }
}
