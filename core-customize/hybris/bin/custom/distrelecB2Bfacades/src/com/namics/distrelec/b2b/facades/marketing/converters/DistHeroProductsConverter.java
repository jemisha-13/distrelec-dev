/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.marketing.converters;

import java.util.ArrayList;
import java.util.List;

import com.namics.distrelec.b2b.core.model.marketing.DistHeroProductsModel;
import com.namics.distrelec.b2b.facades.marketing.data.DistHeroProductsData;
import com.namics.distrelec.b2b.facades.product.converters.populator.DistProductBasicPopulator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * {@code DistHeroProductsConverter}
 *
 *
 * @author <a href="wilhelm-patrick.spalinger@distrelec.com">Wilhelm Spalinger</a>, Distrelec
 * @since Distrelec 4.13
 */
public class DistHeroProductsConverter extends AbstractPopulatingConverter<DistHeroProductsModel, DistHeroProductsData> {

    private DistProductBasicPopulator basicProductPopulator;

    @Override
    protected DistHeroProductsData createTarget() {
        return new DistHeroProductsData();
    }

    @Override
    public void populate(final DistHeroProductsModel source, final DistHeroProductsData target) {
        final List<ProductData> productList = new ArrayList<>();

        ProductData pData = new ProductData();
        basicProductPopulator.populate(source.getPOne(), pData);
        productList.add(pData);

        pData = new ProductData();
        basicProductPopulator.populate(source.getPTwo(), pData);
        productList.add(pData);

        pData = new ProductData();
        basicProductPopulator.populate(source.getPThree(), pData);
        productList.add(pData);

        if (source.getPFour() != null) {
            pData = new ProductData();
            basicProductPopulator.populate(source.getPFour(), pData);
            productList.add(pData);
        }

        if (source.getPFive() != null) {
            pData = new ProductData();
            basicProductPopulator.populate(source.getPFive(), pData);
            productList.add(pData);
        }
        target.setProducts(productList);
        super.populate(source, target);
    }

    public DistProductBasicPopulator getBasicProductPopulator() {
        return basicProductPopulator;
    }

    public void setBasicProductPopulator(final DistProductBasicPopulator basicProductPopulator) {
        this.basicProductPopulator = basicProductPopulator;
    }

}
