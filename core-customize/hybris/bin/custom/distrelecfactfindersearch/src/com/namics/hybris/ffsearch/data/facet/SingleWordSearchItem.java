/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.hybris.ffsearch.data.facet;

import java.io.Serializable;
import java.util.List;

/**
 * {@code SingleWordSearchItem}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.7
 */
public class SingleWordSearchItem<RESULT> implements Serializable {

    private int number;
    private String singleTerm;
    private int count;
    private List<RESULT> items;

    // Getters & Setters

    public int getNumber() {
        return number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public String getSingleTerm() {
        return singleTerm;
    }

    public void setSingleTerm(final String singleTerm) {
        this.singleTerm = singleTerm;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public List<RESULT> getItems() {
        return items;
    }

    public void setItems(final List<RESULT> items) {
        this.items = items;
    }
}
