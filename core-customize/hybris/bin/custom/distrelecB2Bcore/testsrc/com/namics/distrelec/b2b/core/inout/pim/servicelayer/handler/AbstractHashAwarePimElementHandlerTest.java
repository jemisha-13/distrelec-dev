/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

/**
 * Tests the {@link AbstractHashAwarePimElementHandlerTest} class.
 * 
 * @author ascherrer, Namics AG
 * @since Distrelec 2.0
 */
@IntegrationTest
public class AbstractHashAwarePimElementHandlerTest extends ServicelayerTransactionalTest {
    private final String CRLF = System.getProperty("line.separator");

    @Resource
    private PimImportElementHandlerFactory elementHandlerFactory;

    private AbstractHashAwarePimImportElementHandler productElementHandler;
    private String oldXml;
    private String oldHash;

    @Before
    public void setUp() throws Exception {
        productElementHandler = (AbstractHashAwarePimImportElementHandler) elementHandlerFactory
                .createPimImportElementHandler(PimImportElementHandlerTypeEnum.PRODUCT_ELEMENT_HANDLER.getId());

        StringWriter writer = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/distrelecB2Bcore/test/pim/import/product.xml"), writer);
        oldXml = writer.toString();
        oldHash = productElementHandler.calculateHashV2(oldXml.toString());
    }

    @Test
    public void testHashValueGenerationChangedOrder() {
        List<String> lines = Arrays.asList(StringUtils.split(oldXml, CRLF));
        Collections.shuffle(lines);
        final String newXml = StringUtils.join(lines, CRLF);

        String newHashV1 = productElementHandler.calculateHashV1(newXml);
        String newHashV2 = productElementHandler.calculateHashV2(newXml);

        // V1 generates a different hash for re-ordered XML
        Assert.assertFalse(oldHash.equals(newHashV1));

        // V2 generates the same hash for re-ordered XML
        Assert.assertTrue(oldHash.equals(newHashV2));
    }

    @Test
    public void testHashValueGenerationChangedContent() {
        final String oldContent = "DAP208";
        final String newContent = "DAP201";

        Assert.assertTrue(oldXml.contains(oldContent));

        final String newXml = oldXml.replaceAll(oldContent, newContent);

        String newHashV1 = productElementHandler.calculateHashV1(newXml);
        String newHashV2 = productElementHandler.calculateHashV2(newXml);

        Assert.assertFalse(oldHash.equals(newHashV1));
        Assert.assertFalse(oldHash.equals(newHashV2));
    }
}
