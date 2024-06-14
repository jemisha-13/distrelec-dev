package com.namics.distrelec.b2b.core.rma.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public enum RmaMainReturnReasonTypes {

    WRONG_ARTICLE("001", Arrays.asList(RmaSubReturnReasonTypes.INCORRECT_ARTICLE, RmaSubReturnReasonTypes.INCORRECT_DESCRIPTION)),
    INCORRECT_QUANTITY("002", Arrays.asList(RmaSubReturnReasonTypes.INCORRECT_QUANTITY_1, RmaSubReturnReasonTypes.INCORRECT_QUANTITY_2, RmaSubReturnReasonTypes.INCORRECT_QUANTITY_3, RmaSubReturnReasonTypes.NOT_RECEIVED, RmaSubReturnReasonTypes.MISSING_PARTS)),
    ACCIDENTAL("003", Arrays.asList(RmaSubReturnReasonTypes.ACCIDENTAL_1, RmaSubReturnReasonTypes.ACCIDENTAL_2, RmaSubReturnReasonTypes.ACCIDENTAL_3)),
    INCORRECT_DETAILS("004", Arrays.asList(RmaSubReturnReasonTypes.INCORRECT_DETAILS, RmaSubReturnReasonTypes.INCORRECT_DOCUMENT_1, RmaSubReturnReasonTypes.INCORRECT_DOCUMENT_2, RmaSubReturnReasonTypes.INCORRECT_DOCUMENT_3)),
    DAMAGED("005", Arrays.asList(RmaSubReturnReasonTypes.DAMAGED_1, RmaSubReturnReasonTypes.DAMAGED_2, RmaSubReturnReasonTypes.DAMAGED_PACKAGING, RmaSubReturnReasonTypes.NOT_CALIBRATED)),
    NOT_RECEIVED("006", Arrays.asList(RmaSubReturnReasonTypes.SENDING_ERROR, RmaSubReturnReasonTypes.DELIVERY_LEAD_TIME));

    private String code;

    private List<RmaSubReturnReasonTypes> subTypes;

    RmaMainReturnReasonTypes(String code, List<RmaSubReturnReasonTypes> subTypes) {
        this.code = code;
        this.subTypes = subTypes;
    }

    public String getCode() {
        return code;
    }

    public List<RmaSubReturnReasonTypes> getSubTypes() {
        return subTypes;
    }
}
