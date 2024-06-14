package com.namics.distrelec.b2b.facades.order.converters;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.distrelec.webservice.if15.v1.OrdersSearchLine;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistOrderStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

public class SapSearchOrderHistoryConverter extends AbstractPopulatingConverter<OrdersSearchLine, OrderHistoryData> {

    @Autowired
    private DistrelecCodelistService codelistService;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Override
    protected OrderHistoryData createTarget() {
        return new OrderHistoryData();
    }

    @Override
    public void populate(final OrdersSearchLine source, final OrderHistoryData target) {
        target.setCode(source.getOrderId());
        // Setting the order status
        final String orderStatus = source.getOrderStatus();
        if (StringUtils.isNotBlank(StringUtils.trim(orderStatus))) {
            final DistOrderStatusModel distOrderStatus = getCodelistService().getDistOrderStatus(source.getOrderStatus());
            target.setStatus(distOrderStatus.getHybrisOrderStatus());
        }
        if (source.getCurrencyCode() != null) {
            target.setCurrency(source.getCurrencyCode().name());
        }
        target.setTotal(createPriceData(source.getOrderTotal(), source.getCurrencyCode() != null ? source.getCurrencyCode().name() : "N/A"));
        if (source.getOrderDate() != null) {
            target.setPlaced(SoapConversionHelper.convertDate(source.getOrderDate()));
        }

        // UI need to adjust to show this value
        target.setInvoiceIds(StringUtils.join(source.getInvoiceIds(), ", "));

        target.setUserFullName(StringUtils.isNotBlank(source.getContactName()) ? source.getContactName() : "n/a");

        // DISTRELEC-3703: Est. Delivery Date is skipped

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
