package com.namics.hybris.toolbox.impex;

import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ImpexExportToDirectoryTaskTest extends HybrisJUnit4TransactionalTest {

    protected ImpexExportToDirectoryTask exportTask;

    protected String tempDirectory = System.getProperty("java.io.tmpdir");

    @Before
    public void setUp() {
        final List<String> exportList = new ArrayList<String>();
        exportList.add("classpath:test/importexport/ImpexTestExport.impex");

        exportTask = new ImpexExportToDirectoryTask();
        exportTask.setExportDirectory("file://" + tempDirectory);
        exportTask.setOutputExportDetail(true);
        exportTask.setImpexFilelist(exportList);
    }

    @Test
    public void testPerformTask() throws Exception {
        exportTask.performTask();

    }

}
