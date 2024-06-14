/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.Locale;

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

import com.namics.distrelec.b2b.core.model.CategoryCountryModel;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

@IntegrationTest
public class ProductLineCountryElementConverterTest extends ServicelayerTransactionalTest {
    private static final Logger LOG = LoggerFactory.getLogger(ProductManufacturerElementConverterTest.class);

    @Resource
    ProductLineCountryElementConverter productLineCountryElementConverter;

    @Resource
    ModelService modelService;

    @Resource
    CommonI18NService commonI18NService;

    @Resource
    NamicsCommonI18NService namicsCommonI18NService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Before
    public void before() throws Exception {
        createCoreData();
        createDefaultCatalog();

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
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/productline.xml");

        final CategoryModel target = new CategoryModel();
        target.setCode("derCode");
        target.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        modelService.save(target);

        productLineCountryElementConverter.convert(element, target);

        Assert.assertEquals(1, target.getCountrySpecificCategoryAttributes().size());
        CategoryCountryModel categoryCountryModel = null;
        for (final CategoryCountryModel model : target.getCountrySpecificCategoryAttributes()) {
            if (model.getCountry().getIsocode().equals("CH")) {
                categoryCountryModel = model;
            }
        }
        Assert.assertEquals("ein free text auf deutsch", categoryCountryModel.getSeoText(new Locale("de")));
    }

    private Element getRootElement(final String resourcePath) throws DocumentException {
        final SAXReader reader = new SAXReader();
        final Document document = reader.read(getClass().getResourceAsStream(resourcePath));
        return document.getRootElement();
    }
}
