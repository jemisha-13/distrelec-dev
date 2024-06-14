package com.namics.distrelec.b2b.core.service.nps.jobs;

import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.model.jobs.NetPromoterScoreExportCronJobModel;
import com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 *
 */
@UnitTest
public class NetPromoterScoreExportJobTest {

    private static final String TEST_CODE = "123456";
    private static final String COMPANY_NAME = "Test Company";
    private static final String TEST_COMPANY_NAME = COMPANY_NAME;
    private static final String TEST_DOMAIN = "www.distrelec.ch";
    private static final String TEST_EMAIL_ADDRESS = "test@distrelec.com";
    private static final String TEST_ERP_CONTACT_ID = "12345678";
    private static final String TEST_FIRSTNAME = "Distrelec";
    private static final String TEST_SURNAME = "Datwyler";
    private static final Date TODAY = new Date();
    private static final Integer NO_SCORE = Integer.valueOf(-1);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Mock
    private ModelService mockedModelService;
    @Mock
    private DistNetPromoterScoreService mockedDistNetPromoterScoreService;
    @Mock
    private ConfigurationService mockedConfigurationService;
    @Mock
    private NetPromoterScoreExportCronJobModel mockedCronJob;
    @Mock
    private Configuration configuration;
    @InjectMocks
    private NetPromoterScoreExportJob netPromoterScoreExportJob;

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        mockedModelService = Mockito.mock(ModelService.class);
        mockedDistNetPromoterScoreService = Mockito.mock(DistNetPromoterScoreService.class);
        mockedConfigurationService = Mockito.mock(ConfigurationService.class);
        mockedCronJob = Mockito.mock(NetPromoterScoreExportCronJobModel.class);
        String configurablePath="C:\\Export";
        when(configuration.getString(anyString())).thenReturn(configurablePath);
        when(mockedConfigurationService.getConfiguration()).thenReturn(configuration);
        when(mockedModelService.isModified(anyObject())).thenReturn(true);
        setup(netPromoterScoreExportJob);
    }

    @Test
    public void testNoWhiteSpaceIsFilteredOutWhenNoExportsExist(){
        when(mockedDistNetPromoterScoreService.findExported(Mockito.anyBoolean())).thenReturn(Collections.EMPTY_LIST);
        final PerformResult result = netPromoterScoreExportJob.perform(mockedCronJob);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertEquals(result.getResult(), CronJobResult.SUCCESS);
        assertEquals(result.getStatus(), CronJobStatus.FINISHED);
    }

    @Test
    public void testNoWhiteSpaceIsFilteredOutExportsExist(){
        final List<DistNetPromoterScoreModel> models = createNPSData();
        when(mockedDistNetPromoterScoreService.findExported(false)).thenReturn(models);
        when(mockedCronJob.getLastExportDate()).thenReturn(TODAY);
        final PerformResult result = netPromoterScoreExportJob.perform(mockedCronJob);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertEquals(result.getResult(), CronJobResult.SUCCESS);
        assertEquals(result.getStatus(), CronJobStatus.FINISHED);
    }

    @Test
    public void testNoScoresAreFilteredOut() throws IOException {
        final List<DistNetPromoterScoreModel> models = createNPSData();
        when(mockedDistNetPromoterScoreService.findExported(false)).thenReturn(models);
        when(mockedCronJob.getLastExportDate()).thenReturn(TODAY);
        doNothing().when(mockedModelService).saveAll(anyList());

        final PerformResult result = netPromoterScoreExportJob.perform(mockedCronJob);
        assertNotNull(result);
        assertNotNull(result.getResult());
        assertEquals(result.getResult(), CronJobResult.SUCCESS);
        assertEquals(result.getStatus(), CronJobStatus.FINISHED);
     }
    
    private List<DistNetPromoterScoreModel> createNPSData() {
        final List<DistNetPromoterScoreModel> npsData = new ArrayList<>();
        DistNetPromoterScoreModel npsModel  = new DistNetPromoterScoreModel();
        npsModel.setCode(TEST_CODE);
        npsModel.setCompanyName(TEST_COMPANY_NAME);
        npsModel.setDeliveryDate(TODAY);
        npsModel.setDomain(TEST_DOMAIN);
        npsModel.setEmail(TEST_EMAIL_ADDRESS);
        npsModel.setErpContactID(TEST_ERP_CONTACT_ID);
        npsModel.setExported(false);
        npsModel.setFirstname(TEST_FIRSTNAME);
        npsModel.setLastname(TEST_SURNAME);
        npsModel.setCreationtime(new Date());
        npsModel.setValue(NO_SCORE);
        npsData.add(npsModel);
        return npsData;
    }
    
    private void setup(final NetPromoterScoreExportJob netPromoterScoreExportJob) throws Exception {
    	netPromoterScoreExportJob.setDistNetPromoterScoreService(mockedDistNetPromoterScoreService);
    	netPromoterScoreExportJob.setConfigurationService(mockedConfigurationService);
    	netPromoterScoreExportJob.setModelService(mockedModelService);
    }
}
