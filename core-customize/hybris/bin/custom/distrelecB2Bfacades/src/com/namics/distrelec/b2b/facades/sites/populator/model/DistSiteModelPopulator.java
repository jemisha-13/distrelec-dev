package com.namics.distrelec.b2b.facades.sites.populator.model;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmsfacades.data.SiteData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A populator which replaces the underscore characters in site uid with the minus ones because the smartedit does not
 * support those.
 */
public class DistSiteModelPopulator implements Populator<CMSSiteModel, SiteData> {

    @Override
    public void populate(CMSSiteModel source, SiteData target) throws ConversionException {
        String siteUid = source.getUid();
        if (DistUtils.containsUnderscore(siteUid)) {
            String convertedSiteUid = DistUtils.convertSiteUidUnderscoreToMinus(siteUid);
            target.setUid(convertedSiteUid);
        }

        target.setContentCatalogs(getSortedContentCatalogsID(source.getContentCatalogs()));
    }

    private List<String> getSortedContentCatalogsID(List<ContentCatalogModel> contentCatalogs) {
        return contentCatalogs.stream()
            .map(ContentCatalogModel::getId)
            .map(DistUtils::convertCatalogIdUnderscoreToMinus)
            .collect(Collectors.toList());
    }
}
