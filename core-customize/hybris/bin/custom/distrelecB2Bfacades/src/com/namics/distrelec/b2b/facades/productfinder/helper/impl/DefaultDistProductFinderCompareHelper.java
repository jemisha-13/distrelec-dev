/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.helper.impl;

import com.namics.distrelec.b2b.facades.productfinder.helper.DistProductFinderCompareHelper;

/**
 * Default implementation of {@link DistProductFinderCompareHelper} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistProductFinderCompareHelper implements DistProductFinderCompareHelper {

    @Override
    public boolean isBetween(final String value, final String lowerBound, final String upperBound) {
        return isGreaterThanOrEqual(value, lowerBound) && isLowerThanOrEqual(value, upperBound);
    }

    @Override
    public boolean isBetween(final String value, final double lowerBound, final double upperBound) {
        return isGreaterThanOrEqual(value, lowerBound) && isLowerThanOrEqual(value, upperBound);
    }

    @Override
    public boolean isLowerThanOrEqual(final String value1, final String value2) {
        double doubleValue1;
        double doubleValue2;

        try {
            doubleValue1 = Double.parseDouble(value1);
            doubleValue2 = Double.parseDouble(value2);
            return doubleValue1 <= doubleValue2;
        } catch (final NumberFormatException e) {
            // Could not convert parameters into Doubles. Do String compare.
            return value1.compareTo(value2) <= 0;
        }
    }

    @Override
    public boolean isLowerThanOrEqual(final String value1, final double value2) {
        double doubleValue1;

        try {
            doubleValue1 = Double.parseDouble(value1);
            return doubleValue1 <= value2;
        } catch (final NumberFormatException e) {
            // Could not convert parameter into Double. Do String compare.
            return value1.compareTo(Double.toString(value2)) <= 0;
        }
    }

    @Override
    public boolean isGreaterThanOrEqual(final String value1, final String value2) {
        double doubleValue1;
        double doubleValue2;

        try {
            doubleValue1 = Double.parseDouble(value1);
            doubleValue2 = Double.parseDouble(value2);
            return doubleValue1 >= doubleValue2;
        } catch (final NumberFormatException e) {
            // Could not convert parameters into Doubles. Do String compare.
            return value1.compareTo(value2) >= 0;
        }
    }

    @Override
    public boolean isGreaterThanOrEqual(final String value1, final double value2) {
        double doubleValue1;

        try {
            doubleValue1 = Double.parseDouble(value1);
            return doubleValue1 >= value2;
        } catch (final NumberFormatException e) {
            // Could not convert parameter into Double. Do String compare.
            return value1.compareTo(Double.toString(value2)) >= 0;
        }
    }

}
