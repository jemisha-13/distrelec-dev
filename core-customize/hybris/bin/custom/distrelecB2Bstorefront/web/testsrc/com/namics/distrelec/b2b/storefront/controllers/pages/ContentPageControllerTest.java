/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class ContentPageControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ContentPageController contentPageController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(contentPageController).build();
    }

    @Test
    public void addUnsubscribedEmailToModelTestBase() throws UnsupportedEncodingException {

        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String unencodedEmail = "account@example.com";
        final NameValuePair nvp = new BasicNameValuePair(ContentPageController.EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE, unencodedEmail);
        final String encodedQueryString = URLEncodedUtils.format(Collections.singletonList(nvp), StandardCharsets.UTF_8);
        Mockito.when(request.getQueryString()).thenReturn(encodedQueryString);
        final Model model = Mockito.mock(ExtendedModelMap.class);

        contentPageController.addUnsubscribedEmailToModel(request, model);

        Mockito.verify(model).addAttribute(ContentPageController.EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE, unencodedEmail);
    }

    @Test
    public void addUnsubscribedEmailToModelTestEncoding() throws UnsupportedEncodingException {

        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String unencodedEmail = "!#$%&'*+-/=?^_`{|}~@example.com";
        final NameValuePair nvp = new BasicNameValuePair(ContentPageController.EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE, unencodedEmail);
        final String encodedQueryString = URLEncodedUtils.format(Collections.singletonList(nvp), StandardCharsets.UTF_8);
        Mockito.when(request.getQueryString()).thenReturn(encodedQueryString);
        final Model model = Mockito.mock(ExtendedModelMap.class);

        contentPageController.addUnsubscribedEmailToModel(request, model);

        Mockito.verify(model).addAttribute(ContentPageController.EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE, unencodedEmail);
    }

    @Test
    public void addUnsubscribedEmailToModelTestMissing() throws UnsupportedEncodingException {

        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getQueryString()).thenReturn(null);
        final Model model = Mockito.mock(ExtendedModelMap.class);

        contentPageController.addUnsubscribedEmailToModel(request, model);

        Mockito.verify(model, Mockito.times(0)).addAttribute(Mockito.eq(ContentPageController.EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE), Mockito.anyString());
    }

}
