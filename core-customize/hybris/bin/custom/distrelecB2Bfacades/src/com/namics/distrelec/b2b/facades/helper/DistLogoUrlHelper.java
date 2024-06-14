package com.namics.distrelec.b2b.facades.helper;

import de.hybris.platform.cms2.model.site.CMSSiteModel;

public interface DistLogoUrlHelper {

    String getLogoUrl(final CMSSiteModel cmsSite);

    int getSslPort();

    String getLocalhostAndPort();
}
