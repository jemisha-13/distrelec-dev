/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

/**
 * ProductManufacturerCountryElementConverter.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ProductManufacturerCountryElementConverter extends AbstractCountryElementConverter {
    private static final Logger LOG = LogManager.getLogger(ProductManufacturerCountryElementConverter.class);

    private static final String XP_MULTI_VALUE_META_SUPPORT_NR = "Values/MultiValue[@AttributeID='meta_supportnr_txt']";
    private static final String XP_MULTI_VALUE_META_MANUFACTURER_URL = "Values/MultiValue[@AttributeID='meta_manufacturerurl_txt']";
    private static final String XP_MULTI_VALUE_META_SUPPORT_URL = "Values/MultiValue[@AttributeID='meta_supporturl_txt']";
    private static final String XP_MULTI_VALUE_META_SUPPORT_MAIL = "Values/MultiValue[@AttributeID='meta_supportemail_txt']";
    private static final String XP_MULTI_VALUE_META_SUPPORT_REPAIR = "Values/MultiValue[@AttributeID='meta_supportrepair_txt']";

    private final static String GLOBAL_COUNTRY = "xx";
    private final static String GLOBAL_LANGUAGE = "xxx";

    private MultiCountryMultiLanguageMultiValueConverter multiValueConverter;

    private MultiCountryMultiLanguageSingleValueConverter singleValueConverter;

    public void convert(final Element source, final DistManufacturerModel parent) {
        try {
            cleanUpGlobalValues(parent);

            final Map<String, DistManufacturerCountryModel> targetMap = new HashMap<String, DistManufacturerCountryModel>();

            doMultiValue(DistManufacturerModel.GLOBALSUPPORTPHONENUMBERS, DistManufacturerCountryModel.SUPPORTPHONENUMBERS, XP_MULTI_VALUE_META_SUPPORT_NR,
                    source, targetMap, parent);
            doSingleValue(DistManufacturerModel.GLOBALMANUFACTURERURL, DistManufacturerCountryModel.MANUFACTURERURL, XP_MULTI_VALUE_META_MANUFACTURER_URL,
                    source, targetMap, parent);
            doMultiValue(DistManufacturerModel.GLOBALSUPPORTURLS, DistManufacturerCountryModel.SUPPORTURLS, XP_MULTI_VALUE_META_SUPPORT_URL, source, targetMap,
                    parent);
            doMultiValue(DistManufacturerModel.GLOBALSUPPORTEMAILS, DistManufacturerCountryModel.SUPPORTEMAILS, XP_MULTI_VALUE_META_SUPPORT_MAIL, source,
                    targetMap, parent);
            doSingleValue(DistManufacturerModel.GLOBALSUPPORTREPAIRINFO, DistManufacturerCountryModel.SUPPORTREPAIRINFO, XP_MULTI_VALUE_META_SUPPORT_REPAIR,
                    source, targetMap, parent);

            final Set<DistManufacturerCountryModel> manufacturerCountryModels = new HashSet<>(targetMap.values());
            parent.setCountrySpecificManufacturerAttributes(manufacturerCountryModels);
            getModelService().saveAll(manufacturerCountryModels);
        } catch (final ModelSavingException e) {
            LOG.error("Could not process Element {}", new Object[] { source.asXML() }, e);
        }
    }

    private void cleanUpGlobalValues(final DistManufacturerModel parent) {
        if (parent.getCountrySpecificManufacturerAttributes() != null) {
            getModelService().removeAll(parent.getCountrySpecificManufacturerAttributes());
        }

        getModelService().setAttributeValue(parent, DistManufacturerModel.GLOBALSUPPORTPHONENUMBERS, null);
        getModelService().setAttributeValue(parent, DistManufacturerModel.GLOBALMANUFACTURERURL, null);
        getModelService().setAttributeValue(parent, DistManufacturerModel.GLOBALSUPPORTURLS, null);
        getModelService().setAttributeValue(parent, DistManufacturerModel.GLOBALSUPPORTURLS, null);
        getModelService().setAttributeValue(parent, DistManufacturerModel.GLOBALSUPPORTURLS, null);
    }

    private void doMultiValue(final String globalPimAttribute, final String pimAttribute, final String xpathString, final Element source,
            final Map<String, DistManufacturerCountryModel> targetMap, final DistManufacturerModel parent) {
        final XPath xpath = source.createXPath(xpathString);
        final List<Element> nodes = xpath.selectNodes(source);
        if (CollectionUtils.isNotEmpty(nodes)) {
            final Element element = nodes.get(0);
            if (element != null) {
                final Map<String, Map<String, List<String>>> countryMap = multiValueConverter.convert(element);
                if (countryMap != null) {
                    for (final String countryCode : countryMap.keySet()) {
                        if (GLOBAL_COUNTRY.equals(countryCode)) {
                            final Map<String, List<String>> languageMap = countryMap.get(GLOBAL_COUNTRY);
                            if (languageMap.containsKey(GLOBAL_LANGUAGE)) {
                                writeValueForAttribute(parent, globalPimAttribute, languageMap.get(GLOBAL_LANGUAGE));
                            }
                        } else {
                            final DistManufacturerCountryModel target = getTargetModel(targetMap, parent, countryCode);
                            final Map<String, List<String>> languageMap = countryMap.get(countryCode);
                            if (languageMap != null) {
                                for (final String languageCode : languageMap.keySet()) {
                                    writeLocalizedValueForAttribute(target, pimAttribute, languageMap.get(languageCode), languageCode);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void doSingleValue(final String globalPimAttribute, final String pimAttribute, final String xpathString, final Element source,
            final Map<String, DistManufacturerCountryModel> targetMap, final DistManufacturerModel parent) {
        final XPath xpath = source.createXPath(xpathString);
        final List<Element> nodes = xpath.selectNodes(source);
        if (CollectionUtils.isNotEmpty(nodes)) {
            final Element element = nodes.get(0);
            if (element != null) {
                final Map<String, Map<String, String>> countryMap = singleValueConverter.convert(element);
                if (countryMap != null) {
                    for (final String countryCode : countryMap.keySet()) {
                        if (GLOBAL_COUNTRY.equals(countryCode)) {
                            final Map<String, String> languageMap = countryMap.get(GLOBAL_COUNTRY);
                            if (languageMap.containsKey(GLOBAL_LANGUAGE)) {
                                writeValueForAttribute(parent, globalPimAttribute, languageMap.get(GLOBAL_LANGUAGE));
                            }
                        } else {
                            final DistManufacturerCountryModel target = getTargetModel(targetMap, parent, countryCode);
                            final Map<String, String> languageMap = countryMap.get(countryCode);
                            if (languageMap != null) {
                                for (final String languageCode : languageMap.keySet()) {
                                    writeLocalizedValueForAttribute(target, pimAttribute, languageMap.get(languageCode), languageCode);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Lookup in the target map if the country / manufacturer model relation was used before. If the relation does not exist, create a new
     * one and save it to the map.
     * 
     * @param targetMap
     * @param parent
     * @param countryCode
     * @return the DistManufacturerCountryModel
     */
    private DistManufacturerCountryModel getTargetModel(final Map<String, DistManufacturerCountryModel> targetMap, final DistManufacturerModel parent,
            final String countryCode) {
        DistManufacturerCountryModel target = null;
        if (targetMap != null && targetMap.containsKey(countryCode)) {
            target = targetMap.get(countryCode);
        } else {
            target = new DistManufacturerCountryModel();
            final CountryModel countryModel = getOrCreateCountryForIsoCode(countryCode);
            target.setCountry(countryModel);
            target.setManufacturer(parent);
            targetMap.put(countryCode, target);
        }
        return target;
    }

    @Autowired
    public void setMultiValueConverter(final MultiCountryMultiLanguageMultiValueConverter multiValueConverter) {
        this.multiValueConverter = multiValueConverter;
    }

    @Autowired
    public void setSingleValueConverter(final MultiCountryMultiLanguageSingleValueConverter singleValueConverter) {
        this.singleValueConverter = singleValueConverter;
    }

}
