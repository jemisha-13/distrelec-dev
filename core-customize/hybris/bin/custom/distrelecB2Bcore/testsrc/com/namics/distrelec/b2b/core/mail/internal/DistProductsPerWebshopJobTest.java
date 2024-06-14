/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.model.jobs.DistInternalCronjobModel;
import com.namics.distrelec.b2b.core.model.jobs.DistProductsPerWebshopCronjobModel;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

@ManualTest
public class DistProductsPerWebshopJobTest {

    @Mock
    private DistProductsPerWebshopDao distProductsPerWebshopDao;

    @Mock
    private DistProductsPerWebshopCronjobModel productPerWebshopCronJob;

    @InjectMocks
    private DistProductsPerWebshopJob4Test distProductsPerWebshopJob4Test;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {
        final List<List<String>> mockedueryResult = Arrays.asList(
                Arrays.asList("7320", "Distrelec AT", "Lightning", "1", "10"), //
                Arrays.asList("7320", "Distrelec AT", "Lightning", "0", "20"), //
                Arrays.asList("7320", "Distrelec AT", "Semiconductors", "1", "10"), //
                Arrays.asList("7310", "Distrelec CH", "Optoelectronics", "1", "1"), //
                Arrays.asList("7310", "Distrelec CH", "Semiconductors", "1", "2") //
        );
        Mockito.doReturn(mockedueryResult).when(distProductsPerWebshopDao).getQueryResult();
        final PerformResult result = distProductsPerWebshopJob4Test.perform(productPerWebshopCronJob);
        assertEquals(CronJobResult.SUCCESS, result.getResult());
    }
}

class DistProductsPerWebshopJob4Test extends DistProductsPerWebshopJob {

    private static final String OUTPUT_FILE_FOLDER = "/Users/dem/Documents/projects/distrelec-shop";

    @Override
    protected boolean sendEmail(final DistInternalCronjobModel productPerWebshopCronJob, final File file) {
        return true;
    }

    @Override
    protected void writeFileToFilesystem(final XSSFWorkbook workbook, final File file) {
        try {
            final File temp = File.createTempFile("temp-file-name", ".xlsx");
            super.writeFileToFilesystem(workbook, temp);
        } catch (final Exception e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected File getXlsxFile() {
        final URL fileUrl = this.getClass().getResource("/distrelecB2Bcore/test/productsPerWebshop/ProductsPerWebshops.xlsx");
        try {
            return new File(fileUrl.toURI());
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
