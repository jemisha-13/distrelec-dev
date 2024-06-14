package com.namics.distrelec.b2b.facades.util;

import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;

/**
 * Comparator used in OpenOrder used inside OrderHistoryFacade
 * 
 * @author daeneerajs, Elfa Distrelec
 * @since Distrelec 2.0
 */
public class OpenOrderComparator {

    private static final Map<String, AbstractDistComparator<OrderHistoryData>> COMPARATORS = new HashMap<>();

    static {
        COMPARATORS.put("SortByCloseDate", SortByCloseDate.INSTANCE);
        COMPARATORS.put("SortByOrderNo", SortByOrderNo.INSTANCE);
        COMPARATORS.put("SortByStartDate", SortByStartDate.INSTANCE);
        COMPARATORS.put("byTotalPrice:asc", SortByOrderTotal.INSTANCE);
        COMPARATORS.put("SortByOrderReference", SortByOrderReference.INSTANCE);
    }

    protected static class SortByCloseDate extends AbstractDistComparator<OrderHistoryData> {
        protected static final SortByCloseDate INSTANCE = new SortByCloseDate();

        @Override
        protected int compareInstances(final OrderHistoryData arg1, final OrderHistoryData arg2) {
            return compareNullGreater(arg1.getCloseDate(), arg2.getCloseDate());
        }
    }

    protected static class SortByOrderNo extends AbstractDistComparator<OrderHistoryData> {
        protected static final SortByOrderNo INSTANCE = new SortByOrderNo();

        @Override
        protected int compareInstances(final OrderHistoryData arg1, final OrderHistoryData arg2) {
            return compareNullGreater(arg1.getCode(), arg2.getCode());
        }
    }

    protected static class SortByStartDate extends AbstractDistComparator<OrderHistoryData> {
        protected static final SortByStartDate INSTANCE = new SortByStartDate();

        @Override
        protected int compareInstances(final OrderHistoryData arg1, final OrderHistoryData arg2) {
            return compareNullGreater(arg1.getPlaced(), arg2.getPlaced());
        }
    }

    protected static class SortByOrderTotal extends AbstractDistComparator<OrderHistoryData> {
        protected static final SortByOrderTotal INSTANCE = new SortByOrderTotal();

        @Override
        protected int compareInstances(final OrderHistoryData arg1, final OrderHistoryData arg2) {
            return compareNullGreater(arg1.getSubTotal(), arg2.getSubTotal());
        }
    }

    protected static class SortByOrderReference extends AbstractDistComparator<OrderHistoryData> {
        protected static final SortByOrderReference INSTANCE = new SortByOrderReference();

        @Override
        protected int compareInstances(final OrderHistoryData arg1, final OrderHistoryData arg2) {
            return compareNullGreater(arg1.getOrderReference(), arg2.getOrderReference());
        }
    }

    public static Map<String, AbstractDistComparator<OrderHistoryData>> getComparators() {
        return COMPARATORS;
    }
}
