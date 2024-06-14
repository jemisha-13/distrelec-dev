/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.rma;

import java.util.List;

import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.rma.GuestRMACreateRequestForm;
import com.namics.distrelec.b2b.core.rma.ReturnRequestData;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;

import de.hybris.platform.commercefacades.order.data.DistMainReasonData;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * {@code ReturnRequestFacade}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>,
 *         Distrelec
 * @since Namics Extensions 1.0
 */
public interface DistReturnRequestFacade {

    /**
     * Creates a RMA Object and store it into the DB.
     *
     * @param returnRequestData
     * @return the code of the newly created RMA object
     */
    String createReturnRequest(ReturnRequestData returnRequestData);

    /**
     * Look for the list of return requests of the current user
     *
     * @return the list of return requests of the current user
     */
    List<Object> getReturnRequests();

    /**
     * Look for the list of return requests of the current user related to the
     * specified order
     *
     * @param order
     *            the order
     * @return the list of return requests of the current user related to the
     *         specified order
     */
    List<Object> getReturnRequestsByOrder(OrderModel order);

    /**
     * Retrieve the RMA object having the specified code
     *
     * @param rmaCode
     *            the RMA code
     * @return the RMA object having the specified code
     */
    Object getReturnRequest(String rmaCode);

    /**
     * Creates a RMA Return Online request and Submit to SAP.
     *
     * @param createRMARequestForm
     * @return the code of the newly created RMA object
     */
    CreateRMAResponseData createRMACreateRequest(CreateRMARequestForm createRMARequestForm);

    void sendGuestReturnRequestEmail(GuestRMACreateRequestForm guestRMACreateRequestForm);

    void sendUserReturnRequestEmail(UserRMARequestDataWrapper userRmaRequestDataWrapper);

    List<DistMainReasonData> getReturnReasons();

    boolean isOrderReturnDisabled();

    class UserRMARequestDataWrapper {

        private String rmaId;

        private CreateRMARequestForm createRMARequestForm;

        public String getRmaId() {
            return rmaId;
        }

        public void setRmaId(String rmaId) {
            this.rmaId = rmaId;
        }

        public CreateRMARequestForm getCreateRMARequestForm() {
            return createRMARequestForm;
        }

        public void setCreateRMARequestForm(CreateRMARequestForm createRMARequestForm) {
            this.createRMARequestForm = createRMARequestForm;
        }
    }
}
