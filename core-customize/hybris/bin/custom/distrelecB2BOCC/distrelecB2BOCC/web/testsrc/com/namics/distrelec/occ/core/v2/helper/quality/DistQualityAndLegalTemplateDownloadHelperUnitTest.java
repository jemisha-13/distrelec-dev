package com.namics.distrelec.occ.core.v2.helper.quality;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.i18n.I18NService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistQualityAndLegalTemplateDownloadHelperUnitTest {

    @InjectMocks
    private DistQualityAndLegalTemplateDownloadHelper distQualityAndLegalTemplateDownloadHelper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private I18NService i18nService;

    @Before
    public void setup() {
        when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage("qualityAndLegal.template.import.sheet.name",
                                      null,
                                      "import",
                                      Locale.ENGLISH))
          .thenReturn("import");
        when(messageSource.getMessage("qualityAndLegal.template.import.header.cell1.title",
                                      null,
                                      "Elfa/Distrelec Article Number",
                                      Locale.ENGLISH))
          .thenReturn("Elfa/Distrelec Article Number");
        when(messageSource.getMessage("qualityAndLegal.template.import.header.cell2.title",
                                      null,
                                      "Your Reference",
                                      Locale.ENGLISH))
          .thenReturn("Your Reference");
    }

    @Test
    public void testGenerateFileUploadTemplate() {
        try (XSSFWorkbook result = distQualityAndLegalTemplateDownloadHelper.generateFileUploadTemplate()) {
            assertNotNull(result);

            Sheet sheet = result.getSheet("import");
            assertNotNull(sheet);
            assertThat(sheet.getPhysicalNumberOfRows(), is(1));

            Row row = sheet.getRow(0);
            assertNotNull(row);
            assertThat(row.getPhysicalNumberOfCells(), is(2));

            Cell cell1 = row.getCell(0);
            assertNotNull(cell1);
            assertThat(cell1.getStringCellValue(), is("Elfa/Distrelec Article Number"));
            testGenerateFileUploadTemplateTestCellStyleForCell(cell1);

        } catch (IOException e) {
            fail("IOException has occurred: " + e.getMessage());
        }
    }

    private void testGenerateFileUploadTemplateTestCellStyleForCell(Cell cell) {
        CellStyle cellStyle = cell.getCellStyle();
        assertNotNull(cellStyle);

        assertThat(cellStyle.getFontIndexAsInt(), is(1));
        assertThat(cellStyle.getWrapText(), is(false));
        assertThat(cellStyle.getVerticalAlignment(), is(VerticalAlignment.BOTTOM));
        assertThat(cellStyle.getAlignment(), is(HorizontalAlignment.LEFT));
    }

}
