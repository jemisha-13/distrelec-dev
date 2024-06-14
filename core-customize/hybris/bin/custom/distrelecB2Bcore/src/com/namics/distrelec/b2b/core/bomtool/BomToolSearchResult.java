package com.namics.distrelec.b2b.core.bomtool;

import java.util.ArrayList;
import java.util.List;

public final class BomToolSearchResult {

    private List<BomToolSearchResultRow> matchingProducts;
    private List<BomToolSearchResultRow> unavailableProducts;
    private List<BomToolSearchResultRow> punchedOutProducts;
    private List<BomToolSearchResultRow> duplicateMpnProducts;
    private List<BomToolSearchResultRow> notMatchingProductCodes;
    private List<BomToolSearchResultRow> quantityAdjustedProducts;

    public BomToolSearchResult() {
        matchingProducts = new ArrayList<>();
        unavailableProducts = new ArrayList<>();
        punchedOutProducts = new ArrayList<>();
        duplicateMpnProducts = new ArrayList<>();
        notMatchingProductCodes = new ArrayList<>();
        quantityAdjustedProducts = new ArrayList<>();
    }

    public List<BomToolSearchResultRow> getMatchingProducts() {
        return matchingProducts;
    }

    public void setMatchingProducts(List<BomToolSearchResultRow> matchingProducts) {
        this.matchingProducts = matchingProducts;
    }

    public List<BomToolSearchResultRow> getUnavailableProducts() {
        return unavailableProducts;
    }

    public void setUnavailableProducts(List<BomToolSearchResultRow> unavailableProducts) {
        this.unavailableProducts = unavailableProducts;
    }

    public List<BomToolSearchResultRow> getDuplicateMpnProducts() {
        return duplicateMpnProducts;
    }

    public void setDuplicateMpnProducts(List<BomToolSearchResultRow> duplicateMpnProducts) {
        this.duplicateMpnProducts = duplicateMpnProducts;
    }

    public List<BomToolSearchResultRow> getNotMatchingProductCodes() {
        return notMatchingProductCodes;
    }

    public void setNotMatchingProductCodes(List<BomToolSearchResultRow> notMatchingProductCodes) {
        this.notMatchingProductCodes = notMatchingProductCodes;
    }

    public List<BomToolSearchResultRow> getQuantityAdjustedProducts() {
        return quantityAdjustedProducts;
    }

    public void setQuantityAdjustedProducts(List<BomToolSearchResultRow> quantityAdjustedProducts) {
        this.quantityAdjustedProducts = quantityAdjustedProducts;
    }

    public List<BomToolSearchResultRow> getPunchedOutProducts() {
        return punchedOutProducts;
    }

    public void setPunchedOutProducts(List<BomToolSearchResultRow> punchedOutProducts) {
        this.punchedOutProducts = punchedOutProducts;
    }
}
