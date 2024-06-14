/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.DistCreditBlockEnum;
import com.namics.distrelec.b2b.core.inout.erp.OrderService;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.service.order.model.ErpOpenOrderExtModel;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BCartPopulator;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Distrelec specific cart data converter implementation.
 */
public class DistCartPopulator extends B2BCartPopulator<CartData> {

    private Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter;

    private Converter<AbstractDistDeliveryModeModel, DeliveryModeData> distDeliveryModeConverter;

    private Converter<WarehouseModel, WarehouseData> warehouseConverter;

    private ConfigurationService configurationService;

    private OrderService erpOrderService;

    private Converter<AbstractOrderModel, DistErpVoucherInfoData> distErpVoucherInfoDataConverter;

    private CMSSiteService cmsSiteService;

    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Override
    public void populate(final CartModel source, final CartData target) {
        addCommon(source, target);
        addTotals(source, target);
        addEntries(source, target);
        addPromotions(source, target);
        addSavedCartData(source, target);
        addEntryGroups(source, target);
        addWaldomFlag(source, target);
        addPrincipalInformation(source, target);
        target.setGuid(source.getGuid());
        target.setTotalUnitCount(calcTotalUnitCount(source));
        target.setCreditBlocked(isCreditBlocked(source));
        final B2BUnitModel unit = getB2BUnit(source);
        if (source.getPaymentAddress() != null) {
            target.setBillingAddress(getAddressData(source.getPaymentAddress(), unit));
        }

        if (source.getPaymentMode() != null) {
            target.setPaymentMode(getPaymentModeConverter().convert(source.getPaymentMode()));
        }

        if (source.getDeliveryMode() != null) {
            target.setDeliveryMode(
                                   getDistDeliveryModeConverter().convert((AbstractDistDeliveryModeModel) source.getDeliveryMode()));
        }

        if (source.getPickupLocation() != null) {
            target.setPickupLocation(getWarehouseConverter().convert(source.getPickupLocation()));
        }

        setValidDeliveryDates(source, target);

        target.setValidPaymentModes(getValidPaymentModes(source));

        // future delivery date support
        if (source.getRequestedDeliveryDate() != null) {
            target.setReqDeliveryDateHeaderLevel(source.getRequestedDeliveryDate());
        }

        target.setNote(source.getNote());
        target.setProjectNumber(source.getProjectNumber());
        target.setDistCostCenter(source.getCostCenter());

        // Ricevuta Bancaria details for IT shop
        target.setBankCollectionInstitute(source.getBankCollectionInstitute());
        target.setBankCollectionCAB(source.getBankCollectionCAB());
        target.setBankCollectionABI(source.getBankCollectionABI());

        if (source.getPaymentCost() != null) {
            final String currencyIso = source.getCurrency().getIsocode();
            final BigDecimal value = BigDecimal.valueOf(source.getPaymentCost().doubleValue());
            final PriceData price = getPriceDataFactory().create(PriceDataType.BUY, value, currencyIso);
            target.setPaymentCost(price);
        }

        // vouchers
        if (source.getErpVoucherInfo() != null) {
            target.setErpVoucherInfoData(getDistErpVoucherInfoDataConverter().convert(source));
        }

        target.setTotalItemsSingle(calcTotalSingleItems(source));

        // Open order
        Date existingOpenOrderCloseDate = null;
        if (source.isOpenOrder()) {
            target.setOpenOrder(true);
            target.setOpenOrderNew(StringUtils.isBlank(source.getErpOpenOrderCode()));
            target.setOpenOrderErpCode(source.getErpOpenOrderCode());
            target.setOpenOrderEditableForAllContacts(getOpenOrderPermission(source));

            // checkout with new open order
            if (BooleanUtils.isFalse(target.getOpenOrderNew())) {
                // retrive the open order details from SAP
                final ErpOpenOrderExtModel existingOpenOrder = erpOrderService
                                                                              .getOpenOrderForErpOrderCode(source.getErpOpenOrderCode());
                existingOpenOrderCloseDate = existingOpenOrder == null ? null : existingOpenOrder.getOrderCloseDate();
                target.setOpenOrderCreationDate(existingOpenOrder == null ? null : existingOpenOrder.getOrderDate());
                target.setOpenOrderSelectedClosingDate(existingOpenOrderCloseDate);
            } else {
                target.setOpenOrderSelectedClosingDate(source.getOrderCloseDate());
            }

        }

        super.populate(source, target);

        // manage total discounts value
        target.setTotalDiscounts(
                                 source.getTotalDiscounts() == null ? null : createPrice(source, source.getTotalDiscounts()));

        // DISTRELEC-7851: populating the Free Freight data
        if (target.getSubTotal() != null) {
            final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
            final double minFreeFreight = getConfigurationService().getConfiguration()
                                                                   .getDouble("cart.free.freight.min.value." + currentSite.getUid(), 0.0);
            final double minFreeFreightValue = minFreeFreight - target.getSubTotal().getValue().doubleValue();

            final BigDecimal value = BigDecimal.valueOf(minFreeFreightValue);
            final PriceData price = getPriceDataFactory().create(PriceDataType.BUY, value,
                                                                 source.getCurrency().getIsocode());

            target.setFreeFreightValue(price);

        }
        if (null != source.getCurrency()) {
            target.setCurrency(currencyConverter.convert(source.getCurrency()));
        }
        if (source.getCompleteDelivery() != null) {
            target.setCompleteDelivery(source.getCompleteDelivery());
        } else {
            target.setCompleteDelivery(Boolean.FALSE);
        }

        if (source.getDeliveryMode() == null && target.getTotalPrice() != null) {
            subtractDeliveryCostFromTotal(source.getNetDeliveryCost(), target);
        }

        target.setMissingMov(source.getMissingMov());
        target.setMovLimit(source.getMovLimit());
        target.setMovCurrency(source.getMovCurrency());
        if (source.getSkipMovCheck() != null) {
            target.setSkipMovCheck(source.getSkipMovCheck());
        } else {
            target.setSkipMovCheck(false);
        }
        if (source.getReevooEligible() != null) {
            target.setReevooEligible(source.getReevooEligible());
        }

        target.setCodiceCUP(source.getCodiceCUP());
        target.setCodiceCIG(source.getCodiceCIG());
        target.setGhostOrder(source.isGhostOrder());
    }

