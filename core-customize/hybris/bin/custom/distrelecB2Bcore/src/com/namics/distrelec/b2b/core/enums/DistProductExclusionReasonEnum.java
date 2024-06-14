package com.namics.distrelec.b2b.core.enums;

public enum DistProductExclusionReasonEnum {

    PUNCHOUT_PRODUCT_EXCLUDED("PunchoutProductExcluded"),
    PRODUCT_AVAILABLE_TO_B2B_ONLY("ProductAvailableToB2BOnly");

    private String code;

    DistProductExclusionReasonEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
