package com.namics.distrelec.b2b.core.regioncache.region.impl;

import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.region.CacheRegion;
import net.sf.ehcache.Element;

public interface DistCacheRegion extends CacheRegion {

    Element getElement(CacheKey key);

    void put(CacheKey key, Object object);
}
