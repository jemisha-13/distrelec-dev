package com.namics.distrelec.b2b.core.config.impl;

import com.namics.distrelec.b2b.core.config.DistrelecSiteConfigService;
import de.hybris.platform.acceleratorservices.config.impl.DefaultSiteConfigService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DefaultDistrelecSiteConfigService extends DefaultSiteConfigService implements DistrelecSiteConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(DistrelecSiteConfigService.class);

    @Override
    public int getIntForBaseSite(String key, int defaultValue, BaseSiteModel baseSite) {
        String property = getProperty(key, baseSite);
        if (isNotBlank(property)) {
            try {
                return parseInt(property);
            } catch (NumberFormatException ex) {
                LOG.info("Failed to convert value {} to integer. Returning default value", property);
            }
        }
        return defaultValue;
    }

    @Override
    public long getLongForBaseSite(String key, long defaultValue, BaseSiteModel baseSite) {
        String property = getProperty(key, baseSite);
        if (isNotBlank(property)) {
            try {
                return parseLong(property);
            } catch (NumberFormatException ex) {
                LOG.info("Failed to convert value {} to long. Returning default value", property);
            }
        }
        return defaultValue;
    }

    @Override
    public double getDoubleForBaseSite(String key, double defaultValue, BaseSiteModel baseSite) {
        String property = getProperty(key, baseSite);
        if (isNotBlank(property)) {
            try {
                return parseDouble(property);
            } catch (NumberFormatException ex) {
                LOG.info("Failed to convert value {} to double. Returning default value", property);
            }
        }
        return defaultValue;
    }

    @Override
    public boolean getBooleanForBaseSite(String key, boolean defaultValue, BaseSiteModel baseSite) {
        String property = getProperty(key, baseSite);
        if (isNotBlank(property)) {
            try {
                return parseBoolean(property);
            } catch (NumberFormatException ex) {
                LOG.info("Failed to convert value {} to boolean. Returning default value", property);
            }
        }
        return defaultValue;
    }

    @Override
    public String getStringForBaseSite(String key, String defaultValue, BaseSiteModel baseSite) {
        String property = getProperty(key, baseSite);
        if (isEmpty(property)) {
            return defaultValue;
        }
        return property;
    }

    public String getProperty(final String property, BaseSiteModel baseSite)
    {
        Assert.notNull(baseSite, "BaseSite should not be null");

        final Configuration configuration = getConfigurationService().getConfiguration();
        final String currentBaseSiteUid = "." + baseSite.getUid();

        // Try with the site UID appended
        // Fallback to the property key only
        return configuration.getString(property + currentBaseSiteUid, configuration.getString(property, null));
    }
}
