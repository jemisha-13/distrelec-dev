/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public class WebSitemapHelper {

    private static final Logger LOG = LoggerFactory.getLogger(WebSitemapHelper.class);
    private static final String BLACKLIST_FILE_PREFIX = "sitemap.blacklist.file.";

    private static final String ALL_WEBSITES = "distrelec_ALL";

    private ConfigurationService configurationService;

    private Resource getResource(final String path) throws Exception {
        try {
            return Registry.getApplicationContext().getResource("file://" + path);

        } catch (final Exception ex) {
            throw new Exception("Problems while reading the resources", ex);
        }
    }

    public List<String> getBlacklistForSiteAndEntity(final String siteId, final String entityName) {
        try {
            final Resource resource = getResource(getConfiguration().getString(BLACKLIST_FILE_PREFIX + entityName));
            return readBlacklist(resource, siteId);
        } catch (final Exception e) {
            return Collections.emptyList();
        }
    }

    private List<String> readBlacklist(final Resource resourceFile, final String siteId) {
        final List<String> result = new ArrayList<String>();

        InputStream inputStream = null;
        Scanner scanner = null;
        try {
            if (resourceFile.exists()) {
                inputStream = resourceFile.getInputStream();
                scanner = new Scanner(inputStream);
                while (scanner.hasNext()) {
                    final String line = scanner.nextLine().trim();
                    if (line.length() > 0 && line.charAt(0) != '#') {
                        addLineToMap(result, siteId, line);
                    }
                }
            } else {
                LOG.debug("File [{}] cannot be found.", resourceFile.getFilename());
            }
        } catch (IOException e) {
            LOG.info("File [{}] cannot be read.", resourceFile.getFilename(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(scanner);
        }
        return result;
    }

    private void addLineToMap(final List<String> result, final String siteId, final String line) {
        final String[] parts = line.split(";");
        if (parts.length != 2) {
            LOG.error("The configuration line for blacklist is not correct. The line is this one: \n [{}]", line);
            return;
        }
        if (StringUtils.isNotBlank(siteId) && (siteId.trim().equalsIgnoreCase(parts[0].trim()) || ALL_WEBSITES.equalsIgnoreCase(parts[0].trim()))) {
            result.add(parts[1].trim());
        }

    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public Configuration getConfiguration() {
        return getConfigurationService().getConfiguration();
    }

}
