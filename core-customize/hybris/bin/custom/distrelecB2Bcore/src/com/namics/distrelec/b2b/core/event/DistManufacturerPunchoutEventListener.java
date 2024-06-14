package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.Objects;

public class DistManufacturerPunchoutEventListener extends AbstractEventListener<DistManufacturerPunchoutEvent> {

    @Autowired
    private EhCacheCacheManager manufacturerMenuCacheManager;

    @Override
    protected void onEvent(DistManufacturerPunchoutEvent distManufacturerPunchoutEvent) {
        distManufacturerPunchoutEvent.getCmsSites()
                .forEach(cmsSite -> Objects.requireNonNull(manufacturerMenuCacheManager.getCache("manufacturerMenu")).evictIfPresent(cmsSite.getUid()));
    }
}
