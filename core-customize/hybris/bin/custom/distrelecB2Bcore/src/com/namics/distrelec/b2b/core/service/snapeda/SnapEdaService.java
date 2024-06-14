package com.namics.distrelec.b2b.core.service.snapeda;

import com.namics.distrelec.b2b.core.service.snapeda.exception.SnapEdaApiException;

import de.hybris.platform.core.model.product.ProductModel;

public interface SnapEdaService {

    void flagProduct(ProductModel product) throws SnapEdaApiException;

}
