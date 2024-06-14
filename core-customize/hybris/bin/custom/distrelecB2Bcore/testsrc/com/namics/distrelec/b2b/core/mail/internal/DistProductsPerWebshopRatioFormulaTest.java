/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(Parameterized.class)
public class DistProductsPerWebshopRatioFormulaTest {

    @InjectMocks
    private DistProductsPerWebshopJob distProductsPerWebshopJob;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Parameter(0)
    public Double expectedRatio;

    @Parameter(1)
    public Double newVolume;

    @Parameter(2)
    public Double differenceWithPreviousVolume;
    
    @Parameters
    public static Collection<Double[]> data() {
        return Arrays.asList(new Double[][] {
                {0.25D, 100D, 20D}, // new is 100, difference is  +20, so old value was  80. oldValue/difference so is +25%
                {-0.2D, 80D, -20D}, // new is  80, difference is  -20, so old value was 100. oldValue/difference so is -20%
                {1D, 100D, 100D},   // new is 100, difference is +100, so old value was   0. ratio is so 100%
                {0D, 0D, 0D}        // new is   0, difference is   +0, so old value was   0. There is no change, ratio is so 0%
        });
    }


    @Test
    public void testRatioFormula() {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        final XSSFSheet sheet = distProductsPerWebshopJob.getSheet(workbook);
        final XSSFRow row = sheet.createRow(0);
        final XSSFCell newVolumeCell = row.createCell(0);
        newVolumeCell.setCellValue(newVolume);
        final XSSFCell differenceCell = row.createCell(1);
        differenceCell.setCellValue(differenceWithPreviousVolume);
        final XSSFCell ratioCell = row.createCell(2);
        ratioCell.setCellFormula(distProductsPerWebshopJob.createRatioFormula(newVolumeCell.getColumnIndex(), newVolumeCell.getRowIndex()));
        final FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        final CellValue result = evaluator.evaluate(ratioCell);
        assertEquals(expectedRatio, result.getNumberValue(), 0D);
    }

}
