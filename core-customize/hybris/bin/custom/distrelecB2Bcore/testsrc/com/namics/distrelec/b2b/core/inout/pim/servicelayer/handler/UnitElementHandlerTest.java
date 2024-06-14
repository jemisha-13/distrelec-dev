/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import java.util.HashMap;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Import;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Tests the {@link UnitElementHandlerTest} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class UnitElementHandlerTest extends ServicelayerTransactionalTest {

    @Resource
    private PimImportElementHandlerFactory elementHandlerFactory;

    @Resource
    private ClassificationSystemService classificationSystemService;

    @Resource
    private ConfigurationService configurationService;

    private PimImportElementHandler unitElementHandler;

    @Before
    public void setUp() throws Exception {
        createCoreData();
        importStream(
                DistrelecCodelistService.class
                        .getResourceAsStream("/distrelecB2Binitialdata/import/project/productCatalogs/distrelecProductCatalog/catalog.impex"),
                "UTF-8", null);

        unitElementHandler = elementHandlerFactory.createPimImportElementHandler(PimImportElementHandlerTypeEnum.UNIT_ELEMENT_HANDLER.getId());

        // Init handler
        final ImportContext importContext = TestUtils.getImportContext(getClassificationSystemVersion(), null);
        importContext.getHashValues().put(ClassificationAttributeUnitModel._TYPECODE, new HashMap<>());
        unitElementHandler.setImportContext(importContext);
    }

    @Test
    public void testHandler() throws DocumentException {
        // Init
        final SAXReader reader = new SAXReader();
        reader.addHandler("/Unit", unitElementHandler);

        // Action
        reader.read(getClass().getResourceAsStream("/distrelecB2Bcore/test/pim/import/unit.xml"));

        // Evaluation
        final ClassificationSystemVersionModel systemVersion = getClassificationSystemVersion();
        final ClassificationAttributeUnitModel unit = classificationSystemService.getAttributeUnitForCode(systemVersion, "unece.unit.DMT");
        Assert.assertEquals("unece.unit.MTR", unit.getUnitType());
        Assert.assertEquals(Double.valueOf(0.1), unit.getConversionFactor());
    }

    private ClassificationSystemVersionModel getClassificationSystemVersion() {
        final Configuration configuration = configurationService.getConfiguration();
        return classificationSystemService.getSystemVersion(configuration.getString(Import.CLASSIFICATION_SYSTEM_ID),
                configuration.getString(Import.CLASSIFICATION_SYSTEM_VERSION));
    }

}
