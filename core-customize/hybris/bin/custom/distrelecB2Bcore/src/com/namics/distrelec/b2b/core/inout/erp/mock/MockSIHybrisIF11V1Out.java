/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.mock;

import java.util.Arrays;
import java.util.EnumSet;

import org.apache.log4j.Logger;

import com.distrelec.webservice.if11.v3.OpenOrderCalculationRequest;
import com.distrelec.webservice.if11.v3.OpenOrderCalculationResponse;
import com.distrelec.webservice.if11.v3.OrderCalculationRequest;
import com.distrelec.webservice.if11.v3.OrderCalculationResponse;
import com.distrelec.webservice.if11.v3.OrderEntryRequest;
import com.distrelec.webservice.if11.v3.OrderEntryResponse;
import com.distrelec.webservice.if11.v3.P1FaultMessage;
import com.distrelec.webservice.if11.v3.PaymentMethodResponse;
import com.distrelec.webservice.if11.v3.SIHybrisIF11V1Out;
import com.distrelec.webservice.if11.v3.ShippingMethodCode;
import com.distrelec.webservice.if11.v3.ShippingMethodResponse;


/**
 * {@code MockSIHybrisIF11V1Out}
 * 
 * 
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar</a>, DIT
 * @since Distrelec 7.10
 */
public class MockSIHybrisIF11V1Out implements SIHybrisIF11V1Out {

    private static final Logger LOG = Logger.getLogger(MockSIHybrisIF11V1Out.class);
    private static final String[] PAYMENT_METHOD_CODES = { "WB01", "WB02", "WB03", "WB04", "WB05", "WB06", "WB07" };
    private String defaultPaymentMethod = PAYMENT_METHOD_CODES[0];
    private ShippingMethodCode defaultShippingMethod = ShippingMethodCode.X_1;
    
    
    /* (non-Javadoc)
     * @see com.distrelec.webservice.if11.v3.SIHybrisIF11V1Out#if11V1OrderCalculation(com.distrelec.webservice.if11.v3.OrderCalculationRequest)
     */
    @Override
    public OrderCalculationResponse if11V1OrderCalculation(OrderCalculationRequest orderCalculationRequest) throws P1FaultMessage {
        LOG.info("mock if11OrderCalculation");
        final OrderCalculationResponse response = new OrderCalculationResponse();
        double netSum = 0;
        for (final OrderEntryRequest orderEntryRequest : orderCalculationRequest.getOrderEntries()) {
            final OrderEntryResponse orderEntryResponse = new OrderEntryResponse();
            orderEntryResponse.setMaterialNumber(orderEntryRequest.getMaterialNumber());
            final long quantity = orderEntryRequest.getQuantity();
            orderEntryResponse.setOrderQuantity(quantity);
            final boolean isFreeGiftPromotion = orderEntryRequest.isFreeGiftPromotion();
            orderEntryResponse.setFreeGiftPromotion(isFreeGiftPromotion);
            final double price = 0.42;
            orderEntryResponse.setListPrice(price);
            final double totalPrice = price * quantity;
            orderEntryResponse.setListPriceTotal(totalPrice);
            if (isFreeGiftPromotion) {
                orderEntryResponse.setActualPrice(0);
                orderEntryResponse.setActualPriceTotal(0);
            } else {
                orderEntryResponse.setActualPrice(price);
                orderEntryResponse.setActualPriceTotal(totalPrice);
                netSum += totalPrice;
            }
            response.getOrderEntries().add(orderEntryResponse);
        }

        Arrays.stream(PAYMENT_METHOD_CODES).map(pmc -> {
            final PaymentMethodResponse paymentMethod = new PaymentMethodResponse();
            paymentMethod.setCode(pmc);
            paymentMethod.setPrice(7.50);
            paymentMethod.setSelectable(Boolean.TRUE);
            paymentMethod.setSelected(pmc.equals(defaultPaymentMethod));
            return paymentMethod;
        }).forEach(response.getPaymentMethods()::add);

        EnumSet.allOf(ShippingMethodCode.class).stream().map(smc -> {
            final ShippingMethodResponse shippingMethod = new ShippingMethodResponse();
            shippingMethod.setCode(smc);
            shippingMethod.setPrice(7.50);
            shippingMethod.setSelectable(Boolean.TRUE);
            shippingMethod.setSelected(smc.equals(defaultShippingMethod));
            return shippingMethod;
        }).forEach(response.getShippingMethods()::add);

        response.setSubtotal1(netSum);
        final double shippingPrice = 4.95;
        response.setShippingPrice(Double.valueOf(shippingPrice));
        netSum += shippingPrice;
        response.setSubtotal2(netSum);
        final double tax = netSum * 0.2;
        response.setTax(tax);
        netSum += tax;
        response.setTotal(netSum);
        return response;
    }

    /* (non-Javadoc)
     * @see com.distrelec.webservice.if11.v3.SIHybrisIF11V1Out#if11V1OpenOrderCalculation(com.distrelec.webservice.if11.v3.OpenOrderCalculationRequest)
     */
    @Override
    public OpenOrderCalculationResponse if11V1OpenOrderCalculation(OpenOrderCalculationRequest openOrderCalculationRequest) throws P1FaultMessage {
        final OpenOrderCalculationResponse response = new OpenOrderCalculationResponse();
        response.setOrderId(openOrderCalculationRequest.getOrderId());

        // set new order entries
        final OrderEntryResponse newEntryResponse = new OrderEntryResponse();
        newEntryResponse.setMaterialNumber("4022167");
        newEntryResponse.setOrderQuantity(3);
        newEntryResponse.setListPrice(5);
        newEntryResponse.setListPriceTotal(15);
        newEntryResponse.setActualPrice(7);
        newEntryResponse.setActualPriceTotal(21);
        response.getNewOrderEntries().add(newEntryResponse);

        // set confirmed entries
        final OrderEntryResponse confirmedOrderEntry = new OrderEntryResponse();
        confirmedOrderEntry.setMaterialNumber("4022167");
        confirmedOrderEntry.setOrderQuantity(7);
        confirmedOrderEntry.setListPrice(4);
        confirmedOrderEntry.setListPriceTotal(28);
        confirmedOrderEntry.setActualPrice(6);
        confirmedOrderEntry.setActualPriceTotal(42);
        response.getConfirmedOrderEntries().add(confirmedOrderEntry);

        response.setSubtotal1(50.5);
        response.setPaymentPrice(Double.valueOf(2.5));
        response.setShippingPrice(Double.valueOf(5));
        // set vouchers
        // add discounts
        response.setSubtotal2(30);
        response.setTax(15);
        response.setTotal(45);
        // set freeVoucherPromotion

        return response;
    }
}