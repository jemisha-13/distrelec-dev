package com.namics.hybris.ffsearch.export;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.FactFinder;
import com.namics.hybris.exports.model.export.DistExportCronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DistFactFinderExportHelperTest {

    private static final String CRONJOB_CODE = "CRONJOB_CODE";
    private static final String DIRECTORY = "/DIRECTORY";
    private static final String CRONJOB_LOCAL_CONFIG_KEY = FactFinder.EXPORT_DIRECTORY_PREFIX + CRONJOB_CODE + FactFinder.EXPORT_DIRECTORY_SUFFIX;

    private final DistFactFinderExportHelper exportHelper = new DistFactFinderExportHelper();

    private Configuration configuration;

    @Before
    public void prepare() {
        ConfigurationService configurationService = mock(ConfigurationService.class);
        configuration = mock(Configuration.class);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        exportHelper.setConfigurationService(configurationService);
    }

    @Test
    public void testGetUploadDirectoryNameFromCronJobOverridenByLocalProperties() {
        DistExportCronJobModel cronjob = mock(DistExportCronJobModel.class);
        when(cronjob.getCode()).thenReturn(CRONJOB_CODE);

        when(configuration.getString(CRONJOB_LOCAL_CONFIG_KEY)).thenReturn(DIRECTORY);

        String actualDirectory = exportHelper.getUploadDirectoryName(cronjob, true);

        assertEquals(DIRECTORY, actualDirectory);
    }

    @Test
    public void testGetUploadDirectoryNameFromCronJob() {
        DistExportCronJobModel cronjob = mock(DistExportCronJobModel.class);
        when(cronjob.getCode()).thenReturn(CRONJOB_CODE);
        when(cronjob.getExportDirectory()).thenReturn(DIRECTORY);

        String actualDirectory = exportHelper.getUploadDirectoryName(cronjob, true);

        assertEquals(DIRECTORY, actualDirectory);
    }

    @Test
    public void testGetUploadDirectoryFromLocalProperties() {
        when(configuration.getString(FactFinder.EXPORT_UPLOAD_DIRECTORY, StringUtils.EMPTY)).thenReturn(DIRECTORY);

        String actualDirectory = exportHelper.getUploadDirectoryName(null, false);

        assertEquals(DIRECTORY, actualDirectory);
    }
}
