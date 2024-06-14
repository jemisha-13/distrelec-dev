/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.service.feature.DistWebuseAttributesService;
import com.namics.distrelec.b2b.facades.product.data.KeyValueAttributeData;
import com.namics.distrelec.b2b.facades.search.converter.populator.SearchResultProductTechnicalAttributesPopulator;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Populates the technical attributes from a {@link ProductModel} to a {@link ProductData}. <br />
 * The Features will be retrieved by an own service by the current product and the session language. <br />
 * <br />
 * This populator is currently only used for the similar products. All other productlists will be generated from the Factfinder search
 * result {@link SearchResultProductTechnicalAttributesPopulator}
 *
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.20 (DISTRELEC-4581)
 *
 * @param <SOURCE>
 *            extends ProductModel
 * @param <TARGET>
 *            extends ProductData
 */
public class ProductTechnicalAttributesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {

    @Autowired
    @Qualifier("core.distWebuseAttributesService")
    private DistWebuseAttributesService distWebuseAttributesService;

    @Autowired
    @Qualifier("commonI18NService")
    private CommonI18NService commonI18NService;

    private static final String DQS_SITES_KEY = "ff.search.dqs.sites";

    @Autowired
    @Qualifier("cmsSiteService")
    private CMSSiteService cmsSiteService;

    @Autowired
    @Qualifier("configurationService")
    private ConfigurationService configurationService;

    @Override
    public void populate(final ProductModel source, final ProductData target) {
        // get the current session language (features will be searched in this
        // language)

        // get the attributes for this product
        // final Map<String, String> attributes =
        // getDistWebuseAttributesService().getWebuseAttributes(source,
        // language);

        target.setTechnicalAttributes(getWebUseAttributes(source));

    }

    /**
     * retrieves the technical attributes string, splits it and stores it in a array of key value pairs.<br>
     * e.g. |Trageform=Over-Ear|Audiotechnik=Stereo|Anschlüsse=3.5 mm Klinke|Anschlüsse=6.3 mm
     * Klinke|Inlineregler=nein|Farbe=schwarz|Frequenzbereich=14...26 000 Hz|Gewicht~~g=286|Impedanz~~Ohm=50|Kabellänge~~m=3.0|Operating
     * time=?|
     *
     * @param source
     *            current SearchResultValueData
     * @return a Array containing the technical attributes as key value pairs (or an empty array)
     */
    private List<KeyValueAttributeData> getWebUseAttributes(final ProductModel source) {
        final List<KeyValueAttributeData> attributes = new ArrayList<>();
        final LanguageModel language = getCommonI18NService().getCurrentLanguage();

        final String currentLanuage = language.getIsocode().toLowerCase();

        final String currentSiteKey = getCmsSiteService().getCurrentSite().getUid() + "_" + currentLanuage;

        String webUseString = "";

        final String DQS_SITES = getConfigurationService().getConfiguration().getString(DQS_SITES_KEY, StringUtils.EMPTY);

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
            webUseString = source.getPimWebUse(getCommonI18NService().getLocaleForLanguage(language));
        } else {
            webUseString = source.getPimWebUse();
        }

        if (StringUtils.isNotEmpty(webUseString)) {
            final String[] splittedWebUse = StringUtils.split(webUseString, "|");
            for (final String attributeKeyValue : splittedWebUse) {
                final String[] splittedKeyValue = StringUtils.split(attributeKeyValue, "=");
                // Check if there is a key and a value. If not the attribute gets not displayed!
                if (splittedKeyValue.length == 2 && StringUtils.isNotEmpty(splittedKeyValue[0]) && StringUtils.isNotEmpty(splittedKeyValue[1])) {
                    final String key = splittedKeyValue[0];
                    if (StringUtils.contains(key, FACTFINDER_UNIT_PREFIX)) {
                        // move the unit from label to value, e.g: Gewicht~~g=286 --> Gewicht | 286 g
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

    public DistWebuseAttributesService getDistWebuseAttributesService() {
        return distWebuseAttributesService;
    }

    public void setDistWebuseAttributesService(final DistWebuseAttributesService distWebuseAttributesService) {
        this.distWebuseAttributesService = distWebuseAttributesService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
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

}
