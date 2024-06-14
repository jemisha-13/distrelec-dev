/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Element handler for "Asset" XML elements with an image UserTypeID. Such elements will result in a {@link MediaContainerModel} with the
 * multiple formats assigned.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ImageAssetElementHandler extends AbstractHashAwarePimImportElementHandler {

    @Autowired
    private MediaContainerService mediaContainerService;

    @Autowired
    private ModelService modelService;

    public ImageAssetElementHandler() {
        super(MediaContainerModel._TYPECODE);
    }

    @Override
    protected MediaContainerModel getModel(final String qualifier, final Element element) {
        try {
            return mediaContainerService.getMediaContainerForQualifier(qualifier);
        } catch (final UnknownIdentifierException e) {
            final MediaContainerModel mediaContainer = modelService.create(MediaContainerModel.class);
            mediaContainer.setQualifier(qualifier);
            mediaContainer.setCatalogVersion(getImportContext().getProductCatalogVersion());
            return mediaContainer;
        }
    }

}
