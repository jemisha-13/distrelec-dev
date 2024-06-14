/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Element handler for "Asset" Xml elements with UserTypeID="AssetsVideo". Such elements will result in models of type
 * {@link DistVideoMediaModel}.
 * 
 * @author csieber, Namics AG
 * @since Distrelec 1.0
 */
public class VideoAssetElementHandler extends AbstractHashAwarePimImportElementHandler {
    private static final String XP_VIDEO_ID = "ID";
    @Autowired
    private MediaService mediaService;

    @Autowired
    private ModelService modelService;

    public VideoAssetElementHandler() {
        super(DistVideoMediaModel._TYPECODE);
    }

    private boolean isValidElement(final Element element) {
        final String videoId = element.attribute(XP_VIDEO_ID).getStringValue();
        // final String brightCoveId = element.valueOf(VIDEO_ID);
        return StringUtils.isNotEmpty(videoId);
    }

    @Override
    protected ItemModel getModel(final String code, final Element element) {
        try {
            final MediaModel media = mediaService.getMedia(getImportContext().getProductCatalogVersion(), code);
            if (isValidElement(element)) {
                return media;
            }

            modelService.remove(media);
        } catch (final UnknownIdentifierException e) {
            if (isValidElement(element)) {
                final DistVideoMediaModel media = modelService.create(DistVideoMediaModel.class);
                media.setCode(code);
                media.setCatalogVersion(getImportContext().getProductCatalogVersion());
                return media;
            }
        }

        return null;
    }

}
