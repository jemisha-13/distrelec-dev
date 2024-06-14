package com.namics.distrelec.b2b.core.bomtool;

public final class BomToolSearchRow {

    private final String productCodeOrMpn;
    private final String quantity;
    private final String customerReference;

    public BomToolSearchRow(String productCodeOrMpn, String quantity, String customerReference) {
        this.productCodeOrMpn = productCodeOrMpn;
        this.quantity = quantity;
        this.customerReference = customerReference;
    }

    public String getProductCodeOrMpn() {
        return productCodeOrMpn;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getCustomerReference() {
        return customerReference;
    }
}
