/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import junit.framework.Assert;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils;
import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.core.service.media.DistMediaContainerService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaContainerModel;

/**
 * Test class for {@link ProductImage360ElementConverter}
 * 
 * @author pforster, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
@UnitTest
public class ProductImage360ElementConverterTest {

    private static final String PATTERN = "11075319_arduino_a000078__360_{row}-{col}.jpg";
    private static final String HASH = "hash";
    private static final Integer NUMBER_OF_COLUMNS = new Integer(36);
    private static final Integer NUMBER_OF_ROWS = new Integer(1);

    private ProductImage360ElementConverter productImage360ElementConverter = new ProductImage360ElementConverter();

    @Before
    public void setUp() {
        DistMediaContainerService mockedDistMediaContainerService = Mockito.mock(DistMediaContainerService.class);
        Mockito.when(mockedDistMediaContainerService.getMediaContainerForQualifier(null, null)).thenReturn(new MediaContainerModel());
        productImage360ElementConverter.setDistMediaContainerService(mockedDistMediaContainerService);
    }

    @Test
    public void testConvert() throws DocumentException {
        Element image360Element = getRootElement("/distrelecB2Bcore/test/pim/import/image360.xml");
        DistImage360Model target = new DistImage360Model();

        productImage360ElementConverter.convert(image360Element, target, TestUtils.getImportContext(), HASH);

        Assert.assertEquals(NUMBER_OF_ROWS, target.getRows());
        Assert.assertEquals(NUMBER_OF_COLUMNS, target.getColumns());
        Assert.assertEquals(PATTERN, target.getPattern());
        Assert.assertEquals(HASH, target.getPimXmlHashMaster());
        Assert.assertEquals(NUMBER_OF_ROWS * NUMBER_OF_COLUMNS, target.getMediaContainers().size());
    }

    private Element getRootElement(final String resourcePath) throws DocumentException {
        final SAXReader reader = new SAXReader();
        final Document document = reader.read(getClass().getResourceAsStream(resourcePath));
        return document.getRootElement();
    }
}
