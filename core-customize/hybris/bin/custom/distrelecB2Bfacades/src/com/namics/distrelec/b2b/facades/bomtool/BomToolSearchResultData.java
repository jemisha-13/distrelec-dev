package com.namics.distrelec.b2b.facades.bomtool;

import com.namics.distrelec.b2b.facades.importtool.data.ImportToolMatchingData;

import java.util.List;

public final class BomToolSearchResultData {

    private List<ImportToolMatchingData> matchingProducts;
    private List<ImportToolMatchingData> unavailableProducts;
    private List<ImportToolMatchingData> punchedOutProducts;
    private List<ImportToolMatchingData> duplicateMpnProducts;
    private List<ImportToolMatchingData> notMatchingProductCodes;
    private List<ImportToolMatchingData> quantityAdjustedProducts;
    private int quantityAdjustedProductsCount;

    public List<ImportToolMatchingData> getMatchingProducts() {
        return matchingProducts;
    }

    public void setMatchingProducts(List<ImportToolMatchingData> matchingProducts) {
        this.matchingProducts = matchingProducts;
    }

    public List<ImportToolMatchingData> getUnavailableProducts() {
        return unavailableProducts;
    }

    public void setUnavailableProducts(List<ImportToolMatchingData> unavailableProducts) {
        this.unavailableProducts = unavailableProducts;
    }

    public List<ImportToolMatchingData> getDuplicateMpnProducts() {
        return duplicateMpnProducts;
    }

    public void setDuplicateMpnProducts(List<ImportToolMatchingData> duplicateMpnProducts) {
        this.duplicateMpnProducts = duplicateMpnProducts;
    }

    public List<ImportToolMatchingData> getNotMatchingProductCodes() {
        return notMatchingProductCodes;
    }

    public void setNotMatchingProductCodes(List<ImportToolMatchingData> notMatchingProductCodes) {
        this.notMatchingProductCodes = notMatchingProductCodes;
    }

    public List<ImportToolMatchingData> getQuantityAdjustedProducts() {
        return quantityAdjustedProducts;
    }

    public void setQuantityAdjustedProducts(List<ImportToolMatchingData> quantityAdjustedProducts) {
        this.quantityAdjustedProducts = quantityAdjustedProducts;
    }

    public int getQuantityAdjustedProductsCount() {
        return quantityAdjustedProductsCount;
    }

    public void setQuantityAdjustedProductsCount(int quantityAdjustedProductsCount) {
        this.quantityAdjustedProductsCount = quantityAdjustedProductsCount;
    }

    public List<ImportToolMatchingData> getPunchedOutProducts() {
        return punchedOutProducts;
    }

    public void setPunchedOutProducts(List<ImportToolMatchingData> punchedOutProducts) {
        this.punchedOutProducts = punchedOutProducts;
    }
}
