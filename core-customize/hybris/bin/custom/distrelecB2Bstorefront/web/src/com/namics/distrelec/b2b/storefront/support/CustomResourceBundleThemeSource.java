/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.support;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.SimpleTheme;

import java.util.HashMap;
import java.util.Map;

/**
 * CustomResourceBundleThemeSource. Supports site and theme specific resource bundles. Uses a ReloadableResourceBundleMessageSource to load
 * the theme file. Delegates to a message source if the theme specific file cannot be found.
 */
public class CustomResourceBundleThemeSource implements ThemeSource, ResourceLoaderAware {

    private static final String I18N_MESSAGESOURCE_THEME_ENABLED_CONFIG_PROPERTY = "storefront.i18n.dbmessagesource.theme.enabled";
    private static final Logger LOG = Logger.getLogger(CustomResourceBundleThemeSource.class);

    private MessageSource parentMessageSource;
    private int cacheSeconds;
    private ResourceLoader resourceLoader;
    private boolean fallbackToSystemLocale;
    private String siteBasenamePrefix;
    private String themeBasenamePrefix;
    private String defaultEncoding;

    /**
     * Map from theme name to Theme instance
     */
    private final Map<String, Theme> themeCache = new HashMap<String, Theme>();

    // START-NAMICS-CHANGE Set db message source as most high priority message source
    private MessageSource priorityMessageSource;
    @Autowired
    protected ConfigurationService configurationService;

    // END-NAMICS-CHANGE Set db message source as most high priority message source

