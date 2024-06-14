/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */
 package com.namics.distrelec.b2b.core.mail.internal.data;

public class ProductsPerWebshop {

    private String webshopName;
    private String topLevelCategoryName;
    private boolean buyableInShop;
    private Integer count;
    private Float change;

    public String getWebshopName() {
        return webshopName;
    }

    public void setWebshopName(final String webshopName) {
        this.webshopName = webshopName;
    }

    public String getTopLevelCategoryName() {
        return topLevelCategoryName;
    }

    public void setTopLevelCategoryName(final String topLevelCategoryName) {
        this.topLevelCategoryName = topLevelCategoryName;
    }

    public boolean isBuyableInShop() {
        return buyableInShop;
    }

    public void setBuyableInShop(final boolean buyableInShop) {
        this.buyableInShop = buyableInShop;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }

    public Float getChange() {
        return change;
    }

    public void setChange(final Float change) {
        this.change = change;
    }

    @Override
    public String toString() {
        return String.format("ProductsPerWebshop [webshopName=%s, topLevelCategoryName=%s, buyableInShop=%s, count=%s, change=%s]", webshopName,
                topLevelCategoryName, buyableInShop, count, change);
    }
    
}
