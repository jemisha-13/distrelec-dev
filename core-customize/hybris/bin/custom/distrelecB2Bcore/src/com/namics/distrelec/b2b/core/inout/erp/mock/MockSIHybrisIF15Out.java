/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import com.distrelec.webservice.if15.v1.AddressWithId;
import com.distrelec.webservice.if15.v1.CurrencyCode;
import com.distrelec.webservice.if15.v1.Deliveries;
import com.distrelec.webservice.if15.v1.Delivery;
import com.distrelec.webservice.if15.v1.HandlingUnit;
import com.distrelec.webservice.if15.v1.Item;
import com.distrelec.webservice.if15.v1.OpenOrders;
import com.distrelec.webservice.if15.v1.OrderReadEntry;
import com.distrelec.webservice.if15.v1.OrderSearchRequestV2;
import com.distrelec.webservice.if15.v1.OrderSearchResponseV2;
import com.distrelec.webservice.if15.v1.OrdersSearchLine;
import com.distrelec.webservice.if15.v1.P2FaultMessage;
import com.distrelec.webservice.if15.v1.ReadAllOpenOrdersRequestV2;
import com.distrelec.webservice.if15.v1.ReadAllOpenOrdersResponseV2;
import com.distrelec.webservice.if15.v1.ReadOrderRequestV2;
import com.distrelec.webservice.if15.v1.ReadOrderResponseV2;
import com.distrelec.webservice.if15.v1.SIHybrisIF15Out;
import com.distrelec.webservice.if15.v1.Title;

public class MockSIHybrisIF15Out implements SIHybrisIF15Out {

    private static final Logger LOG = Logger.getLogger(MockSIHybrisIF15Out.class);

    private static final int AMOUNT_OF_ORDERS_TO_GENERATE_FOR_SEARCH = 10;
    private static final int AMOUNT_OF_ORDERS_TO_GENERATE_FOR_READ_ALL_OPEN_ORDERS = 10;

    private static final String[] ORDER_STATUSES = {"01", "02", "03", "04", "05"};
    private static final String[] VALID_PRODUCT_CODES = {"11010133", "11015087", "11015089", "11015090", "11015092", "11015092"};

    @Override
    public OrderSearchResponseV2 if15SearchOrders(final OrderSearchRequestV2 orderSearchRequest) throws P2FaultMessage {
        final OrderSearchResponseV2 orderSearchResponseV2 = new OrderSearchResponseV2();
        orderSearchResponseV2.setCustomerId(orderSearchRequest.getCustomerId());
        orderSearchResponseV2.setResultTotalSize(BigInteger.valueOf(AMOUNT_OF_ORDERS_TO_GENERATE_FOR_SEARCH));

        final Random randomGenerator = new Random(orderSearchRequest.getCustomerId().hashCode());
        IntStream.rangeClosed(1, AMOUNT_OF_ORDERS_TO_GENERATE_FOR_SEARCH).forEach(n -> {
            final OrdersSearchLine order = new OrdersSearchLine();
            order.setContactName("Distrelec Test");
            order.setCurrencyCode(CurrencyCode.CHF);
            order.setOrderDate(BigInteger.valueOf(System.currentTimeMillis()));
            order.setOrderId(String.valueOf(randomGenerator.nextInt(Integer.MAX_VALUE)));
            order.setOrderStatus(ORDER_STATUSES[randomGenerator.nextInt(ORDER_STATUSES.length)]);
            order.setOrderTotal(randomGenerator.nextInt(100000) / 100.0);
            orderSearchResponseV2.getOrders().add(order);
        });
        return orderSearchResponseV2;
    }

    @Override
    public ReadOrderResponseV2 if15ReadOrder(final ReadOrderRequestV2 readOrderRequest) throws P2FaultMessage {
        final ReadOrderResponseV2 readOrderResponse = new ReadOrderResponseV2();
        readOrderResponse.setOrderId(readOrderRequest.getOrderId());
        readOrderResponse.setCustomerId(readOrderRequest.getCustomerId());
        readOrderResponse.setTotal(1221.98);
        readOrderResponse.setSubtotal2(1091.22);
        readOrderResponse.setContactName("Distrelec Test");
        readOrderResponse.setCostCenterId("D1S1");
        final AddressWithId address = createAddress();
        readOrderResponse.setBillingAddress(address);
        readOrderResponse.setCustomerReferenceHeaderLevel("Ref002");
        final Deliveries deliveries = new Deliveries();
        final Delivery delivery = new Delivery();
        final Delivery delivery2 = new Delivery();
        final DateFormat dateFormat = new SimpleDateFormat("YYYYMMDD");
        final String strDate = dateFormat.format(new Date());
        XMLGregorianCalendar calDate;
        try {
            calDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(strDate);
            delivery.setDeliveryDate(calDate);
        } catch (final DatatypeConfigurationException e) {
            LOG.error(e.getMessage(), e);
        }

        delivery.setDeliveryID("21312309");
        delivery2.setDeliveryID("21312310");
        final HandlingUnit hUnit = new HandlingUnit();
        final HandlingUnit hUnit2 = new HandlingUnit();
        hUnit.setHandlingUnitID("8442021");
        hUnit2.setHandlingUnitID("8442022");
        hUnit.setHandlingUnitTrackingURL("track.it");
        final Item item1 = new Item();
        item1.setArticleName("Appliance socket 7-pin, MAB 7100 S, Belden Hirschmann");
        item1.setManufacturer("broadcom");
        item1.setMaterialID("17504939");
        item1.setQuantity(Long.valueOf(3));
        item1.setUnit("PCS");

        final Item item2 = new Item();
        item2.setArticleName("LED 3 mm (T1) Green, HLMP-1503, Broadcom");
        item2.setManufacturer("hirschmann");
        item2.setMaterialID("14250038");
        item2.setQuantity(Long.valueOf(9));
        item2.setUnit("PCS");

        final Item item3 = new Item();
        item3.setArticleName("LED 3 mm (T1) Green, HLMP-1503, Broadcom");
        item3.setManufacturer("broadcom");
        item3.setMaterialID("17504939");
        item3.setQuantity(Long.valueOf(3));
        item3.setUnit("PCS");
        hUnit.getItems().add(item1);
        hUnit.getItems().add(item2);
        hUnit.getItems().add(item3);
        hUnit2.getItems().add(item1);
        hUnit2.getItems().add(item2);
        hUnit2.getItems().add(item3);

        delivery.getHandlingUnits().add(hUnit);
        delivery.getHandlingUnits().add(hUnit2);
        delivery2.getHandlingUnits().add(hUnit);
        delivery2.getHandlingUnits().add(hUnit2);
        deliveries.getDelivery().add(delivery);
        deliveries.getDelivery().add(delivery2);
        readOrderResponse.setOrderStatus("02");
        if (!readOrderRequest.getOrderId().equalsIgnoreCase("100128849") && !readOrderRequest.getOrderId().equalsIgnoreCase("100128850")) {
            readOrderResponse.setDeliveries(deliveries);
            readOrderResponse.setOrderStatus("04");
        }
        readOrderResponse.setOrderChannel("WEB");
        readOrderResponse.setOrderDate(BigInteger.valueOf(System.currentTimeMillis()));
        readOrderResponse.setOrderNote("Order Note");
        readOrderResponse.setPaymentMethodCode("Z001");
        readOrderResponse.setPaymentPrice(1221.98);
        readOrderResponse.setShippingAddress(address);
        readOrderResponse.setShippingMethodCode(com.distrelec.webservice.if15.v1.ShippingMethodCode.N_1);
        readOrderResponse.setShippingPrice(30.76);
        readOrderResponse.setSubtotal1(1191.98);
        readOrderResponse.setTax(100);
        readOrderResponse.setCurrencyCode(CurrencyCode.CHF);

        return readOrderResponse;
    }

