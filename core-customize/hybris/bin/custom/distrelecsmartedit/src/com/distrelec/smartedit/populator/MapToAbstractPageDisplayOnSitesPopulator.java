package com.distrelec.smartedit.populator;

import com.namics.distrelec.b2b.core.util.DistUtils;
import com.distrelec.smartedit.populator.data.SmarteditSiteData;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class MapToAbstractPageDisplayOnSitesPopulator implements Populator<Map<String, Object>, AbstractPageModel> {

    private static final Logger LOG = LoggerFactory.getLogger(MapToAbstractPageDisplayOnSitesPopulator.class);

    private static final String UID_FIELD = "uid";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Override
    public void populate(Map<String, Object> stringObjectMap, AbstractPageModel abstractPageModel) throws ConversionException {
        List<Map<String, String>> displayOnSitesList = (List<Map<String, String>>) stringObjectMap.get(SmarteditSiteData.DISPLAY_ON_SITES_KEY);

        if (displayOnSitesList != null) {
            List<CMSSiteModel> currentDisplayOnSites = new ArrayList<>(abstractPageModel.getDisplayOnSites());
            List<String> displayOnSiteUidList = displayOnSitesList.stream()
                    .map(map -> map.get(UID_FIELD))
                    .map(DistUtils::revertSiteUidMinusToUnderscore)
                    .collect(Collectors.toList());

            Set<CMSSiteModel> newDisplayOnSites = currentDisplayOnSites.stream()
                    .filter(site -> displayOnSiteUidList.contains(site.getUid()))
                    .collect(Collectors.toSet());

            for (String uid : displayOnSiteUidList) {
                Optional<CMSSiteModel> cmsSiteModel = cmsSiteService.getSites().stream()
                        .filter(cmsSite -> cmsSite.getUid().equals(uid))
                        .findFirst();

                if (cmsSiteModel.isPresent()) {
                    newDisplayOnSites.add(cmsSiteModel.get());
                } else {
                    LOG.warn("Could not find site with uid {}", uid);
                }
            }

            abstractPageModel.setDisplayOnSites(new ArrayList<>(newDisplayOnSites));
        }
    }
}
