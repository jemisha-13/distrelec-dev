/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.ProductCountryModel;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

/**
 * ProductCountryElementConverter.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 */
public class ProductCountryElementConverter extends AbstractCountryElementConverter {
    private static final Logger LOG = LogManager.getLogger(ProductCountryElementConverter.class);

    private static final String PRODUCT_HIT_LABEL = "Values/MultiValue[@AttributeID='hitlabel_txt']";
    private static final String PRODUCT_NOMOVERS_LABEL = "Values/MultiValue[@AttributeID='nomoverslabel_txt']";
    private static final String PRODUCT_HOTOFFER_LABEL = "Values/MultiValue[@AttributeID='hotofferlabel_txt']";
    private static final String PRODUCT_TOP_LABEL = "Values/MultiValue[@AttributeID='toplabel_txt']";

    private static final String FROM_DATE_PREFIX = "FromDate";
    private static final String UNTIL_DATE_PREFIX = "UntilDate";

    private final static String DATE_FORMAT_STRING = "yyyyMMdd";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH);

    private static final String PATTERN_STRING = "^([0-9]{8})[-]([0-9]{8})";

    private MultiCountrySingleValueConverter singleValueConverter;

    public void convert(final Element source, final ProductModel parent) {
        try {
            final Map<String, ProductCountryModel> targetMap = new HashMap<String, ProductCountryModel>();

            // TODO nbenothman: performance improvement possible by detecting and keeping unchanged attributes
            if (parent.getCountrySpecificProductAttributes() != null) {
                getModelService().removeAll(parent.getCountrySpecificProductAttributes());
            }

            doSingleValue(stripFromAttributePrefix(ProductCountryModel.SHOWHITLABELFROMDATE), PRODUCT_HIT_LABEL, source, targetMap, parent);
            doSingleValue(stripFromAttributePrefix(ProductCountryModel.SHOWNOMOVERLABELFROMDATE), PRODUCT_NOMOVERS_LABEL, source, targetMap, parent);
            doSingleValue(stripFromAttributePrefix(ProductCountryModel.SHOWHOTOFFERLABELFROMDATE), PRODUCT_HOTOFFER_LABEL, source, targetMap, parent);
            doSingleValue(stripFromAttributePrefix(ProductCountryModel.SHOWTOPLABELFROMDATE), PRODUCT_TOP_LABEL, source, targetMap, parent);

            final Set<ProductCountryModel> productCountryModelList = new HashSet<>(targetMap.values());
            parent.setCountrySpecificProductAttributes(productCountryModelList);
            getModelService().saveAll(productCountryModelList);
        } catch (final ModelSavingException e) {
            LOG.error("Could not process Element {}", new Object[] { source.asXML() }, e);
        }
    }

    private String stripFromAttributePrefix(final String rawAttributeQualifier) {
        return rawAttributeQualifier.substring(0, rawAttributeQualifier.lastIndexOf(FROM_DATE_PREFIX));
    }

    private void doSingleValue(final String pimAttribute, final String xpathString, final Element source, final Map<String, ProductCountryModel> targetMap,
            final ProductModel parent) {
        final XPath xpath = source.createXPath(xpathString);
        final List<Element> nodes = xpath.selectNodes(source);
        if (CollectionUtils.isNotEmpty(nodes)) {
            final Element element = nodes.get(0);
            if (element != null) {
                final Map<String, String> countryMap = singleValueConverter.convert(element);
                if (countryMap != null) {
                    for (final String countryCode : countryMap.keySet()) {
                        final ProductCountryModel target = getTargetModel(targetMap, parent, countryCode);
                        writeFromUntilValueForAttribute(target, pimAttribute, countryMap.get(countryCode));
                    }
                }
            }
        }
    }

    private void writeFromUntilValueForAttribute(final ProductCountryModel target, final String pimAttribute, final String elementValue) {
        final Pattern pattern = Pattern.compile(PATTERN_STRING);
        final Matcher matcher = pattern.matcher(elementValue);
        if (matcher.find()) {
            try {
                final Date from = DATE_FORMAT.parse(matcher.group(1));
                final Date until = DATE_FORMAT.parse(matcher.group(2));

                writeValueForAttribute(target, pimAttribute + FROM_DATE_PREFIX, from);
                writeValueForAttribute(target, pimAttribute + UNTIL_DATE_PREFIX, until);
            } catch (final ParseException e) {
                LOG.error("Could not parse date with format [{}]", new Object[] { DATE_FORMAT_STRING }, e);
            }
        } else {
            LOG.error("Could not parse String [{}] for product [{}]. Expected Format was [{}]",
                    new Object[] { elementValue, target.getProduct().getCode(), PATTERN_STRING });
        }
    }

    /**
     * Lookup in the target map if the country / product model relation was used before. If the relation does not exist, create a new one
     * and save it to the map.
     * 
     * @param targetMap
     * @param parent
     * @param countryCode
     * @return the ProductCountryModel
     */
    private ProductCountryModel getTargetModel(final Map<String, ProductCountryModel> targetMap, final ProductModel parent, final String countryCode) {
        ProductCountryModel target = null;
        if (targetMap != null && targetMap.containsKey(countryCode)) {
            target = targetMap.get(countryCode);
        } else {
            target = new ProductCountryModel();
            final CountryModel countryModel = getOrCreateCountryForIsoCode(countryCode);
            target.setCountry(countryModel);
            target.setProduct(parent);
            targetMap.put(countryCode, target);
        }
        return target;
    }

    @Autowired
    public void setSingleValueConverter(final MultiCountrySingleValueConverter singleValueConverter) {
        this.singleValueConverter = singleValueConverter;
    }

}
