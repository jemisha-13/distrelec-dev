/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.attributes;

/**
 * An {@link OptionalAttribute} is an {@link Attribute} with a default value and will return it when it is asked for.
 * 
 * @author sbechtold, Namics Deutschland GmbH
 * @since Namics Extensions 1.0
 * 
 * @param <T>
 *            the type of object the attribute is resolved to
 */
public class OptionalAttribute<T extends Object> extends Attribute<T> {
    private final T defaultValue;

    OptionalAttribute(final String name, final Class<T> type) {
        super(name, type);
        this.defaultValue = null;
    }

    OptionalAttribute(final String name, final T defaultValue) {
        super(name, (Class<T>) defaultValue.getClass());
        this.defaultValue = defaultValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return "OptionalAttribute [name=" + getName() + ", type=" + getType().getName() + ", default=" + getDefaultValue() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OptionalAttribute other = (OptionalAttribute) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null) {
                return false;
            }
        } else if (!defaultValue.equals(other.defaultValue)) {
            return false;
        }
        return true;
    }
}
