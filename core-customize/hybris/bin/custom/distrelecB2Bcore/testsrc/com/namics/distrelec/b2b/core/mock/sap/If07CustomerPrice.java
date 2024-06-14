package com.namics.distrelec.b2b.core.mock.sap;

public class If07CustomerPrice {

    private final String productCode;
    private final long quantity;
    private final double price;

    public If07CustomerPrice(String productCode, long quantity, double price) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductCode() {
        return productCode;
    }

    public long getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
