/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.interceptors.i18n;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.LocaleResolver;

import com.namics.distrelec.b2b.storefront.interceptors.LanguageInterceptor;

import de.hybris.platform.commerceservices.i18n.LanguageResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 *
 */
public class LocaleInterceptorTest {
    private final LanguageInterceptor interceptor = new LanguageInterceptor();

    @Mock
    private LanguageResolver languageResolver;

    @Mock
    private LocaleResolver localeResolver;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private HttpServletRequest request;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);

        interceptor.setLanguageResolver(languageResolver);
        interceptor.setCommonI18NService(commonI18NService);

    }

    @Test
    public void testOnceCalledIfAttributeNotNull() throws Exception {
        BDDMockito.given(request.getMethod()).willThrow(new ExpectedException());

        BDDMockito.given(request.getAttribute(interceptor.INTERCEPTOR_ONCE_KEY)).willReturn(Boolean.TRUE);
        BDDMockito.given(request.getParameter(LanguageInterceptor.DEFAULT_LANG_PARAM)).willReturn("dummy");

        interceptor.preHandle(request, null, null);// call does no harm flag call once is set up
        interceptor.preHandle(request, null, null);// call does no harm flag call once is set up
    }

    @Test
    public void testOnceCalled() throws Exception {
        BDDMockito.given(request.getMethod()).willThrow(new ExpectedException());

        BDDMockito.given(request.getAttribute(interceptor.INTERCEPTOR_ONCE_KEY)).willReturn(null);
        BDDMockito.given(request.getParameter(LanguageInterceptor.DEFAULT_LANG_PARAM)).willReturn("dummy");

        try {
            interceptor.preHandle(request, null, null);// call does harm flag call once is not set up
            Assert.fail("should call internal , so 'ExpectedException' exception should be thrown");
        } catch (final ExpectedException e) {
            // ok
        }

        try {
            interceptor.preHandle(request, null, null);// call does harm flag call once is not set up
            Assert.fail("should call internal , so 'ExpectedException' exception should be thrown");
        } catch (final ExpectedException e) {
            // ok
        }
    }

    @Test
    public void testOnceCalledGetWhereNoLangParam() throws Exception {
        BDDMockito.given(request.getMethod()).willReturn("GET");

        BDDMockito.given(request.getAttribute(interceptor.INTERCEPTOR_ONCE_KEY)).willReturn(Boolean.TRUE);
        BDDMockito.given(request.getParameter(LanguageInterceptor.DEFAULT_LANG_PARAM)).willReturn(null); // null param

        interceptor.preHandle(request, null, null);// call does no harm flag call once is set up

        Mockito.verifyZeroInteractions(commonI18NService);
        Mockito.verifyZeroInteractions(languageResolver);
    }

    @Test
    public void testCallForNonGetRequest() throws Exception {
        testCallForNonGetRequest("PUT");
        testCallForNonGetRequest("Put");
        testCallForNonGetRequest("put");

        testCallForNonGetRequest("POST");
        testCallForNonGetRequest("Post");
        testCallForNonGetRequest("post");

        testCallForNonGetRequest("DELETE");
        testCallForNonGetRequest("Delete");
        testCallForNonGetRequest("delete");
    }

    @Test
    public void testCallForAnyGetRequest() throws Exception {
        testCallForGetRequest("GET");
        testCallForGetRequest("Get");
        testCallForGetRequest("get");
    }

    private void testCallForNonGetRequest(final String nonGet) throws Exception {
        BDDMockito.given(request.getMethod()).willReturn(nonGet);
        BDDMockito.given(request.getParameter(LanguageInterceptor.DEFAULT_LANG_PARAM)).willReturn("dummy");

        interceptor.preHandle(request, null, null);

        Mockito.verifyZeroInteractions(commonI18NService);
        Mockito.verifyZeroInteractions(languageResolver);

        Mockito.reset(languageResolver, request, commonI18NService);
    }

    private void testCallForGetRequest(final String getMethod) throws Exception {
        final LanguageModel lang = Mockito.mock(LanguageModel.class);

        BDDMockito.given(languageResolver.getLanguage(Mockito.anyString())).willReturn(lang);
        BDDMockito.given(request.getMethod()).willReturn(getMethod);
        BDDMockito.given(request.getParameter(LanguageInterceptor.DEFAULT_LANG_PARAM)).willReturn("dummy");

        interceptor.preHandle(request, null, null);

        Mockito.verify(languageResolver).getLanguage("dummy");
        Mockito.verify(commonI18NService).setCurrentLanguage(lang);

        Mockito.reset(languageResolver, request, commonI18NService);
    }

    class ExpectedException extends RuntimeException {
        /**/
    }

}
