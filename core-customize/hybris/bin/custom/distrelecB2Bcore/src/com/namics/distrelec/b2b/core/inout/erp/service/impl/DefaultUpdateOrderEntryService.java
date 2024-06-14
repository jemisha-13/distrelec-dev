/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.service.impl;

import static com.namics.distrelec.b2b.core.inout.erp.converters.response.OrderCalculationResponseCommonMapping.IS_BACKORDER;
import static java.util.Objects.isNull;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.webservice.if11.v3.OrderEntryResponse;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.erp.service.UpdateOrderEntryService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;

/**
 * @author datneerajs, Namics AG
 * @since Distrelec 1.1
 */
public class DefaultUpdateOrderEntryService implements UpdateOrderEntryService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUpdateOrderEntryService.class);

    private static final double ZERO_AS_DOUBLE = 0.00;

    private static final long ZERO_AS_LONG = 0;

    @Autowired
    private DistCommercePriceService distCommercePriceService;

    @Override
    public void updateOrderEntry(final OrderEntryResponse orderEntryResponse, final AbstractOrderEntryModel abstractOrderEntryModel,
                                 final Map<String, OrderEntryResponse> additionalAvailabilityInfoMap) {

        boolean isNormalEntry = BooleanUtils.isFalse(abstractOrderEntryModel.getConfirmed());
        final long existingQuantity = isNormalEntry || abstractOrderEntryModel.getQuantity() == null ? ZERO_AS_LONG : abstractOrderEntryModel.getQuantity();
        long finalQuantity = ZERO_AS_LONG;
        final OrderEntryResponse additionalAvailabilityOrderEntryResponse = additionalAvailabilityInfoMap.get(orderEntryResponse.getMaterialNumber());

        if (additionalAvailabilityOrderEntryResponse != null) {
            finalQuantity = additionalAvailabilityOrderEntryResponse.getOrderQuantity();
        }
        finalQuantity += existingQuantity + orderEntryResponse.getOrderQuantity();
        abstractOrderEntryModel.setQuantity(finalQuantity);

        abstractOrderEntryModel.setBaseListPrice(orderEntryResponse.getListPrice());

        final double existingTotalListPrice = getPriceIfExistsOrFallback(abstractOrderEntryModel.getTotalListPrice(), isNormalEntry);
        final OrderEntryResponse additionalOrderEntryResponse = additionalAvailabilityInfoMap.get(abstractOrderEntryModel.getProduct().getCode());

        double totalListPrice = existingTotalListPrice + orderEntryResponse.getListPriceTotal();
        if (additionalOrderEntryResponse != null) {
            totalListPrice = totalListPrice + additionalOrderEntryResponse.getListPriceTotal();
            abstractOrderEntryModel.setTotalListPrice(totalListPrice);
        } else {
            abstractOrderEntryModel.setTotalListPrice(totalListPrice);
        }

        if (orderEntryResponse.getActualPrice() == ZERO_AS_DOUBLE) {
            LOG.error("{} {} Zero Price for the Product {}", ErrorLogCode.ZERO_PRICE_ERROR, ErrorSource.SAP_FAULT, orderEntryResponse.getItemNumber());
        }

        abstractOrderEntryModel.setBasePrice(orderEntryResponse.getActualPrice());
        abstractOrderEntryModel.setBaseNetPrice(orderEntryResponse.getActualPrice());

        final double existingTotalPrice = getPriceIfExistsOrFallback(abstractOrderEntryModel.getTotalPrice(), isNormalEntry);
        if (additionalOrderEntryResponse != null) {
            final double totalPrice = existingTotalPrice + orderEntryResponse.getActualPriceTotal() + additionalOrderEntryResponse.getActualPriceTotal();
            abstractOrderEntryModel.setTotalPrice(totalPrice);
        } else {
            abstractOrderEntryModel.setTotalPrice(existingTotalPrice + orderEntryResponse.getActualPriceTotal());
        }

        final double existingTotalNetPrice = getPriceIfExistsOrFallback(abstractOrderEntryModel.getTotalNetPrice(), isNormalEntry);
        abstractOrderEntryModel.setTotalNetPrice(existingTotalNetPrice + orderEntryResponse.getActualPriceTotal());
        abstractOrderEntryModel.setCalculated(Boolean.TRUE);
        abstractOrderEntryModel.setIsBOM(isHigherLevelItemNotEqual(orderEntryResponse));

        // Quotation handling
        if (StringUtils.isNotBlank(orderEntryResponse.getQuotationId())) {
            handleQuotation(orderEntryResponse, abstractOrderEntryModel);
        }

        setNetMargin(abstractOrderEntryModel);
        abstractOrderEntryModel.setMview(orderEntryResponse.getMview() != null ? orderEntryResponse.getMview() : StringUtils.EMPTY);
        if (additionalOrderEntryResponse != null) {
            abstractOrderEntryModel.setIsBackOrder(StringUtils.equalsIgnoreCase(additionalOrderEntryResponse.getBackOrderFlag(), IS_BACKORDER));
        }
    }

    private double getPriceIfExistsOrFallback(Double price, boolean isNormalEntry) {
        return isNormalEntry || isNull(price) ? ZERO_AS_DOUBLE : price;
    }

    private void setNetMargin(AbstractOrderEntryModel abstractOrderEntryModel) {
        final double fallbackValue = 0.0;
        if (abstractOrderEntryModel.getNetMargin() == null) {
            List<PriceInformation> pricesInfos = distCommercePriceService.getScaledPriceInformations(abstractOrderEntryModel.getProduct(), Boolean.TRUE);
            if (CollectionUtils.isNotEmpty(pricesInfos)) {
                DistPriceRow priceRow = (DistPriceRow) pricesInfos.iterator().next().getQualifierValue(PriceRow.PRICEROW);
                abstractOrderEntryModel.setNetMargin(isIF07Data(priceRow) ? calculateNetMargin(priceRow) : fallbackValue);
            } else {
                abstractOrderEntryModel.setNetMargin(fallbackValue);
            }
        }
    }

    private double calculateNetMargin(DistPriceRow priceRow) {
        return priceRow.getNetMargin() / priceRow.getUnitFactor();
    }

    private boolean isIF07Data(DistPriceRow priceRow) {
        // can be null, because IF07 doesn't return netMargin
        return priceRow != null && priceRow.getNetMargin() != null && priceRow.getUnitFactor() != null;
    }

    private Boolean isHigherLevelItemNotEqual(OrderEntryResponse orderEntryResponse) {
        return "000000".equals(orderEntryResponse.getHigherLevelItem()) ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public void handleQuotation(final OrderEntryResponse orderEntryResponse, final AbstractOrderEntryModel abstractOrderEntryModel) {
        abstractOrderEntryModel.setLineNumber(orderEntryResponse.getQuotationItem());
        abstractOrderEntryModel.setQuotationId(orderEntryResponse.getQuotationId());
        abstractOrderEntryModel.setQuotation(Boolean.TRUE);
        abstractOrderEntryModel.setDummyItem(orderEntryResponse.isDummyItem());
        abstractOrderEntryModel.setArticleDescription(orderEntryResponse.getArticleDescription());
        if (orderEntryResponse.getQuotationExpiryDate() != null && orderEntryResponse.getQuotationExpiryDate().intValue() > 0) {
            try {
                abstractOrderEntryModel.setQuotationExpiryDate(SoapConversionHelper.convertDate(orderEntryResponse.getQuotationExpiryDate()));
            } catch (final Exception exp) {
                // NOP
            }
        }
    }
}
