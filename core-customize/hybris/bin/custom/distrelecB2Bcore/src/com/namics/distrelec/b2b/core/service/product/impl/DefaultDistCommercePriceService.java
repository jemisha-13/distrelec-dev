/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.impl;

import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistPriceService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.price.impl.DefaultCommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.DiscountInformation;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static de.hybris.platform.europe1.jalo.GeneratedPriceRow.MINQTD;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Default implementation of {@link DistCommercePriceService}.
 *
 * @author dsivakumaran, Namics AG
 */
public class DefaultDistCommercePriceService extends DefaultCommercePriceService implements DistCommercePriceService {

    @Autowired
    private UserService userService;

    @Override
    public boolean isOnlinePricingCustomer() {
        if (!userService.isAnonymousUser(userService.getCurrentUser())) {
            B2BCustomerModel customerModel = (B2BCustomerModel) userService.getCurrentUser();
            return customerModel.getDefaultB2BUnit().getOnlinePriceCalculation();
        }
        return false;
    }

    @Override
    public PriceInformation getWebPriceForProduct(final ProductModel product) {
        validateParameterNotNull(product, "Product model cannot be null");
        final List<PriceInformation> prices = getPriceService().getPriceInformationsForProduct(product);
        // As the prices are already sorted by the minqtd we can take the first price
        return CollectionUtils.isNotEmpty(prices) ? prices.get(0) : null;
    }

    @Override
    public PriceInformation getWebPriceForProduct(final ProductModel product, final boolean onlinePrice) {
        validateParameterNotNull(product, "Product model cannot be null");
        final List<PriceInformation> prices = getPriceService().getPriceInformationsForProduct(product, onlinePrice);
        // As the prices are already sorted by the minqtd, the priority and the start date, we can take the first price
        return CollectionUtils.isNotEmpty(prices) ? prices.get(0) : null;
    }

    @Override
    public PriceInformation getWebPriceForProduct(final ProductModel product, final boolean onlinePrice, final boolean force) {
        validateParameterNotNull(product, "Product model cannot be null");
        final List<PriceInformation> prices = getPriceService().getPriceInformationsForProduct(product, onlinePrice, force);
        if (CollectionUtils.isNotEmpty(prices)) {
            // As the prices are already sorted by the minqtd we can take the first price
            final PriceInformation pi = prices.get(0);
            if (prices.size() >= 2 && BooleanUtils.isTrue((Boolean) pi.getQualifierValue(DistPriceRow.SCALE_SHIFT))) {
                return prices.get((prices.size() / 2) - 1);
            }

            return pi;
        }
        return null;
    }

    @Override
    public Map<String, PriceInformation> getWebPriceForProducts(final List<ProductModel> products, final boolean onlinePrice, final boolean force) {
        if (products == null || products.isEmpty()) {
            return MapUtils.EMPTY_MAP;
        }

        final Map<String, List<PriceInformation>> prices = getPriceService().getPriceInformationsForProducts(products, onlinePrice, force);
        if (MapUtils.isEmpty(prices)) {
            return MapUtils.EMPTY_MAP;
        }

        final Map<String, PriceInformation> pricesMap = new HashMap<>();

        for (final String code : prices.keySet()) {
            if (!prices.containsKey(code) || CollectionUtils.isEmpty(prices.get(code))) {
                continue;
            }
            final List<PriceInformation> priceList = prices.get(code);
            // As the prices are already sorted by the minqtd we can take the first price
            PriceInformation pi = priceList.get(0);
            if (priceList.size() >= 2 && BooleanUtils.isTrue((Boolean) pi.getQualifierValue(DistPriceRow.SCALE_SHIFT))) {
                pi = priceList.get((prices.size() / 2) - 1);
            }
            pricesMap.put(code, pi);
        }

        return pricesMap;
    }

    @Override
    @SuppressWarnings("deprecation")
    public PriceInformation getListWebPriceForProduct(final ProductModel product) {
        validateParameterNotNull(product, "Product model cannot be null");
        final List<PriceInformation> prices = getPriceService().getListPriceInformationsForProduct(product);
        if (CollectionUtils.isNotEmpty(prices)) {
            return prices.iterator().next();
        }
        return null;
    }

    @Override
    public PriceInformation getListWebPriceForProduct(final ProductModel product, final boolean onlinePrice) {
        validateParameterNotNull(product, "Product model cannot be null");
        final List<PriceInformation> prices = getPriceService().getListPriceInformationsForProduct(product, onlinePrice);
        if (CollectionUtils.isNotEmpty(prices)) {
            // As the prices are already sorted by the minqtd we can take the first price
            return prices.get(0);
        }
        return null;
    }

    @Override
    public PriceInformation getWebPriceForProductAndQuantity(ProductModel product, int quantity) {
        List<PriceInformation> prices = getPriceService().getPriceInformationsForProduct(product);
        Optional<PriceInformation> maxQuantityPrice = getHighestPriceForMatchingQuantity(quantity, prices);

        return maxQuantityPrice.orElseGet(() -> CollectionUtils.isNotEmpty(prices) ? prices.iterator().next() : null);
    }

    @Override
    public PriceInformation getOnlineWebPriceForProductAndQuantity(final ProductModel product, final boolean onlinePrice, int quantity) {
        validateParameterNotNull(product, "Product model cannot be null");
        final List<PriceInformation> prices = getPriceService().getPriceInformationsForProduct(product, onlinePrice);
        Optional<PriceInformation> maxQuantityPrice = getHighestPriceForMatchingQuantity(quantity, prices);

        return maxQuantityPrice.orElseGet(() -> CollectionUtils.isNotEmpty(prices) ? prices.iterator().next() : null);
    }

    private Optional<PriceInformation> getHighestPriceForMatchingQuantity(int quantity, List<PriceInformation> priceInformation) {
        return priceInformation
                .stream()
                .filter(information -> quantity >= (Long) information.getQualifierValue(MINQTD))
                .max(Comparator.comparingLong(info -> (Long) info.getQualifierValue(MINQTD)));
    }

    @Override
    public List<PriceInformation> getScaledPriceInformations(final ProductModel product) {
        validateParameterNotNull(product, "Product model cannot be null");
        return getPriceService().getPriceInformationsForProduct(product);
    }


    @Override
    public List<PriceInformation> getScaledPriceInformations(final ProductModel product, final boolean onlinePrice) {
        validateParameterNotNull(product, "Product model cannot be null");
        return getPriceService().getPriceInformationsForProduct(product, onlinePrice);
    }

    @Override
    public List<DiscountInformation> getWebDiscountsForProduct(final ProductModel product) {
        validateParameterNotNull(product, "Product model cannot be null");
        return getPriceService().getDiscountInformationsForProduct(product);
    }

    @Override
    protected DistPriceService getPriceService() {
        return (DistPriceService) super.getPriceService();
    }
}
