package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.LANGUAGE_PARAM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;

@UnitTest
public class LanguageParamsPopulatorTest extends AbstractParamsPopulatorTest {

    @InjectMocks
    LanguageParamsPopulator langParamsPopulator;

    @Mock
    DistCommerceCommonI18NService distCommerceCommonI18NService;

    @Test
    public void testPopulateBaseLanguageIsoCode() {
        String langIsoCode = "en";

        LanguageModel currentLang = mock(LanguageModel.class);
        LanguageModel baseLang = mock(LanguageModel.class);

        when(distCommerceCommonI18NService.getCurrentLanguage()).thenReturn(currentLang);
        when(distCommerceCommonI18NService.getBaseLanguage(currentLang)).thenReturn(baseLang);
        when(baseLang.getIsocode()).thenReturn(langIsoCode);

        langParamsPopulator.populate(searchRequestTuple, params);

        Collection<String> values = params.get(LANGUAGE_PARAM);
        assertEquals(1, values.size());
        assertEquals(langIsoCode, values.iterator().next());
    }
}
