package com.namics.distrelec.b2b.core.cache.impl;

import com.namics.distrelec.b2b.core.cache.DistAlternativesCacheEvictionStrategy;
import org.springframework.cache.annotation.CacheEvict;

public class DefaultDistAlternativesCacheEvictionStrategy implements DistAlternativesCacheEvictionStrategy {

    @Override
    @CacheEvict(value = "alternativesResponse", allEntries = true, cacheManager = "alternativesCacheManager")
    public void evictAllAlternativesCache() {}
}
