/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */

package com.namics.distrelec.b2b.core.service.process.strategies.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import de.hybris.platform.core.model.c2l.LanguageModel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.namics.distrelec.b2b.core.service.process.strategies.DistEmailTemplateTranslationStrategy;
import com.namics.distrelec.b2b.core.util.ParameterizedHashMap;

import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;

/**
 * Copied from hybris 5
 * 
 */
public class DefaultDistEmailTemplateTranslationStrategy implements DistEmailTemplateTranslationStrategy {

    protected static final String LANG_ATTR = "$lang";
    private static final String SITE_ATTR = "$site";

    private CommonI18NService commonI18NService;
    private MediaService mediaService;
    private String defaultLanguageIso;

    @Override
    public Map<String, Object> translateMessagesForTemplate(final RendererTemplateModel renderTemplate, String languageIso, final String siteUid) {
        if (languageIso == null) {
            languageIso = defaultLanguageIso;
        }

        // Get the location of the properties file
        final List<String> propertiesRootPaths = getPropertiesRootPath(renderTemplate, languageIso, siteUid);

        // Load property file into context
        return getPropertiesFromRootPaths(propertiesRootPaths);
    }

    protected Map<String, Object> getPropertiesFromRootPaths(List<String> propertiesRootPaths) {
        final Map<String, Object> messages = new ParameterizedHashMap<String, Object>();
        for (final String path : propertiesRootPaths) {
            // Load property file
            final Map properties = loadPropertyfile(path);

            // Add contents to message map in the context
            for (final Object key : properties.keySet()) {
                messages.put(String.valueOf(key), properties.get(key));
            }
        }
        return messages;
    }

    protected List<String> getPropertiesRootPath(final RendererTemplateModel renderTemplate, final String languageIso, final String siteUid) {
        final MediaModel content = renderTemplate.getContent();
        final List<String> messageSources = new ArrayList<String>();
        if (content != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(mediaService.getStreamFromMedia(content), "UTF-8"));
                String line = reader.readLine();
                while (StringUtils.isNotEmpty(line)) {
                    String messageSource = null;

                    if (line.trim().startsWith("<")) {
                        break;
                    } else if (line.contains("## messageSource=")) {
                        messageSource = StringUtils.substring(line, line.indexOf("## messageSource=") + 17);
                    } else if (line.contains("##messageSource=")) {
                        messageSource = StringUtils.substring(line, line.indexOf("##messageSource=") + 16);
                    }

                    if (StringUtils.isNotEmpty(messageSource)) {
                        if (messageSource.contains(LANG_ATTR)) {
                            // needs to all message sources for all fallback languages
                            LanguageModel languageModel = getCommonI18NService().getLanguage(languageIso);
                            addMessageSourcesPerLanguages(messageSources, messageSource, siteUid, languageModel);
                        } else {
                            addMessageSource(messageSources, messageSource, siteUid);
                        }
                    }
                    line = reader.readLine();
                }
                return messageSources;
            } catch (final IOException e) {
                throw new RendererException("Problem during rendering", e);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
        return messageSources;
    }

    /**
     * Adds message sources for all fallback languages.
     */
    protected void addMessageSourcesPerLanguages(final List<String> messageSources, String messageSource,
            final String siteUid, final LanguageModel languageModel) {
        // add fallback language translation in backward direction
        List<LanguageModel> fallbackLanguages = languageModel.getFallbackLanguages();
        ListIterator<LanguageModel> iterator = fallbackLanguages.listIterator(fallbackLanguages.size());
        while (iterator.hasPrevious()) {
            LanguageModel fallbackLanguage = iterator.previous();
            addMessageSource(messageSources, messageSource, siteUid, fallbackLanguage);
        }

        addMessageSource(messageSources, messageSource, siteUid, languageModel);
    }

    /**
     * Adds message source if language is not provided nor needed.
     */
    protected void addMessageSource(final List<String> messageSources, final String messageSource, final String siteUid) {
        addMessageSource(messageSources, messageSource, siteUid, null);
    }

    private void addMessageSource(final List<String> messageSources, final String messageSource, final String siteUid,
            final LanguageModel language) {
        String finalMessageSource = messageSource;
        if (finalMessageSource.contains(LANG_ATTR)) {
            if (language != null) {
                finalMessageSource = finalMessageSource.replace(LANG_ATTR, language.getIsocode());
            } else {
                throw new IllegalStateException("Language must be set");
            }
        }
        if (finalMessageSource.contains(SITE_ATTR)) {
            finalMessageSource = finalMessageSource.replace(SITE_ATTR, siteUid);
        }

        messageSources.add(finalMessageSource);
    }

    protected Map loadPropertyfile(final String path) {
        final Properties properties = new Properties();
        Reader reader = null;
        try {
            final Resource propertyResource = getApplicationContext().getResource(path);
            if (propertyResource != null && (propertyResource.exists()) && (propertyResource.isReadable())) {
                reader = new InputStreamReader(new BOMInputStream(propertyResource.getInputStream()), "UTF-8");
                properties.load(reader);
            }
        } catch (final IOException e) {
            throw new RendererException("Problem during rendering", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return properties;
    }

    protected ApplicationContext getApplicationContext() {
        return Registry.getApplicationContext();
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    protected MediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    /**
     * Default language which is used if languageIso parameter is null
     * 
     * @return default language
     */
    public String getDefaultLanguageIso() {
        return defaultLanguageIso;
    }

    /**
     * Set default language which is used if languageIso parameter is null
     * 
     * @param defaultLanguageIso
     */
    public void setDefaultLanguageIso(final String defaultLanguageIso) {
        this.defaultLanguageIso = defaultLanguageIso;
    }

}
