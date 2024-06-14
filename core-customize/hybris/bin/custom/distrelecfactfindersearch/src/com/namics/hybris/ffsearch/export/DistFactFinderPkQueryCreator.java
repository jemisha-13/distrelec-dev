package com.namics.hybris.ffsearch.export;

import de.hybris.platform.cms2.model.site.CMSSiteModel;

public interface DistFactFinderPkQueryCreator {

    String CMSSITE = "CmsSite";
    String DATE = "Date";
    String ERP_SYSTEM = "ErpSystem";

    String createPkQuery(CMSSiteModel cmsSite);
}