    private List<DistPaymentModeData> getValidPaymentModes(CartModel source) {
        return CollectionUtils.isEmpty(source.getValidPaymentModes()) ? Collections.emptyList()
                                                                      : source.getValidPaymentModes().stream()
                                                                              .map(paymentMode -> getPaymentModeConverter().convert(paymentMode))
                                                                              .collect(toList());
    }

    private AddressData getAddressData(AddressModel address, B2BUnitModel unit) {
        AddressData addressData = getAddressConverter().convert(address);
        if (nonNull(unit) && nonNull(addressData) && StringUtils.isNotBlank(unit.getVatID())) {
            addressData.setCodiceFiscale(unit.getVatID());
        }
        return addressData;
    }

    private B2BUnitModel getB2BUnit(CartModel source) {
        return (source.getUser() instanceof B2BCustomerModel)
                                                              ? ((B2BCustomerModel) source.getUser()).getDefaultB2BUnit()
                                                              : null;
    }

    private void setValidDeliveryDates(CartModel source, CartData target) {
        List<DeliveryModeData> validDeliveryModes = CollectionUtils.isEmpty(source.getValidDeliveryModes()) ? Collections.emptyList()
                                                                                                            : source.getValidDeliveryModes().stream()
                                                                                                                    .map(deliveryMode -> getDistDeliveryModeConverter().convert(deliveryMode))
                                                                                                                    .collect(toList());

        for (DeliveryModeData deliveryModeData : validDeliveryModes) {
            if (source.getDeliveryMode() != null) {
                deliveryModeData.setSelected(StringUtils.equals(deliveryModeData.getCode(), source.getDeliveryMode().getCode()));
            } else {
                deliveryModeData.setSelected(Boolean.FALSE);
            }
        }
        target.setValidDeliveryModes(validDeliveryModes);
    }

    private void subtractDeliveryCostFromTotal(Double sourceDeliveryCost, CartData target) {
        BigDecimal deliveryCost = Optional.ofNullable(sourceDeliveryCost)
                                          .map(BigDecimal::new)
                                          .orElse(BigDecimal.ZERO);
        PriceData totalPrice = target.getTotalPrice();
        if (!deliveryCost.equals(BigDecimal.ZERO) && totalPrice != null) {
            totalPrice.setValue(totalPrice.getValue().subtract(deliveryCost));
            target.setDeliveryCostExcluded(true);
        }
    }

