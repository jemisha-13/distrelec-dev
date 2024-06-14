package com.namics.distrelec.b2b.core.config;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

/**
 * This service is to be used when fetching Base Site dependent properties
 * in background processes like cronjobs where the user session is not
 * applicable.
 * It is an extension of the @SiteConfigService and functions in the same way
 * with two differences:
 * 1) When resolving the Base Site dependent property it takes the Base Site
 * as an argument to its methods instead of using the @BaseSiteService to
 * resolve the current Base Site from the session.
 * 2) When resolving the property it does not include the UI Experience Level
 * in the search. This is because a background process will have no UI Experience
 * Level.
 */

public interface DistrelecSiteConfigService extends SiteConfigService {

    int getIntForBaseSite(String key, int defaultValue, BaseSiteModel baseSite);

    long getLongForBaseSite(String key, long defaultValue, BaseSiteModel baseSite);

    double getDoubleForBaseSite(String key, double defaultValue, BaseSiteModel baseSite);

    boolean getBooleanForBaseSite(String key, boolean defaultValue, BaseSiteModel baseSite);

    String getStringForBaseSite(String key, String defaultValue, BaseSiteModel baseSite);
}
