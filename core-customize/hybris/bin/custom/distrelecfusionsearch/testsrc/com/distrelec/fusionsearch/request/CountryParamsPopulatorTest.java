package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.COUNTRY_PARAM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CountryModel;

@UnitTest
public class CountryParamsPopulatorTest extends AbstractParamsPopulatorTest {

    @InjectMocks
    CountryParamsPopulator countryParamsPopulator;

    @Mock
    CMSSiteService cmsSiteService;

    @Test
    public void testSetCurrentCountry() {
        String countryIsoCode = "SV";

        CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);
        CountryModel countryModel = mock(CountryModel.class);

        when(cmsSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
        when(cmsSiteModel.getCountry()).thenReturn(countryModel);
        when(countryModel.getIsocode()).thenReturn(countryIsoCode);

        countryParamsPopulator.populate(searchRequestTuple, params);

        Collection<String> values = params.get(COUNTRY_PARAM);
        assertEquals(1, values.size());
        assertEquals(countryIsoCode, values.iterator().next());
    }
}
