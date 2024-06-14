/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils.getImportContext;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import junit.framework.Assert;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.namics.distrelec.b2b.core.service.media.DistMediaFormatService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Tests the {@link UnitElementConverter} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class ImageAssetElementConverterTest {

    private final ImageAssetElementConverter imageAssetElementConverter = new ImageAssetElementConverter();

    @Test
    public void testConvert() throws DocumentException {
        // Init
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/manufacturerImageAsset.xml");
        final MediaContainerModel mediaContainer = new MediaContainerModel();
        final String hash = "hash";

        final ModelService mockedModelService = mock(ModelService.class);
        final MediaModel media = new MediaModel();
        when(mockedModelService.create(MediaModel.class)).thenReturn(media);
        imageAssetElementConverter.setModelService(mockedModelService);

        final DistMediaFormatService mockedDistMediaFormatService = mock(DistMediaFormatService.class);
        final MediaFormatModel mediaFormat = new MediaFormatModel();
        mediaFormat.setQualifier("brand_logo");
        when(mockedDistMediaFormatService.getMediaFormatForQualifier(mediaFormat.getQualifier())).thenReturn(mediaFormat);
        imageAssetElementConverter.setDistMediaFormatService(mockedDistMediaFormatService);

        // Action
        imageAssetElementConverter.convert(element, mediaContainer, getImportContext(), hash);

        // Evaluation
        Assert.assertEquals(hash, mediaContainer.getPimXmlHashMaster());
        Assert.assertEquals(1, mediaContainer.getMedias().size());
        Assert.assertEquals(media, mediaContainer.getMedias().iterator().next());
        Assert.assertEquals(mediaFormat, mediaContainer.getMedias().iterator().next().getMediaFormat());
    }

    private Element getRootElement(final String resourcePath) throws DocumentException {
        final SAXReader reader = new SAXReader();
        final Document document = reader.read(getClass().getResourceAsStream(resourcePath));
        return document.getRootElement();
    }

}
