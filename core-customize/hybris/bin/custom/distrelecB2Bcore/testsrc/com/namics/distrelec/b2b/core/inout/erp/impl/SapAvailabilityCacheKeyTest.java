/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.namics.distrelec.b2b.core.inout.erp.impl.cache.SapAvailabilityCacheKey;

import de.hybris.bootstrap.annotations.UnitTest;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests the {@link SapAvailabilityCacheKey}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class SapAvailabilityCacheKeyTest {

    private static final String CACHE_NAME = "testCache";
    private static final String SALES_ORG_CODE = "salesOrgCode";

    @Mock
    DistSalesOrgModel salesOrg;

    @Before
    public void initCache() {
        MockitoAnnotations.initMocks(this);
        doReturn(SALES_ORG_CODE).when(salesOrg).getCode();

        final CacheManager cacheManager = CacheManager.getInstance();
        final Cache sapAvailabilityCache = new Cache(CACHE_NAME, 1000000, false, false, 3, 0);
        cacheManager.addCacheIfAbsent(sapAvailabilityCache);
    }

    @After
    public void removeCache() {
        CacheManager.getInstance().removeCache(CACHE_NAME);
    }

    @Test
    public void testSameKeys() {
        // init keys
        final SapAvailabilityCacheKey key1 = generateKey();
        final SapAvailabilityCacheKey key2 = generateKey();

        // action
        final Cache cache = CacheManager.getInstance().getCache(CACHE_NAME);
        cache.put(new Element(key1, "foo"));
        final Element elem = cache.get(key2);

        // evaluation
        assertEquals("keys don't map to same value", "foo", elem.getObjectValue());
    }

    @Test
    public void testKeysWithDifferentWarehouseOrder() {
        // init keys
        final SapAvailabilityCacheKey key1 = generateKey();
        final SapAvailabilityCacheKey key2 = generateKey();

        // action
        final Cache cache = CacheManager.getInstance().getCache(CACHE_NAME);
        cache.put(new Element(key1, "foo"));
        final Element elem = cache.get(key2);

        // evaluation
        assertEquals("keys don't map to same value", "foo", elem.getObjectValue());
    }

    @Ignore("this test tests the behaviour of ehcache only and takes some time")
    @Test
    public void testExpirationAfterTTL() throws Exception {
        // init key
        final SapAvailabilityCacheKey key1 = generateKey();

        // action
        final Cache cache = CacheManager.getInstance().getCache(CACHE_NAME);
        cache.put(new Element(key1, "foo"));
        Thread.sleep(3500);
        final Element elem = cache.get(key1);

        // evaluation
        assertNull("cached element is not expired", elem);
    }

    @Ignore("this test tests the behaviour of ehcache only and takes some time")
    @Test
    public void testExpirationAfterTTLWithCacheHit() throws Exception {
        // init key
        final SapAvailabilityCacheKey key1 = generateKey();

        // action
        final Cache cache = CacheManager.getInstance().getCache(CACHE_NAME);
        cache.put(new Element(key1, "foo"));
        Thread.sleep(2500);
        Element elem = cache.get(key1);
        Thread.sleep(1000);
        elem = cache.get(key1);

        // evaluation
        assertNull("cached element is not expired", elem);
    }

    private SapAvailabilityCacheKey generateKey() {
        return new SapAvailabilityCacheKey("1234", salesOrg);
    }

}
