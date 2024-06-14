/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.message.queue.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;
import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
public class ManufacturerILMessageHandlerUnitTest {

    @InjectMocks
    private final ManufacturerILMessageHandler manufacturerILMessageHandler = new ManufacturerILMessageHandler();

    @Mock
    DistManufacturerService distManufacturerService;

    @Mock
    BaseSiteService baseSiteService;

    @Mock
    InternalLinkMessageQueueService internalLinkMessageQueueService;

    @Mock
    ConfigurationService configurationService;

    @Mock
    Configuration configuration;

    @Mock
    CommonI18NService commonI18NService;

    @Mock
    CMSSiteModel mockSite;

    @Mock
    BaseStoreModel mockedStore;

    Set<LanguageModel> supportedLanguages = new HashSet<>();


    @Before
    public void setUp() throws ImpExException {
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(Collections.singletonList(mockedStore)).when(mockSite).getStores();
        Mockito.doReturn(supportedLanguages).when(mockedStore).getLanguages();
        Mockito.doReturn(configuration).when(configurationService).getConfiguration();
        final LanguageModel english = Mockito.mock(LanguageModel.class);
        supportedLanguages.add(english);
        Mockito.doReturn(Locale.ENGLISH).when(commonI18NService).getLocaleForLanguage(english);
        final LanguageModel french = Mockito.mock(LanguageModel.class);
        supportedLanguages.add(french);
        Mockito.doReturn(Locale.FRENCH).when(commonI18NService).getLocaleForLanguage(french);
    }

    @Test
    public void doHandleTest() {
        // when
        final DistManufacturerModel manufacturer = Mockito.mock(DistManufacturerModel.class);
        Mockito.doReturn(manufacturer).when(distManufacturerService).getManufacturerByCode("testManufacturer1");
        Mockito.doReturn(mockSite).when(baseSiteService).getBaseSiteForUID("distrelec_CH");
        Mockito.doReturn(Collections.emptyList()).when(internalLinkMessageQueueService).fetchRelatedCategoriesForManufacturer(manufacturer, mockSite);
        Mockito.doReturn(Collections.emptyList()).when(internalLinkMessageQueueService).fetchRelatedManufacturers(manufacturer, mockSite);
        Mockito.doReturn(Collections.emptyList()).when(internalLinkMessageQueueService).fetchRelatedManufacturers(Mockito.eq(manufacturer),
                Mockito.eq(mockSite), Mockito.anyList());
        Mockito.doReturn(Collections.emptyList()).when(internalLinkMessageQueueService).fetchNewArrivalsOfManufacturer(manufacturer, mockSite);
        Mockito.doReturn(Collections.emptyList()).when(internalLinkMessageQueueService).fetchTopSellersOfManufacturer(manufacturer, mockSite);
        // Mockito.doReturn(5).when(configuration).getInt(Constants.IL_MAX_LINKS_KEY);

        final InternalLinkMessage message = InternalLinkMessage.createInternalLinkMessage("testManufacturer1", "distrelec_CH", RowType.MANUFACTURER, "en",
                true);

        // do
        manufacturerILMessageHandler.doHandle(message);

        // verify
        Mockito.verify(internalLinkMessageQueueService, Mockito.times(1)).fetchRelatedCategoriesForManufacturer(manufacturer, mockSite);
        Mockito.verify(internalLinkMessageQueueService, Mockito.times(0)).fetchRelatedManufacturers(manufacturer, mockSite); // Do not use
                                                                                                                             // the slow
                                                                                                                             // method
        Mockito.verify(internalLinkMessageQueueService, Mockito.times(1)).fetchRelatedManufacturers(Mockito.eq(manufacturer), Mockito.eq(mockSite), // use
                                                                                                                                                    // the
                                                                                                                                                    // fast
                                                                                                                                                    // method
                                                                                                                                                    // instead
                Mockito.anyList());
        Mockito.verify(internalLinkMessageQueueService, Mockito.times(1)).fetchNewArrivalsOfManufacturer(manufacturer, mockSite);
        Mockito.verify(internalLinkMessageQueueService, Mockito.times(1)).fetchTopSellersOfManufacturer(manufacturer, mockSite);
    }
}
