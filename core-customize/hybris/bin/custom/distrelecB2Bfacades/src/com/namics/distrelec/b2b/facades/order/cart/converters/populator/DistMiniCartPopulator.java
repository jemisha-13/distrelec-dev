package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import static java.util.stream.Collectors.groupingBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;

import de.hybris.platform.commercefacades.order.converters.populator.MiniCartPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DistMiniCartPopulator extends MiniCartPopulator {

    @Autowired
    private Converter<AbstractOrderModel, DistErpVoucherInfoData> distErpVoucherInfoDataConverter;

    @Autowired
    private DistPriceDataFactory priceDataFactory;

    @Override
    public void populate(CartModel source, CartData target) {
        super.populate(source, target);
        if (source != null) {
            addEntries(source, target);
            addPromotions(source, target);

            if (source.getErpVoucherInfo() != null) {
                target.setErpVoucherInfoData(distErpVoucherInfoDataConverter.convert(source));
            }
        }
    }

    @Override
    protected void addEntries(final AbstractOrderModel source, final AbstractOrderData target) {
        final CartData cartData = (CartData) target;
        final Map<Boolean, List<AbstractOrderEntryModel>> map = source.getEntries().stream()
                                                                      .collect(groupingBy(entry -> Boolean.TRUE.equals(entry.getConfirmed())));

        cartData.setEntries(Converters.convertAll(map.get(Boolean.FALSE), getOrderEntryConverter()));
        cartData.setConfirmedEntries(Converters.convertAll(map.get(Boolean.TRUE), getOrderEntryConverter()));
    }

    @Override
    protected void addTotals(final AbstractOrderModel source, final AbstractOrderData target) {
        super.addTotals(source, target);
        String currencyIsoCode = source.getCurrency().getIsocode();
        target.setSubTotal(createPriceWithoutCurrency(source.getNetSubTotal() == null ? 0D : source.getNetSubTotal(), currencyIsoCode));
        target.setDeliveryCost(createPriceWithoutCurrency(source.getNetDeliveryCost() == null ? 0D : source.getNetDeliveryCost(), currencyIsoCode));
        target.setPaymentCost(createPriceWithoutCurrency(source.getNetPaymentCost() == null ? 0D : source.getNetPaymentCost(), currencyIsoCode));
        target.setTotalTax(createPriceWithoutCurrency(source.getTotalTax() == null ? 0D : source.getTotalTax(), currencyIsoCode));
        target.setTotalPrice(createPriceWithoutCurrency(source.getTotalPrice() == null ? 0D : source.getTotalPrice(), currencyIsoCode));
    }

    private PriceData createPriceWithoutCurrency(Double price, String currency) {
        return priceDataFactory.createWithoutCurrency(PriceDataType.BUY, BigDecimal.valueOf(price), currency);
    }
}
