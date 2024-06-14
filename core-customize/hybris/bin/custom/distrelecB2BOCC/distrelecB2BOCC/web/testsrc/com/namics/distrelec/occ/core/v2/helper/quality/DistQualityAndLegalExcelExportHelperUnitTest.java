package com.namics.distrelec.occ.core.v2.helper.quality;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
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

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistQualityAndLegalExcelExportHelperUnitTest {

    @InjectMocks
    private DistQualityAndLegalExcelExportHelper distQualityAndLegalExcelExportHelper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private I18NService i18nService;

    @Mock
    private DistCustomerFacade b2bCustomerFacade;

    @Mock
    private DistrelecProductFacade productFacade;

    private QualityAndLegalDownloadWsDTO qualityAndLegalDownloadWsDTO;

    private Date svhcReviewDate;

    @Before
    public void setup() {
        qualityAndLegalDownloadWsDTO = mock(QualityAndLegalDownloadWsDTO.class);
        svhcReviewDate = mock(Date.class);

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
        mockProducts(mockProduct1, mockProduct2, mockProduct3);
        when(productFacade.getProductListForCodesAndOptions(productCodes, options)).thenReturn(mockProductDataList);

        mockCustomer();
        when(qualityAndLegalDownloadWsDTO.getCustomerName()).thenReturn("Speedy Gonzales");
        when(qualityAndLegalDownloadWsDTO.getProductCodes()).thenReturn(productCodes);

        when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        mockProperties();
    }

    private void mockProducts(ProductData mockProduct1, ProductData mockProduct2, ProductData mockProduct3) {
        DistManufacturerData mockDistManufacturer = mock(DistManufacturerData.class);
        when(mockDistManufacturer.getName()).thenReturn("Manufacturer");

        // Product 1
        when(mockProduct1.getCode()).thenReturn("11111111");
        when(mockProduct1.getTypeName()).thenReturn("man-11111111");
        when(mockProduct1.getDistManufacturer()).thenReturn(mockDistManufacturer);
        when(mockProduct1.getName()).thenReturn("Product 1 name");
        when(mockProduct1.getRohs()).thenReturn("rohs");
        when(mockProduct1.getSvhcReviewDate()).thenReturn(svhcReviewDate);
        when(mockProduct1.getHasSvhc()).thenReturn(true);
        when(mockProduct1.getSvhc()).thenReturn("svhc");
        when(mockProduct1.getScip()).thenReturn("scip");
        when(mockProduct1.getSvhcURL()).thenReturn("svhc-url");

        // Product 2
        when(mockProduct2.getCode()).thenReturn("22222222");
        when(mockProduct2.getTypeName()).thenReturn("man-22222222");
        when(mockProduct2.getDistManufacturer()).thenReturn(mockDistManufacturer);
        when(mockProduct2.getName()).thenReturn("Product 2 name");
        when(mockProduct2.getRohs()).thenReturn("rohs");
        when(mockProduct2.getSvhcReviewDate()).thenReturn(svhcReviewDate);
        when(mockProduct2.getHasSvhc()).thenReturn(false);
        when(mockProduct2.getSvhc()).thenReturn("svhc");
        when(mockProduct2.getScip()).thenReturn("scip");
        when(mockProduct2.getSvhcURL()).thenReturn("svhc-url");

        // Product 3
        when(mockProduct3.getCode()).thenReturn("33333333");
        when(mockProduct3.getTypeName()).thenReturn("man-33333333");
        when(mockProduct3.getDistManufacturer()).thenReturn(mockDistManufacturer);
        when(mockProduct3.getName()).thenReturn("Product 3 name");
        when(mockProduct3.getRohs()).thenReturn("rohs");
        when(mockProduct3.getSvhcReviewDate()).thenReturn(svhcReviewDate);
        when(mockProduct3.getHasSvhc()).thenReturn(true);
        when(mockProduct3.getSvhc()).thenReturn("svhc");
        when(mockProduct3.getScip()).thenReturn("scip");
        when(mockProduct3.getSvhcURL()).thenReturn("svhc-url");
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

    private void mockProperties() {
        when(messageSource.getMessage("qualityAndLegal.excel.export.sheet.name",
                                      null,
                                      "Report",
                                      Locale.ENGLISH))
          .thenReturn("Report");
        when(messageSource.getMessage("qualityAndLegal.excel.export.header.title",
                                      null,
                                      "Distrelec Article Environmental Compliance Report \n" +
                                      "RoHS 2011/65/EU (RoHS 2015/863/EU) | REACH 1907/2006/EC |\"SCIP\" EU Waste Framework Directive (EU)2018/851",
                                      Locale.ENGLISH))
          .thenReturn("Distrelec Article Environmental Compliance Report \n" +
                      "RoHS 2011/65/EU (RoHS 2015/863/EU) | REACH 1907/2006/EC |\"SCIP\" EU Waste Framework Directive (EU)2018/851");
        when(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.title",
                                      null,
                                      "Customer Details",
                                      Locale.ENGLISH))
          .thenReturn("Customer Details");
        when(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerName",
                                      null,
                                      "Customer Name:",
                                      Locale.ENGLISH))
          .thenReturn("Customer Name:");
        when(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerNumber",
                                      null,
                                      "Customer Number:",
                                      Locale.ENGLISH))
          .thenReturn("Customer Number:");
        when(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerAddress",
                                      null,
                                      "Customer Address:",
                                      Locale.ENGLISH))
          .thenReturn("Customer Address:");
        when(messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerEmailAddress",
                                      null,
                                      "Customer E-Mail Address:",
                                      Locale.ENGLISH))
          .thenReturn("Customer E-Mail Address:");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.title",
                                      null,
                                      "Article Details",
                                      Locale.ENGLISH))
          .thenReturn("Article Details");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.distrelecArticleNumber",
                                      null,
                                      "Distrelec Article Number",
                                      Locale.ENGLISH))
          .thenReturn("Distrelec Article Number");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.manufacturerPartNumber",
                                      null,
                                      "Manufacturers Part Number",
                                      Locale.ENGLISH))
          .thenReturn("Manufacturers Part Number");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.manufacturerName",
                                      null,
                                      "Manufacturer Name",
                                      Locale.ENGLISH))
          .thenReturn("Manufacturer Name");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.description",
                                      null,
                                      "Description",
                                      Locale.ENGLISH))
          .thenReturn("Description");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.euRohsDirective",
                                      null,
                                      "EU RoHS Directive",
                                      Locale.ENGLISH))
          .thenReturn("EU RoHS Directive");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.rohsExemptions",
                                      null,
                                      "RoHS Exemptions",
                                      Locale.ENGLISH))
          .thenReturn("EU RoHS Directive");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.reachRawDate",
                                      null,
                                      "REACH Review Date",
                                      Locale.ENGLISH))
          .thenReturn("REACH Review Date");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.svhcAboveTreshold",
                                      null,
                                      "SVHC Above Threshold",
                                      Locale.ENGLISH))
          .thenReturn("SVHC Above Threshold");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.svhcSubstance",
                                      null,
                                      "SVHC Substance (CAS-Nr.)",
                                      Locale.ENGLISH))
          .thenReturn("SVHC Substance (CAS-Nr.)");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.distrelecScipNumber",
                                      null,
                                      "Distrelec SCIP Number",
                                      Locale.ENGLISH))
          .thenReturn("Distrelec SCIP Number");
        when(messageSource.getMessage("qualityAndLegal.excel.export.articleDetails.manufacturersComplianceUrl",
                                      null,
                                      "Manufacturers Compliance URL",
                                      Locale.ENGLISH))
          .thenReturn("Manufacturers Compliance URL");
        when(messageSource.getMessage("qualityAndLegal.excel.export.disclaimer.text",
                                      null,
                                      "The above information we provide to you is based solely on the information presented to us, under good faith that our suppliers act within their legal requirements. \n"
                                      +
                                      "Distrelec is therefore in no way responsible for any damages or penalties suffered by you as a result of using or relying upon such information.\n"
                                      +
                                      "This document was electronically created and is valid without official signature.",
                                      Locale.ENGLISH))
          .thenReturn("The above information we provide to you is based solely on the information presented to us, under good faith that our suppliers act within their legal requirements. \n"
                      +
                      "Distrelec is therefore in no way responsible for any damages or penalties suffered by you as a result of using or relying upon such information.\n"
                      +
                      "This document was electronically created and is valid without official signature.");
        when(messageSource.getMessage("qualityAndLegal.excel.export.reportDate.title",
                                      null,
                                      "Report Date:",
                                      Locale.ENGLISH))
          .thenReturn("Report Date:");
    }

    @Test
    public void testGenerateEnvironmentalInformationXlsReport() {
        try (XSSFWorkbook result = distQualityAndLegalExcelExportHelper.generateEnvironmentalInformationXlsReport(qualityAndLegalDownloadWsDTO)) {
            assertNotNull(result);

            Sheet sheet = result.getSheet("Report");
            assertNotNull(sheet);
            assertThat(sheet.getPhysicalNumberOfRows(), is(13)); // 3 rows for 3 products, plus 10 rows for headers and other info

            /*
             * Rows are numbered from 1 in Excel, but their count starts from 0 in POI.
             * In the output document the first row (1), and the first column (A) are empty.
             */
            // Empty row
            testFirstRow(sheet);

            // Header
            testHeader(sheet);

            // Customer details
            testCustomerDetails(sheet);

            // Article details
            testArticleDetails(sheet);

            // Disclaimer text
            testDisclaimerText(sheet);

            // Report date
            testReportDate(sheet);
        } catch (IOException e) {
            fail("IOException has occurred: " + e.getMessage());
        }
    }

    private void testFirstRow(Sheet sheet) {
        Row row1 = sheet.getRow(0); // 1
        assertNotNull(row1);
        assertThat(row1.getPhysicalNumberOfCells(), is(0)); // First row is empty
    }

    private void testHeader(Sheet sheet) {
        Row row2 = sheet.getRow(1); // 2
        assertNotNull(row2);
        assertThat(row2.getPhysicalNumberOfCells(), is(10));
        Cell row2cellB = row2.getCell(1);
        assertNotNull(row2cellB);
        assertThat(row2cellB.getStringCellValue(), is("Distrelec Article Environmental Compliance Report \n" +
                                                      "RoHS 2011/65/EU (RoHS 2015/863/EU) | REACH 1907/2006/EC |\"SCIP\" EU Waste Framework Directive (EU)2018/851"));
    }

    private void testCustomerDetails(Sheet sheet) {
        Row row3 = sheet.getRow(2); // 3
        assertNotNull(row3);
        assertThat(row3.getPhysicalNumberOfCells(), is(10));
        Cell row3cellB = row3.getCell(1);
        assertNotNull(row3cellB);
        assertThat(row3cellB.getStringCellValue(), is("Customer Details"));

        Row row4 = sheet.getRow(3); // 4
        assertNotNull(row4);
        assertThat(row4.getPhysicalNumberOfCells(), is(9)); // Due to cell merge it is 9 and not 10
        Cell row4cellB = row4.getCell(1);
        assertNotNull(row4cellB);
        assertThat(row4cellB.getStringCellValue(), is("Customer Name:"));
        Cell row4cellC = row4.getCell(2);
        assertNotNull(row4cellC);
        assertThat(row4cellC.getStringCellValue(), is("Speedy Gonzales"));
        Cell row4cellE = row4.getCell(4);
        assertNotNull(row4cellE);
        assertThat(row4cellE.getStringCellValue(), is("Customer Number:"));
        Cell row4cellF = row4.getCell(5);
        assertNotNull(row4cellF);
        assertThat(row4cellF.getStringCellValue(), is("0123456789"));
        Cell row4cellG = row4.getCell(6);
        assertNotNull(row4cellG);
        assertThat(row4cellG.getStringCellValue(), is(""));

        Row row5 = sheet.getRow(4); // 5
        assertNotNull(row5);
        assertThat(row5.getPhysicalNumberOfCells(), is(10));
        Cell row5cellB = row5.getCell(1);
        assertNotNull(row5cellB);
        assertThat(row5cellB.getStringCellValue(), is("Customer Address:"));
        Cell row5cellC = row5.getCell(2);
        assertNotNull(row5cellC);
        assertThat(row5cellC.getStringCellValue(), is("Address Line 1, postal code, Town"));
        Cell row5cellE = row5.getCell(4);
        assertNotNull(row5cellE);
        assertThat(row5cellE.getStringCellValue(), is("Customer E-Mail Address:"));
        Cell row5cellF = row5.getCell(5);
        assertNotNull(row5cellF);
        assertThat(row5cellF.getStringCellValue(), is("mail@mail.com"));
        Cell row5cellG = row5.getCell(6);
        assertNotNull(row5cellG);
        assertThat(row5cellG.getStringCellValue(), is(""));
    }

    private void testArticleDetails(Sheet sheet) {
        Row row6 = sheet.getRow(5); // 6
        assertNotNull(row6);
        assertThat(row6.getPhysicalNumberOfCells(), is(10));
        Cell row6cellB = row6.getCell(1);
        assertNotNull(row6cellB);
        assertThat(row6cellB.getStringCellValue(), is("Article Details"));

        // table header
        Row row7 = sheet.getRow(6); // 7
        assertNotNull(row7);
        assertThat(row7.getPhysicalNumberOfCells(), is(10));
        Cell row7cellB = row7.getCell(1);
        assertNotNull(row7cellB);
        assertThat(row7cellB.getStringCellValue(), is("Distrelec Article Number"));
        Cell row7cellC = row7.getCell(2);
        assertNotNull(row7cellC);
        assertThat(row7cellC.getStringCellValue(), is("Manufacturers Part Number"));
        Cell row7cellD = row7.getCell(3);
        assertNotNull(row7cellD);
        assertThat(row7cellD.getStringCellValue(), is("Manufacturer Name"));
        Cell row7cellE = row7.getCell(4);
        assertNotNull(row7cellE);
        assertThat(row7cellE.getStringCellValue(), is("Description"));
        Cell row7cellF = row7.getCell(5);
        assertNotNull(row7cellF);
        assertThat(row7cellF.getStringCellValue(), is("EU RoHS Directive"));
        Cell row7cellG = row7.getCell(6);
        assertNotNull(row7cellG);
        assertThat(row7cellG.getStringCellValue(), is("REACH Review Date"));
        Cell row7cellH = row7.getCell(7);
        assertNotNull(row7cellH);
        assertThat(row7cellH.getStringCellValue(), is("SVHC Above Threshold"));
        Cell row7cellI = row7.getCell(8);
        assertNotNull(row7cellI);
        assertThat(row7cellI.getStringCellValue(), is("SVHC Substance (CAS-Nr.)"));
        Cell row7cellJ = row7.getCell(9);
        assertNotNull(row7cellJ);
        assertThat(row7cellJ.getStringCellValue(), is("Distrelec SCIP Number"));
        Cell row7cellK = row7.getCell(10);
        assertNotNull(row7cellK);
        assertThat(row7cellK.getStringCellValue(), is("Manufacturers Compliance URL"));

        // product1
        Row row8 = sheet.getRow(7); // 8
        assertNotNull(row8);
        assertThat(row8.getPhysicalNumberOfCells(), is(10));
        Cell row8cellB = row8.getCell(1);
        assertNotNull(row8cellB);
        assertThat(row8cellB.getStringCellValue(), is("11111111"));
        Cell row8cellC = row8.getCell(2);
        assertNotNull(row8cellC);
        assertThat(row8cellC.getStringCellValue(), is("man-11111111"));
        Cell row8cellD = row8.getCell(3);
        assertNotNull(row8cellD);
        assertThat(row8cellD.getStringCellValue(), is("Manufacturer"));
        Cell row8cellE = row8.getCell(4);
        assertNotNull(row8cellE);
        assertThat(row8cellE.getStringCellValue(), is("man-11111111 - Product 1 name, Manufacturer"));
        Cell row8cellF = row8.getCell(5);
        assertNotNull(row8cellF);
        assertThat(row8cellF.getStringCellValue(), is("rohs"));
        Cell row8cellG = row8.getCell(6);
        assertNotNull(row8cellG);
        assertThat(row8cellG.getDateCellValue(), is(svhcReviewDate));
        Cell row8cellH = row8.getCell(7);
        assertNotNull(row8cellH);
        assertThat(row8cellH.getStringCellValue(), is("Yes"));
        Cell row8cellI = row8.getCell(8);
        assertNotNull(row8cellI);
        assertThat(row8cellI.getStringCellValue(), is("svhc"));
        Cell row8cellJ = row8.getCell(9);
        assertNotNull(row8cellJ);
        assertThat(row8cellJ.getStringCellValue(), is("scip"));
        Cell row8cellK = row8.getCell(10);
        assertNotNull(row8cellK);
        assertThat(row8cellK.getStringCellValue(), is("svhc-url"));

        // product2
        Row row9 = sheet.getRow(8); // 9
        assertNotNull(row9);
        assertThat(row9.getPhysicalNumberOfCells(), is(10));
        Cell row9cellB = row9.getCell(1);
        assertNotNull(row9cellB);
        assertThat(row9cellB.getStringCellValue(), is("22222222"));
        Cell row9cellC = row9.getCell(2);
        assertNotNull(row9cellC);
        assertThat(row9cellC.getStringCellValue(), is("man-22222222"));
        Cell row9cellD = row9.getCell(3);
        assertNotNull(row9cellD);
        assertThat(row9cellD.getStringCellValue(), is("Manufacturer"));
        Cell row9cellE = row9.getCell(4);
        assertNotNull(row9cellE);
        assertThat(row9cellE.getStringCellValue(), is("man-22222222 - Product 2 name, Manufacturer"));
        Cell row9cellF = row9.getCell(5);
        assertNotNull(row9cellF);
        assertThat(row9cellF.getStringCellValue(), is("rohs"));
        Cell row9cellG = row9.getCell(6);
        assertNotNull(row9cellG);
        assertThat(row9cellG.getDateCellValue(), is(svhcReviewDate));
        Cell row9cellH = row9.getCell(7);
        assertNotNull(row9cellH);
        assertThat(row9cellH.getStringCellValue(), is("No"));
        Cell row9cellI = row9.getCell(8);
        assertNotNull(row9cellI);
        assertThat(row9cellI.getStringCellValue(), is("svhc"));
        Cell row9cellJ = row9.getCell(9);
        assertNotNull(row9cellJ);
        assertThat(row9cellJ.getStringCellValue(), is("scip"));
        Cell row9cellK = row9.getCell(10);
        assertNotNull(row9cellK);
        assertThat(row9cellK.getStringCellValue(), is("svhc-url"));

        // product3
        Row row10 = sheet.getRow(9); // 10
        assertNotNull(row10);
        assertThat(row10.getPhysicalNumberOfCells(), is(10));
        Cell row10cellB = row10.getCell(1);
        assertNotNull(row10cellB);
        assertThat(row10cellB.getStringCellValue(), is("33333333"));
        Cell row10cellC = row10.getCell(2);
        assertNotNull(row10cellC);
        assertThat(row10cellC.getStringCellValue(), is("man-33333333"));
        Cell row10cellD = row10.getCell(3);
        assertNotNull(row10cellD);
        assertThat(row10cellD.getStringCellValue(), is("Manufacturer"));
        Cell row10cellE = row10.getCell(4);
        assertNotNull(row10cellE);
        assertThat(row10cellE.getStringCellValue(), is("man-33333333 - Product 3 name, Manufacturer"));
        Cell row10cellF = row10.getCell(5);
        assertNotNull(row10cellF);
        assertThat(row10cellF.getStringCellValue(), is("rohs"));
        Cell row10cellG = row10.getCell(6);
        assertNotNull(row10cellG);
        assertThat(row10cellG.getDateCellValue(), is(svhcReviewDate));
        Cell row10cellH = row10.getCell(7);
        assertNotNull(row10cellH);
        assertThat(row10cellH.getStringCellValue(), is("Yes"));
        Cell row10cellI = row10.getCell(8);
        assertNotNull(row10cellI);
        assertThat(row10cellI.getStringCellValue(), is("svhc"));
        Cell row10cellJ = row10.getCell(9);
        assertNotNull(row10cellJ);
        assertThat(row10cellJ.getStringCellValue(), is("scip"));
        Cell row10cellK = row10.getCell(10);
        assertNotNull(row10cellK);
        assertThat(row10cellK.getStringCellValue(), is("svhc-url"));
    }

    private void testDisclaimerText(Sheet sheet) {
        Row row11 = sheet.getRow(10); // 11
        assertNotNull(row11);
        assertThat(row11.getPhysicalNumberOfCells(), is(2));
        Cell row11cellB = row11.getCell(1);
        assertNotNull(row11cellB);
        assertThat(row11cellB.getStringCellValue(),
                   is("The above information we provide to you is based solely on the information presented to us, under good faith that our suppliers act within their legal requirements. \n"
                      +
                      "Distrelec is therefore in no way responsible for any damages or penalties suffered by you as a result of using or relying upon such information.\n"
                      +
                      "This document was electronically created and is valid without official signature."));
    }

    private void testReportDate(Sheet sheet) {
        Row row12 = sheet.getRow(11); // 12
        assertNotNull(row12);
        assertThat(row12.getPhysicalNumberOfCells(), is(3));

        Cell row12cellB = row12.getCell(1);
        assertNotNull(row12cellB);
        assertThat(row12cellB.getStringCellValue(), is("Report Date:"));

        Cell row12cellC = row12.getCell(2);
        assertNotNull(row12cellC);
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date valueFromExcelCell = formatter.parse(formatter.format(row12cellC.getDateCellValue()));
            Date now = formatter.parse(formatter.format(new Date()));
            assertThat(valueFromExcelCell, is(now));
        } catch (ParseException e) {
            fail("ParseException has occurred: " + e.getMessage());
        }
    }

}
