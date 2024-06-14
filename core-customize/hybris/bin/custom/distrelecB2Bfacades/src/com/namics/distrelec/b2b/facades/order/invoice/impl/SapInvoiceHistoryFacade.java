/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.invoice.impl;

import com.distrelec.webservice.if12.v1.SearchRequest;
import com.distrelec.webservice.if12.v1.SearchResponse;
import com.distrelec.webservice.sap.v1.InvoiceSearchRequest;
import com.distrelec.webservice.sap.v1.InvoiceSearchResponse;
import com.namics.distrelec.b2b.core.inout.erp.InvoiceService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistPaginationData;
import com.namics.distrelec.b2b.facades.order.invoice.InvoiceHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SapInvoiceHistoryFacade implements InvoiceHistoryFacade {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final String BY_TOTAL_PRICE = "bytotalprice";
    // spring
    private UserService userService;
    private ConfigurationService configurationService;
    private InvoiceService invoiceService;
    private Converter<PageableData, InvoiceSearchRequest> distInvoiceSearchRequestConverter;
    private Converter<InvoiceSearchResponse, List<DistB2BInvoiceHistoryData>> distB2BInvoiceHistoryDataConverter;

    private Converter<PageableData, SearchRequest> distSearchRequestConverter;
    private Converter<SearchResponse, List<DistB2BInvoiceHistoryData>> distB2BInvoiceSearchDataConverter;


    @Override
    public SearchPageData<DistB2BInvoiceHistoryData> getInvoiceHistory(final DistInvoiceHistoryPageableData pageableData) {

        // create the request object for InvoiceService
        final InvoiceSearchRequest request = getDistInvoiceSearchRequestConverter().convert(pageableData);

        // call the service to search the invoices
        final InvoiceSearchResponse response = getInvoiceService().searchInvoices(request);

        // create the result SearchPageData object
        final SearchPageData<DistB2BInvoiceHistoryData> searchPageData = new SearchPageData<DistB2BInvoiceHistoryData>();
        final DistPaginationData resultPagination = new DistPaginationData();
        resultPagination.setCurrentPage(pageableData.getCurrentPage());
       final int resultTotalSize = response.getResultTotalSize() == null ? ZERO : response.getResultTotalSize().intValue();
       resultPagination.setNumberOfPages(calculateNumberOfPages(resultTotalSize, pageableData.getPageSize()));
        resultPagination.setPageSize(pageableData.getPageSize());
        resultPagination.setSort(pageableData.getSort());
        resultPagination.setTotalNumberOfResults(resultTotalSize);
        searchPageData.setPagination(resultPagination);

        // convert invoice data result data
        searchPageData.setResults(getDistB2BInvoiceHistoryDataConverter().convert(response));

        return searchPageData;
    }

    @Override
    public SearchPageData<DistB2BInvoiceHistoryData> getInvoiceSearchHistory(final DistOnlineInvoiceHistoryPageableData pageableData) {

        // create the request object for InvoiceService
        final SearchRequest request = getDistSearchRequestConverter().convert(pageableData);

        // call the service to search the invoices
        final SearchResponse response = getInvoiceService().searchInvoices(request);

        if(pageableData.getSort().equalsIgnoreCase(BY_TOTAL_PRICE) && response.getInvoices()!=null){
            Comparator<SearchResponse.Invoices.Invoice> comp = Comparator.comparing(SearchResponse.Invoices.Invoice::getInvoiceTotalAmountIncludingTaxes);
            List<SearchResponse.Invoices.Invoice> invoices = response.getInvoices().getInvoice();

           if(pageableData.isSortAscending()){
               Collections.sort(invoices, comp);
           } else {
               Collections.sort(invoices, comp.reversed());
           }
        }

        // create the result SearchPageData object
        final SearchPageData<DistB2BInvoiceHistoryData> searchPageData = new SearchPageData<DistB2BInvoiceHistoryData>();
        final DistPaginationData resultPagination = new DistPaginationData();
        resultPagination.setCurrentPage(pageableData.getCurrentPage());
        final int resultTotalSize = response.getResultTotalSize() == null ? ZERO : response.getResultTotalSize().intValue();
        resultPagination.setNumberOfPages(calculateNumberOfPages(resultTotalSize, pageableData.getPageSize()));
        resultPagination.setPageSize(pageableData.getPageSize());
        resultPagination.setSort(pageableData.getSort());
        resultPagination.setTotalNumberOfResults(resultTotalSize);
        searchPageData.setPagination(resultPagination);

        // convert invoice data result data
        searchPageData.setResults(getDistB2BInvoiceSearchDataConverter().convert(response));

        return searchPageData;
    }



    private int calculateNumberOfPages(final int resultTotalSize, final int pageSize) {
        if (resultTotalSize == ZERO) {
            return ZERO;
        } else if (resultTotalSize <= pageSize) {
            return ONE;
        } else {
            return (resultTotalSize + pageSize - ONE) / pageSize;
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public InvoiceService getInvoiceService() {
        return invoiceService;
    }

    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public Converter<PageableData, InvoiceSearchRequest> getDistInvoiceSearchRequestConverter() {
        return distInvoiceSearchRequestConverter;
    }

    public void setDistInvoiceSearchRequestConverter(Converter<PageableData, InvoiceSearchRequest> distInvoiceSearchRequestConverter) {
        this.distInvoiceSearchRequestConverter = distInvoiceSearchRequestConverter;
    }

    public Converter<InvoiceSearchResponse, List<DistB2BInvoiceHistoryData>> getDistB2BInvoiceHistoryDataConverter() {
        return distB2BInvoiceHistoryDataConverter;
    }

    public void setDistB2BInvoiceHistoryDataConverter(Converter<InvoiceSearchResponse, List<DistB2BInvoiceHistoryData>> distB2BInvoiceHistoryDataConverter) {
        this.distB2BInvoiceHistoryDataConverter = distB2BInvoiceHistoryDataConverter;
    }

    public Converter<PageableData, SearchRequest> getDistSearchRequestConverter() {
        return distSearchRequestConverter;
    }

    public void setDistSearchRequestConverter(Converter<PageableData, SearchRequest> distSearchRequestConverter) {
        this.distSearchRequestConverter = distSearchRequestConverter;
    }

    public Converter<SearchResponse, List<DistB2BInvoiceHistoryData>> getDistB2BInvoiceSearchDataConverter() {
        return distB2BInvoiceSearchDataConverter;
    }

    public void setDistB2BInvoiceSearchDataConverter(Converter<SearchResponse, List<DistB2BInvoiceHistoryData>> distB2BInvoiceSearchDataConverter) {
        this.distB2BInvoiceSearchDataConverter = distB2BInvoiceSearchDataConverter;
    }
}
