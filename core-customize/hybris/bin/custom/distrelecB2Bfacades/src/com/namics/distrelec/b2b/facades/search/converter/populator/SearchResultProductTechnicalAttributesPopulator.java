/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.TECHNICAL_ATTRIBUTES;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.WEB_USE;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.facades.product.data.KeyValueAttributeData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Populator for the Technical-Attributes on {@link ProductData}.
 *
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.20 (DISTRELEC-4581)
 *
 */
public class SearchResultProductTechnicalAttributesPopulator extends AbstractSearchResultPopulator {

    private static final String DQS_SITES_KEY = "ff.search.dqs.sites";

    private static final String DQS_WEBUSES_KEY = "ff.search.dqs.webuses";

    private CMSSiteService cmsSiteService;

    private ConfigurationService configurationService;

    private DistrelecStoreSessionFacade storeSessionFacade;

    @Override
    public void populate(final SearchResultValueData source, final ProductData target) {
        target.setTechnicalAttributes(getWebUseAttributes(source));
    }

    private String getWebUseValueFor(final SearchResultValueData source, final String code) {
        final String result = getValue(source, code);
        if (result == null) {
            return "";
        }
        return result.concat("|");
    }

    /**
     * retrieves the technical attributes string, splits it and stores it in a
     * array of key value pairs.<br>
     * e.g. |Trageform=Over-Ear|Audiotechnik=Stereo|Anschlüsse=3.5 mm
     * Klinke|Anschlüsse=6.3 mm
     * Klinke|Inlineregler=nein|Farbe=schwarz|Frequenzbereich=14...26 000
     * Hz|Gewicht~~g=286|Impedanz~~Ohm=50|Kabellänge~~m=3.0|Operating time=?|
     *
     * @param source
     *            current SearchResultValueData
     * @return a Array containing the technical attributes as key value pairs
     *         (or an empty array)
     */
    private List<KeyValueAttributeData> getWebUseAttributes(final SearchResultValueData source) {
        final List<KeyValueAttributeData> attributes = new ArrayList<>();

        final String currentLanuage = getStoreSessionFacade().getCurrentLanguage().getIsocode().toLowerCase();

        final String currentSiteKey = getCmsSiteService().getCurrentSite().getUid() + "_" + currentLanuage;

        String webUseString = "";

        final String DQS_SITES = getConfigurationService().getConfiguration().getString(DQS_SITES_KEY,
                                                                                        StringUtils.EMPTY);

        if (DQS_SITES.contains(currentSiteKey)) {
            // TODO commented because of DISTRELEC-10639
            // final String DQS_WEBUSES_STR =
            // getConfigurationService().getConfiguration().getString(DQS_WEBUSES_KEY,
            // StringUtils.EMPTY);
            // final String DQS_WEBUSES[] = DQS_WEBUSES_STR.isEmpty() ? null :
            // DQS_WEBUSES_STR.split(",");
            // final StringBuilder webUsesBuilder = new StringBuilder();
            // if (DQS_WEBUSES != null) {
            // for (final String webUse : DQS_WEBUSES) {
            // webUsesBuilder.append(getWebUseValueFor(source, webUse));
            // }
            // }
            //
            // webUseString = webUsesBuilder.toString().replace("||", "|");
            webUseString = getValue(source, WEB_USE.getValue());
        } else {
            webUseString = getValue(source, TECHNICAL_ATTRIBUTES.getValue());
        }

        if (StringUtils.isNotEmpty(webUseString)) {
            final String[] splittedWebUse = StringUtils.split(webUseString, "|");
            for (final String attributeKeyValue : splittedWebUse) {
                final String[] splittedKeyValue = StringUtils.split(attributeKeyValue, "=");
                // Check if there is a key and a value. If not the attribute
                // gets not displayed!
                if (splittedKeyValue.length == 2 && StringUtils.isNotEmpty(splittedKeyValue[0])
                        && StringUtils.isNotEmpty(splittedKeyValue[1])) {
                    final String key = splittedKeyValue[0];
                    if (StringUtils.contains(key, FACTFINDER_UNIT_PREFIX)) {
                        // move the unit from label to value
                        // e.g: Gewicht~~g=286 --> Gewicht | 286 g
                        final String[] keyAndUnit = StringUtils.split(key, FACTFINDER_UNIT_PREFIX);
                        if (keyAndUnit.length == 2) {
                            final String unit = StringUtils.remove(keyAndUnit[1], FACTFINDER_UNIT_PREFIX);
                            final String value = splittedKeyValue[1] + " " + unit;
                            KeyValueAttributeData keyValueAttributeData = new KeyValueAttributeData();
                            keyValueAttributeData.setKey(keyAndUnit[0]);
                            keyValueAttributeData.setValue(value);
                            attributes.add(keyValueAttributeData);
                        }
                    } else {
                        KeyValueAttributeData keyValueAttributeData = new KeyValueAttributeData();
                        keyValueAttributeData.setKey(splittedKeyValue[0]);
                        keyValueAttributeData.setValue(splittedKeyValue[1]);

                        attributes.add(keyValueAttributeData);
                    }
                }
            }

        }

        return attributes;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(final DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }
}
