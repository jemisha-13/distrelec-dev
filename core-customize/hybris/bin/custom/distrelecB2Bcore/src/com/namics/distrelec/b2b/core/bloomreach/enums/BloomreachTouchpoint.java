package com.namics.distrelec.b2b.core.bloomreach.enums;

import java.util.HashMap;
import java.util.Map;

public enum BloomreachTouchpoint {
    FOOTER("footer"),
    POPUP("popup"),
    MYACCOUNT("myaccount"),
    SUBUSER("subuser"),
    GUEST("guest"),
    REGISTRATION("registration"),
    ORDER_CONFIRMATION("order_confirmation"),
    RS_REGISTRATION("rs_registration");

    private static final Map<String, BloomreachTouchpoint> BY_TOUCHPOINT = new HashMap<>();

    static {
        for (BloomreachTouchpoint e : values()) {
            BY_TOUCHPOINT.put(e.touchpoint, e);
        }
    }

    public final String touchpoint;

    private BloomreachTouchpoint(String touchpoint) {
        this.touchpoint = touchpoint;
    }

    public static BloomreachTouchpoint valueOfTouchpoint(String type) {
        return BY_TOUCHPOINT.get(type);
    }

}
