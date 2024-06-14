package com.namics.distrelec.b2b.facades.order.converters;

import java.math.BigDecimal;

import org.springframework.util.Assert;

import com.namics.distrelec.b2b.facades.order.data.SapVoucherData;

import de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;

public class DistOrderPopulator extends OrderPopulator {

    @Override
    public void populate(final OrderModel source, final OrderData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        addCommon(source, target);
        addTotals(source, target);
        addEntries(source, target);
        addDeliveryMethod(source, target);
        addPaymentInformation(source, target);
        addGeneratedVoucher(source, target);
        checkForGuestCustomer(source, target);
    }

    @Override
    protected void addCommon(final AbstractOrderModel source, final AbstractOrderData target) {
        target.setCode(source.getCode());
        target.setGuid(source.getGuid());
        target.setTotalItems(calcTotalItems(source));
        target.setNet(Boolean.TRUE.equals(source.getNet()));
        ((OrderData) target).setCreated(source.getCreationtime());
        ((OrderData) target).setStatus(source.getStatus());
        ((OrderData) target).setRequestedDeliveryDate(source.getRequestedDeliveryDate());
        ((OrderData) target).setOpenOrder(source.isOpenOrder());
        ((OrderData) target).setErpOpenOrderCode(source.getErpOpenOrderCode());
    }

    @Override
    protected void addTotals(final AbstractOrderModel source, final AbstractOrderData prototype) {
        super.addTotals(source, prototype);

        prototype.setSubTotal(createPrice(source, source.getNetSubTotal()));
        prototype.setDeliveryCost(source.getDeliveryMode() != null ? createPrice(source, source.getNetDeliveryCost()) : null);
        ((OrderData) prototype).setPaymentCost(source.getPaymentMode() != null ? createPrice(source, source.getNetPaymentCost()) : null);
    }

    protected void addGeneratedVoucher(final AbstractOrderModel source, final AbstractOrderData target) {
        if (source.getGeneratedVoucher() != null) {
            final SapVoucherData voucher = new SapVoucherData();
            voucher.setCode(source.getGeneratedVoucher().getCode());
            voucher.setValidFrom(source.getGeneratedVoucher().getValidFrom());
            voucher.setValidUntil(source.getGeneratedVoucher().getValidUntil());
            voucher.setValue(getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(source.getGeneratedVoucher().getValue()),
                                                          source.getCurrency().getIsocode()));
            ((OrderData) target).setGeneratedVoucher(voucher);
        }
    }
}
