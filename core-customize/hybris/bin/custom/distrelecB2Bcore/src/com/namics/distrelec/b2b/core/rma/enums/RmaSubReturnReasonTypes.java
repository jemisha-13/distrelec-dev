package com.namics.distrelec.b2b.core.rma.enums;

import com.namics.distrelec.b2b.core.enums.EnumDropdown;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public enum  RmaSubReturnReasonTypes implements EnumDropdown {

    INCORRECT_ARTICLE("001",Collections.singletonList("cart.return.items.return.reason.desc.a001")),
    INCORRECT_QUANTITY_1("002", Collections.singletonList("cart.return.items.return.reason.desc.a002.a1")),
    INCORRECT_QUANTITY_2("002", Collections.singletonList("cart.return.items.return.reason.desc.a002.a2")),
    INCORRECT_QUANTITY_3("002", Collections.singletonList("cart.return.items.return.reason.desc.a002.a3")),
    ACCIDENTAL_1("003", Collections.singletonList("cart.return.items.return.reason.desc.a003.a1")),
    ACCIDENTAL_2("003", Collections.singletonList("cart.return.items.return.reason.desc.a003.a2")),
    ACCIDENTAL_3("003", Collections.singletonList("cart.return.items.return.reason.desc.a003.a3")),
    INCORRECT_DETAILS("004", Collections.singletonList("cart.return.items.return.reason.desc.a004")),
    DAMAGED_1("005", Collections.singletonList("cart.return.items.return.reason.desc.a005.a1")),
    DAMAGED_2("005", Collections.singletonList("cart.return.items.return.reason.desc.a005.a2")),
    NOT_RECEIVED("006", Collections.singletonList("cart.return.items.return.reason.desc.a006")),
    INCORRECT_DOCUMENT_1("007", Collections.singletonList("cart.return.items.return.reason.desc.a007.a1")),
    INCORRECT_DOCUMENT_2("007", Collections.singletonList("cart.return.items.return.reason.desc.a007.a2")),
    INCORRECT_DOCUMENT_3("007", Collections.singletonList("cart.return.items.return.reason.desc.a007.a3")),
    MISSING_PARTS("008", Collections.singletonList("cart.return.items.return.reason.desc.a008")),
    INCORRECT_DESCRIPTION("010",Collections.singletonList("cart.return.items.return.reason.desc.a010")),
    SENDING_ERROR("011",Collections.singletonList("cart.return.items.return.reason.desc.a011")),
    DELIVERY_LEAD_TIME("012",Collections.singletonList("cart.return.items.return.reason.desc.a012")),
    DAMAGED_PACKAGING("015",Collections.singletonList("cart.return.items.return.reason.desc.a015")),
    NOT_CALIBRATED("016",Collections.singletonList("cart.return.items.return.reason.desc.a016"));

    private String code;

    private List<String> messageKeys;

    RmaSubReturnReasonTypes(String code, List<String> messageKeys) {
        this.code = code;
        this.messageKeys = messageKeys;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public List<String> getMessageKeys() {
        return messageKeys;
    }

    public static RmaSubReturnReasonTypes getByCode(String code) {
        return Stream.of(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
