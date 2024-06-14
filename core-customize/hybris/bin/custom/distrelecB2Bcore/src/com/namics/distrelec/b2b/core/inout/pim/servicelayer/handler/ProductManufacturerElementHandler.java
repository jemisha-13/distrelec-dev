/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Element handler for "Product" XML elements with UserTypeID = "Manufacturer". Such elements will result in model of type
 * {@link DistManufacturerModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ProductManufacturerElementHandler extends AbstractHashAwarePimImportElementHandler {

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Autowired
    private ModelService modelService;

    public ProductManufacturerElementHandler() {
        super(DistManufacturerModel._TYPECODE);
    }

    @Override
    protected ItemModel getModel(final String manufacturerCode, final Element element) {
        try {
            return distManufacturerService.getManufacturerByCode(manufacturerCode);
        } catch (final UnknownIdentifierException e) {
            final DistManufacturerModel manufacturer = modelService.create(DistManufacturerModel.class);
            manufacturer.setCode(manufacturerCode);
            return manufacturer;
        }
    }

    @Override
    protected void doAfterProcess(final String id) {
        super.doAfterProcess(id);
        getImportContext().getCodes().get(DistManufacturerModel._TYPECODE).remove(id);
    }

}
