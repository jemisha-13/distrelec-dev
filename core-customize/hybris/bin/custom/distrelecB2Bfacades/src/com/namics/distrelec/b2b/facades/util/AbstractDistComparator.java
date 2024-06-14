/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.util;

import de.hybris.platform.commerceservices.util.AbstractComparator;
import org.apache.commons.lang.ObjectUtils;

public abstract class AbstractDistComparator<T> extends AbstractComparator<T> {

    protected int compareNullGreater(final Comparable c1, final Comparable c2) {
        return ObjectUtils.compare(c1, c2, true);
    }

    protected int compareNullNotGreater(final Comparable c1, final Comparable c2) {
        return ObjectUtils.compare(c1, c2, false);
    }
}
