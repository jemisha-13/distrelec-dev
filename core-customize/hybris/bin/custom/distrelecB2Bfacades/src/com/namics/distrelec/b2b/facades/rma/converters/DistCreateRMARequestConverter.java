package com.namics.distrelec.b2b.facades.rma.converters;

import com.distrelec.webservice.if19.v1.RMACreateReqItem;
import com.distrelec.webservice.if19.v1.RMACreateReqOrder;
import com.distrelec.webservice.if19.v1.RMACreateRequest;
import com.namics.distrelec.b2b.core.rma.CreateRMAOrderEntryDataForm;
import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DistCreateRMARequestConverter extends AbstractPopulatingConverter<CreateRMARequestForm, RMACreateRequest> {

    public static final String RMA_COMMENT_SEPARATOR = "||";
    public static final int RMA_SECOND_LEVEL_FIXED_LENGTH = 60;

    @Autowired
    private B2BCustomerService b2bCustomerService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Override
    public void populate(final CreateRMARequestForm createRMARequestForm, final RMACreateRequest rmaCreateRequest) {

        final B2BCustomerModel customerModel = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();

        rmaCreateRequest.setCustomerId(customerModel.getDefaultB2BUnit().getErpCustomerID());
        rmaCreateRequest.setSalesOrganization(distSalesOrgService.getCurrentSalesOrg().getCode());
        rmaCreateRequest.setSessionLanguage(customerModel.getSessionLanguage().getIsocode());

        final RMACreateReqOrder rmaCreateReqOrder = new RMACreateReqOrder();
        rmaCreateReqOrder.setOrderId(createRMARequestForm.getOrderId());

        final List<CreateRMAOrderEntryDataForm> createRMAOrderEntryDataList = createRMARequestForm.getOrderItems();
        for (final CreateRMAOrderEntryDataForm createRMAOrderEntryDataForm : createRMAOrderEntryDataList) {

            if (!StringUtils.isEmpty(createRMAOrderEntryDataForm.getReturnReasonID())) {
                final RMACreateReqItem rmaCreateReqItem = new RMACreateReqItem();

                rmaCreateReqItem.setCustomerText(getCustomerText(createRMAOrderEntryDataForm));
                rmaCreateReqItem.setItemNumber(createRMAOrderEntryDataForm.getItemNumber());
                rmaCreateReqItem.setQuantity(createRMAOrderEntryDataForm.getQuantity());
                rmaCreateReqItem.setRefundType(createRMAOrderEntryDataForm.getRefundType());
                rmaCreateReqItem.setReturnReasonID(createRMAOrderEntryDataForm.getReturnReasonID());
                rmaCreateReqOrder.getItems().add(rmaCreateReqItem);
            }
        }

        rmaCreateRequest.setOrders(rmaCreateReqOrder);
    }

    private String getCustomerText(CreateRMAOrderEntryDataForm createRMAOrderEntryDataForm) {
        //  secondLevelReason was added here, to allow checking which was the reason of returning in ZR01 document in ERP
        //  This was done because multiple 2nd level reasons share the same code and because of that they aren't
        //  able to map what is the correct return reason which the customer selected
        return getFixedLengthSubReason(createRMAOrderEntryDataForm.getReturnSubReason()) + RMA_COMMENT_SEPARATOR + createRMAOrderEntryDataForm.getCustomerText();
    }

    private String getFixedLengthSubReason(String subReason) {
        return StringUtils.rightPad(subReason, RMA_SECOND_LEVEL_FIXED_LENGTH, StringUtils.EMPTY);
    }
}
