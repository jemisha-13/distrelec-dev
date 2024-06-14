package com.distrelec.smartedit.converter;

import com.namics.distrelec.b2b.core.media.DistMediaService;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class StringToDistVideoMediaConverter implements Converter<String, DistVideoMediaModel> {

    @Autowired
    private DistMediaService distMediaService;

    @Autowired
    private CMSAdminSiteService cmsAdminSiteService;

    @Override
    public DistVideoMediaModel convert(String code) {
        CatalogVersionModel catalogVersion = cmsAdminSiteService.getActiveCatalogVersion();
        if (!StringUtils.isEmpty(code) && catalogVersion != null) {
            return distMediaService.findVideoMedia(code, catalogVersion);
        }
        return null;
    }
}
