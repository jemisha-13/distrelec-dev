package com.namics.distrelec.b2b.facades.snapeda.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.snapeda.SnapEdaService;
import com.namics.distrelec.b2b.facades.snapeda.SnapEdaFacade;

import de.hybris.platform.core.model.product.ProductModel;

public class DistrelecSnapEdaFacadeImpl implements SnapEdaFacade {

    @Autowired
    private SnapEdaService snapEdaService;

    @Override
    public void flagProduct(ProductModel product) {
        snapEdaService.flagProduct(product);
    }
}
