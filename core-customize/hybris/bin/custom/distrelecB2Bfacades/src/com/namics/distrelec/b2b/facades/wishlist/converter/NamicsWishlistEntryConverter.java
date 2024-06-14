/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.wishlist.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;

/**
 * NamicsWishlistEntryConverter.
 *
 * @author mwegener, Namics AG
 * @since Namics Extensions 1.0
 */
public class NamicsWishlistEntryConverter extends AbstractConverter<Wishlist2EntryModel, NamicsWishlistEntryData> {

    protected static final Logger LOG = LoggerFactory.getLogger(NamicsWishlistEntryConverter.class);

    //@formatter:off
    protected static final List<ProductOption> OPTIONS = Arrays.asList(ProductOption.MIN_BASIC,
            ProductOption.STOCK,
            ProductOption.PROMOTION_LABELS,
            ProductOption.DIST_MANUFACTURER,
            ProductOption.CHANNEL_AVAILABILITY,
            ProductOption.BREADCRUMBS);


    protected static final List<ProductOption> COMPARE_OPTIONS = new ArrayList<>(OPTIONS);

    static {
        COMPARE_OPTIONS.add(ProductOption.CLASSIFICATION);
        COMPARE_OPTIONS.add(ProductOption.DOWNLOADS);
    }

    //@formatter:on

    @Autowired
    private Converter<ProductModel, ProductData> productConverter;

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistCommercePriceService commercePriceService;

    @Autowired
    private DistPriceDataFactory priceDataFactory;

    @Override
    public void populate(final Wishlist2EntryModel source, final NamicsWishlistEntryData target) {
        try {
            if (source.getProduct() != null) {
                final ProductData product = productFacade.getProductForCodeAndOptions(source.getProduct().getCode(),
                        (source.getWishlist()
                                .getListType() == NamicsWishlistType.COMPARE ? COMPARE_OPTIONS
                                : OPTIONS));
                target.setProduct(product);

                if (source.getProduct() instanceof VariantProductModel) {
                    final ProductModel baseProduct = ((VariantProductModel) source.getProduct()).getBaseProduct();
                    target.setBaseProduct(productConverter.convert(baseProduct));
                }

                target.setAddedDate(source.getAddedDate());
                target.setComment(source.getComment());
                target.setDesired(source.getDesired());
                target.setReceived(source.getReceived());
                target.setSalesStatus(product.getSalesStatus());
                boolean isPunchedOut = productFacade.isProductPunchedOut(product.getCode());
                target.setIsPunchedOut(isPunchedOut);
                if (isPunchedOut) {
                    target.setIsBuyable(false);
                } else {
                    target.setIsBuyable(productFacade.isProductBuyable(product.getCode(), false));
                }
            }
        } catch (final Exception e) {
            LOG.error("Wishlist entry {} of wishlist {} can not be converted!", source, source.getWishlist().getPk(), e);
        }
    }
}
