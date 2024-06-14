/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.message.queue.cassandra;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code ILCodeHelper}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public final class ILCodeHelper {

    /**
     * Create a new instance of {@code ILCodeHelper}
     */
    private ILCodeHelper() {
        // NOOP
    }

    public static String getCode(final ProductModel product) {
        if (product == null || product.getManufacturer() == null || product.getPrimarySuperCategory() == null) {
            return null;
        }

        return new StringBuilder(product.getManufacturer().getCode()).append("_").append(product.getPrimarySuperCategory().getCode()).toString();
    }

    public static String getCode(final DistManufacturerModel manufacturer) {
        return manufacturer != null ? manufacturer.getCode() : null;
    }

    public static String getCode(final CategoryModel category) {
        return category != null ? category.getCode() : null;
    }
}
