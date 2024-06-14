package com.distrelec.solrfacetsearch.provider.impl;

import static org.apache.commons.lang.StringUtils.EMPTY;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.provider.impl.ItemIdentityProvider;

public class DistItemIdentityProvider extends ItemIdentityProvider {

    @Override
    public String getIdentifier(IndexConfig indexConfig, ItemModel item) {
        String suffix = indexConfig.getCmsSite() != null ? ("-" + indexConfig.getCmsSite().getCountry().getIsocode()) : EMPTY;

        if (item instanceof ProductModel product) {
            return product.getCode() + suffix;
        }
        if (item instanceof DistManufacturerModel distManufacturer) {
            return distManufacturer.getCode() + suffix;
        }
        if (item instanceof CategoryModel category) {
            return category.getCode() + suffix;
        }

        return item.getPk().getLongValueAsString();
    }
}
