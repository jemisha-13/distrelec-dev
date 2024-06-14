package com.namics.distrelec.b2b.backoffice.cockpitng.labels.impl;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

class LabelCacheKey {
    private final Object object;
    private final Locale locale;

    LabelCacheKey(Object object, Locale locale) {
        this.object = requireNonNull(object);
        this.locale = requireNonNull(locale);
    }

    Object getObject() {
        return object;
    }

    Locale getLocale() {
        return locale;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + object.hashCode();
        hash = 31 * hash + locale.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        LabelCacheKey other = (LabelCacheKey) obj;
        Object otherObject = other.getObject();

        // compare inner object classes to suppress ClassCastException - DISTRELEC-18392
        if (!object.getClass().equals(otherObject.getClass())) {
            return false;
        }

        if (!object.equals(otherObject)) {
            return false;
        }

        if (!locale.equals(other.getLocale())) {
            return false;
        }
        return true;
    }

}
