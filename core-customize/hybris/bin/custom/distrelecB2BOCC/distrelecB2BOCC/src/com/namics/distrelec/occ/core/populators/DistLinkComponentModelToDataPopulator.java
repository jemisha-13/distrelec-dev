
package com.namics.distrelec.occ.core.populators;

import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.LOCALIZEDURL;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.URL;

import java.util.Map;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.rendering.populators.LinkComponentModelToDataPopulator;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistLinkComponentModelToDataPopulator extends LinkComponentModelToDataPopulator {
    private UrlResolver<ContentPageModel> contentPageUrlResolver;

    @Override
    public void populate(final CMSItemModel cmsItemModel, final Map<String, Object> stringObjectMap) throws ConversionException {
        super.populate(cmsItemModel, stringObjectMap);
        if (cmsItemModel instanceof CMSLinkComponentModel) {
            CMSLinkComponentModel cmsLinkComponentModel = (CMSLinkComponentModel) cmsItemModel;
            ContentPageModel contentPageModel = cmsLinkComponentModel.getContentPage();
            if (contentPageModel != null) {
                stringObjectMap.put(URL, getContentPageUrlResolver().resolve(contentPageModel));
            }
            stringObjectMap.put(LOCALIZEDURL, cmsLinkComponentModel.getLocalizedUrl());
        }
    }

    protected UrlResolver<ContentPageModel> getContentPageUrlResolver() {
        return contentPageUrlResolver;
    }

    public void setContentPageUrlResolver(UrlResolver<ContentPageModel> contentPageUrlResolver) {
        this.contentPageUrlResolver = contentPageUrlResolver;
    }
}
