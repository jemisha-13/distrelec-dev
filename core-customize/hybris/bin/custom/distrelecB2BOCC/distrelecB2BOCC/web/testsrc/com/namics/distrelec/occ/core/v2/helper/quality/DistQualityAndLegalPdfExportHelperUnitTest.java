package com.namics.distrelec.occ.core.v2.helper.quality;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.occ.core.qualityandlegal.ws.dto.QualityAndLegalDownloadWsDTO;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.servicelayer.i18n.I18NService;
import net.sf.jasperreports.engine.JRException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistQualityAndLegalPdfExportHelperUnitTest {

    @InjectMocks
    private DistQualityAndLegalPdfExportHelper distQualityAndLegalPdfExportHelper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private I18NService i18nService;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private DistCustomerFacade b2bCustomerFacade;

    @Mock
    private DistrelecProductFacade productFacade;

    private QualityAndLegalDownloadWsDTO qualityAndLegalDownloadWsDTO;

    private OutputStream output;

    @Before
    public void setup() {
        qualityAndLegalDownloadWsDTO = mock(QualityAndLegalDownloadWsDTO.class);
        output = mock(OutputStream.class);
        List<String> productCodes = Arrays.asList("1111111",
                                                  "22222222",
                                                  "33333333");
        Collection<ProductOption> options = List.of(ProductOption.BASIC,
                                                    ProductOption.MIN_BASIC,
                                                    ProductOption.DESCRIPTION,
                                                    ProductOption.DIST_MANUFACTURER,
                                                    ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION);
        ProductData mockProduct1 = mock(ProductData.class);
        ProductData mockProduct2 = mock(ProductData.class);
        ProductData mockProduct3 = mock(ProductData.class);
        List<ProductData> mockProductDataList = List.of(mockProduct1, mockProduct2, mockProduct3);
        ClassLoader classLoader = mock(ClassLoader.class);
        distQualityAndLegalPdfExportHelper.setClassLoader(classLoader);
        mockCustomer();
        String dummyXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                          "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" name=\"JasperReport_A4_Landscape\" pageWidth=\"842\" pageHeight=\"595\" orientation=\"Landscape\" columnWidth=\"802\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\" whenResourceMissingType=\"Empty\" uuid=\"aeb8e534-3be1-4311-a7aa-c54b55e4b911\">"
                          +
                          "</jasperReport>";
        Resource resource = new ByteArrayResource(dummyXML.getBytes());

        when(qualityAndLegalDownloadWsDTO.getProductCodes()).thenReturn(productCodes);
        when(qualityAndLegalDownloadWsDTO.getCustomerName()).thenReturn("Speedy Gonzales");
        when(productFacade.getProductListForCodesAndOptions(productCodes, options)).thenReturn(mockProductDataList);
        when(resourceLoader.getResource(any())).thenReturn(resource);
        try {
            URL url = new URL("https", "test.com", "");
            when(classLoader.getResource(any())).thenReturn(url);
        } catch (MalformedURLException e) {
            fail("MalformedURLException has occurred: " + e.getMessage());
        }
    }

    private void mockCustomer() {
        CustomerData customerData = mock(CustomerData.class);
        B2BUnitData unit = mock(B2BUnitData.class);
        AddressData mockAddressData = mock(AddressData.class);
        List<AddressData> addresses = Collections.singletonList(mockAddressData);

        when(customerData.getEmail()).thenReturn("mail@mail.com");
        when(b2bCustomerFacade.getCurrentCustomer()).thenReturn(customerData);
        when(b2bCustomerFacade.getCurrentCustomer().getCustomerType()).thenReturn(CustomerType.B2B);
        when(b2bCustomerFacade.getCurrentCustomer().getUnit()).thenReturn(unit);
        when(b2bCustomerFacade.getCurrentCustomer().getUnit().getErpCustomerId()).thenReturn("0123456789");
        when(b2bCustomerFacade.getCurrentCustomer().getUnit().getAddresses()).thenReturn(addresses);
        when(mockAddressData.getLine1()).thenReturn("Address Line 1");
        when(mockAddressData.getPostalCode()).thenReturn("postal code");
        when(mockAddressData.getTown()).thenReturn("Town");
    }

    @Test
    public void testGetPDFStreamForQualityAndLegalReportWithCustomerName() {
        try {
            distQualityAndLegalPdfExportHelper.getPDFStreamForQualityAndLegalReport(qualityAndLegalDownloadWsDTO, output);

            verify(messageSource, times(8)).getMessage(any(), any(), any(), any());
            verify(i18nService, times(8)).getCurrentLocale();
            verify(resourceLoader, times(1)).getResource(any());

        } catch (IOException e) {
            fail("IOException has occurred: " + e.getMessage());
        } catch (JRException e) {
            e.printStackTrace();
            fail("JRException has occurred: " + e.getMessage());
        }
    }

    @Test
    public void testGetPDFStreamForQualityAndLegalReportWithoutCustomerName() {
        try {
            qualityAndLegalDownloadWsDTO.setCustomerName(null);
            distQualityAndLegalPdfExportHelper.getPDFStreamForQualityAndLegalReport(qualityAndLegalDownloadWsDTO, output);

            verify(messageSource, times(8)).getMessage(any(), any(), any(), any());
            verify(i18nService, times(8)).getCurrentLocale();
            verify(resourceLoader, times(1)).getResource(any());
            verify(b2bCustomerFacade, times(5)).getCurrentCustomer();

        } catch (IOException e) {
            fail("IOException has occurred: " + e.getMessage());
        } catch (JRException e) {
            e.printStackTrace();
            fail("JRException has occurred: " + e.getMessage());
        }
    }

}
