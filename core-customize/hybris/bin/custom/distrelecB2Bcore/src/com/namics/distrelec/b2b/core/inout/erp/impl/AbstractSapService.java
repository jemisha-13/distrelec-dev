/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.inout.erp.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;

/**
 * {@code AbstractSapService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public abstract class AbstractSapService {

    private String cacheName;
    @Autowired
    @Qualifier("sapCacheManager")
    private CacheManager distCacheManager;
    private Cache cache;

    /**
     * Create a new instance of {@code AbstractSapService}
     * 
     * @param cacheName
     */
    public AbstractSapService(final String cacheName) {
        this.cacheName = cacheName;
    }

    /**
     * Clears cache. This method is intended to use only in tests.
     */
    public void clearCache() {
        getCache().clear();
    }

    /**
     * Initialize the private cache if it is not yet initialized and returns it.
     * 
     * @return the private local cache.
     */
    protected final Cache getCache() {
        if (cache == null && cacheName != null) {
            cache = distCacheManager.getCache(cacheName);
        }
        return cache;
    }

    /**
     * Get the value from the cache
     * 
     * @param requestKey
     * @param clazz
     * @return the value mapped to the specified key from the cache.
     */
    protected <R, T> T getFromCache(final R requestKey, final Class<T> clazz) {
        return getCache() != null ? getCache().get(requestKey, clazz) : null;
    }

    /**
     * Map the specified key to the specified value into the cache
     * 
     * @param cacheKey
     * @param value
     */
    protected <R, T> void putIntoCache(final R cacheKey, final T value) {
        if (getCache() != null) {
            getCache().put(cacheKey, value);
        }
    }

    protected void evictFromCache(final Object requestKey) {
        getCache().evict(requestKey);
    }
}
