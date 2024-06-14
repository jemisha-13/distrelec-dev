/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.facades.product.converters.populator;

import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.facades.customer.impl.DistCustomerUtilFacade;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * Populator for product volume prices.
 *
 * @param <SOURCE> extends ProductModel
 * @param <TARGET> extends ProductData
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 */
public class ProductVolumePricesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {

    public static final Long ORDER_QUANTITY_MINIMUM = Long.valueOf(1);

    public static final Long ORDER_QUANTITY_STEP = Long.valueOf(1);

    public static final String CUSTOM = "custom";

    public static final String LIST = "list";

    public static final String PRICEPERXUOMDESCRIPTION = "pricePerXUOMDesc";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private DistCommercePriceService distCommercePriceService;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    @Autowired
    private DistCustomerUtilFacade distCustomerUtilFacade;

    @Override
    public void populate(final SOURCE productModel, final TARGET productData) {
        final DistSalesOrgProductModel salesOrgProduct = distSalesOrgProductService.getCurrentSalesOrgProduct(productModel);
        if (salesOrgProduct != null) {
            productData.setOrderQuantityMinimum(salesOrgProduct.getOrderQuantityMinimum());
            productData.setOrderQuantityStep(salesOrgProduct.getOrderQuantityStep());
        }
        if (productData.getOrderQuantityMinimum() == null || productData.getOrderQuantityMinimum().longValue() <= 0) {
            productData.setOrderQuantityMinimum(ORDER_QUANTITY_MINIMUM);
        }
        if (productData.getOrderQuantityStep() == null || productData.getOrderQuantityStep().longValue() <= 0) {
            productData.setOrderQuantityStep(ORDER_QUANTITY_STEP);
        }
        productData.setSalesUnit(productModel.getRelevantSalesUnit());

        List<PriceInformation> pricesInfos = null;

        if (distCustomerUtilFacade.skipPrice() || // DISTRELEC-7670
                (pricesInfos = getDistCommercePriceService().getScaledPriceInformations(productModel, true)) == null) { // DISTRELEC-7670
            productData.setVolumePricesMap(MapUtils.EMPTY_MAP);
            return;
        }

        // TreeMap does the sorting
        // XXX this map has an overridden get method which always return a non-null value
        final Map<Long, Map<String, PriceData>> volPrices = createVolumePriceMap();
        final boolean onlinePriceCustomer = distCommercePriceService.isOnlinePricingCustomer();
        boolean hasCustomPrice = false;

        if (onlinePriceCustomer) { // Online price customers processing.
            // Retrieve the offline prices.
            final List<PriceInformation> pricesInfosHybris = getDistCommercePriceService().getScaledPriceInformations(productModel, false);
            if (CollectionUtils.isNotEmpty(pricesInfosHybris)) {
                for (final PriceInformation priceInfo : pricesInfosHybris) {
                    final Long minQuantity = getMinQuantity(priceInfo);
                    final Map<String, PriceData> priceMap = volPrices.get(minQuantity);
                    final PriceData volPrice = createPriceData(PriceDataType.BUY, priceInfo, minQuantity, false);

                    if (isCustomPrice(priceInfo) && !priceMap.containsKey(CUSTOM)) { // When a product has a special price.
                        priceMap.put(CUSTOM, volPrice);
                        hasCustomPrice = true;
                    } else if (!priceMap.containsKey(LIST)) {
                        priceMap.put(LIST, volPrice);
                    }
                }
            }
        }

        for (final PriceInformation priceInfo : pricesInfos) {
            final Long minQuantity = getMinQuantity(priceInfo);
            if (minQuantity == null) {
                continue;
            }

            final PriceData volPrice = createPriceData(PriceDataType.BUY, priceInfo, minQuantity, onlinePriceCustomer);
            final Map<String, PriceData> priceMap = volPrices.get(minQuantity);

            if (isCustomPrice(priceInfo)) {
                hasCustomPrice = true;
            }

            if (isCustomPrice(priceInfo) || onlinePriceCustomer) {
                if (!priceMap.containsKey(CUSTOM)) {
                    priceMap.put(CUSTOM, volPrice);
                }
            } else {
                if (!priceMap.containsKey(LIST)) {
                    priceMap.put(LIST, volPrice);
                }
            }

            if (priceMap.containsKey(CUSTOM) && priceMap.containsKey(LIST)) {
                calculateSaving(productData, priceMap.get(LIST), priceMap.get(CUSTOM));
            }
        }

        // DISTRELEC-14210: Check if volPrices has any entries so it doesn't break when min is not set
        if (!volPrices.isEmpty()) {
            // DISTRELEC-14024: Change the first VolumeBand Key to match the MOQ.
            final Long oldMinimumKey = volPrices.entrySet().stream().min(Comparator.comparingLong(Map.Entry::getKey)).map(Map.Entry::getKey).orElse(null);
            if (oldMinimumKey != null && oldMinimumKey.intValue() != productData.getOrderQuantityMinimum()) {
                if (volPrices.containsKey(oldMinimumKey)) {
                    final Long newMinimumKey = productData.getOrderQuantityMinimum().longValue();
                    volPrices.put(newMinimumKey, volPrices.remove(oldMinimumKey));
                }
            }
        }

        // DISTRELEC-10951 In case of custom prices, we should remove all scales without ZN00 price.
        if (hasCustomPrice) {
            final List<Long> scales = new ArrayList<>(volPrices.keySet());
            for (final Long scale : scales) {
                if (!volPrices.get(scale).containsKey(CUSTOM)) {
                    volPrices.remove(scale);
                }
            }
        }

        VolumePrices.setMaxQuantities(volPrices);
        productData.setVolumePricesMap(volPrices);

        List<PriceData> volumePrices = volPrices.entrySet().stream().map(this::convertEntrySet).collect(Collectors.toList());
        productData.setVolumePrices(volumePrices);
    }

