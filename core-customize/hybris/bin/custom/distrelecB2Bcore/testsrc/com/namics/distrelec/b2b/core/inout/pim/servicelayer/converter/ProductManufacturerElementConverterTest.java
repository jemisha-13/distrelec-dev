/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

@IntegrationTest
public class ProductManufacturerElementConverterTest extends ServicelayerTransactionalTest {
    private static final Logger LOG = LoggerFactory.getLogger(ProductManufacturerElementConverterTest.class);

    @Resource
    ModelService modelService;

    @Resource
    CommonI18NService commonI18NService;

    @Resource
    NamicsCommonI18NService namicsCommonI18NService;

    @Before
    public void before() throws Exception {
        createCoreData();

        try {
            final LanguageModel german = commonI18NService.getLanguage("de");
            german.setIsocodePim("ger");
            modelService.save(german);
        } catch (final UnknownIdentifierException e) {
            final LanguageModel german = new LanguageModel();
            german.setIsocode("de");
            german.setIsocodePim("ger");
            modelService.save(german);
        }

        try {
            final LanguageModel french = commonI18NService.getLanguage("fr");
            french.setIsocodePim("fre");
            modelService.save(french);
        } catch (final UnknownIdentifierException e) {
            final LanguageModel french = new LanguageModel();
            french.setIsocode("fr");
            french.setIsocodePim("fre");
            modelService.save(french);
        }
    }

    @Test
    public void test() throws DocumentException {
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/manufacturer.xml");
        final DistManufacturerModel target = new DistManufacturerModel();
        target.setCode("derCode");
        modelService.save(target);

        DistManufacturerCountryModel manufacturerCountryModel = null;
        for (final DistManufacturerCountryModel model : target.getCountrySpecificManufacturerAttributes()) {
            if (model.getCountry().getIsocode().equals("CH")) {
                manufacturerCountryModel = model;
            }
        }

        final Element elementNew = getRootElement("/distrelecB2Bcore/test/pim/import/manufacturernew.xml");

        DistManufacturerCountryModel manufacturerCountryModelNew = null;
        for (final DistManufacturerCountryModel model : target.getCountrySpecificManufacturerAttributes()) {
            if (model.getCountry().getIsocode().equals("CH")) {
                manufacturerCountryModelNew = model;
            }
        }
    }

    @Test
    public void test2() throws DocumentException {
        final long start = System.currentTimeMillis();
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/manufacturer.xml");
        final DistManufacturerModel target = new DistManufacturerModel();
        target.setCode("derCode2");
        modelService.save(target);

        LOG.info("Convertion took {}ms", String.valueOf(System.currentTimeMillis() - start));
    }

    @Test
    public void regexTest() {
        final String pattern = "^([a-zA-Z]{2})[_]([a-zA-Z]{3})[_](.*)";
        final String line = "ch_fre_0213334455 (franz blah)";

        final Pattern r = Pattern.compile(pattern);
        final Matcher m = r.matcher(line);
        if (m.find()) {
            final String country = m.group(1);
            final String language = m.group(2);
            final String value = m.group(3);

            Assert.assertEquals("ch", country);
            Assert.assertEquals("fre", language);
            Assert.assertEquals("0213334455 (franz blah)", value);
        } else {
            fail("Nothing found!");
        }
    }

    private Element getRootElement(final String resourcePath) throws DocumentException {
        final SAXReader reader = new SAXReader();
        final Document document = reader.read(getClass().getResourceAsStream(resourcePath));
        return document.getRootElement();
    }
}
