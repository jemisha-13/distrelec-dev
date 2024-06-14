package com.namics.distrelec.b2b.core.bloomreach.enums;

import java.util.HashMap;
import java.util.Map;

public enum BloomreachAction {

    ACCEPT("accept"),
    REJECT("reject"),
    NEW("new");

    private static final Map<String, BloomreachAction> BY_ACTION = new HashMap<>();

    static {
        for (BloomreachAction e : values()) {
            BY_ACTION.put(e.action, e);
        }
    }

    public final String action;

    private BloomreachAction(String action) {
        this.action = action;
    }

    public static BloomreachAction valueOfEventType(String type) {
        return BY_ACTION.get(type);
    }

}