    /**
     * This implementation returns a SimpleTheme instance, holding a ResourceBundle-based MessageSource whose basename corresponds to the
     * given site name (prefixed by the configured "siteBasenamePrefix") which then delegates to a ResourceBundle-based MessageSource whose
     * basename corresponds to the theme name (prefixed by the configured "themeBasenamePrefix") which in turn delegates to the
     * {@link #getParentMessageSource()}.
     * 
     * <p>
     * SimpleTheme instances are cached per theme name.
     * 
     * <p>
     * Uses reloadable MessageSources to reflect changes to the underlying files. Set the {@link #setCacheSeconds(int)} to control how long
     * the files should be cached for.
     * 
     * @param themeName
     *            the theme name
     * @see #setSiteBasenamePrefix
     * @see #setThemeBasenamePrefix
     */
    @Override
    public Theme getTheme(final String themeName) {
        if (themeName == null) {
            return null;
        }

        synchronized (this.themeCache) {
            // Look for the theme in the cache
            {
                final Theme theme = this.themeCache.get(themeName);
                if (theme != null) {
                    return theme;
                }
            }

            // Create the new theme

            // Split the theme name into site and theme parts
            final String[] strings = splitThemeName(themeName);
            final String sitePart = strings[0];
            final String themePart = strings[1];

            final String siteBasename = getSiteBasenamePrefix() + sitePart;
            final String themeBasename = getThemeBasenamePrefix() + themePart;

            final MessageSource themeMessageSource = createThemeMessageSource(themeBasename);
            final MessageSource siteMessageSource = createSiteMessageSource(siteBasename, themeMessageSource);

            // START-NAMICS-CHANGE Set db message source as most high priority message source
            Theme theme;
            if (configurationService.getConfiguration().getBoolean(I18N_MESSAGESOURCE_THEME_ENABLED_CONFIG_PROPERTY, true)) {
                final MessageSource priorityMessageSource = createPriorityMessageSource(siteMessageSource);
                theme = new SimpleTheme(themeName, priorityMessageSource);
            } else {
                theme = new SimpleTheme(themeName, siteMessageSource);
                // final Theme theme = new SimpleTheme(themeName, siteUiExperienceMessageSource);
            }
            // END-NAMICS-CHANGE Set db message source as most high priority message source
            this.themeCache.put(themeName, theme);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Theme created: name '" + themeName + "', siteBasename [" + siteBasename + "], themeBasename [" + themeBasename + "]");
            }
            return theme;
        }
    }

    protected String[] splitThemeName(final String themeName) {
        return themeName.split(",", 2);
    }

    protected MessageSource createSiteMessageSource(final String basename, final MessageSource themeMessageSource) {
        final AbstractMessageSource messageSource = createMessageSource(basename);
        messageSource.setParentMessageSource(themeMessageSource);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    protected MessageSource createThemeMessageSource(final String basename) {
        final AbstractMessageSource messageSource = createMessageSource(basename);
        messageSource.setParentMessageSource(getParentMessageSource());
        return messageSource;
    }

    protected AbstractMessageSource createMessageSource(final String basename) {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setCacheSeconds(getCacheSeconds());
        messageSource.setResourceLoader(getResourceLoader());
        messageSource.setFallbackToSystemLocale(isFallbackToSystemLocale());
        messageSource.setDefaultEncoding(getDefaultEncoding());
        return messageSource;
    }

    // START-NAMICS-CHANGE Set db message source as most high priority message source
    protected MessageSource createPriorityMessageSource(final MessageSource parentMessageSource) {
        final MessageSource priorityMessageSource = getPriorityMessageSource();
        if (priorityMessageSource instanceof AbstractMessageSource) {
            final AbstractMessageSource messageSource = (AbstractMessageSource) priorityMessageSource;
            messageSource.setParentMessageSource(parentMessageSource);
            messageSource.setUseCodeAsDefaultMessage(true);
            return messageSource;
        } else {
            if (priorityMessageSource != null) {
                LOG.warn("The priority message source of type '" + priorityMessageSource.getClass()
                        + "' can not be cast to AbstractMessageSource. Parent message source is used.");
            } else {
                LOG.warn("The priority message was null. Parent message source is used.");
            }
            return parentMessageSource;
        }
    }

    // END-NAMICS-CHANGE Set db message source as most high priority message source

    protected MessageSource getParentMessageSource() {
        return parentMessageSource;
    }

    @Required
    public void setParentMessageSource(final MessageSource parentMessageSource) {
        this.parentMessageSource = parentMessageSource;
    }

    protected String getDefaultEncoding() {
        return defaultEncoding;
    }

    @Required
    public void setDefaultEncoding(final String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public int getCacheSeconds() {
        return cacheSeconds;
    }

    @Required
    public void setCacheSeconds(final int cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public boolean isFallbackToSystemLocale() {
        return fallbackToSystemLocale;
    }

    public void setFallbackToSystemLocale(final boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    protected String getSiteBasenamePrefix() {
        return siteBasenamePrefix;
    }

    /**
     * Set the prefix that gets applied to the ResourceBundle basenames, i.e. the theme names. E.g.: basenamePrefix="test.",
     * themeName="theme" -> basename="test.theme".
     * <p>
     * Note that ResourceBundle names are effectively classpath locations: As a consequence, the JDK's standard ResourceBundle treats dots
     * as package separators. This means that "test.theme" is effectively equivalent to "test/theme", just like it is for programmatic
     * <code>java.util.ResourceBundle</code> usage.
     * 
     * @param basenamePrefix
     *            the base name prefix
     * @see java.util.ResourceBundle#getBundle(String)
     */
    @Required
    public void setSiteBasenamePrefix(final String basenamePrefix) {
        this.siteBasenamePrefix = basenamePrefix != null ? basenamePrefix : "";
    }

    protected String getThemeBasenamePrefix() {
        return themeBasenamePrefix;
    }

    /**
     * Set the prefix that gets applied to the ResourceBundle basenames, i.e. the theme names. E.g.: basenamePrefix="test.",
     * themeName="theme" -> basename="test.theme".
     * <p>
     * Note that ResourceBundle names are effectively classpath locations: As a consequence, the JDK's standard ResourceBundle treats dots
     * as package separators. This means that "test.theme" is effectively equivalent to "test/theme", just like it is for programmatic
     * <code>java.util.ResourceBundle</code> usage.
     * 
     * @param basenamePrefix
     *            the base name prefix
     * @see java.util.ResourceBundle#getBundle(String)
     */
    @Required
    public void setThemeBasenamePrefix(final String basenamePrefix) {
        this.themeBasenamePrefix = basenamePrefix != null ? basenamePrefix : "";
    }

    public MessageSource getPriorityMessageSource() {
        return priorityMessageSource;
    }

    public void setPriorityMessageSource(final MessageSource priorityMessageSource) {
        this.priorityMessageSource = priorityMessageSource;
    }
}
