/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.util;

import java.io.Serializable;
import java.util.Iterator;

/**
 * {@code DistTuple}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.18
 */
public class DistTuple<T> implements Serializable, Iterator<T> {

    private Entry<T> head;
    private Entry<T> current;
    private Entry<T> tail;

    /**
     * Create a new instance of {@code DistTuple}
     * 
     * @param element
     */
    public DistTuple(final T element) {
        if (element == null) {
            throw new NullPointerException("The element cannot be null");
        }

        this.head = new Entry<T>(element);
        this.tail = this.head;
        this.current = this.head;
    }

    /**
     * Create a new instance of {@code DistTuple}
     * 
     * @param elements
     */
    public DistTuple(final T... elements) {
        if (elements == null) {
            throw new NullPointerException("The elements array cannot be null");
        }
        for (final T element : elements) {
            add(element);
        }
    }

    /**
     * Initialize the tuple with the new head.
     * 
     * @param head
     */
    private void init(final T head) {
        this.head = new Entry<T>(head);
        this.tail = this.head;
        this.current = this.head;
    }

    /**
     * Add a new value to the tuple. The {@code null} values will be ignored.
     * 
     * @param newValue
     *            the new value to add.
     */
    public void add(final T newValue) {
        if (newValue == null) {
            return;
        }
        if (this.head == null) {
            init(newValue);
        } else {
            final Entry<T> newTail = new Entry<T>(newValue);
            this.tail.next = newTail;
            this.tail = newTail;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
        if (this.current == null) {
            return null;
        }

        final T value = current.value;
        this.current = this.current.next;
        return value;
    }

    /**
     * Reset the iterator to the head.
     */
    public void reset() {
        this.current = this.head;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return this.current != null && this.current.next != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("This method is not supported and should not be used!");
    }

    /**
     * {@code Entry}
     * 
     * @param <T>
     *
     * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
     * @since Distrelec 5.18
     */
    private static class Entry<T> {

        private T value;
        private Entry<T> next;

        /**
         * Create a new instance of {@code Entry}
         * 
         * @param value
         *            the value of the entry
         */
        public Entry(final T value) {
            this.value = value;
        }
    }
}