    @Override
    protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
                                 final AbstractOrderData target) {
        super.addPromotions(source, promoOrderResults, target);

        if (promoOrderResults != null) {
            final CartData cartData = (CartData) target;
            cartData.setPotentialOrderPromotions(getPromotions(promoOrderResults.getPotentialOrderPromotions()));
            cartData.setPotentialProductPromotions(getPromotions(promoOrderResults.getPotentialProductPromotions()));
        }
    }

    protected void addSavedCartData(final CartModel source, final CartData target) {
        if (StringUtils.isNotEmpty(source.getName())) {
            target.setName(source.getName());
        }
        if (StringUtils.isNotEmpty(source.getDescription())) {
            target.setDescription(source.getDescription());
        }
        if (source.getSaveTime() != null) {
            target.setSaveTime(source.getSaveTime());
        }
        if (source.getExpirationTime() != null) {
            target.setExpirationTime(source.getExpirationTime());
        }

        if (source.getSavedBy() != null) {
            final PrincipalData savedBy = new PrincipalData();
            if (StringUtils.isNotEmpty(source.getSavedBy().getName())) {
                savedBy.setName(source.getSavedBy().getName());
            }

            if (StringUtils.isNotEmpty(source.getSavedBy().getUid())) {
                savedBy.setUid(source.getSavedBy().getUid());

            }
            target.setSavedBy(savedBy);
        }
    }

    private boolean getOpenOrderPermission(final CartModel source) {
        return source.getEditableByAllContacts() == null ? false : source.getEditableByAllContacts().booleanValue();
    }

    @Override
    protected void addEntries(final AbstractOrderModel source, final AbstractOrderData prototype) {
        final CartData cartData = (CartData) prototype;

        final Map<Boolean, List<AbstractOrderEntryModel>> map = source.getEntries().stream()
                                                                      .collect(groupingBy(entry -> Boolean.TRUE.equals(entry.getConfirmed())));

        cartData.setEntries(Converters.convertAll(map.get(Boolean.FALSE), getOrderEntryConverter()));
        cartData.setConfirmedEntries(Converters.convertAll(map.get(Boolean.TRUE), getOrderEntryConverter()));
    }

    private long calcTotalSingleItems(final CartModel source) {
        return CollectionUtils.isEmpty(source.getEntries()) ? 0L
                                                            : source.getEntries().stream().map(entry -> entry.getQuantity().longValue()).reduce(0L,
                                                                                                                                                (a, b) -> a
                                                                                                                                                          + b);
    }

    private void addWaldomFlag(final AbstractOrderModel source, final CartData cartData) {
        boolean hasWaldom = source.getEntries().stream()
                                  .anyMatch(entry -> StringUtils.equals(DistConstants.Product.WALDOM, entry.getMview()));
        cartData.setWaldom(hasWaldom);
    }

    private boolean isCreditBlocked(CartModel source) {
        if (StringUtils.isNotEmpty(source.getCreditBlocked())) {
            return DistCreditBlockEnum.B.getValue().equalsIgnoreCase(source.getCreditBlocked())
                    || DistCreditBlockEnum.C.getValue().equalsIgnoreCase(source.getCreditBlocked());
        }
        return Boolean.FALSE;
    }

    @Override
    protected void addTotals(final AbstractOrderModel source, final AbstractOrderData prototype) {
        super.addTotals(source, prototype);

        prototype.setSubTotal(createPrice(source, source.getNetSubTotal()));
        prototype.setDeliveryCost(source.getDeliveryMode() != null ? createPrice(source, source.getNetDeliveryCost()) : null);
        prototype.setPaymentCost(source.getPaymentMode() != null ? createPrice(source, source.getNetPaymentCost()) : null);
    }

    public Converter<PaymentModeModel, DistPaymentModeData> getPaymentModeConverter() {
        return paymentModeConverter;
    }

    public void setPaymentModeConverter(final Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter) {
        this.paymentModeConverter = paymentModeConverter;
    }

    public Converter<WarehouseModel, WarehouseData> getWarehouseConverter() {
        return warehouseConverter;
    }

    public void setWarehouseConverter(final Converter<WarehouseModel, WarehouseData> warehouseConverter) {
        this.warehouseConverter = warehouseConverter;
    }

    public Converter<AbstractDistDeliveryModeModel, DeliveryModeData> getDistDeliveryModeConverter() {
        return distDeliveryModeConverter;
    }

    public void setDistDeliveryModeConverter(final Converter<AbstractDistDeliveryModeModel, DeliveryModeData> distDeliveryModeConverter) {
        this.distDeliveryModeConverter = distDeliveryModeConverter;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public OrderService getErpOrderService() {
        return erpOrderService;
    }

    public void setErpOrderService(final OrderService erpOrderService) {
        this.erpOrderService = erpOrderService;
    }

    public Converter<AbstractOrderModel, DistErpVoucherInfoData> getDistErpVoucherInfoDataConverter() {
        return distErpVoucherInfoDataConverter;
    }

    public void setDistErpVoucherInfoDataConverter(
                                                   final Converter<AbstractOrderModel, DistErpVoucherInfoData> distErpVoucherInfoDataConverter) {
        this.distErpVoucherInfoDataConverter = distErpVoucherInfoDataConverter;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public Converter<CurrencyModel, CurrencyData> getCurrencyConverter() {
        return currencyConverter;
    }

    public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter) {
        this.currencyConverter = currencyConverter;
    }

}
