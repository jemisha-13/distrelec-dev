package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSessionParams.LAST_PAGE_SIZE;

import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;

class PageableDataParamsPopulator implements Populator<SearchRequestTuple, MultiValuedMap<String, String>> {

    static final String ROWS_PARAM = "rows";

    static final String START_PARAM = "start";

    @Autowired
    private SessionService sessionService;

    @Override
    public void populate(SearchRequestTuple searchRequestTuple, MultiValuedMap<String, String> params) throws ConversionException {
        PageableData pageableData = searchRequestTuple.getPageableData();
        int currentPage = pageableData.getCurrentPage(); // starts from 1
        int pageSize = pageableData.getPageSize();

        Integer lastPageSize = sessionService.getAttribute(LAST_PAGE_SIZE);
        if (lastPageSize != null && lastPageSize.intValue() != pageSize) {
            // reduce current according to changed page size
            currentPage = Math.floorDiv(lastPageSize * (currentPage - 1), pageSize) + 1;
        }

        params.put(START_PARAM, Integer.toString((currentPage - 1) * pageSize));
        params.put(ROWS_PARAM, Integer.toString(pageSize));
    }
}
