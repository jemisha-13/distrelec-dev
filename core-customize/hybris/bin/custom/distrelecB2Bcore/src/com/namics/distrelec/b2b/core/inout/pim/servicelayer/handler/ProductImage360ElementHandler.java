/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.core.service.media.DistImage360Service;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Handles the xml import of a 360 image.
 * 
 * @author pforster, Namics AG
 * @since Distrelec 3.0.0
 */
public class ProductImage360ElementHandler extends AbstractHashAwarePimImportElementHandler {

    @Autowired
    private DistImage360Service distImage360Service;

    @Autowired
    private ModelService modelService;

    public ProductImage360ElementHandler() {
        super(DistImage360Model._TYPECODE);
    }

    @Override
    protected ItemModel getModel(String code, Element element) {
        try {
            return distImage360Service.getImage360(getImportContext().getProductCatalogVersion(), code);
        } catch (UnknownIdentifierException e) {
            DistImage360Model model = modelService.create(DistImage360Model.class);
            model.setCode(code);
            model.setCatalogVersion(getImportContext().getProductCatalogVersion());
            return model;
        }
    }

}
