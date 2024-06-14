package com.namics.distrelec.b2b.facades.order.comparators;

import java.util.Comparator;

import com.namics.distrelec.b2b.facades.order.data.DistAvailabilityData;

public class DistAvailabilityDataComparator implements Comparator<DistAvailabilityData> {
    @Override
    public int compare(DistAvailabilityData availabilityData, DistAvailabilityData availabilityData2) {
        return availabilityData.getEstimatedDate().compareTo(availabilityData2.getEstimatedDate());
    }
}
