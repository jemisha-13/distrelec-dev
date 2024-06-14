/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.addons

import com.namics.distrelec.occ.test.setup.TestSetupUtils
import de.hybris.platform.util.Config
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([ExtendedCartTests.class, AccProductTests.class, ExtendedCustomersTests.class, ExtendedCustomersTests.class])
class AllAccTests {

    @BeforeClass
    public static void setUpClass() {
        if (Config.getBoolean("distrelecB2BOCCtest.enableAccTest", false)
                && Config.getBoolean("distrelecB2BOCCtest.enableV1", false)) {
            TestSetupUtils.loadData();
            TestSetupUtils.startServer();
        }
    }

    @AfterClass
    public static void tearDown() {
        if (Config.getBoolean("distrelecB2BOCCtest.enableAccTest", false)
                && Config.getBoolean("distrelecB2BOCCtest.enableV1", false)) {
            TestSetupUtils.stopServer();
            TestSetupUtils.cleanData();
        }
    }

    @Test
    public static void testing() {
        //dummy test class
    }
}
