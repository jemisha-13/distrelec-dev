package com.namics.distrelec.b2b.core.service.netmargin;

import java.util.SortedSet;

public class NetMarginConfiguration {

    private final SortedSet<PerValueRange> perValueRanges;

    public NetMarginConfiguration(SortedSet<PerValueRange> perValueRanges) {
        this.perValueRanges = perValueRanges;
    }

    public SortedSet<PerValueRange> getPerValueRanges() {
        return perValueRanges;
    }

    public static class PerValueRange implements Comparable<PerValueRange> {
        private final int minValue;
        private final SortedSet<PerPercentageRange> perPercentageRanges;

        public PerValueRange(int minValue, SortedSet<PerPercentageRange> perPercentageRanges) {
            this.minValue = minValue;
            this.perPercentageRanges = perPercentageRanges;
        }

        public int getMinValue() {
            return minValue;
        }

        public SortedSet<PerPercentageRange> getPerPercentageRanges() {
            return perPercentageRanges;
        }

        @Override
        public int compareTo(PerValueRange other) {
            return Integer.compare(this.minValue, other.minValue);
        }
    }

    public static class PerPercentageRange implements Comparable<PerPercentageRange> {
        private final double percentage;
        private final int rank;

        public PerPercentageRange(float percentage, int rank) {
            this.percentage = percentage;
            this.rank = rank;
        }

        public double getPercentage() {
            return percentage;
        }

        public int getRank() {
            return rank;
        }

        @Override
        public int compareTo(PerPercentageRange other) {
            return Double.compare(this.percentage, other.percentage);
        }
    }
}
