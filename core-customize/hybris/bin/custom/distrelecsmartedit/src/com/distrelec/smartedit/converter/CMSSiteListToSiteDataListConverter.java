package com.distrelec.smartedit.converter;

import com.distrelec.smartedit.populator.data.SmarteditSiteData;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.site.CMSSiteModel;

import java.util.Locale;

public class CMSSiteListToSiteDataListConverter implements Converter<CMSSiteModel, SmarteditSiteData> {

    @Override
    public SmarteditSiteData convert(CMSSiteModel siteModel) {
        return new SmarteditSiteData(siteModel.getUid(), siteModel.getName(Locale.ENGLISH));
    }

}
