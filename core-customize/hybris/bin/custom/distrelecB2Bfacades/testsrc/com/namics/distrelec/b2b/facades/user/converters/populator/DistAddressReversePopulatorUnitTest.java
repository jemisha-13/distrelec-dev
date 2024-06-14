package com.namics.distrelec.b2b.facades.user.converters.populator;

import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistAddressReversePopulatorUnitTest {

    @Mock
    private DistrelecCodelistService codelistService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private AddressData source;

    @InjectMocks
    private DistAddressReversePopulator populator;

    @Test
    public void testPopulatePoBox() {
        // given
        AddressModel target = new AddressModel();

        // when
        when(source.getPobox()).thenReturn("POBox");

        populator.populate(source, target);

        // then
        assertThat(target.getPobox(), equalTo("POBox"));
    }

    @Test
    public void testPopulateContactAddress() {
        // given
        AddressModel target = new AddressModel();

        // when
        when(source.isContactAddress()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.getContactAddress(), is(true));
    }

    @Test
    public void testPopulateDepartmentCode() {
        // given
        AddressModel target = new AddressModel();
        DistDepartmentModel distDepartmentModel = mock(DistDepartmentModel.class);

        // when
        when(source.getDepartmentCode()).thenReturn("Dept1");
        when(codelistService.getDistDepartment("Dept1")).thenReturn(distDepartmentModel);

        populator.populate(source, target);

        // then
        assertThat(target.getDistDepartment(), equalTo(distDepartmentModel));
    }

    @Test
    public void testPopulateCellphone() {
        // given
        AddressModel target = new AddressModel();

        // when
        when(source.getCellphone()).thenReturn("1234567890");

        populator.populate(source, target);

        // then
        assertThat(target.getCellphone(), equalTo("1234567890"));
    }

    @Test
    public void testPopulateFax() {
        // given
        AddressModel target = new AddressModel();

        // when
        when(source.getFax()).thenReturn("0987654321");

        populator.populate(source, target);

        // then
        assertThat(target.getFax(), equalTo("0987654321"));
    }

    @Test
    public void testPopulateCompanyName2() {
        // given
        AddressModel target = new AddressModel();

        // when
        when(source.getCompanyName2()).thenReturn("Company Name 2");

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName2(), equalTo("Company Name 2"));
    }

    @Test
    public void testPopulateCompanyName3() {
        // given
        AddressModel target = new AddressModel();

        // when
        when(source.getCompanyName3()).thenReturn("Company Name 3");

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName3(), equalTo("Company Name 3"));
    }

    @Test
    public void testPopulateAdditionalAddressCompany() {
        // given
        AddressModel target = new AddressModel();

        // when
        when(source.getAdditionalAddress()).thenReturn("Additional Address");

        populator.populate(source, target);

        // then
        assertThat(target.getAdditionalAddressCompany(), equalTo("Additional Address"));
    }

    @Test
    public void testPopulateEmail() {
        // given
        AddressModel target = new AddressModel();

        // when
        when(source.getEmail()).thenReturn("distrelec@distrelec.com");

        populator.populate(source, target);

        // then
        assertThat(target.getEmail(), equalTo("distrelec@distrelec.com"));
    }

    @Test
    public void testPopulateCountryAndRegionBothValid() {
        // given
        AddressModel target = new AddressModel();
        CountryData countryData = mock(CountryData.class);
        RegionData regionData = mock(RegionData.class);
        CountryModel countryModel = mock(CountryModel.class);
        RegionModel regionModel = mock(RegionModel.class);

        // when
        when(source.getCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn("CH");
        when(source.getRegion()).thenReturn(regionData);
        when(regionData.getIsocode()).thenReturn("CH-SG");
        when(commonI18NService.getCountry("CH")).thenReturn(countryModel);
        when(commonI18NService.getRegion(countryModel, "CH-SG")).thenReturn(regionModel);

        populator.populate(source, target);

        // then
        assertThat(target.getRegion(), equalTo(regionModel));
    }

    @Test(expected = ConversionException.class)
    public void testPopulateCountryAndRegionUnknownCountry() {
        // given
        AddressModel target = new AddressModel();
        CountryData countryData = mock(CountryData.class);

        // when
        when(source.getCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn("UnknownCountryCode");
        when(commonI18NService.getCountry("UnknownCountryCode")).thenThrow(new UnknownIdentifierException("Invalid country"));

        populator.populate(source, target);
    }

    @Test(expected = ConversionException.class)
    public void testPopulateCountryAndRegionAmbiguousCountry() {
        // given
        AddressModel target = new AddressModel();
        CountryData countryData = mock(CountryData.class);

        // when
        when(source.getCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn("AmbiguousCountryCode");
        when(commonI18NService.getCountry("AmbiguousCountryCode")).thenThrow(new AmbiguousIdentifierException("Multiple countries"));

        populator.populate(source, target);
    }

    @Test(expected = ConversionException.class)
    public void testPopulateCountryAndRegionUnknownRegion() {
        // given
        AddressModel target = new AddressModel();
        CountryData countryData = mock(CountryData.class);
        RegionData regionData = mock(RegionData.class);
        CountryModel countryModel = mock(CountryModel.class);

        // when
        when(source.getCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn("CH");
        when(source.getRegion()).thenReturn(regionData);
        when(regionData.getIsocode()).thenReturn("UnknownRegionCode");
        when(commonI18NService.getCountry("CH")).thenReturn(countryModel);
        when(commonI18NService.getRegion(countryModel, "UnknownRegionCode")).thenThrow(new UnknownIdentifierException("Invalid region"));

        populator.populate(source, target);
    }

    @Test(expected = ConversionException.class)
    public void testPopulateCountryAndRegionAmbiguousRegion() {
        // given
        AddressModel target = new AddressModel();
        CountryData countryData = mock(CountryData.class);
        RegionData regionData = mock(RegionData.class);
        CountryModel countryModel = mock(CountryModel.class);

        // when
        when(source.getCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn("CH");
        when(source.getRegion()).thenReturn(regionData);
        when(regionData.getIsocode()).thenReturn("AmbiguousRegionCode");
        when(commonI18NService.getCountry("CH")).thenReturn(countryModel);
        when(commonI18NService.getRegion(countryModel, "AmbiguousRegionCode")).thenThrow(new AmbiguousIdentifierException("Multiple regions"));

        populator.populate(source, target);
    }

    @Test
    public void testPopulateCountryAndRegionBothNull() {
        // given
        AddressModel target = new AddressModel();

        // when
        when(source.getCountry()).thenReturn(null);
        when(source.getRegion()).thenReturn(null);

        populator.populate(source, target);

        // then
        assertNull(target.getRegion());
    }
}
