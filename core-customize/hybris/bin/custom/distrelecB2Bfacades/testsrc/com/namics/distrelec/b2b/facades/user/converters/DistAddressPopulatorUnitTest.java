package com.namics.distrelec.b2b.facades.user.converters;

import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistAddressPopulatorUnitTest {

    @Mock
    private AddressModel source;

    @Mock
    private Map<String, Converter<AddressModel, StringBuilder>> addressFormatConverterMap;

    @Mock
    private Converter<AddressModel, StringBuilder> defaultAddressFormatConverter;

    @Mock
    private Converter<CountryModel, CountryData> countryConverter;

    @Mock
    private DistUserService userService;

    @InjectMocks
    private DistAddressPopulator populator;

    @Test
    public void testPopulateCompanyName2() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getCompanyName2()).thenReturn("CompanyName2");

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName2(), equalTo("CompanyName2"));
    }

    @Test
    public void testPopulateCompanyName3() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getCompanyName3()).thenReturn("CompanyName3");

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName3(), equalTo("CompanyName3"));
    }

    @Test
    public void testPopulatePobox() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getPobox()).thenReturn("POBox");

        populator.populate(source, target);

        // then
        assertThat(target.getPobox(), equalTo("POBox"));
    }

    @Test
    public void testPopulateContactAddress() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getContactAddress()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.isContactAddress(), is(true));
    }

    @Test
    public void testPopulateWithDistDepartment() {
        // given
        AddressData target = new AddressData();
        DistDepartmentModel distDepartmentModel = mock(DistDepartmentModel.class);
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getDistDepartment()).thenReturn(distDepartmentModel);
        when(distDepartmentModel.getRelevantName()).thenReturn("Sales");
        when(distDepartmentModel.getRelevantName(Locale.ENGLISH)).thenReturn("Sales");
        when(distDepartmentModel.getCode()).thenReturn("SL");

        populator.populate(source, target);

        // then
        assertThat(target.getDepartment(), equalTo("Sales"));
        assertThat(target.getDepartmentEN(), equalTo("Sales"));
        assertThat(target.getDepartmentCode(), equalTo("SL"));
    }

    @Test
    public void testPopulateWithGeneralDepartment() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getDistDepartment()).thenReturn(null);
        when(source.getDepartment()).thenReturn("Support");

        populator.populate(source, target);

        // then
        assertThat(target.getDepartment(), equalTo("Support"));
        assertThat(target.getDepartmentEN(), equalTo("Support"));
    }

    @Test
    public void testPopulateWithNoDepartment() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getDistDepartment()).thenReturn(null);
        when(source.getDepartment()).thenReturn(null);

        populator.populate(source, target);

        // then
        assertThat(target.getDepartment(), equalTo(null));
        assertThat(target.getDepartmentEN(), equalTo(null));
        assertThat(target.getDepartmentCode(), equalTo(null));
    }

    @Test
    public void testPopulatePhone1() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getPhone1()).thenReturn("1234567890");

        populator.populate(source, target);

        // then
        assertThat(target.getPhone1(), equalTo("1234567890"));
    }

    @Test
    public void testPopulatePhone2() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getPhone2()).thenReturn("0987654321");

        populator.populate(source, target);

        // then
        assertThat(target.getPhone2(), equalTo("0987654321"));
    }

    @Test
    public void testPopulatePhone() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getPhone1()).thenReturn("1234567890");

        populator.populate(source, target);

        // then
        assertThat(target.getPhone(), equalTo("1234567890"));
    }

    @Test
    public void testPopulateFax() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getFax()).thenReturn("1122334455");

        populator.populate(source, target);

        // then
        assertThat(target.getFax(), equalTo("1122334455"));
    }

    @Test
    public void testPopulateAdditionalAddress() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getAdditionalAddressCompany()).thenReturn("Sam Street 123");

        populator.populate(source, target);

        // then
        assertThat(target.getAdditionalAddress(), equalTo("Sam Street 123"));
    }

    @Test
    public void testPopulateErpAddressID() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getErpAddressID()).thenReturn("ERP123456");

        populator.populate(source, target);

        // then
        assertThat(target.getErpAddressID(), equalTo("ERP123456"));
    }

    @Test
    public void testPopulateCountry() {
        // given
        AddressData target = new AddressData();
        CountryData expectedCountryData = new CountryData();
        CountryModel countryModel = mock(CountryModel.class);
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getCountry()).thenReturn(countryModel);
        when(countryConverter.convert(countryModel)).thenReturn(expectedCountryData);

        populator.populate(source, target);

        // then
        assertThat(target.getCountry(), equalTo(expectedCountryData));
    }

    @Test
    public void testPopulateRegion() {
        // given
        CountryModel countryModel = mock(CountryModel.class);
        AddressData target = new AddressData();
        CountryData countryData = new CountryData();
        RegionModel regionModel = mock(RegionModel.class);
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getCountry()).thenReturn(countryModel);
        when(countryConverter.convert(countryModel)).thenReturn(countryData);
        when(source.getRegion()).thenReturn(regionModel);
        when(regionModel.getIsocode()).thenReturn("CH-SG");
        when(regionModel.getName()).thenReturn("Sankt Gallen");
        when(regionModel.getName(Locale.ENGLISH)).thenReturn("Sankt Gallen");

        populator.populate(source, target);

        // then
        assertThat(target.getRegion().getIsocode(), equalTo("CH-SG"));
        assertThat(target.getRegion().getName(), equalTo("Sankt Gallen"));
        assertThat(target.getRegion().getNameEN(), equalTo("Sankt Gallen"));
    }

    @Test
    public void testPopulateWithNullCountry() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getCountry()).thenReturn(null);

        populator.populate(source, target);

        // then
        assertThat(target.getCountry(), is(nullValue()));
    }

    @Test
    public void testPopulateWithNullRegion() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(source.getRegion()).thenReturn(null);

        populator.populate(source, target);

        // then
        assertThat(target.getRegion(), is(nullValue()));
    }

    @Test
    public void testPopulateDefaultBillingTrue() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(userModel.getDefaultPaymentAddress()).thenReturn(source);

        populator.populate(source, target);

        // then
        assertThat(target.isDefaultBilling(), is(true));
    }

    @Test
    public void testPopulateDefaultBillingFalse() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);
        AddressModel differentAddress = new AddressModel();

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(userModel.getDefaultPaymentAddress()).thenReturn(differentAddress);

        populator.populate(source, target);

        // then
        assertThat(target.isDefaultBilling(), is(false));
    }

    @Test
    public void testPopulateDefaultShippingTrue() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(userModel.getDefaultShipmentAddress()).thenReturn(source);

        populator.populate(source, target);

        // then
        assertThat(target.isDefaultShipping(), is(true));
    }

    @Test
    public void testPopulateDefaultShippingFalse() {
        // given
        AddressData target = new AddressData();
        UserModel userModel = mock(UserModel.class);
        AddressModel differentAddress = new AddressModel();

        // when
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(userModel.getDefaultShipmentAddress()).thenReturn(differentAddress);

        populator.populate(source, target);

        // then
        assertThat(target.isDefaultShipping(), equalTo(false));
    }
}
