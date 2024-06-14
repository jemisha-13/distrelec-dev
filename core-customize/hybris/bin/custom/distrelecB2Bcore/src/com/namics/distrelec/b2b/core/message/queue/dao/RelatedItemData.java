/*
 * Copyright 2000-2018 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.message.queue.dao;

import java.io.Serializable;

import de.hybris.platform.core.model.ItemModel;

/**
 * {@code RelatedItemData}
 * 
 * @param <T>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @since Distrelec 7.0
 */
public class RelatedItemData<T extends ItemModel> implements Serializable {

    private T item;
    private long count;

    /**
     * Create a new instance of {@code RelatedItemData}
     */
    public RelatedItemData() {
        super();
    }

    /**
     * Create a new instance of {@code RelatedItemData}
     * 
     * @param item
     * @param count
     */
    public RelatedItemData(final T item, final long count) {
        this.item = item;
        this.count = count;
    }
    
    
    public T getItem() {
        return item;
    }

    public void setItem(final T item) {
        this.item = item;
    }

    public long getCount() {
        return count;
    }

    public void setCount(final long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return String.format("RelatedItemData [getItem()=%s, getCount()=%s]", getItem(), getCount());
    }
}
