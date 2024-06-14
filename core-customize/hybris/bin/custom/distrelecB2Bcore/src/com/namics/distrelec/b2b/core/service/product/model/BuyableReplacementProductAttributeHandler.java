/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.model;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.data.PIMAlternateResult;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Dynamic attribute handler for buyability of the replacement product.
 * 
 * @author bhauser, Namics AG
 * @author pnueesch, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class BuyableReplacementProductAttributeHandler extends AbstractDynamicAttributeHandler<Boolean, ProductModel> {

    private static final int SIZE = 10;

    private static final int OFFSET = 0;

    @Autowired
    private DistProductService distProductService;

    @Override
    public Boolean get(final ProductModel product) {
        List<ProductReferenceTypeEnum> referenceTypes = Arrays.asList(ProductReferenceTypeEnum.DIS_ALTERNATIVE_BETTERVALUE,
                                                                      ProductReferenceTypeEnum.DIS_ALTERNATIVE_DE,
                                                                      ProductReferenceTypeEnum.DIS_ALTERNATIVE_SIMILAR,
                                                                      ProductReferenceTypeEnum.DIS_ALTERNATIVE_UPGRADE);
        final PIMAlternateResult pimAlternateResult = getDistProductService().getProductsReferencesForAlternative(Arrays.asList(product), referenceTypes,
                                                                                                                  OFFSET, SIZE, false);
        if (pimAlternateResult != null) {
            return pimAlternateResult.getAlternativeProducts() != null && pimAlternateResult.getAlternativeProducts().size() > 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        return Boolean.FALSE;
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    public void setDistProductService(final DistProductService distProductService) {
        this.distProductService = distProductService;
    }
}
