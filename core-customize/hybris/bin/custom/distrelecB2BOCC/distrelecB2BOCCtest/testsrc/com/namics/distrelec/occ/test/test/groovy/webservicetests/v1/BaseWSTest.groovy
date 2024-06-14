/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 *
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.v1

import com.namics.distrelec.occ.test.setup.TestSetupUtils
import com.namics.distrelec.occ.test.test.groovy.webservicetests.TestNamePrinter
import com.namics.distrelec.occ.test.test.groovy.webservicetests.TestUtil
import com.namics.distrelec.occ.test.test.groovy.webservicetests.config.TestConfigFactory
import com.namics.distrelec.occ.test.test.groovy.webservicetests.docu.BaseWSTestWatcher
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.core.Registry
import de.hybris.platform.servicelayer.config.ConfigurationService
import de.hybris.platform.util.Config
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule

import static org.junit.Assert.fail

/**
 * Base class for all groovy webservice tests
 */
@ManualTest
class BaseWSTest {
    private static final ThreadLocal<Boolean> SERVER_NEEDS_SHUTDOWN = new ThreadLocal<Boolean>();
    protected final TestUtil testUtil;
    protected final ConfigObject config;

    private final ConfigurationService configurationService = Registry.getApplicationContext()
            .getBean("configurationService", ConfigurationService.class)

    protected String getConfigurationProperty(final String propertyName) {
        return configurationService.getConfiguration().getString(propertyName)
    }

    @BeforeClass
    public static void startServerIfNeeded() {
        if (!TestSetupUtils.isServerStarted()) {
            SERVER_NEEDS_SHUTDOWN.set(true);
            TestSetupUtils.startServer();
        }
    }

    @AfterClass
    public static void stopServerIfNeeded() {
        if (SERVER_NEEDS_SHUTDOWN.get()) {
            TestSetupUtils.stopServer();
            SERVER_NEEDS_SHUTDOWN.set(false);
        }
    }

    @Before
    public void ignoreIf() {
        org.junit.Assume.assumeTrue(Config.getBoolean("distrelecB2BOCCtest.enableV1", false))
    }

    @Rule
    public TestNamePrinter tnp = new TestNamePrinter(System.out);

    protected BaseWSTest() {
        config = TestConfigFactory.createConfig("v1", "/distrelecB2BOCCtest/groovytests-property-file.groovy");
        testUtil = new TestUtil(config);
    }

    /**
     * Ancillary method to mark that some webservice resources is not tested
     * @param resource
     */
    protected void missingTest(String resource) {
        fail("Missing test for resource :" + resource);
    }

    @Rule
    public BaseWSTestWatcher testWatcher = new BaseWSTestWatcher();
}
