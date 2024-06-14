package com.namics.distrelec.occ.core.mapping.resolvers;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.occ.core.mapping.resolvers.impl.DistHeadlessURLResolver;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
public class DistHeadlessURLResolverTest {

    @InjectMocks
    private DistHeadlessURLResolver distHeadlessURLResolver;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private LanguageModel languageOne;

    @Mock
    private LanguageModel languageTwo;

    @Mock
    private LanguageModel languageThree;

    @Mock
    private LanguageModel languageFour;

    @Mock
    private LanguageModel languageFive;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(languageOne.getIsocode()).thenReturn("en");
        when(languageOne.getActive()).thenReturn(TRUE);

        when(languageTwo.getIsocode()).thenReturn("de");
        when(languageTwo.getActive()).thenReturn(TRUE);

        when(languageThree.getIsocode()).thenReturn("it");
        when(languageThree.getActive()).thenReturn(FALSE);

        when(languageFour.getIsocode()).thenReturn("fr");
        when(languageFour.getActive()).thenReturn(null);

        when(languageFive.getIsocode()).thenReturn("de_DE");
        when(languageFive.getActive()).thenReturn(TRUE);

        when(commonI18NService.getAllLanguages())
          .thenReturn(List.of(languageOne, languageTwo, languageThree, languageFour, languageFive));

        distHeadlessURLResolver.afterPropertiesSet();
    }

    @Test
    public void testResolveHeadlessUrlPath() {
        assertEquals(distHeadlessURLResolver.resolve("/en/74165-bit-shift-register-piso-tp/pf/DC-44700"),
                     "/74165-bit-shift-register-piso-tp/pf/DC-44700");
        assertEquals(distHeadlessURLResolver.resolve("en/74165-bit-shift-register-piso-tp/pf/DC-44700"),
                     "/74165-bit-shift-register-piso-tp/pf/DC-44700");
        assertEquals(distHeadlessURLResolver.resolve("/74165-bit-shift-register-piso-tp/pf/DC-44700"),
                     "/74165-bit-shift-register-piso-tp/pf/DC-44700");
        assertEquals(distHeadlessURLResolver.resolve("74165-bit-shift-register-piso-tp/pf/DC-44700"),
                     "74165-bit-shift-register-piso-tp/pf/DC-44700");
        assertEquals(distHeadlessURLResolver.resolve("/EN/74165-bit-shift-register-piso-tp/pf/DC-44700"),
                     "/74165-bit-shift-register-piso-tp/pf/DC-44700");
        assertEquals(distHeadlessURLResolver.resolve("EN/74165-bit-shift-register-piso-tp/pf/DC-44700"),
                     "/74165-bit-shift-register-piso-tp/pf/DC-44700");
        assertEquals(distHeadlessURLResolver.resolve("https://www.distrelec.ch"), "https://www.distrelec.ch");
        assertEquals(distHeadlessURLResolver.resolve(EMPTY), EMPTY);
        assertEquals(distHeadlessURLResolver.resolve(null), null);
    }

    @Test
    public void testAllowedLanguages() {
        assertEquals(distHeadlessURLResolver.resolve("/en/74165-bit-shift-register-piso-tp/pf/DC-44700"),
                     "/74165-bit-shift-register-piso-tp/pf/DC-44700");
        assertEquals(distHeadlessURLResolver.resolve("/de/de-name/pf/DC-44700"),
                     "/de-name/pf/DC-44700");
    }

    @Test
    public void testBlockedLanguages() {
        assertEquals(distHeadlessURLResolver.resolve("/it/it-name/pf/DC-44700"),
                     "/it/it-name/pf/DC-44700");
        assertEquals(distHeadlessURLResolver.resolve("/fr/fr-name/pf/DC-44700"),
                     "/fr/fr-name/pf/DC-44700");
        assertEquals(distHeadlessURLResolver.resolve("/de_DE/de-name/pf/DC-44700"),
                     "/de_DE/de-name/pf/DC-44700");
    }

}
