package com.namics.distrelec.b2b.core.bloomreach.enums;

import java.util.HashMap;
import java.util.Map;

public enum BloomreachEvent {

    CONSENT("consent"),
    REGISTRATION("registration"),
    DOUBLE_OPT_IN("double_opt_in");

    private static final Map<String, BloomreachEvent> BY_EVENTTYPE = new HashMap<>();

    static {
        for (BloomreachEvent e : values()) {
            BY_EVENTTYPE.put(e.eventType, e);
        }
    }

    public final String eventType;

    private BloomreachEvent(String eventType) {
        this.eventType = eventType;
    }

    public static BloomreachEvent valueOfEventType(String type) {
        return BY_EVENTTYPE.get(type);
    }

}
