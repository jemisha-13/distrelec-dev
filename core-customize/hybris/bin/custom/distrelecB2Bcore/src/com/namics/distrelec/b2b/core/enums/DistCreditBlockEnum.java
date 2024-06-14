package com.namics.distrelec.b2b.core.enums;

public enum DistCreditBlockEnum {
    A("A", "Credit check was executed, document OK"),
    B("B", "Credit check was executed, document not OK"),
    C("C", "Credit check was executed, document not OK, partial release"),
    D("D", "Document released by credit representative");

    String value;
    String description;

    DistCreditBlockEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
