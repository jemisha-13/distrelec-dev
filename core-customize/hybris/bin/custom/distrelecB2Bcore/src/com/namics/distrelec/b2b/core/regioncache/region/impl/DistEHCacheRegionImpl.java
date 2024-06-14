package com.namics.distrelec.b2b.core.regioncache.region.impl;

import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import net.sf.ehcache.Element;

public class DistEHCacheRegionImpl extends EHCacheRegion implements DistCacheRegion {

    private final Long ttlSeconds;

    public DistEHCacheRegionImpl(String name, int maxEntries, String evictionPolicy, boolean exclusiveComputation, boolean statsEnabled) {
        this(name, maxEntries, evictionPolicy, exclusiveComputation, statsEnabled, null);

    }

    public DistEHCacheRegionImpl(String name, int maxEntries, String evictionPolicy, boolean exclusiveComputation, boolean statsEnabled, Long ttlSeconds) {
        super(name, maxEntries, evictionPolicy, exclusiveComputation, statsEnabled, ttlSeconds);
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public Element getElement(CacheKey key) {
        Element element = this.cacheMap.getQuiet(key);
        if (isStatsEnabled()) {
            if (element != null) {
                stats.hit(key.getTypeCode());
            } else {
                stats.missed(key.getTypeCode());
            }
        }

        return element;
    }

    @Override
    public void put(CacheKey key, Object object) {
        Element element = getElement(key);
        Element newElement = createElement(key, object);
        if (element != null) {
            cacheMap.replace(element, newElement);
            if (isStatsEnabled()) {
                stats.invalidated(key.getTypeCode());
            }
        } else {
            cacheMap.put(newElement);
            if (isStatsEnabled()) {
                stats.fetched(key.getTypeCode());
            }
        }
    }

    private Element createElement(CacheKey key, Object value) {
        Element elem = new Element(key, value);
        if (ttlSeconds == null || ttlSeconds == 0) {
            elem.setTimeToIdle(0);
            elem.setTimeToLive(0);
            elem.setEternal(true);
        }

        return elem;
    }
}
