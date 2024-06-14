/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.attributes;

/**
 * A {@link RequiredAttribute} is an {@link Attribute} with no default value and will throw an {@link RequiredAttributeMissingException}
 * when it is asked for one.
 * 
 * @author sbechtold, Namics Deutschland GmbH
 * @since Namics Extensions 1.0
 * 
 * @param <T>
 *            the type of object the attribute is resolved to
 */
public class RequiredAttribute<T extends Object> extends Attribute<T> {
    RequiredAttribute(final String name, final Class<T> type) {
        super(name, type);
    }

    @Override
    public String toString() {
        return "RequiredAttribute [name=" + getName() + ", type=" + getType().getName() + "]";
    }
}
