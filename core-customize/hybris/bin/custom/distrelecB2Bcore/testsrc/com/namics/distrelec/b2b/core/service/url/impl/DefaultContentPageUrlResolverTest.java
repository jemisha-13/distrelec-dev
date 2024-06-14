/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.i18n.I18NService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

/**
 * Test class for {@link DefaultContentPageUrlResolver}.
 * 
 * @author lstuker, Namics AG
 * 
 */
public class DefaultContentPageUrlResolverTest {

    private ContentPageModel contentPageModel;
    private DefaultContentPageUrlResolver contentPageUrlResolver;

    private static final String URL_PATTERN = "/{language}/{page-title}/cms/{page-label}";
    private static final String CONTENT_PAGE_LABEL = "testlabel";
    private static final String CONTENT_PAGE_LABEL_2 = "/testlabel";
    private static final String CONTENT_PAGE_NAME = "TestPage";
    private static final String CONTENT_PAGE_TITLE_DE = "Test Page DE";
    private static final String ISO_CODE = "de";

    @Before
    public void setUp() throws Exception {

        contentPageUrlResolver = new DefaultContentPageUrlResolver();
        contentPageModel = mock(ContentPageModel.class);

        when(contentPageModel.getLabel()).thenReturn(CONTENT_PAGE_LABEL);
        when(contentPageModel.getName()).thenReturn(CONTENT_PAGE_NAME);
        when(contentPageModel.getTitle()).thenReturn(CONTENT_PAGE_TITLE_DE);

        // mock services and models
        final CommonI18NService commonI18NService = mock(CommonI18NService.class);
        final BaseSiteService baseSiteService = mock(BaseSiteService.class);

        contentPageUrlResolver.setCommonI18NService(commonI18NService);
        contentPageUrlResolver.setBaseSiteService(baseSiteService);

        I18NService mockI18NService = mock(I18NService.class);
        when(mockI18NService.getCurrentLocale()).thenReturn(Locale.GERMAN);

        ReflectionTestUtils.setField(contentPageUrlResolver,"i18NService", mockI18NService);

        final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);

        final LanguageModel langageModel = new LanguageModel();
        langageModel.setIsocode(ISO_CODE);

        when(commonI18NService.getCurrentLanguage()).thenReturn(langageModel);
        when(baseSiteModel.getContentUrlPattern()).thenReturn(URL_PATTERN);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
    }

    @Test
    public void testResolveURL() {
        final String url = contentPageUrlResolver.resolveInternal(contentPageModel);
        Assert.assertEquals("/de/test-page-de/cms/testlabel", url);

        when(contentPageModel.getLabel()).thenReturn(CONTENT_PAGE_LABEL_2);
        final String url2 = contentPageUrlResolver.resolveInternal(contentPageModel);
        Assert.assertEquals("/de/test-page-de/cms/testlabel", url2);

    }

}
