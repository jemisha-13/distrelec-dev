package com.namics.hybris.ffsearch.export;

import de.hybris.platform.cms2.model.site.CMSSiteModel;

public interface DistFactFinderDetailQueryCreator {

    String PK_LIST = "pkList";
    String DATE = "Date";
    String LANGUAGE_ISOCODE = "LanguageIsocode";
    String PROMOLABEL_ATTRNAME = "dplParam_";

    String createDetailQuery(CMSSiteModel cmsSite);
}
