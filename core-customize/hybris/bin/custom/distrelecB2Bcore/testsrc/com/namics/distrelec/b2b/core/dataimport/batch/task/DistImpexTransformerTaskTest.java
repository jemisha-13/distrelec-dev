package com.namics.distrelec.b2b.core.dataimport.batch.task;

import com.namics.distrelec.b2b.core.dataimport.batch.DistBatchHeader;
import com.namics.distrelec.b2b.core.dataimport.batch.converter.DistPriceImpexConverter;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@IntegrationTest
public class DistImpexTransformerTaskTest {

    @Mock
    private DistBatchHeader batchHeader;
    @Mock
    private DistPriceImpexConverter impexConverter;
    @InjectMocks
    private DistImpexTransformerTask task;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private CSVReader reader;

    private static final String RESPONSE = "\t;0164562947;11026382;SalesOrg_UPG_7801_01;EUR;;1;1;22.75;20190401000000;20191231235959;ZN00;true;20181228100633;20190622180915560\n" +
            "\t\t\t\t;0164562947;11026382;SalesOrg_UPG_7801_01;EUR;;1;10;20.47;20190401000000;20191231235959;ZN00;true;20181228100633;20190622180915560\n" +
            "\t\t\t\t;0164562947;11026382;SalesOrg_UPG_7801_01;EUR;;1;25;19.33;20190401000000;20191231235959;ZN00;true;20181228100633;20190622180915560";

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown(){
        folder.delete();
    }


    @Test
    public void testZeroUnitFactorAreCoercedToOne() throws IOException {
        final File erpFile = folder.newFile("erp_price10scales-7801-20190622180915460.csv");
        FileUtils.writeStringToFile(erpFile, "PriceConditionID;PriceListType;Materialnumber;Salesorganisation;Currency;PriceUnitFaktor;Scale_1;Price_1;Scale_2;Price_2;Scale_3;Price_3;Scale_4;Price_4;Scale_5;Price_5;Scale_6;Price_6;Scale_7;Price_7;Scale_8;Price_8;Scale_9;Price_9;Scale_10;Price_10;DateValidFrom;DateValidUntil;SpecialPrice;Deleted;Timestamp_modificat");
        FileUtils.writeStringToFile(erpFile, "0164562947;01;11026382;7801;EUR;0;1;22.75;10;20.47;25;19.33;0;0.0;0;0.0;0;0.0;;;;;;;;;20190401;20191231;ZN00;false;20181228104633");

        final File impexFile = folder.newFile("impex_1_erp_price10scales-7310-20190620014311563.csv");

        reader = new CSVReader(erpFile, CSVConstants.HYBRIS_ENCODING);
        CSVReader spiedReader = spy(reader);

        when(impexConverter.getVersion()).thenReturn("10_SCALES");
        when(impexConverter.filter(anyMapOf(Integer.class, String.class))).thenReturn(true);
        when(impexConverter.convertInsertUpdate(anyMap(), anyLong(), anyString())).thenReturn(RESPONSE);

        final boolean converted = task.convertFile(batchHeader, erpFile, impexFile, impexConverter);
        assertTrue(converted);

        final String fileAsString = FileUtils.readFileToString(impexFile);
        assertEquals("null\n" + RESPONSE,fileAsString);

        final String[] chunks = fileAsString.split(";");
        assertEquals("1", chunks[6]);

    }
}
