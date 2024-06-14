package com.namics.distrelec.b2b.facades.media.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.media.DistMediaService;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistMediaFacadeUnitTest {

    @InjectMocks
    private DefaultDistMediaFacade mediaFacade;

    @Mock
    private DistMediaService distMediaService;

    @Mock
    private UniqueItemIdentifierService uniqueItemIdentifierService;

    private CatalogModel catalogModel;
    private CatalogVersionModel catalogVersionModel;
    private DistVideoMediaModel videoMediaModel;

    private static final String CATALOG_VERSION_UID = "catalogVersionUid";
    private static final String MEDIA_CODE = "media";
    private static final String UNKNOWN = "unknown";

    @Before
    public void setUp() {
        catalogModel = mock(CatalogModel.class);
        catalogVersionModel = mock(CatalogVersionModel.class);
        when(uniqueItemIdentifierService.getItemModel(CATALOG_VERSION_UID, CatalogVersionModel.class)).thenReturn(Optional.ofNullable(catalogVersionModel));
        when(uniqueItemIdentifierService.getItemModel(UNKNOWN, CatalogVersionModel.class)).thenThrow(UnknownIdentifierException.class);

        videoMediaModel = mock(DistVideoMediaModel.class);
        when(videoMediaModel.getCode()).thenReturn("code");
        when(videoMediaModel.getCatalogVersion()).thenReturn(catalogVersionModel);
        when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
        when(catalogModel.getId()).thenReturn("id");
        when(videoMediaModel.getCatalogVersion().getVersion()).thenReturn("version");
        when(videoMediaModel.getDescription()).thenReturn("description");
        when(videoMediaModel.getCode()).thenReturn("code");
        when(videoMediaModel.getYoutubeUrl()).thenReturn("url");

        when(distMediaService.searchVideoMedia(anyString(),anyInt(), anyInt(), anyObject())).thenReturn(Collections.singletonList(videoMediaModel));
        when(distMediaService.findVideoMedia(MEDIA_CODE, catalogVersionModel)).thenReturn(videoMediaModel);
        when(distMediaService.findVideoMedia(UNKNOWN, catalogVersionModel)).thenReturn(null);
    }

    @Test
    public void testSearchVideoMedia() {
        List<MediaData> result = mediaFacade.searchVideoMedia("term", 1, 1, CATALOG_VERSION_UID);
        assertEquals( 1, result.size());
    }

    @Test
    public void testSearchVideoMedia_CatalogVersionUnknown() {
        assertThrows(UnknownIdentifierException.class, () -> {
            mediaFacade.searchVideoMedia("term", 1, 1, UNKNOWN);
        });
    }

    @Test
    public void testFindVideoMedia() {
        assertNotNull(mediaFacade.findVideoMedia(MEDIA_CODE, CATALOG_VERSION_UID));
    }

    @Test
    public void testFindVideoMedia_MediaIsNull() {
        assertNull(mediaFacade.findVideoMedia(UNKNOWN, CATALOG_VERSION_UID));
    }

}
