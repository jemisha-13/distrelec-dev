/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.converter;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.service.sitemap.DistrelecSitemapException;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.WebSitemapUrl;

/**
 * Convert a String value to an url
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class CategoryStringToWebSitemapUrlConverter implements WebSitemapUrlConverter<List<String>> {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(CategoryStringToWebSitemapUrlConverter.class);

    @Override
    public WebSitemapUrl convert(final List<String> value) throws DistrelecSitemapException {
        if (value == null) {
            throw new DistrelecSitemapException("Map doesn't contain an url");
        }
        WebSitemapUrl.Options urlOptions = null;
        try {
            urlOptions = new WebSitemapUrl.Options(value.get(0));
            if (value.get(1) != null) {
                urlOptions.lastMod(value.get(1));
            }
        } catch (final ParseException e) {
            LOG.warn("Could not parse last modified date", e);
        } catch (final MalformedURLException e) {
            throw new DistrelecSitemapException("Error parsing url", e);
        }
        return urlOptions.build();
    }
}
