/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;

import com.distrelec.webservice.if12.v1.P3FaultMessage;
import com.distrelec.webservice.if12.v1.SIHybrisIF12Out;
import com.distrelec.webservice.if12.v1.SearchRequest;
import com.distrelec.webservice.if12.v1.SearchResponse;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * Test class for SapCustomerService logic
 * 
 * @author francesco, Distrelec AG
 * @since Distrelec 1.0
 * 
 */
@UnitTest
public class SapInvoiceServiceTest {

    @Mock
    private SIHybrisIF12Out webServiceIF12Client;

    @Mock
    private CacheManager distCacheManager;

    @Mock
    private EhCacheCache ehCacheCache;

    @InjectMocks
    private final SapInvoiceService invoiceService = new SapInvoiceService();


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testCacheMiss() throws P3FaultMessage {

        // When
        final SearchRequest request = new SearchRequest();
        Mockito.when(distCacheManager.getCache(Mockito.anyString())).thenReturn(ehCacheCache);
        Mockito.when(ehCacheCache.get(Mockito.any(), Mockito.<Class<SearchResponse>> any())).thenReturn(null);
        final SearchResponse mockedSearchresponse = Mockito.mock(SearchResponse.class);
        Mockito.when(webServiceIF12Client.search(Mockito.any())).thenReturn(mockedSearchresponse);

        // Do
        invoiceService.searchInvoices(request);

        // Verify
        Mockito.verify(ehCacheCache, Mockito.times(1)).get(Mockito.any(), Mockito.<Class<SearchResponse>> any());
        Mockito.verify(webServiceIF12Client, Mockito.times(1)).search(request);
        Mockito.verify(ehCacheCache, Mockito.times(1)).put(Mockito.anyString(), Mockito.any(Class.class));
    }

    @Test
    public void testCacheHit() throws P3FaultMessage {

        // When
        final SearchRequest request = new SearchRequest();
        Mockito.when(distCacheManager.getCache(Mockito.anyString())).thenReturn(ehCacheCache);
        final SearchResponse mockedSearchresponse = Mockito.mock(SearchResponse.class);
        Mockito.when(ehCacheCache.get(Mockito.any(), Mockito.<Class<SearchResponse>> any())).thenReturn(mockedSearchresponse);

        // Do
        invoiceService.searchInvoices(request);

        // Verify
        Mockito.verify(ehCacheCache, Mockito.times(1)).get(Mockito.any(), Mockito.<Class<SearchResponse>> any());
        Mockito.verify(webServiceIF12Client, Mockito.times(0)).search(request);
        Mockito.verify(ehCacheCache, Mockito.times(0)).put(Mockito.anyString(), Mockito.any(Class.class));
    }
}
