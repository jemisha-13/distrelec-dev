/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MultiCountryMultiLanguageMultiValueConverterTest {
    private MultiCountryMultiLanguageMultiValueConverter multiValueConverter;

    private static final String XP_MULTI_VALUE_META_SUPPORT_NR = "Values/MultiValue[@AttributeID='meta_supportnr_txt']";
    private static final String XP_MULTI_VALUE_META_MANUFACTURER_URL = "Values/MultiValue[@AttributeID='meta_manufacturerurl_txt']";

    private Element manufacturerElement;

    @Before
    public void before() throws DocumentException {
        final String xmlDocument = "/distrelecB2Bcore/test/pim/import/manufacturer.xml";
        final SAXReader reader = new SAXReader();
        final Document document = reader.read(getClass().getResourceAsStream(xmlDocument));
        manufacturerElement = document.getRootElement();
        multiValueConverter = new MultiCountryMultiLanguageMultiValueConverter();
    }

    @Test
    public void test() {
        final XPath xpath = manufacturerElement.createXPath(XP_MULTI_VALUE_META_SUPPORT_NR);
        final List<Element> nodes = xpath.selectNodes(manufacturerElement);
        final Map<String, Map<String, List<String>>> countryMap = multiValueConverter.convert(nodes.get(0));
        final String value = countryMap.get("ch").get("ger").get(1);

        Assert.assertEquals("004122222222 (Kat 2)", value);
    }

    @Test
    public void testGlobal() {
        final XPath xpath = manufacturerElement.createXPath(XP_MULTI_VALUE_META_MANUFACTURER_URL);
        final List<Element> nodes = xpath.selectNodes(manufacturerElement);
        final Map<String, Map<String, List<String>>> countryMap = multiValueConverter.convert(nodes.get(0));
        final String value = countryMap.get("xx").get("xxx").get(0);

        Assert.assertEquals("http://www.globalexample.com/", value);
    }

}
