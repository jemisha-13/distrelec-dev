/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.platform.testframework.HybrisJUnit4ClassRunner;
import de.hybris.platform.testframework.runlistener.LogRunListener;
import de.hybris.platform.testframework.runlistener.PlatformRunListener;
import de.hybris.platform.testframework.RunListeners;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

@RunWith(HybrisJUnit4ClassRunner.class)
@RunListeners({ LogRunListener.class, PlatformRunListener.class })
public class HybrisConfigurationPropertyPlaceholderConfigurerTest {

    protected HybrisConfigurationPropertyPlaceholderConfigurer configurer;
    protected Properties props;

    @Before
    public void setUp() throws Exception {
        configurer = new HybrisConfigurationPropertyPlaceholderConfigurer();
        props = new Properties();
        props.setProperty("testKey", "testValue");
        configurer.setProperties(props);
    }

    @Test
    public void testResolvePlaceholderStringPropertiesInt() {
        assertEquals("testValue", configurer.resolvePlaceholder("testKey", props, PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_NEVER));
        assertEquals("value", configurer.resolvePlaceholder("namtoolbox.key", props, PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_NEVER));
        assertNull(configurer.resolvePlaceholder("migroscore.keykey", props, PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_NEVER));
    }

    @Test
    public void testResolvePlaceholderStringProperties() {
        assertEquals("testValue", configurer.resolvePlaceholder("testKey", props));
        assertEquals("value", configurer.resolvePlaceholder("namtoolbox.key", props));
        assertNull(configurer.resolvePlaceholder("migroscore.keykey", props));
    }

    @Test
    public void testResolveHybrisPropertyPlaceholder() {
        assertEquals("value", configurer.resolveHybrisPropertyPlaceholder("namtoolbox.key"));
        assertNull(configurer.resolveHybrisPropertyPlaceholder("migroscore.keykey"));
    }

}
