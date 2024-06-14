/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.storesession.impl;

import java.io.Serializable;

/**
 * {@code SessionAttributeWrapper}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public class SessionAttributeWrapper<T extends Serializable> implements Serializable {

    final T element;

    /**
     * Create a new instance of {@code SessionAttributeWrapper}
     *
     * @param element
     */
    public SessionAttributeWrapper(final T element) {
        this.element = element;
    }

    public T getElement() {
        return element;
    }
}
