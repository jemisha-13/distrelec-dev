package com.namics.distrelec.b2b.facades.helper.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.facades.helper.DistLogoUrlHelper;

import de.hybris.platform.acceleratorcms.model.components.SimpleBannerComponentModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import static java.util.Collections.singletonList;

public class DistLogoUrlHelperImpl implements DistLogoUrlHelper {

    public static final String LOCALHOST_PATH = "https://localhost:";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CMSComponentService cmsComponentService;

    @Override
    public String getLogoUrl(final CMSSiteModel cmsSite) {
        AbstractCMSComponentModel abstractCMSComponent;

        CatalogVersionModel activeCatalogVersion = cmsSite.getCountryContentCatalog() != null ?
                cmsSite.getCountryContentCatalog().getActiveCatalogVersion() :
                cmsSite.getContentCatalogs().iterator().next().getActiveCatalogVersion();

        try {
            abstractCMSComponent = cmsComponentService.getAbstractCMSComponent("LogoComponent",
                                                                               singletonList(activeCatalogVersion));
        } catch (final CMSItemNotFoundException e) {
            return StringUtils.EMPTY;
        }

        if (abstractCMSComponent instanceof SimpleBannerComponentModel) {
            final SimpleBannerComponentModel simpleBannerComponent = (SimpleBannerComponentModel) abstractCMSComponent;
            final MediaModel media = simpleBannerComponent.getMedia();
            final String url = media == null ? null : media.getURL();
            return url == null ? null : StringEscapeUtils.escapeXml(getLocalhostAndPort() + url);
        }

        return StringUtils.EMPTY;
    }

    @Override
    public int getSslPort() {
        return configurationService.getConfiguration().getInt("tomcat.ssl.port", 9002);
    }

    @Override
    public String getLocalhostAndPort() {
        return LOCALHOST_PATH + getSslPort();
    }
}
