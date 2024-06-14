package com.namics.distrelec.b2b.core.bmecat.export.query;

public interface DistBMECatQueryCreator {

    String DATASHEET_PARAM = "datasheet";
    String DEFAULT_LANG_ISOCODE_PARAM = "defaultLanguageIsocode";
    String LANG_ISOCODE_PARAM = "languageIsocode";
    String SALESORG_PARAM = "distSalesOrg";

    String createQuery(boolean isDefaultLanguageDifferent);
}
