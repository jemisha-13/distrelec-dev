package com.namics.distrelec.b2b.core.enums;

public enum DistSalesOrgEnum {

    SWITZERLAND("7310"),
    AUSTRIA("7320"),
    ITALY("7330"),
    GERMANY("7350"),
    SWEDEN("7640"),
    NORWAY("7650"),
    POLAND("7660"),
    FINLAND("7670"),
    DENMARK("7680"),
    ESTONIA("7790"),
    NETHERLANDS("7800"),
    EXPORT("7801"),
    LATVIA("7810"),
    LITHUANIA("7820"),
    FRANCE("7900");

    private String code;

    DistSalesOrgEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

