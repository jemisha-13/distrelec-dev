package com.namics.hybris.toolbox.impex;

import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.junit.Before;
import org.junit.Test;

public class ImpexImportFromDirectoryTaskTest extends HybrisJUnit4TransactionalTest {

    protected ImpexImportFromDirectoryTask importTask;

    @Before
    public void setUp() {
        importTask = new ImpexImportFromDirectoryTask();
        importTask.setImportDirectory("classpath:test/importexport");
        importTask.setOutputImportDetail(true);
        importTask.setWildcard("ImpexTestImport.impex");
    }

    @Test
    public void testPerformTask() throws Exception {
        importTask.performTask();

    }

}
