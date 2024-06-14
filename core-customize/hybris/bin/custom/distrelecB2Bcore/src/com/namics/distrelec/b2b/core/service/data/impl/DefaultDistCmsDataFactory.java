/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.data.impl;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.data.DistCmsDataFactory;
import com.namics.distrelec.b2b.core.service.data.DistRestrictionData;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.data.impl.DefaultCMSDataFactory;
import de.hybris.platform.core.model.product.ProductModel;

public class DefaultDistCmsDataFactory extends DefaultCMSDataFactory implements DistCmsDataFactory {

    @Override
    public DistRestrictionData createRestrictionData(final DistManufacturerModel manufacturer) {
        final DistRestrictionData data = new DefaultDistRestrictionData();
        data.setManufacturer(manufacturer);
        return data;
    }

    @Override
    public RestrictionData createRestrictionData(String categoryCode, String productCode, String catalogCode) {
        if (isNotEmpty(productCode) && isEmpty(categoryCode) && isEmpty(catalogCode)) {
            // PDP
            ProductModel product = productService.getProductForCode(productCode);
            DistManufacturerModel manufacturer = product.getManufacturer();
            CategoryModel primarySuperCategory = product.getPrimarySuperCategory();

            DistRestrictionData data = new DefaultDistRestrictionData();
            data.setCategory(primarySuperCategory);
            data.setManufacturer(manufacturer);
            data.setProduct(product);
            return data;
        } else {
            return super.createRestrictionData(categoryCode, productCode, catalogCode);
        }
    }
}
