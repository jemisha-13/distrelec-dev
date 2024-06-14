/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.feature.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.namics.distrelec.b2b.core.service.feature.dao.DistWebuseAttributesDao;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Unit test for {@link DefaultDistWebuseAttributesDao}.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.20 (DISTRELEC-4581)
 * 
 */

public class DefaultDistWebuseAttributesDaoTest extends ServicelayerTransactionalTest {

    @Resource
    private ProductService productService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource(name = "core.distWebuseAttributesDao")
    private DistWebuseAttributesDao distWebuseAttributesDao;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private SessionService sessionService;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistWebuseAttributesDaoTest.class);

    static final List<String> VALID_VISIBILITIES = new ArrayList<String>();
    {
        VALID_VISIBILITIES.add(ClassificationAttributeVisibilityEnum.A_VISIBILITY.getCode());
        VALID_VISIBILITIES.add(ClassificationAttributeVisibilityEnum.B_VISIBILITY.getCode());
        VALID_VISIBILITIES.add(ClassificationAttributeVisibilityEnum.C_VISIBILITY.getCode());
    }

    private static final String PRODUCT_CODE_PREFIX = "test";

    private LanguageModel langEn;
    private LanguageModel langDe;

    @Before
    public void prepare() throws Exception {
        importCsv("/distrelecB2Bcore/test/testDistWebuseAttributesDao.impex", "utf-8");

        // set catalog version
        catalogVersionService.setSessionCatalogVersion("distrelecProductCatalog", "Online");

        langEn = commonI18NService.getLanguage("en");
        langDe = commonI18NService.getLanguage("de");

    }

    // TO_NVARCHAR is used in the queries, not supported in MYSQL
    @Ignore
    @Test
    public void getWebuseAttributes_de() {
        // init
        commonI18NService.setCurrentLanguage(langDe); // the attribute names will be returned in this language as the session locale is used
                                                      // for the execution of the query
        ProductModel product1 = productService.getProductForCode(PRODUCT_CODE_PREFIX + "1");

        // execute
        Map<String, String> attributes = distWebuseAttributesDao.getWebuseAttributes(product1, langDe, VALID_VISIBILITIES);
        LOG.debug("Attributes: {}", attributes);

        // evaluate
        Assert.assertEquals(3, attributes.keySet().size()); // the 4th attribute should not be in the list because of visibility D

        String value1 = attributes.get("name1_de");
        Assert.assertEquals("value1_de", value1);
        String value2 = attributes.get("name2_de");
        Assert.assertEquals("2.0 vlt", value2); // the second attribute is numeric and has a unit
        String value3 = attributes.get("name3_de");
        Assert.assertEquals("value3_de", value3);
    }

    // TO_NVARCHAR is used in the queries, not supported in MYSQL
    @Ignore
    @Test
    public void getWebuseAttributes_en() {
        // init
        commonI18NService.setCurrentLanguage(langEn);
        ProductModel product1 = productService.getProductForCode(PRODUCT_CODE_PREFIX + "1");

        // execute
        Map<String, String> attributes = distWebuseAttributesDao.getWebuseAttributes(product1, langEn, VALID_VISIBILITIES);
        LOG.debug("Attributes: {}", attributes);

        // evaluate
        Assert.assertEquals(4, attributes.keySet().size()); // the 4th attribute should not be in the list because of visibility D

        String value1 = attributes.get("name1_en");
        Assert.assertEquals("value1_en", value1);
        String value2 = attributes.get("name2_en");
        Assert.assertEquals("1.0 vlt", value2); // the second attribute is numeric and has a unit (other value than german)
        String value3 = attributes.get("name3_en");
        Assert.assertEquals("value3_en", value3);
    }

    // TO_NVARCHAR is used in the queries, not supported in MYSQL
    @Ignore
    @Test
    public void getWebuseAttributes_emptyName() {
        // init
        commonI18NService.setCurrentLanguage(langDe); // the attribute names will be returned in this language as the session locale is used
                                                      // for the execution of the query
        ProductModel product1 = productService.getProductForCode(PRODUCT_CODE_PREFIX + "1");

        // execute
        Map<String, String> attributes = distWebuseAttributesDao.getWebuseAttributes(product1, langDe, VALID_VISIBILITIES);
        LOG.debug("Attributes: {}", attributes);

        // evaluate
        Assert.assertEquals(3, attributes.keySet().size()); // the 4th attribute should not be in the list because of visibility D and the
                                                            // 5th because the name is missing for DE

        String value1 = attributes.get("name1_de");
        Assert.assertEquals("value1_de", value1);
        String value2 = attributes.get("name2_de");
        Assert.assertEquals("2.0 vlt", value2); // the second attribute is numeric and has a unit
        String value3 = attributes.get("name3_de");
        Assert.assertEquals("value3_de", value3);
    }

    // TO_NVARCHAR is used in the queries, not supported in MYSQL
    @Ignore
    @Test
    public void checkOrder() {
        // init
        commonI18NService.setCurrentLanguage(langEn);
        ProductModel product1 = productService.getProductForCode(PRODUCT_CODE_PREFIX + "1");

        // execute
        Map<String, String> attributes = distWebuseAttributesDao.getWebuseAttributes(product1, langEn, VALID_VISIBILITIES);
        LOG.debug("Attributes: {}", attributes);

        // evaluate
        int i = 1;
        for (String key : attributes.keySet()) {
            Assert.assertEquals("name" + i++ + "_en", key);
        }
    }

    /**
     * Test to show it doesn't matter, that the features will be shown in the hmc. without a value, they don't appear on this result list. <br>
     * technically, there is only a relation to a class but no ProductFeature entry for the product.
     */
    // TO_NVARCHAR is used in the queries, not supported in MYSQL
    @Ignore
    @Test
    public void getWebuseAttributes_multiclass() {
        // init
        commonI18NService.setCurrentLanguage(langDe);
        ProductModel product = productService.getProductForCode(PRODUCT_CODE_PREFIX + "3");

        List<String> visibilities = new ArrayList<String>(VALID_VISIBILITIES);
        visibilities.add(ClassificationAttributeVisibilityEnum.D_VISIBILITY.getCode());

        // execute
        Map<String, String> attributes = distWebuseAttributesDao.getWebuseAttributes(product, langDe, visibilities);
        LOG.debug("Attributes: {}", attributes);

        // evaluate
        Assert.assertEquals(1, attributes.keySet().size());
        String value2 = attributes.get("name4_de");
        Assert.assertEquals("31.0 MMT", value2);
    }

    // TO_NVARCHAR is used in the queries, not supported in MYSQL
    @Ignore
    @Test
    public void getWebuseAttributes_onlyAVisibility() {
        // init
        commonI18NService.setCurrentLanguage(langEn);
        ProductModel product1 = productService.getProductForCode(PRODUCT_CODE_PREFIX + "1");

        List<String> visibilities = new ArrayList<String>();
        visibilities.add(ClassificationAttributeVisibilityEnum.A_VISIBILITY.getCode());

        // execute
        Map<String, String> attributes = distWebuseAttributesDao.getWebuseAttributes(product1, langEn, visibilities);
        LOG.debug("Attributes: {}", attributes);

        // evaluate
        Assert.assertEquals(3, attributes.keySet().size()); // only the first 2 A-Attributes should be returned

        String value1 = attributes.get("name1_en");
        Assert.assertEquals("value1_en", value1);
        String value2 = attributes.get("name2_en");
        Assert.assertEquals("1.0 vlt", value2); // the second attribute is numeric and has a unit (other value than german)
    }

}
