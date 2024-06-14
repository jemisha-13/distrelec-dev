/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimXmlHashDto;
import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Import;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils;
import com.namics.distrelec.b2b.core.service.product.DistProductService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Tests the {@link ProductElementHandlerTest} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class ProductElementHandlerTest extends ServicelayerTransactionalTest {

    @Resource
    private PimImportElementHandlerFactory elementHandlerFactory;

    @Resource
    private ClassificationSystemService classificationSystemService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private ConfigurationService configurationService;

    @Resource
    private ModelService modelService;

    private PimImportElementHandler productElementHandler;

    @Resource
    private DistProductService distProductService;

    @Before
    public void setUp() throws Exception {
        createCoreData();
        importCsv("/distrelecB2Binitialdata/import/project/productCatalogs/distrelecProductCatalog/catalog.impex", "UTF-8");
        importCsv("/distrelecB2Bcore/test/pim/import/product.impex", "UTF-8");

        productElementHandler = elementHandlerFactory.createPimImportElementHandler(PimImportElementHandlerTypeEnum.PRODUCT_ELEMENT_HANDLER.getId());

        // Init handler
        final ClassificationClassModel classificationClass = modelService.create(ClassificationClassModel.class);
        classificationClass.setDeclaredClassificationAttributeAssignments(Collections.<ClassAttributeAssignmentModel> emptyList());

        final ImportContext importContext = TestUtils.getImportContext(getClassificationSystemVersion(), classificationClass);
        importContext.getHashValues().put(ProductModel._TYPECODE, getProductHashes());
        importContext.setCurrentClassificationClass(classificationClass);
        importContext.setImportProductsOfCurrentProductLine(true);
        importContext.setProductCatalogVersion(catalogVersionService.getCatalogVersion("distrelecProductCatalog", "Online"));

        productElementHandler.setImportContext(importContext);
    }

    @Test
    public void testHandler() throws DocumentException {
        // Init
        final SAXReader reader = new SAXReader();
        reader.addHandler("/Product", productElementHandler);

        // Action
        reader.read(getClass().getResourceAsStream("/distrelecB2Bcore/test/pim/import/product.xml"));

        // Evaluation
        final ProductModel product = distProductService.getProductForCode("600325");
        Assert.assertNotNull(product);
        Assert.assertEquals(1, product.getSupercategories().size());
        Assert.assertEquals("cat-DC-71109", product.getSupercategories().iterator().next().getCode());
    }

    private ClassificationSystemVersionModel getClassificationSystemVersion() {
        final Configuration configuration = configurationService.getConfiguration();
        return classificationSystemService.getSystemVersion(configuration.getString(Import.CLASSIFICATION_SYSTEM_ID),
                configuration.getString(Import.CLASSIFICATION_SYSTEM_VERSION));
    }

    private Map<String, PimXmlHashDto> getProductHashes() {
        PimXmlHashDto pimXmlHashDto = new PimXmlHashDto();
        pimXmlHashDto.setPimXmlHashMaster("oldHash");
        pimXmlHashDto.setPimHashTimestamp(new Date());
        return Collections.singletonMap("17002437", pimXmlHashDto);
    }

}
