/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.data;

import de.hybris.platform.commercefacades.product.data.FeatureValueData;

/**
 * {@code DistFeatureValueData}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.2
 */
public class DistFeatureValueData extends FeatureValueData implements Comparable<FeatureValueData> {

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final FeatureValueData other) {
        return getValue().compareTo(other.getValue());
    }

}
