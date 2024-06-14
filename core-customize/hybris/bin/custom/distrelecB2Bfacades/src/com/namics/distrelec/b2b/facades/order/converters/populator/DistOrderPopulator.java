/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.converters.populator;

import static java.util.Objects.nonNull;

import java.math.BigDecimal;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.facades.order.data.DistDiscountData;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;

import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BOrderPopulator;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;

public class DistOrderPopulator extends B2BOrderPopulator {

    private Converter<WarehouseModel, WarehouseData> warehouseConverter;

    private Converter<AddressModel, AddressData> addressConverter;

    private Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter;

    private Converter<DiscountModel, DistDiscountData> discountConverter;

    private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;

    private Converter<BaseStoreModel, BaseStoreData> baseStoreConverter;

    private Converter<BaseSiteModel, BaseSiteData> baseSiteConverter;

    private Converter<AbstractOrderModel, DistErpVoucherInfoData> distErpVoucherInfoDataConverter;

    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Autowired
    private EnumerationService enumerationService;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Override
    public void populate(final OrderModel orderModel, final OrderData b2BOrderData) throws ConversionException {
        super.populate(orderModel, b2BOrderData);

        b2BOrderData.setErpOrderCode(orderModel.getErpOrderCode());
        b2BOrderData.setCode(orderModel.getCode());
        b2BOrderData.setCustomerType(b2BOrderData.getB2bCustomerData().getCustomerType().getCode());
        b2BOrderData.setOrderDate(orderModel.getDate());

        if (orderModel.getPickupLocation() != null) {
            b2BOrderData.setPickupLocation(warehouseConverter.convert(orderModel.getPickupLocation()));
        }
        b2BOrderData.setDeliveryAddress(convertAddress(orderModel.getDeliveryAddress()));
        b2BOrderData.setBillingAddress(convertAddress(orderModel.getPaymentAddress()));

        if (orderModel.getDeliveryMode() != null) {
            b2BOrderData.setDeliveryMode(deliveryModeConverter.convert(orderModel.getDeliveryMode()));
        }
        b2BOrderData.setDeliveryCost(createPrice(orderModel.getNetDeliveryCost(), orderModel.getCurrency()));

        if (orderModel.getStore() != null) {
            b2BOrderData.setBaseStore(baseStoreConverter.convert(orderModel.getStore()));
        }

        if (orderModel.getSite() != null) {
            b2BOrderData.setBaseSite(baseSiteConverter.convert(orderModel.getSite()));
        }

        if (orderModel.getSalesApplication() != null) {
            b2BOrderData.setSalesApplication(enumerationService.getEnumerationName(orderModel.getSalesApplication()));
        }

        if (null != orderModel.getCurrency()) {
            b2BOrderData.setCurrency(currencyConverter.convert(orderModel.getCurrency()));
        }
        b2BOrderData.setProjectNumber(orderModel.getProjectNumber());
        b2BOrderData.setDistCostCenter(orderModel.getCostCenter());
        b2BOrderData.setNote(orderModel.getNote());

        if (CollectionUtils.isNotEmpty(orderModel.getDiscounts())) {
            for (final DiscountModel discountModel : orderModel.getDiscounts()) {
                b2BOrderData.getDiscounts().add(discountConverter.convert(discountModel));
            }
        }
        b2BOrderData.setPaymentCost(createPrice(orderModel.getNetPaymentCost(), orderModel.getCurrency()));
        b2BOrderData.setPaymentMode(new DistPaymentModeData());

        if (orderModel.getPaymentMode() instanceof AbstractDistPaymentModeModel) {
            b2BOrderData.setPaymentMode(paymentModeConverter.convert(orderModel.getPaymentMode()));
        }

        b2BOrderData.setSubTotal(createPrice(orderModel.getNetSubTotal(), orderModel.getCurrency()));

        if (orderModel.getErpVoucherInfo() != null) {
            b2BOrderData.setErpVoucherInfoData(getDistErpVoucherInfoDataConverter().convert(orderModel));
        }

        b2BOrderData.setExceededBudgetPrice(createPrice(orderModel.getExceededBudget(), orderModel.getCurrency()));
    }

    private PriceData createPrice(Double value, CurrencyModel currency) {
        return priceDataFactory.create(PriceDataType.BUY,
                                       nonNull(value) ? BigDecimal.valueOf(value) : BigDecimal.ZERO,
                                       currency.getIsocode());
    }

    private AddressData convertAddress(final AddressModel address) {
        return address != null && address.getPk() != null ? addressConverter.convert(address) : null;
    }

    public Converter<WarehouseModel, WarehouseData> getWarehouseConverter() {
        return warehouseConverter;
    }

    public void setWarehouseConverter(final Converter<WarehouseModel, WarehouseData> warehouseConverter) {
        this.warehouseConverter = warehouseConverter;
    }

    public Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }

    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    public Converter<DiscountModel, DistDiscountData> getDiscountConverter() {
        return discountConverter;
    }

    public void setDiscountConverter(final Converter<DiscountModel, DistDiscountData> discountConverter) {
        this.discountConverter = discountConverter;
    }

    public Converter<DeliveryModeModel, DeliveryModeData> getDeliveryModeConverter() {
        return deliveryModeConverter;
    }

    public void setDeliveryModeConverter(
                                         final Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter) {
        this.deliveryModeConverter = deliveryModeConverter;
    }

    public Converter<BaseStoreModel, BaseStoreData> getBaseStoreConverter() {
        return baseStoreConverter;
    }

    public void setBaseStoreConverter(final Converter<BaseStoreModel, BaseStoreData> baseStoreConverter) {
        this.baseStoreConverter = baseStoreConverter;
    }

    public Converter<BaseSiteModel, BaseSiteData> getBaseSiteConverter() {
        return baseSiteConverter;
    }

    public void setBaseSiteConverter(final Converter<BaseSiteModel, BaseSiteData> baseSiteConverter) {
        this.baseSiteConverter = baseSiteConverter;
    }

    public Converter<PaymentModeModel, DistPaymentModeData> getPaymentModeConverter() {
        return paymentModeConverter;
    }

    public void setPaymentModeConverter(final Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter) {
        this.paymentModeConverter = paymentModeConverter;
    }

    public Converter<AbstractOrderModel, DistErpVoucherInfoData> getDistErpVoucherInfoDataConverter() {
        return distErpVoucherInfoDataConverter;
    }

    public void setDistErpVoucherInfoDataConverter(
                                                   final Converter<AbstractOrderModel, DistErpVoucherInfoData> distErpVoucherInfoDataConverter) {
        this.distErpVoucherInfoDataConverter = distErpVoucherInfoDataConverter;
    }

    public Converter<CurrencyModel, CurrencyData> getCurrencyConverter() {
        return currencyConverter;
    }

    public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter) {
        this.currencyConverter = currencyConverter;
    }

}
