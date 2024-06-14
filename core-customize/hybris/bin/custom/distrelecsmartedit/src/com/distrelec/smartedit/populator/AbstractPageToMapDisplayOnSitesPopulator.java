package com.distrelec.smartedit.populator;

import com.distrelec.smartedit.populator.data.SmarteditSiteData;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AbstractPageToMapDisplayOnSitesPopulator implements Populator<AbstractPageModel, Map<String, Object>> {

    @Override
    public void populate(AbstractPageModel abstractPageModel, Map<String, Object> itemMap) throws ConversionException {
        List<SmarteditSiteData> displayOnSites = abstractPageModel.getDisplayOnSites().stream()
                .distinct()
                .map(site -> {
                    SmarteditSiteData siteData = new SmarteditSiteData(site.getUid(), site.getName(Locale.ENGLISH));
                    return siteData;
                })
                .collect(Collectors.toList());
        itemMap.put(SmarteditSiteData.DISPLAY_ON_SITES_KEY, displayOnSites);
    }
}
