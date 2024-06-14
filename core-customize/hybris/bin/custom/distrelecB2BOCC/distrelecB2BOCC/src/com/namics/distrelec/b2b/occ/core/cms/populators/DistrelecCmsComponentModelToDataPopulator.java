package com.namics.distrelec.b2b.occ.core.cms.populators;

import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2lib.model.components.AbstractBannerComponentModel;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.rendering.populators.CMSComponentModelToDataRenderingPopulator;

import java.util.HashMap;
import java.util.Map;

public class DistrelecCmsComponentModelToDataPopulator extends CMSComponentModelToDataRenderingPopulator {

    private static final String LOCALIZED_URL_LINK = "localizedUrlLink";

    @Override
    public void populate(final AbstractCMSComponentModel source, final AbstractCMSComponentData target) {

        super.populate(source, target);

        if (source instanceof SimpleResponsiveBannerComponentModel) {

            // Update other properties
            final Map<String, Object> otherProperties = new HashMap<>();
            otherProperties.put(LOCALIZED_URL_LINK, ((SimpleResponsiveBannerComponentModel) source).getUrlLink());

            target.setOtherProperties(otherProperties);

        }
        if (source instanceof AbstractBannerComponentModel) {
            final Boolean priority = ((AbstractBannerComponentModel) source).getPriority() != null ? ((AbstractBannerComponentModel) source).getPriority()
                                                                                                   : Boolean.FALSE;

            // Update other properties
            final Map<String, Object> otherProperties = target.getOtherProperties() != null ? target.getOtherProperties() : new HashMap<>();

            otherProperties.put("priority", priority);

            target.setOtherProperties(otherProperties);
        }

    }
}
