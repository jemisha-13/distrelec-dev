/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

/**
 * Test class for <code>DistHeroRotatingTeaserValidateInterceptor</code>.
 * 
 * @author pbueschi, Namics AG
 * 
 */
@UnitTest
public class DistCarpetContentTeaserValidateInterceptorTest {

    @Mock
    private CatalogVersionService catalogVersionService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private L10NService l10nService;

    @Mock
    private CMSSiteService cmsSiteService;

    @Mock
    private LanguageModel language;

    @Mock
    private CMSSiteModel cmsSiteModel;

    @Mock
    private CatalogVersionModel catalogVersion;

    @Mock
    private DistCarpetContentTeaserModel component;

    @Mock
    private MediaModel smallImage;

    private DistCarpetContentTeaserValidateInterceptor interceptor = new DistCarpetContentTeaserValidateInterceptor();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        language.setActive(Boolean.TRUE);
        language.setIsocode("de");
        Mockito.when(commonI18NService.getLanguage(Mockito.any(String.class))).thenReturn(language);

        Mockito.when(l10nService.getLocalizedString("validations.distcarpet.teaser.nosmallimage")).thenReturn("Das kleine Bild ist zwingend");

        cmsSiteModel.setActive(Boolean.TRUE);
        cmsSiteModel.setUid("testSite");
        Mockito.when(cmsSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
        Mockito.when(cmsSiteModel.getDefaultLanguage()).thenReturn(language);
        Mockito.when(language.getIsocode()).thenReturn("de");

        interceptor.setCmsSiteService(cmsSiteService);
        interceptor.setL10nService(l10nService);

        catalogVersion.setCatalog(Mockito.any(CatalogModel.class));
        catalogVersion.setVersion("Staged");
        catalogVersion.setLanguages(Collections.singletonList(language));
        Mockito.when(catalogVersionService.getCatalogVersion(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(catalogVersion);

        component.setUid("testComponent");
        component.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Staged"));

        Mockito.when(component.getImageSmall(Mockito.any(Locale.class))).thenReturn(smallImage);
        Mockito.when(component.getImageSmall(Mockito.any(Locale.class)).getCode()).thenReturn("testImage");
    }

    @Test
    // Create a teaser without Small Image
    public void testTeaserWithoutSmallImage() {
        try {
            interceptor.onValidate(component, null);
        } catch (InterceptorException e) {
            Assert.assertTrue(e.getMessage().contains("Das kleine Bild ist zwingend")); // validations.distcarpet.teaser.nosmallimage
        }
    }

    @Test
    // Create a teaser with a small image
    public void testTeaserOk() {
        try {
            interceptor.onValidate(component, null);
        } catch (InterceptorException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertNotNull(component.getImageSmall(Mockito.any(Locale.class)));
        Assert.assertEquals("testImage", component.getImageSmall(Mockito.any(Locale.class)).getCode());
    }
}
