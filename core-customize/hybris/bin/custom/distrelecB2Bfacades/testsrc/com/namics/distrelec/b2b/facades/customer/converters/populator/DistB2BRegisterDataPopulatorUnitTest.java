package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistB2BRegisterDataPopulatorUnitTest {

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private DistCustomerFacade customerFacade;

    @InjectMocks
    private DistB2BRegisterDataPopulator populator;


    @Test
    public void testPopulateWhenCompanyIsNotBlankSetsCompanyName() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        DistRegisterData target = new DistRegisterData();

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(source.getCompany()).thenReturn("Distrelec AG");
        when(customerFacade.formatCompanyName("Distrelec AG")).thenReturn(Arrays.asList("Distrelec", "AG"));

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName(), equalTo("Distrelec"));
    }

    @Test
    public void testPopulateWhenCompanyHasMultiplePartsSetsCompanyName2() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        DistRegisterData target = new DistRegisterData();

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(source.getCompany()).thenReturn("Distrelec AG");
        when(customerFacade.formatCompanyName("Distrelec AG")).thenReturn(Arrays.asList("Distrelec", "AG"));

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName2(), equalTo("AG"));
    }

    @Test
    public void testPopulateWhenCompanyHasLessThanThreePartsSetsCompanyName3AsEmpty() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        DistRegisterData target = new DistRegisterData();


        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(source.getCompany()).thenReturn("Distrelec AG");
        when(customerFacade.formatCompanyName("Distrelec AG")).thenReturn(Arrays.asList("Distrelec", "AG"));

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName3(), equalTo(StringUtils.EMPTY));
    }

    @Test
    public void testPopulateWhenCompanyHasThreeOrMorePartsSetsAllCompanyNames() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(source.getCompany()).thenReturn("Distrelec AG Corporation");
        when(customerFacade.formatCompanyName("Distrelec AG Corporation")).thenReturn(Arrays.asList("Distrelec", "AG", "Corporation"));

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName(), equalTo("Distrelec"));
        assertThat(target.getCompanyName2(), equalTo("AG"));
        assertThat(target.getCompanyName3(), equalTo("Corporation"));
    }

    @Test
    public void testPopulateSetsDuns() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(source.getDuns()).thenReturn("12345");

        populator.populate(source, target);

        // then
        assertThat(target.getDuns(), equalTo("12345"));
    }

    @Test
    public void testPopulateSetsFunctionCode() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(source.getFunctionCode()).thenReturn("Manager");

        populator.populate(source, target);

        // then
        assertThat(target.getFunctionCode(), equalTo("Manager"));
    }

    @Test
    public void testPopulateSetsVatId() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(source.getVatId()).thenReturn("VAT1234");

        populator.populate(source, target);

        // then
        assertThat(target.getVatId(), equalTo("VAT1234"));
    }

    @Test
    public void testPopulateOrganizationalNumberWhenSalesOrgIsNotWithCode7650() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(source.getOrganizationalNumber()).thenReturn("123 45 67");

        populator.populate(source, target);

        // then
        assertThat(target.getOrganizationalNumber(), equalTo("123 45 67"));
    }

    @Test
    public void testPopulateSetsCustomerType() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");

        populator.populate(source, target);

        // then
        assertThat(target.getCustomerType(), equalTo(CustomerType.B2B));
    }

    @Test
    public void testPopulateSetsRegistrationType() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");

        populator.populate(source, target);

        // then
        assertThat(target.getRegistrationType(), equalTo(RegistrationType.STANDALONE));
    }

    @Test
    public void testPopulateSetsInvoiceEmailWhenBlank() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
        when(source.getInvoiceEmail()).thenReturn("");
        when(source.getLogin()).thenReturn("distrelec@distrelec.com");

        populator.populate(source, target);

        // then
        assertThat(target.getInvoiceEmail(), equalTo("distrelec@distrelec.com"));
    }

    @Test
    public void testPopulateSetsInvoiceEmailWhenNotBlank() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(source.getInvoiceEmail()).thenReturn("invoice@distrelec.com");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");

        populator.populate(source, target);

        // then
        assertThat(target.getInvoiceEmail(), equalTo("invoice@distrelec.com"));
    }

    @Test
    public void testPopulateSetsPhoneNumber() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");

        when(source.getPhoneNumber()).thenReturn("0038598111222");

        populator.populate(source, target);

        // then
        assertThat(target.getPhoneNumber(), equalTo("0038598111222"));
    }

    @Test
    public void testPopulateRemoveWhitespaceFromOrgNumber() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(source.getLogin()).thenReturn("distrelec.customer@distrelec.com");
        when(source.getPhoneNumber()).thenReturn("1234567890");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn(DistConstants.SalesOrg.SALES_ORG_7650);
        when(source.getOrganizationalNumber()).thenReturn("123 456 789");

        populator.populate(source, target);

        // then
        assertThat(target.getOrganizationalNumber(), equalTo("123456789"));
    }

    @Test
    public void testPopulateSetLongCompanyName() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        String companyName = "Max-Planck-Institut für Radioastronomie";

        // when
        when(source.getLogin()).thenReturn("distrelec.customer@distrelec.com");
        when(source.getPhoneNumber()).thenReturn("1234567890");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn(DistConstants.SalesOrg.SALES_ORG_7650);
        when(source.getCompany()).thenReturn(companyName);
        when(customerFacade.formatCompanyName(companyName)).thenReturn(Arrays.asList("Max-Planck-Institut für", "Radioastronomie"));

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName(), equalTo("Max-Planck-Institut für"));
        assertThat(target.getCompanyName2(), equalTo("Radioastronomie"));
        assertThat(target.getCompanyName3(), equalTo(StringUtils.EMPTY));
    }
}
