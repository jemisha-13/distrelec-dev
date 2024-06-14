package com.distrelec.smartedit.converter;

import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.LinkedHashMap;

public class SiteDataListToCMSSiteListConverter implements Converter<LinkedHashMap<String, String>, CMSSiteModel> {

    @Autowired
    private CMSSiteService cmsSiteService;

    @Override
    public CMSSiteModel convert(LinkedHashMap<String, String> mappedObject) {
        Collection<CMSSiteModel> allSites = cmsSiteService.getSites();

        String siteUid = mappedObject.get("uid");

        return allSites.stream()
                .filter(siteModel -> siteModel.getUid().equals(siteUid))
                .findFirst().orElse(null);
    }
}
