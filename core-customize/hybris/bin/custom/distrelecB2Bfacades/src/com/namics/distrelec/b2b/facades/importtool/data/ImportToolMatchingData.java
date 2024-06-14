/*
 * Copyright 2000-2013 Distrelec. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.importtool.data;

import java.util.List;

import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * {@code ImportToolMatchingData}
 *
 * @author daezamofinl, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class ImportToolMatchingData extends ProductJsonData implements Comparable<ImportToolMatchingData> {

    private String searchTerm;

    private ProductData product;

    private String salesStatus;

    private int position;

    private String mpn;

    private List<ProductData> duplicateMpnProducts;

    private String quantityRaw;

    // Getters & Setters
    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public ProductData getProduct() {
        return product;
    }

    public void setProduct(final ProductData product) {
        this.product = product;
    }

    public String getSalesStatus() {
        return salesStatus;
    }

    public void setSalesStatus(final String salesStatus) {
        this.salesStatus = salesStatus;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(final int position) {
        this.position = position;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final ImportToolMatchingData other) {
        return this.position - other.position;
    }

    public String getMpn() {
        return mpn;
    }

    public void setMpn(String mpn) {
        this.mpn = mpn;
    }

    public List<ProductData> getDuplicateMpnProducts() {
        return duplicateMpnProducts;
    }

    public void setDuplicateMpnProducts(List<ProductData> duplicateMpnProducts) {
        this.duplicateMpnProducts = duplicateMpnProducts;
    }

    public String getQuantityRaw() {
        return quantityRaw;
    }

    public void setQuantityRaw(String quantityRaw) {
        this.quantityRaw = quantityRaw;
    }
}
