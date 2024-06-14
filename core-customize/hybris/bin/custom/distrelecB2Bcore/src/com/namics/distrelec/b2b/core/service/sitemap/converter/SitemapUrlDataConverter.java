/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.converter;

import com.namics.distrelec.b2b.core.service.sitemap.DistrelecSitemapException;
import com.namics.distrelec.b2b.core.service.sitemap.SitemapUrlData;

/**
 * WebSitemapUrlConverter.
 * 
 * @author fbersani, Distrelec AG
 * @since Namics Extensions 1.0
 * 
 * @param <V>
 */
public interface SitemapUrlDataConverter<V> {
    SitemapUrlData convert(V value) throws DistrelecSitemapException;
}
