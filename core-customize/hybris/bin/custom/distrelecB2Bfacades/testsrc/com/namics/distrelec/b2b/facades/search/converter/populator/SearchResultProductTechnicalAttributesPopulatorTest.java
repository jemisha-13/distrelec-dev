/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.TECHNICAL_ATTRIBUTES;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.facades.product.data.KeyValueAttributeData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Unit Test for {@link SearchResultProductTechnicalAttributesPopulator}.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.20
 * 
 */
@UnitTest
public class SearchResultProductTechnicalAttributesPopulatorTest {

    private static final String DQS_SITES_KEY = "ff.search.dqs.sites";

    private static final String FF_WEBUSE_STRING = "|Trageform=Over-Ear|Audiotechnik=Stereo|Anschlüsse=3.5 mm Klinke|Anschlüsse=6.3 mm Klinke|Inlineregler=nein|Farbe=schwarz|Frequenzbereich=14...26 000 Hz|Gewicht~~g=286|Impedanz~~Ohm=50|Kabellänge~~m=3.0|Operating time=?|";

    private SearchResultValueData searchResultData;

    private ProductData target;

    @Mock
    private CMSSiteService cmsSiteService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Mock
    private Configuration configuration;

    @InjectMocks
    private final SearchResultProductTechnicalAttributesPopulator populator = new SearchResultProductTechnicalAttributesPopulator();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final String key = TECHNICAL_ATTRIBUTES.getValue();

        final Map<String, Object> values = new HashMap<>();
        values.put(key, FF_WEBUSE_STRING);

        searchResultData = new SearchResultValueData();
        searchResultData.setValues(values);

        target = new ProductData();

        LanguageData languageData = new LanguageData();
        languageData.setIsocode("en");
        CMSSiteModel cmsSiteModel = new CMSSiteModel();
        cmsSiteModel.setUid("distrelec");

        when(configuration.getString(DQS_SITES_KEY, StringUtils.EMPTY)).thenReturn(StringUtils.EMPTY);
        when(storeSessionFacade.getCurrentLanguage()).thenReturn(languageData);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(cmsSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
    }

    @Test
    public void testGetTechnicalAttributes() {
        // action
        populator.populate(searchResultData, target);

        // evaluate
        final KeyValueAttributeData[] attributes = (KeyValueAttributeData[]) target.getTechnicalAttributes().toArray();

        assertEquals(11, attributes.length);

        assertEquals("Trageform", attributes[0].getKey());
        assertEquals("Over-Ear", attributes[0].getValue());

        assertEquals("Audiotechnik", attributes[1].getKey());
        assertEquals("Stereo", attributes[1].getValue());

        assertEquals("Anschlüsse", attributes[2].getKey());
        assertEquals("3.5 mm Klinke", attributes[2].getValue());

        assertEquals("Anschlüsse", attributes[3].getKey());
        assertEquals("6.3 mm Klinke", attributes[3].getValue());

        assertEquals("Inlineregler", attributes[4].getKey());
        assertEquals("nein", attributes[4].getValue());

        assertEquals("Farbe", attributes[5].getKey());
        assertEquals("schwarz", attributes[5].getValue());

        assertEquals("Frequenzbereich", attributes[6].getKey());
        assertEquals("14...26 000 Hz", attributes[6].getValue());

        assertEquals("Gewicht", attributes[7].getKey());
        assertEquals("286 g", attributes[7].getValue()); // Unit should appear after the value

        assertEquals("Impedanz", attributes[8].getKey());
        assertEquals("50 Ohm", attributes[8].getValue()); // Unit should appear after the value

        assertEquals("Kabellänge", attributes[9].getKey());
        assertEquals("3.0 m", attributes[9].getValue()); // Unit should appear after the value

        assertEquals("Operating time", attributes[10].getKey());
        assertEquals("?", attributes[10].getValue());

    }

}
