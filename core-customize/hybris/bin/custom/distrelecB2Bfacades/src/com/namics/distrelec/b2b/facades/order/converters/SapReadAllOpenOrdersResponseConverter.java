package com.namics.distrelec.b2b.facades.order.converters;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.distrelec.webservice.if15.v1.OpenOrders;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistOrderStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.enums.OrderStatus;

public class SapReadAllOpenOrdersResponseConverter extends AbstractPopulatingConverter<OpenOrders, OrderHistoryData> {

    @Autowired
    private DistrelecCodelistService codelistService;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Deprecated
    @Override
    protected OrderHistoryData createTarget() {
        return new OrderHistoryData();
    }

    @Override
    public void populate(final OpenOrders source, final OrderHistoryData target) {
        // Order Nr.
        target.setCode(source.getOrderId());

        // Close Date

        if (source.getOrderCloseDate() != null) {
            target.setCloseDate(SoapConversionHelper.convertDate(source.getOrderCloseDate()));
            target.setPlaced(SoapConversionHelper.convertDate(source.getOrderCloseDate()));
        }

        // Order Total
        if (source.getCurrencyCode() != null) {
            target.setCurrency(source.getCurrencyCode().name());
        }
        target.setTotal(createPriceData(source.getSubtotal2(), source.getCurrencyCode() != null ? source.getCurrencyCode().name() : "N/A"));
        if (source.getOrderDate() != null) {
            target.setPlaced(SoapConversionHelper.convertDate(source.getOrderDate()));
        }

        // Start Date
        if (source.getOrderDate() != null) {
            target.setPlaced(SoapConversionHelper.convertDate(source.getOrderDate()));
        }

        // Order status
        final String orderStatus = source.getOrderStatus();
        if (null != orderStatus & !orderStatus.trim().equals("")) {
            final DistOrderStatusModel distOrderStatus = getCodelistService().getDistOrderStatus(source.getOrderStatus());
            target.setStatus(distOrderStatus.getHybrisOrderStatus());
        } else {
            target.setStatus(OrderStatus.ERP_STATUS_UNKNOWN);
        }

        // Your Order Reference
        target.setOrderReference(source.getCustomerReferenceHeaderLevel());

        super.populate(source, target);
    }

    protected PriceData createPriceData(final double value, final String currencyIso) {
        final Double val = Double.valueOf(value);
        Assert.notNull(val, "Price value cannot be null.");
        return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(val != null ? val.doubleValue() : 0), currencyIso);
    }

    public DistrelecCodelistService getCodelistService() {
        return codelistService;
    }

    public void setCodelistService(final DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }
}
