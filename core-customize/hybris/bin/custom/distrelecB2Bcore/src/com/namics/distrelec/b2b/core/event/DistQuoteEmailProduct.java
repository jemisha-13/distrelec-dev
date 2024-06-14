package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.commercefacades.product.data.PriceData;

public class DistQuoteEmailProduct {

    private int row;
    private Long quantity;
    private String articleNumber;
    private String mpn;
    private String note;
    private String price;
    private PriceData priceData;

    public DistQuoteEmailProduct() {}

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getMpn() {
        return mpn;
    }

    public void setMpn(String mpn) {
        this.mpn = mpn;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public PriceData getPriceData() {
        return priceData;
    }

    public void setPriceData(PriceData priceData) {
        this.priceData = priceData;
    }
}
