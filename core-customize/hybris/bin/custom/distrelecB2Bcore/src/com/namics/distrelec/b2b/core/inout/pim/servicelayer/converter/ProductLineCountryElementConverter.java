/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.CategoryCountryModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

/**
 * Converts a product line "Product" XML element into a hybris {@link CategoryCountryModel}.
 * 
 * @author csieber, Namics AG
 * @since Distrelec 1.0
 */
public class ProductLineCountryElementConverter extends AbstractCountryElementConverter {

    @SuppressWarnings("unused")
    private static final Logger LOG = LogManager.getLogger(ProductLineCountryElementConverter.class);

    private static final String SEO_TEXT_PL = "Values/MultiValue[@AttributeID='seotextpl_txt']";

    private MultiCountryMultiLanguageSingleValueConverter singleValueConverter;

    public void convert(final Element source, final CategoryModel parent) {
        try {
            if (parent.getCountrySpecificCategoryAttributes() != null) {
                getModelService().removeAll(parent.getCountrySpecificCategoryAttributes());
            }

            final Map<String, CategoryCountryModel> targetMap = new HashMap<>();
            doSingleValue(CategoryCountryModel.SEOTEXT, SEO_TEXT_PL, source, targetMap, parent);
            final Set<CategoryCountryModel> categoryCountryModelList = new HashSet<>(targetMap.values());
            parent.setCountrySpecificCategoryAttributes(categoryCountryModelList);

            getModelService().saveAll(categoryCountryModelList);
        } catch (final ModelSavingException e) {
            LOG.error("Could not process Element {}", new Object[] { source.asXML() }, e);
        }
    }

    private void doSingleValue(final String pimAttribute, final String xpathString, final Element source, final Map<String, CategoryCountryModel> targetMap,
            final CategoryModel parent) {
        final XPath xpath = source.createXPath(xpathString);
        final List<Element> nodes = xpath.selectNodes(source);
        if (CollectionUtils.isNotEmpty(nodes)) {
            final Element element = nodes.get(0);
            if (element != null) {
                final Map<String, Map<String, String>> countryMap = singleValueConverter.convert(element);
                if (countryMap != null) {
                    for (final String countryCode : countryMap.keySet()) {
                        final CategoryCountryModel target = getTargetModel(targetMap, parent, countryCode);
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

    /**
     * Lookup in the target map if the country / manufacturer model relation was used before. If the relation does not exist, create a new
     * one and save it to the map.
     * 
     * @param targetMap
     * @param parent
     * @param countryCode
     * @return the CategoryCountryModel
     */
    private CategoryCountryModel getTargetModel(final Map<String, CategoryCountryModel> targetMap, final CategoryModel parent, final String countryCode) {
        CategoryCountryModel target = null;
        if (targetMap != null && targetMap.containsKey(countryCode)) {
            target = targetMap.get(countryCode);
        } else {
            target = new CategoryCountryModel();
            final CountryModel countryModel = getOrCreateCountryForIsoCode(countryCode);
            target.setCountry(countryModel);
            target.setCategory(parent);
            targetMap.put(countryCode, target);
        }
        return target;
    }

    @Autowired
    public void setSingleValueConverter(final MultiCountryMultiLanguageSingleValueConverter singleValueConverter) {
        this.singleValueConverter = singleValueConverter;
    }

}
