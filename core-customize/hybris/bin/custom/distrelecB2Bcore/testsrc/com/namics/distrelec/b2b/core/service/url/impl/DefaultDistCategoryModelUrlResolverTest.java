/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Locale;

import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang3.LocaleUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for {@link DefaultDistCategoryModelUrlResolver}.
 * 
 * @author lstuker, Namics AG
 */
public class DefaultDistCategoryModelUrlResolverTest {

    private CategoryModel categoryModel;
    private DefaultDistCategoryModelUrlResolver categoryModelUrlResolver;
    private static final String URL_PATTERN = "/{language}/{category-path}/c/{category-code}";
    private static final String CATEGORY_NAME = "Testcategory";
    private static final String CATEGORY_CODE = "testcode";
    private static final String ISO_CODE = "de";
    private static final Locale LOCALE_ISO_CODE = new Locale("de");

    @Before
    public void setUp() throws Exception {

        categoryModelUrlResolver = new DefaultDistCategoryModelUrlResolver();

        // mock services and models
        final CommonI18NService commonI18NService = mock(CommonI18NService.class);
        final I18NService i18NService = mock(I18NService.class);
        final CommerceCategoryService commerceCategoryService = mock(CommerceCategoryService.class);
        final BaseSiteService baseSiteService = mock(BaseSiteService.class);

        categoryModelUrlResolver.setCommonI18NService(commonI18NService);
        categoryModelUrlResolver.setCommerceCategoryService(commerceCategoryService);
        categoryModelUrlResolver.setBaseSiteService(baseSiteService);
        ReflectionTestUtils.setField(categoryModelUrlResolver, "i18NService", i18NService);

        categoryModel = mock(CategoryModel.class);
        final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);

        final LanguageModel langageModel = new LanguageModel();
        langageModel.setIsocode(ISO_CODE);

        when(commonI18NService.getCurrentLanguage()).thenReturn(langageModel);
        when(i18NService.getCurrentLocale()).thenReturn(LocaleUtils.toLocale(ISO_CODE));
        when(categoryModel.getCode()).thenReturn(CATEGORY_CODE);
        when(categoryModel.getNameSeo()).thenReturn(CATEGORY_NAME);
        when(categoryModel.getNameSeo(LOCALE_ISO_CODE)).thenReturn(CATEGORY_NAME);
        when(categoryModel.getLevel()).thenReturn(Integer.valueOf(1));
        when(commerceCategoryService.getPathsForCategory(any(CategoryModel.class))).thenReturn(Arrays.asList(Arrays.asList(categoryModel)));
        when(baseSiteModel.getCategoryUrlPattern()).thenReturn(URL_PATTERN);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
    }

    @Test
    public void testResolveURL() {
        final String url = categoryModelUrlResolver.resolveInternal(categoryModel);
        Assert.assertEquals("/de/testcategory/c/testcode", url);
    }

}
