/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.mock;

import java.util.List;

import com.distrelec.webservice.if19.v1.OrdersList;
import com.distrelec.webservice.if19.v1.OrdersList2;
import com.distrelec.webservice.if19.v1.RMACreateReqItem;
import com.distrelec.webservice.if19.v1.RMACreateReqOrder;
import com.distrelec.webservice.if19.v1.RMACreateRequest;
import com.distrelec.webservice.if19.v1.RMACreateRespItem;
import com.distrelec.webservice.if19.v1.RMACreateRespOrder;
import com.distrelec.webservice.if19.v1.RMACreateResponse;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderRequest;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderResponse;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsRequest;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsResponse;
import com.distrelec.webservice.if19.v1.RMAList;
import com.distrelec.webservice.if19.v1.RMAList2;
import com.distrelec.webservice.if19.v1.SIHybrisIF19Out;

public class MockSIHybrisIF19Out implements SIHybrisIF19Out {

    @Override
    public RMAGetOrderItemsResponse if19RMAGetOrderItems(final RMAGetOrderItemsRequest rmaGetOrderItemsRequest)
            throws com.distrelec.webservice.if19.v1.P2FaultMessage {
        // YTODO Auto-generated method stub
        return null;
    }

    @Override
    public RMACreateResponse if19RMACreate(final RMACreateRequest rmaCreateRequest) throws com.distrelec.webservice.if19.v1.P2FaultMessage {
        final RMACreateResponse rmaCreateResponse = new RMACreateResponse();
        final RMACreateRespOrder rmaCreateRespOrder = new RMACreateRespOrder();
        rmaCreateRespOrder.setOfficeAddress("Switzerland");
        rmaCreateRespOrder.setRmaHeaderStatus("Return Raised");
        rmaCreateRespOrder.setRmaNumber("11000011");

        final RMACreateReqOrder rmaCreateReqOrder = rmaCreateRequest.getOrders();
        final List<RMACreateReqItem> inputList = rmaCreateReqOrder.getItems();

        for (final RMACreateReqItem rmaCreateReqItem : inputList) {
            final RMACreateRespItem rmaCreateRespItem = new RMACreateRespItem();
            rmaCreateRespItem.setRmaItemNumber(rmaCreateReqItem.getItemNumber());
            rmaCreateRespItem.setRmaItemStatus("Return Raised");
            rmaCreateRespOrder.getItems().add(rmaCreateRespItem);
        }

        rmaCreateResponse.setRmaOrder(rmaCreateRespOrder);
        return rmaCreateResponse;
    }

    @Override
    public RMAGetListForOrderResponse if19RMAGetListForOrder(final RMAGetListForOrderRequest rmaGetListForOrderRequest)
            throws com.distrelec.webservice.if19.v1.P2FaultMessage {

        final RMAGetListForOrderResponse response = new RMAGetListForOrderResponse();

        final OrdersList ordersList = new OrdersList();
        final OrdersList2 OrdersListdata = new OrdersList2();
        final RMAList rmaList = new RMAList();
        final RMAList2 rmaListdata1 = new RMAList2();
        final RMAList2 rmaListdata2 = new RMAList2();

        rmaListdata1.setRmaNumber("00001");
        rmaListdata1.setRmaHeaderStatus("Return Raised");

        rmaListdata2.setRmaNumber("00002");
        rmaListdata2.setRmaHeaderStatus("Return In Processes");

        rmaList.getRma().add(rmaListdata1);
        rmaList.getRma().add(rmaListdata2);

        OrdersListdata.setOrderId(rmaGetListForOrderRequest.getOrders().getOrderId().get(0));
        OrdersListdata.setRmas(rmaList);

        ordersList.getOrder().add(OrdersListdata);

        response.setOrders(ordersList);

        return response;
    }

}
