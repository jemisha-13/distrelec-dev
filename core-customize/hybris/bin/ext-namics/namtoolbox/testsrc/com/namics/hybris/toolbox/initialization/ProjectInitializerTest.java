/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.initialization;

import de.hybris.platform.testframework.HybrisJUnit4ClassRunner;
import de.hybris.platform.testframework.runlistener.ItemCreationListener;
import de.hybris.platform.testframework.runlistener.LogRunListener;
import de.hybris.platform.testframework.runlistener.PlatformRunListener;
import de.hybris.platform.testframework.RunListeners;
import de.hybris.platform.testframework.runlistener.TransactionRunListener;
import de.hybris.platform.testframework.Transactional;
import de.hybris.platform.util.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HybrisJUnit4ClassRunner.class)
@RunListeners({ TransactionRunListener.class, ItemCreationListener.class, LogRunListener.class, PlatformRunListener.class })
@Transactional
public class ProjectInitializerTest {

    protected Map<String, Object> props;

    protected Properties results;

    /**
     * The original hybris properties to roll back after each test.
     */
    private Map<?, ?> originalConfigPropertiesMap;

    /**
     * Puts all properties from <code>configPropertiesMap</code> to the hybris context.
     */
    private void loadPropertiesToConfig(final Map<?, ?> configPropertiesMap) {
        for (final Object key : configPropertiesMap.keySet()) {
            Config.setParameter((String) key, (String) configPropertiesMap.get(key));
        }
    }

    @Before
    public void setUp() throws Exception {
        originalConfigPropertiesMap = Config.getAllParameters();

        this.props = new HashMap<String, Object>();

        // this.props.put("nullPlaceholder", null);
        this.props.put("emptyPlaceholder", "");
        this.props.put("noPlaceholder", "Text without a Placeholder.");
        this.props.put("useOfNormalePlaceholder", "Text with a ${noPlaceholder} Placeholder.");
        this.props.put("outerPlaceholder", "Text with inner ${innerPlaceholder} Placeholder.");
        this.props.put("innerPlaceholder", "Text with inner ${moreinnerPlaceholder} Placeholder.");
        this.props.put("moreinnerPlaceholder", "Inner Text without a Placeholder.");
        this.props.put("circularPlaceholder", "Text with a circular ${circularPlaceholder1} Placeholder.");
        this.props.put("circularPlaceholder1", "Text with a circular ${circularPlaceholder2} Placeholder.");
        this.props.put("circularPlaceholder2", "Text with a circular ${circularPlaceholder3} Placeholder.");
        this.props.put("circularPlaceholder3", "Text without a Placeholder.");
        loadPropertiesToConfig(this.props);

        this.results = new Properties();
        this.results.setProperty("noPlaceholder", "Text without a Placeholder.");
        this.results.setProperty("useOfNormalePlaceholder", "Text with a Text without a Placeholder. Placeholder.");
        this.results.setProperty("outerPlaceholder", "Text with inner Text with inner Inner Text without a Placeholder. Placeholder. Placeholder.");

    }

    @After
    public void tearDown() throws FileNotFoundException, IOException {
        loadPropertiesToConfig(originalConfigPropertiesMap);
    }

    @Test
    public void testHybrisPlaceholderReplacement() {
        ProjectInitializer.resolveNestedConfigurationProperties();
        for (final Object key : this.results.keySet()) {
            Assert.assertEquals("Test for property key '" + key + "' failed.", this.results.get(key), Config.getParameter((String) key));
        }
    }

}
