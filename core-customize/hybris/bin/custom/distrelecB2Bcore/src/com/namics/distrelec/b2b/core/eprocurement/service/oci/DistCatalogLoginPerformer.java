/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.oci;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.namics.distrelec.distrelecoci.data.DistSapProduct;
import com.namics.distrelec.distrelecoci.data.DistSapProductList;
import com.namics.distrelec.distrelecoci.exception.OciException;
import com.namics.distrelec.distrelecoci.utils.OutboundSection;

import de.hybris.platform.core.model.product.ProductModel;

/**
 * DistCatalogLoginPerformer
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public abstract interface DistCatalogLoginPerformer {
    void login(final HttpServletRequest paramHttpServletRequest, final HttpServletResponse paramHttpServletResponse, final OutboundSection paramOutboundSection)
            throws OciException;

    DistSapProductList backgroundSearch(final List<ProductModel> backgroundSearchPlist, final boolean ociSession);

    DistSapProduct getProductInfoForValidation(final String paramString, final double paramDouble, final boolean ociSession);

    String getHookURLFieldName();
}
