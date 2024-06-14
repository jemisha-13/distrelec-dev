/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistDownloadMediaModel;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Element handler for "Asset" Xml elements with UserTypeID="PDF". Suche elements will result in models of type
 * {@link DistDownloadMediaModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DataSheetAssetElementHandler extends AbstractHashAwarePimImportElementHandler {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private ModelService modelService;

    public DataSheetAssetElementHandler() {
        super(DistDownloadMediaModel._TYPECODE);
    }

    @Override
    protected ItemModel getModel(final String code, final Element element) {
        try {
            return mediaService.getMedia(getImportContext().getProductCatalogVersion(), code);
        } catch (final UnknownIdentifierException e) {
            final DistDownloadMediaModel media = modelService.create(DistDownloadMediaModel.class);
            media.setCode(code);
            media.setCatalogVersion(getImportContext().getProductCatalogVersion());
            return media;
        }
    }

}
