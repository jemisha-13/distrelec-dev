/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils.getImportContext;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.converter.impl.DefaultLocaleProvider;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import junit.framework.Assert;
import org.mockito.Mockito;

/**
 * Tests the {@link UnitElementConverter} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class UnitElementConverterTest {

    private final UnitElementConverter unitElementConverter = new UnitElementConverter();

    @Before
    public void setUp() {
        ConverterLanguageUtil mockedConverterLanguageUtil = Mockito.mock(ConverterLanguageUtil.class);
        Mockito.when(mockedConverterLanguageUtil.getLocalizedValues(Mockito.any(), Mockito.anyString())).thenReturn(singletonMap(Locale.GERMAN, "dm"));
        unitElementConverter.setConverterLanguageUtil(mockedConverterLanguageUtil);
    }

    @Test
    public void testGetId() throws DocumentException {
        // Init
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/unit.xml");

        // Action
        final String id = unitElementConverter.getId(element);

        // Evaluation
        Assert.assertEquals("unece.unit.DMT", id);
    }

    @Test
    public void testGetIdElementWithoutId() throws DocumentException {
        // Init
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/unitWithoutId.xml");

        // Action
        final String id = unitElementConverter.getId(element);

        // Evaluation
        Assert.assertNull(id);
    }

    @Test
    public void testConvert() throws DocumentException {
        // Init
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/unit.xml");
        final ClassificationAttributeUnitModel unit = new ClassificationAttributeUnitModel();
        final String hash = "hash";

        // Action
        unitElementConverter.convert(element, unit, getImportContext(), hash);

        // Evaluation
        Assert.assertEquals("dm", unit.getSymbol());
        Assert.assertEquals("unece.unit.MTR", unit.getUnitType());
        Assert.assertEquals(0.1, unit.getConversionFactor());
        Assert.assertEquals(hash, unit.getPimXmlHashMaster());
    }

    @Test
    public void testConvertScientificNotation() throws DocumentException {
        // Init
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/unitScientificNotation.xml");
        final ClassificationAttributeUnitModel unit = new ClassificationAttributeUnitModel();
        final String hash = "hash";

        // Action
        unitElementConverter.convert(element, unit, getImportContext(), hash);

        // Evaluation
        Assert.assertEquals("V/W", unit.getSymbol());
        Assert.assertEquals("unece.unit.VPW", unit.getUnitType());
        Assert.assertEquals(0.000001, unit.getConversionFactor());
        Assert.assertEquals(hash, unit.getPimXmlHashMaster());
    }

    @Test
    public void testConvertLocalized() throws DocumentException {
        // Init
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/unit.xml");
        final ClassificationAttributeUnitModel unit = new ClassificationAttributeUnitModel();
        final Locale locale = Locale.GERMAN;
        // unit.setLocaleProvider(new StubLocaleProvider(locale));
        final String hash = "hash";

        // Action
        unitElementConverter.convert(element, unit, getImportContext(), hash);

        I18NService mockI18NService = mock(I18NService.class);
        when(mockI18NService.getBestMatchingLocale(locale)).thenReturn(locale);
        LocaleProvider localeProvider = new DefaultLocaleProvider(mockI18NService);
        when(localeProvider.getCurrentDataLocale()).thenReturn(locale);

        ItemModelContextImpl itemModelContext = (ItemModelContextImpl) unit.getItemModelContext();
        itemModelContext.setLocaleProvider(localeProvider);

        // Evaluation
        Assert.assertEquals("dm", unit.getName());
        Assert.assertEquals(hash, unit.getPimXmlHashMaster());
    }

    private Element getRootElement(final String resourcePath) throws DocumentException {
        final SAXReader reader = new SAXReader();
        final Document document = reader.read(getClass().getResourceAsStream(resourcePath));
        return document.getRootElement();
    }

}
