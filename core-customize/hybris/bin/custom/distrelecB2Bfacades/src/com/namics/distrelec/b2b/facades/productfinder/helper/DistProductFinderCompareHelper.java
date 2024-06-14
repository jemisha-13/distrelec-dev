/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.helper;

/**
 * Contains helper methods to compare product finder values.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface DistProductFinderCompareHelper {

    boolean isBetween(final String value, final String lowerBound, final String upperBound);

    boolean isBetween(final String value, final double lowerBound, final double upperBound);

    boolean isLowerThanOrEqual(final String value1, final String value2);

    boolean isLowerThanOrEqual(final String value1, final double value2);

    boolean isGreaterThanOrEqual(final String value1, final String value2);

    boolean isGreaterThanOrEqual(final String value1, final double value2);

}
