package com.namics.distrelec.b2b.facades.order.invoice.impl;

import com.distrelec.webservice.if12.v1.SearchRequest;
import com.distrelec.webservice.if12.v1.SearchResponse;
import com.distrelec.webservice.sap.v1.InvoiceSearchRequest;
import com.distrelec.webservice.sap.v1.InvoiceSearchResponse;
import com.namics.distrelec.b2b.core.inout.erp.InvoiceService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapInvoiceHistoryFacadeUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private Converter<DistInvoiceHistoryPageableData, Object> distInvoiceSearchRequestConverter;

    @Mock
    private Converter<InvoiceSearchResponse, List<DistB2BInvoiceHistoryData>> distB2BInvoiceHistoryDataConverter;

    @Mock
    private Converter<PageableData, SearchRequest> distSearchRequestConverter;

    @Mock
    private Converter<SearchResponse, List<DistB2BInvoiceHistoryData>> distB2BInvoiceSearchDataConverter;

    @InjectMocks
    private SapInvoiceHistoryFacade sapInvoiceHistoryFacade;

    @Test
    public void testGetInvoiceHistory() {
        // given
        DistInvoiceHistoryPageableData pageableData = new DistInvoiceHistoryPageableData();
        pageableData.setPageSize(10);
        InvoiceSearchRequest request = mock(InvoiceSearchRequest.class);
        InvoiceSearchResponse response = mock(InvoiceSearchResponse.class);

        // when
        when(invoiceService.searchInvoices(request)).thenReturn(response);
        when(response.getResultTotalSize()).thenReturn(BigInteger.valueOf(25));
        when(distInvoiceSearchRequestConverter.convert(pageableData)).thenReturn(request);

        SearchPageData<DistB2BInvoiceHistoryData> result = sapInvoiceHistoryFacade.getInvoiceHistory(pageableData);

        // then
        assertThat(result.getPagination().getTotalNumberOfResults(), equalTo(25));
        assertThat(result.getPagination().getNumberOfPages(), equalTo(3));
        assertThat(result.getPagination().getPageSize(), equalTo(10));
    }
}
