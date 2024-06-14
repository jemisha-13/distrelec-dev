package com.namics.distrelec.b2b.facades.bomtool;

import de.hybris.platform.commercefacades.product.data.ProductData;

public class BomToolImportRowData {
    private ProductData product;

    private long quantity;

    private String CustomerReference;

    public ProductData getProduct() {
        return product;
    }

    public void setProduct(ProductData product) {
        this.product = product;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getCustomerReference() {
        return CustomerReference;
    }

    public void setCustomerReference(String customerReference) {
        CustomerReference = customerReference;
    }
}
