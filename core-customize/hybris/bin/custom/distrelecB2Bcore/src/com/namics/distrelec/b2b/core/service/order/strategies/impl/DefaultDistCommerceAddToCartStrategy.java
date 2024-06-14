/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.order.strategies.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.service.order.strategies.DistCommerceAddToCartStrategy;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.product.model.StockLevelData;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

/**
 * {@code DefaultDistCommerceAddToCartStrategy}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public class DefaultDistCommerceAddToCartStrategy extends DefaultCommerceAddToCartStrategy implements DistCommerceAddToCartStrategy {

    private static final long NOT_AVAILABLE = 0;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    /**
     * Create a new instance of {@code DefaultDistCommerceAddToCartStrategy}
     */
    public DefaultDistCommerceAddToCartStrategy() {
        super();
    }

    @Override
    public boolean isOrderEntryNotUpdatable(final AbstractOrderEntryModel entryToUpdate) {
        return !isOrderEntryUpdatable(entryToUpdate);
    }

    @Override
    public boolean isOrderEntryUpdatable(final AbstractOrderEntryModel entryToUpdate) {
        return super.isOrderEntryUpdatable(entryToUpdate);
    }

    @Override
    public long getAllowedCartAdjustmentForProduct(final CartModel cartModel, final ProductModel productModel, final long quantityToAdd,
                                                   final PointOfServiceModel pointOfServiceModel) {
        final DistSalesOrgProductModel salesOrgProduct = distSalesOrgProductService.getCurrentSalesOrgProduct(productModel);
        final long moq = salesOrgProduct.getOrderQuantityMinimum() != null ? salesOrgProduct.getOrderQuantityMinimum() : 1L;
        final long step = salesOrgProduct.getOrderQuantityStep() != null ? salesOrgProduct.getOrderQuantityStep() : 1L;
        final long cartLevel = checkCartLevel(productModel, cartModel, pointOfServiceModel);

        long allowedQuantityToAdd = quantityToAdd;

        if (cartLevel + quantityToAdd < moq) {
            allowedQuantityToAdd = moq - cartLevel;
        }

        long totalQuantity = cartLevel + allowedQuantityToAdd;

        allowedQuantityToAdd = (long) (Math.ceil((double) totalQuantity / step) * step) - cartLevel;

        final long stockLevel = getAvailableStockLevel(productModel, pointOfServiceModel);

        // How many will we have in our cart if we add quantity
        final long newTotalQuantity = cartLevel + allowedQuantityToAdd;

        // Now limit that to the total available in stock
        final long newTotalQuantityAfterStockLimit = Math.min(newTotalQuantity, stockLevel);

        // So now work out what the maximum allowed to be added is (note that
        // this may be negative!)
        final Integer maxOrderQuantity = productModel.getMaxOrderQuantity();

        if (isMaxOrderQuantitySet(maxOrderQuantity)) {
            final long newTotalQuantityAfterProductMaxOrder = Math.min(newTotalQuantityAfterStockLimit, maxOrderQuantity.longValue());
            return newTotalQuantityAfterProductMaxOrder - cartLevel;
        }
        return newTotalQuantityAfterStockLimit - cartLevel;
    }

    private boolean isPhasedOutOrSuspended(ProductModel productModel) {
        return distProductService.isProductNotForSale(productModel) || distProductService.isSuspendedProduct(productModel);
    }

    @Override
    protected long getAvailableStockLevel(ProductModel productModel, PointOfServiceModel pointOfServiceModel) {
        List<ProductAvailabilityExtModel> availability = availabilityService.getAvailability(Collections.singletonList(productModel.getCode()), Boolean.TRUE);
        if (isPhasedOutOrSuspended(productModel)) {
            return availability.stream().mapToInt(ProductAvailabilityExtModel::getStockLevelTotal).sum();
        }

        if (isNotWaldom(availability) && (isBackorderAllowed() || distProductService.isProductBuyableOutOfStock(productModel))) {
            return super.getAvailableStockLevel(productModel, pointOfServiceModel);
        }

        return availability.stream().mapToInt(ProductAvailabilityExtModel::getStockLevelTotal).sum();
    }

    private boolean isNotWaldom(List<ProductAvailabilityExtModel> availability) {
        return CollectionUtils.isNotEmpty(availability) && availability.get(0).getStockLevels()
                                                                       .stream()
                                                                       .noneMatch(StockLevelData::isWaldom);
    }

    private boolean isBackorderAllowed() {
        BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
        return currentBaseStore != null && BooleanUtils.isTrue(currentBaseStore.getBackorderAllowed());
    }
}
