/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.spring;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import de.hybris.platform.util.Config;

/**
 * <p>
 * Additional to spring's {@link PropertyPlaceholderConfigurer}, the <code>HybrisConfigurationPropertyPlaceholderConfigurer</code> is
 * looking in the configuration map of hybris for a value.
 * </p>
 * 
 * <p>
 * hybris exposes its configuration properties by the {@link Config} class, e.g.
 * 
 * <pre>
 * String value = Config.getString(..., "default value");
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Purpose of spring's property placeholder configurer:<br>
 * <i>A property resource configurer that resolves placeholders in bean property values of context definitions. It pulls values from a
 * properties file into bean definitions.</i>
 * </p>
 * 
 * <p>
 * In hybris 3.1.x, you could load properties with a definition like this:
 * 
 * <pre>
 * &lt;beans xmlns="http://www.springframework.org/schema/beans"
 *     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *     xmlns:context="http://www.springframework.org/schema/context"
 *     xsi:schemaLocation="http://www.springframework.org/schema/beans
 *     http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
 *     http://www.springframework.org/schema/context
 *     http://www.springframework.org/schema/context/spring-context-2.5.xsd"&gt;
 *     
 *  &lt;context:property-placeholder location="classpath:runtime.properties" /&gt;
 *  
 * &lt;/beans&gt;
 * </pre>
 * 
 * This way doesn't work in hybris 4.x, because the file runtime.properties doesn't exists. Use the following code instead:
 * 
 * <pre>
 * &lt;beans xmlns="http://www.springframework.org/schema/beans"
 *  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;
 * 
 *     &lt;bean class="com.namics.migros.hybris.spring.HybrisConfigurationPropertyPlaceholderConfigurer" &gt;&lt;/bean&gt;
 * &lt;/beans&gt;
 * </pre>
 * 
 * @author jonathan.weiss, namics ag
 * @since MGB PIM 1.0
 * 
 */
public class HybrisConfigurationPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#resolvePlaceholder(java.lang.String,
     * java.util.Properties, int)
     */
    @Override
    protected String resolvePlaceholder(final String placeholder, final Properties props, final int systemPropertiesMode) {
        final String value = super.resolvePlaceholder(placeholder, props, systemPropertiesMode);
        if (value == null) {
            return resolveHybrisPropertyPlaceholder(placeholder);
        } else {
            return value;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#resolvePlaceholder(java.lang.String,
     * java.util.Properties)
     */
    @Override
    protected String resolvePlaceholder(final String placeholder, final Properties props) {
        final String value = super.resolvePlaceholder(placeholder, props);
        if (value == null) {
            return resolveHybrisPropertyPlaceholder(placeholder);
        } else {
            return value;
        }
    }

    /**
     * Look up the hybris configuration for a property.
     * 
     * @see Config#getString(String, String)
     * @param placeholder
     *            The hybris configuration property to look for.
     * @return A configuration value from hybris or <code>null</code>.
     */
    protected String resolveHybrisPropertyPlaceholder(final String placeholder) {
        final String value = Config.getString(placeholder, null);
        return value;
    }

}
