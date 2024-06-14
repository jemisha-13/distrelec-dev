package com.namics.distrelec.b2b.core.bloomreach.enums;

import java.util.HashMap;
import java.util.Map;

public enum BloomreachConsentCategory {

    SMS("sms_marketing"),
    PAPER("paper"),
    PHONE("phone"),
    EMAIL("email"),
    PERSONALISATION("personalisation"),
    PROFILING("profiling"),
    PERSONALISED_RECOMMENDATIONS("personalised_recommendations"),
    CONTENT_NEWS("content_news"),
    NPS("nps"),
    SALES_CLEARANCE("sales_clearance"),
    NEW_BRANDS("new_brands");
    
    private static final Map<String, BloomreachConsentCategory> BY_CATEGORY = new HashMap<>();

    static {
        for (BloomreachConsentCategory e : values()) {
            BY_CATEGORY.put(e.category, e);
        }
    }

    public final String category;

    private BloomreachConsentCategory(String category) {
        this.category = category;
    }

    public static BloomreachConsentCategory valueOfConsentCategory(String type) {
        return BY_CATEGORY.get(type);
    }

}
