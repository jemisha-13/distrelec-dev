package com.namics.distrelec.b2b.core.constants;

public class DistCDNConstants {

    /**
     * The max-age cache-control directive value. It indicates how long responses remain fresh in a local browser cache. It should be short duration as it
     * cannot be purged on a demand.
     * The duration is expressed in seconds.
     */
    public static final int LOCAL_TTL = 300; // 5 minutes

    /**
     * The s-maxage cache-control directive value. It indicates how long responses remain fresh in a CDN. It can be configured for long time as it is purged on
     * a cms change.
     * The duration is expressed in seconds.
     */
    public static final int CDN_TTL = 86400; // 24 hours

    private DistCDNConstants() {
    }
}