    private PriceData convertEntrySet(Map.Entry<Long, Map<String, PriceData>> entry) {
        if (entry.getValue().size() > 1) {
            // there are custom and list prices
            PriceData listPriceData = entry.getValue().get(LIST);
            PriceData customPriceData = entry.getValue().get(CUSTOM);
            customPriceData.setOriginalValue(listPriceData.getBasePrice());
            return customPriceData;
        }
        return entry.getValue().get(LIST);
    }

    /**
     * {@code DistHashMap}
     * <p>
     * A HashMap that returns the value linked to the key {@code defaultValueKey} if there is no value linked to the {@code key} argument
     * passed to the {@link #get(Object)} method. If there is no value linked to the key {@code defaultValueKey}, then {@code null} is
     * returned.
     * </p>
     *
     * @param <K>
     * @param <V>
     * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
     * @since Distrelec 5.12
     */
    protected static class DistHashMap<K, V> extends HashMap<K, V> {

        private final K defaultValueKey;

        /**
         * Create a new instance of {@code DistHashMap}
         *
         * @param defaultValueKey
         */
        public DistHashMap(final K defaultValueKey) {
            this.defaultValueKey = defaultValueKey;
        }

        /*
         * (non-Javadoc)
         * @see java.util.HashMap#get(java.lang.Object)
         */
        @Override
        public V get(final Object key) {
            final V value = super.get(key);
            return value != null ? value : super.get(defaultValueKey);
        }
    }

    /**
     * A volume price POJO
     *
     * @author daehusir, Distrelec
     * @since Distrelec 1.0
     */
    public static class VolumePrices {
        public static void setMaxQuantities(final Map<Long, Map<String, PriceData>> prices) {
            final Iterator<Long> iterator = prices.keySet().iterator();
            if (iterator.hasNext()) {
                Long key = iterator.next();
                while (iterator.hasNext()) {
                    final Long nextKey = iterator.next();
                    final Map<String, PriceData> priceMap = prices.get(key);
                    final Long maxQuantity = Long.valueOf(nextKey.longValue() - 1);
                    setMaxQuantity(priceMap.get(LIST), maxQuantity);
                    setMaxQuantity(priceMap.get(CUSTOM), maxQuantity);
                    prices.put(key, priceMap);
                    key = nextKey;
                }
            }
        }

