package com.namics.distrelec.b2b.facades.search.converter;

import com.distrelec.webservice.if15.v1.ReadOrderResponseV2;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsResponse;
import de.hybris.platform.b2b.model.B2BCustomerModel;

/**
 *
 * @author daeneerajs, Elfa Distrelec AB
 * @since Namics Extensions 1.0
 *
 */
public class ReadOrderResponseWraper {

    private ReadOrderResponseV2 readOrderResponse;
    private B2BCustomerModel b2BCustomerModel;
    private RMAGetOrderItemsResponse rmaGetOrderItemsResponse;

    public ReadOrderResponseWraper(final ReadOrderResponseV2 readOrderResponse, final B2BCustomerModel b2bCustomerModel,
            final RMAGetOrderItemsResponse rmaGetOrderItemsResponse) {
        super();
        this.readOrderResponse = readOrderResponse;
        this.b2BCustomerModel = b2bCustomerModel;
        this.rmaGetOrderItemsResponse = rmaGetOrderItemsResponse;
    }

    public ReadOrderResponseV2 getReadOrderResponse() {
        return readOrderResponse;
    }

    public void setReadOrderResponse(final ReadOrderResponseV2 readOrderResponse) {
        this.readOrderResponse = readOrderResponse;
    }

    public B2BCustomerModel getB2BCustomerModel() {
        return b2BCustomerModel;
    }

    public void setB2BCustomerModel(final B2BCustomerModel b2bCustomerModel) {
        b2BCustomerModel = b2bCustomerModel;
    }

    public RMAGetOrderItemsResponse getRmaGetOrderItemsResponse() {
        return rmaGetOrderItemsResponse;
    }

    public void setRmaGetOrderItemsResponse(final RMAGetOrderItemsResponse rmaGetOrderItemsResponse) {
        this.rmaGetOrderItemsResponse = rmaGetOrderItemsResponse;
    }

}
