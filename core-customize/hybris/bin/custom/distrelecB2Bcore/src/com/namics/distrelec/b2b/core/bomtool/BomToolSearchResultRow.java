package com.namics.distrelec.b2b.core.bomtool;

import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import de.hybris.platform.core.model.product.ProductModel;

public class BomToolSearchResultRow {
    private String searchTerm;
    private ProductModel product;
    private DistSalesStatusModel salesStatus;
    private long quantity;
    private String customerReference;
    private int position;
    private String quantityRaw;

    public BomToolSearchResultRow(String searchTerm, ProductModel product, DistSalesStatusModel salesStatus, long quantity, String customerReference, int position, String quantityRaw) {
        this.searchTerm = searchTerm;
        this.product = product;
        this.salesStatus = salesStatus;
        this.quantity = quantity;
        this.customerReference = customerReference;
        this.position = position;
        this.quantityRaw = quantityRaw;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(ProductModel product) {
        this.product = product;
    }

    public DistSalesStatusModel getSalesStatus() {
        return salesStatus;
    }

    public void setSalesStatus(DistSalesStatusModel salesStatus) {
        this.salesStatus = salesStatus;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getQuantityRaw() {
        return quantityRaw;
    }

    public void setQuantityRaw(String quantityRaw) {
        this.quantityRaw = quantityRaw;
    }
}
