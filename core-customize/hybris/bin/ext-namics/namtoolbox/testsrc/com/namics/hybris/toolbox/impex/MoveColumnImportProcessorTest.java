package com.namics.hybris.toolbox.impex;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class MoveColumnImportProcessorTest extends ServicelayerTransactionalTest {

    @Before
    public void setUp() throws Exception {
        createCoreData();
        createDefaultCatalog();
    }

    @Test
    public void testMovingColumnImportProcessor() throws Exception {

        importCsv("/namtoolbox/test/impex/moveColumnImportProcessorTest.impex", "UTF-8");

    }

}
