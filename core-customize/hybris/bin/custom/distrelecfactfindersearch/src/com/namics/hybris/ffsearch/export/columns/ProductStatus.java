package com.namics.hybris.ffsearch.export.columns;

public enum ProductStatus {
    // @formatter:off
    AVAILABLE_DELIVERY("1_AvailableDelivery"),
    AVAILABLE_PICKUP("2_AvailablePickup"),
    EXCLUSIVE("3_Exclusive"),
    NEW("4_New"),
    OFFER("5_Offer"),
    CAD_IMAGE("6_CAD"),
    CALIBRATION("7_Calibration");
    // @formatter:on

    private String value;

    ProductStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