    private AddressWithId createAddress() {
        final AddressWithId address = new AddressWithId();
        address.setAddressId("1213");
        address.setCompanyName1("Distrelec");
        address.setCompanyName2("DIT");
        address.setCountry(Locale.GERMANY.getCountry());
        address.setFirstName("My FirstName");
        address.setLastName("My LastName");

        address.setPhoneNumber("+41880358789");
        address.setPobox("6");
        address.setPostalCode("8606");
        address.setRegion("Zurich");
        address.setStreetName("Hagenholzstrasse");
        address.setStreetNumber("1");
        address.setTitle(Title.MR);
        address.setTown("Zurich");
        return address;
    }

    @Override
    public ReadAllOpenOrdersResponseV2 if15ReadAllOpenOrders(final ReadAllOpenOrdersRequestV2 readAllOpenOrdersRequest) throws P2FaultMessage {
        final ReadAllOpenOrdersResponseV2 readAllOpenOrdersResponse = new ReadAllOpenOrdersResponseV2();
        final Random randomGenerator = new Random(readAllOpenOrdersRequest.getCustomerId().hashCode());
        IntStream.rangeClosed(1, AMOUNT_OF_ORDERS_TO_GENERATE_FOR_READ_ALL_OPEN_ORDERS).forEach(n -> {
            final OpenOrders openOrder = new OpenOrders();
            openOrder.setCustomerId(readAllOpenOrdersRequest.getCustomerId());
            openOrder.setContactId(readAllOpenOrdersRequest.getCustomerId());
            openOrder.setOrderId(String.valueOf(randomGenerator.nextInt(Integer.MAX_VALUE)));
            openOrder.setOrderDate(BigInteger.valueOf(System.currentTimeMillis()));
            openOrder.setOrderStatus("02");
            openOrder.setOrderChannel("WEB");
            openOrder.setCurrencyCode(CurrencyCode.CHF);
            final AddressWithId address = createAddress();
            openOrder.setBillingAddressId(address);
            openOrder.setShippingAddressId(address);
            final int entriesSize = randomGenerator.nextInt(VALID_PRODUCT_CODES.length);
            IntStream.rangeClosed(1, entriesSize).forEach(m -> {
                final OrderReadEntry orderEntry = new OrderReadEntry();
                orderEntry.setOrderPosition(String.valueOf(randomGenerator.nextInt(entriesSize)));
                orderEntry.setMaterialNumber(VALID_PRODUCT_CODES[m]);
                orderEntry.setOrderQuantity(randomGenerator.nextInt(100));
                orderEntry.setUnit("PCS");
                orderEntry.setPrice(randomGenerator.nextInt(100000) / 100.0);
                orderEntry.setTotal(orderEntry.getPrice() * orderEntry.getOrderQuantity());
                openOrder.getOrderEntries().add(orderEntry);
            });
            openOrder.setOrderStatus(ORDER_STATUSES[randomGenerator.nextInt(ORDER_STATUSES.length)]);
            openOrder.setSubtotal1(randomGenerator.nextInt(100000) / 100.0);
            openOrder.setSubtotal2(randomGenerator.nextInt(100000) / 100.0);
            openOrder.setTax(randomGenerator.nextInt(100000) / 100.0);
            openOrder.setShippingPrice(randomGenerator.nextInt(100000) / 100.0);
            openOrder.setTotal(randomGenerator.nextInt(100000) / 100.0);
            openOrder.setShippingMethodCode(com.distrelec.webservice.if15.v1.ShippingMethodCode.N_1);
            readAllOpenOrdersResponse.getOpenOrders().add(openOrder);
        });
        return readAllOpenOrdersResponse;
    }
}


