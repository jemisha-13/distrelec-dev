package com.namics.distrelec.b2b.facades.user.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistB2BUnitPopulatorUnitTest {

    @Mock
    private B2BUnitModel source;

    @Mock
    private CountryModel countryModel;

    @Mock
    private Converter<CountryModel, CountryData> countryConverter;

    @InjectMocks
    private DistB2BUnitPopulator populator;

    @Test
    public void testPopulateVatID() {
        // given
        B2BUnitData target = new B2BUnitData();

        // when
        when(source.getVatID()).thenReturn("VAT123456");

        populator.populateUnit(source, target);

        // then
        assertThat(target.getVatID(), equalTo("VAT123456"));
    }

    @Test
    public void testPopulateErpCustomerId() {
        // given
        B2BUnitData target = new B2BUnitData();

        // when
        when(source.getErpCustomerID()).thenReturn("ERP123456");

        populator.populateUnit(source, target);

        // then
        assertThat(target.getErpCustomerId(), equalTo("ERP123456"));
    }

    @Test
    public void testPopulateCountry() {
        // given
        B2BUnitData target = new B2BUnitData();
        CountryData expectedCountryData = mock(CountryData.class);

        // when
        when(source.getCountry()).thenReturn(countryModel);
        when(countryConverter.convert(countryModel)).thenReturn(expectedCountryData);

        populator.populateUnit(source, target);

        // then
        assertThat(target.getCountry(), equalTo(expectedCountryData));
    }

    @Test
    public void testPopulateOnlinePriceCalculationTrue() {
        // given
        B2BUnitData target = new B2BUnitData();

        // when
        when(source.getOnlinePriceCalculation()).thenReturn(true);

        populator.populateUnit(source, target);

        // then
        assertThat(target.isOnlinePriceCalculation(), is(true));
    }

    @Test
    public void testPopulateOnlinePriceCalculationFalse() {
        // given
        B2BUnitData target = new B2BUnitData();

        // when
        when(source.getOnlinePriceCalculation()).thenReturn(false);

        populator.populateUnit(source, target);

        // then
        assertThat(target.isOnlinePriceCalculation(), is(false));
    }
}
