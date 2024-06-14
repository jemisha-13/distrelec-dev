package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSessionParams.LAST_PAGE_SIZE;
import static com.distrelec.fusionsearch.request.PageableDataParamsPopulator.ROWS_PARAM;
import static com.distrelec.fusionsearch.request.PageableDataParamsPopulator.START_PARAM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.session.SessionService;

@UnitTest
public class PageableDataParamsPopulatorTest extends AbstractParamsPopulatorTest {

    @InjectMocks
    PageableDataParamsPopulator pageableDataParamsPopulator;

    @Mock
    SessionService sessionService;

    @Test
    public void testPopulatePageableData() {
        int currentPage = 3;
        int pageSize = 50;
        int start = 100;

        PageableData pageableData = new PageableData();
        pageableData.setCurrentPage(currentPage);
        pageableData.setPageSize(pageSize);

        when(searchRequestTuple.getPageableData()).thenReturn(pageableData);

        pageableDataParamsPopulator.populate(searchRequestTuple, params);

        assertContainsParam(START_PARAM, Integer.toString(start));
        assertContainsParam(ROWS_PARAM, Integer.toString(pageSize));
    }

    @Test
    public void testRecalculateStartBecausePageSizeIsChanged() {
        int currentPage = 88; // 88 page in case of 10 page size
        int pageSize = 50;
        int recalculatedStart = 850; // matches 850 products - increment of new page size 50
        int lastPageSize = 10;

        PageableData pageableData = new PageableData();
        pageableData.setCurrentPage(currentPage);
        pageableData.setPageSize(pageSize);

        when(sessionService.getAttribute(LAST_PAGE_SIZE)).thenReturn(lastPageSize);
        when(searchRequestTuple.getPageableData()).thenReturn(pageableData);

        pageableDataParamsPopulator.populate(searchRequestTuple, params);

        assertContainsParam(START_PARAM, Integer.toString(recalculatedStart));
        assertContainsParam(ROWS_PARAM, Integer.toString(pageSize));
    }

    private void assertContainsParam(String paramName, String paramValue) {
        Collection<String> values = params.get(paramName);
        assertEquals(1, values.size());
        assertEquals(paramValue, values.iterator().next());
    }
}