        protected static void setMaxQuantity(final PriceData price, final Long maxQuantity) {
            if (price != null) {
                price.setMaxQuantity(maxQuantity);
            }
        }
    }

    /**
     * Create a Map of type {@code TreeMap} with an overridden {@code get} method which always return a non-null value
     *
     * @return a map for the volume prices.
     * @since Distrelec 5.12
     */
    protected Map<Long, Map<String, PriceData>> createVolumePriceMap() {
        return new TreeMap<>() {

            /*
             * (non-Javadoc)
             * @see java.util.TreeMap#get(java.lang.Object)
             */
            @Override
            public Map<String, PriceData> get(final Object key) {
                if (!this.containsKey(key)) {
                    this.put((Long) key, new DistHashMap<>(LIST));
                }

                return super.get(key);
            }
        };
    }

    /**
     * Calculate the percentage of saving with the {@code volumePrice} compared to the {@code basePrice}.
     *
     * @param productData the target product data
     * @param basePrice   the source base price
     * @param volumePrice the target volume price.
     */
    public static void calculateSaving(final ProductData productData, final PriceData basePrice, final PriceData volumePrice) {
        if (basePrice == null || basePrice.getValue() == null || volumePrice == null || volumePrice.getValue() == null
                || basePrice.getValue().doubleValue() <= 0) {
            return;
        }
        final BigDecimal basePriceValue = basePrice.getValue();
        final BigDecimal diff = basePriceValue.subtract(volumePrice.getValue());
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            volumePrice.setSaving((int) Math.round(100 * diff.doubleValue() / basePriceValue.doubleValue()));
            if (!Boolean.TRUE.equals(productData.getWithSavings()) && volumePrice.getSaving() > 3) {
                productData.setWithSavings(true);
            }
        }
    }

    /**
     * Checks whether the price is a discounted price.
     *
     * @param priceInfo the source price
     * @return {@code true} if the specified price is a discounted one, {@code false} otherwise.
     */
    protected boolean isCustomPrice(final PriceInformation priceInfo) {
        final DistPriceRow priceRow = (DistPriceRow) priceInfo.getQualifierValue(PriceRow.PRICEROW);
        return priceRow != null && (priceRow.isSpecialPrice() || BooleanUtils.isTrue((Boolean) priceInfo.getQualifierValue(DistPriceRow.DISCOUNTED_PRICE)));
    }

    /**
     * Calculate the scale of the given price.
     *
     * @param priceInfo the price.
     * @return the scale of the price.
     */
    protected Long getMinQuantity(final PriceInformation priceInfo) {
        final Map qualifiers = priceInfo.getQualifiers();
        final Object minQtdObj = qualifiers.get(PriceRow.MINQTD);
        if (minQtdObj instanceof Long) {
            return (Long) minQtdObj;
        }
        return null;
    }

    /**
     * Create a {@code PriceData} data object for the given price info.
     *
     * @param priceType   the price type.
     * @param priceInfo   the price info.
     * @param minQuantity the price scale.
     * @return a new instance of {@code PriceData}.
     * @see #createPriceData(PriceDataType, PriceInformation)
     * @since Distrelec 5.11
     */
    protected PriceData createPriceData(final PriceDataType priceType, final PriceInformation priceInfo, final Long minQuantity,
                                        final boolean isCustomPrice) {
        final PriceData price = createPriceData(priceType, priceInfo);
        final DistPriceRow distPriceRow = (DistPriceRow) priceInfo.getQualifierValue(DistPriceRow.PRICEROW);
        price.setMinQuantity(minQuantity);
        if (null != distPriceRow && !isCustomPrice) {
            price.setPricePerX(null != distPriceRow.getPricePerX() ? (BigDecimal.valueOf(distPriceRow.getPricePerX().doubleValue())) : BigDecimal.valueOf(0));
            price.setPricePerXBaseQty(null != distPriceRow.getPricePerXBaseQty() ? Long.valueOf(distPriceRow.getPricePerXBaseQty()) : 0);
            price.setPricePerXUOM(null != distPriceRow.getPricePerXUoM() ? distPriceRow.getPricePerXUoM().getCode() : "");
            price.setPricePerXUOMDesc(null != distPriceRow.getPricePerXUoM() ? distPriceRow.getPricePerXUoM().getName() : "");
            price.setPricePerXUOMQty(null != distPriceRow.getPricePerXUoMQty() ? Long.valueOf(distPriceRow.getPricePerXUoMQty()) : 0);
            price.setVatPercentage(null != distPriceRow.getVatPercentage() ? distPriceRow.getVatPercentage() : 0);
            price.setVatValue(
                    null != distPriceRow.getVatValue() ? distPriceRow.getVatValue()
                            .divide(BigDecimal.valueOf(distPriceRow.getUnitFactorAsPrimitive()))
                            : ZERO);
            price.setBasePrice(null != distPriceRow.getPrice() ? BigDecimal.valueOf(distPriceRow.getPrice() / distPriceRow.getUnitFactorAsPrimitive()) : ZERO);
            price.setPriceWithVat(
                    null != distPriceRow.getPriceWithVat() ? distPriceRow.getPriceWithVat()
                            .divide(BigDecimal.valueOf(distPriceRow.getUnitFactorAsPrimitive()))
                            : ZERO);
        } else {
            final BigDecimal pricePerX = (BigDecimal) priceInfo.getQualifierValue(DistPriceRow.PRICEPERX);
            final Long pricePerXBaseQty = (Long) priceInfo.getQualifierValue(DistPriceRow.PRICEPERXBASEQTY);
            final String pricePerXUOM = (String) priceInfo.getQualifierValue(DistPriceRow.PRICEPERXUOM);
            final String pricePerXUOMDesc = (String) priceInfo.getQualifierValue(PRICEPERXUOMDESCRIPTION);
            final Long pricePerXUOMQty = (Long) priceInfo.getQualifierValue(DistPriceRow.PRICEPERXUOMQTY);
            final Double priceWithVat = (Double) priceInfo.getQualifierValue(DistPriceRow.PRICEWITHVAT);
            final double basePrice = priceInfo.getPriceValue().getValue();

            price.setPricePerX(null != pricePerX ? pricePerX : BigDecimal.ZERO);
            price.setPricePerXBaseQty(pricePerXBaseQty);
            price.setPricePerXUOMDesc(pricePerXUOMDesc);
            price.setPricePerXUOM(pricePerXUOM);
            price.setPricePerXUOMQty(pricePerXUOMQty);
            price.setPriceWithVat(null != priceWithVat ? BigDecimal.valueOf(priceWithVat) : BigDecimal.ZERO);
            price.setBasePrice(BigDecimal.valueOf(basePrice));
        }
        return price;
    }

    /**
     * Create a {@code PriceData} data object for the given price info.
     *
     * @param priceType the price type.
     * @param priceInfo the price info.
     * @return a new instance of {@code PriceData}.
     */
    protected PriceData createPriceData(final PriceDataType priceType, final PriceInformation priceInfo) {
        return getPriceDataFactory().create(priceType, BigDecimal.valueOf(priceInfo.getPriceValue().getValue()),
                priceInfo.getPriceValue().getCurrencyIso());
    }

    // Getters & Setters.

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    protected PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public DistCommercePriceService getDistCommercePriceService() {
        return distCommercePriceService;
    }

    public void setDistCommercePriceService(final DistCommercePriceService distCommercePriceService) {
        this.distCommercePriceService = distCommercePriceService;
    }

    public DistSalesOrgProductService getDistSalesOrgProductService() {
        return distSalesOrgProductService;
    }

    public void setDistSalesOrgProductService(final DistSalesOrgProductService distSalesOrgProductService) {
        this.distSalesOrgProductService = distSalesOrgProductService;
    }

}
