/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.converter;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.service.sitemap.DistrelecSitemapException;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.ChangeFreq;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.WebSitemapUrl;

/**
 * Convert a List of String values where the values are in the following order (index)
 * <ul>
 * <li>0 - url</li>
 * <li>2 - lastMod</li>
 * <li>3 - changeFreq</li>
 * <li>4 - priority</li>
 * </ul>
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ContentListToWebSitemapUrlConverter implements WebSitemapUrlConverter<List<String>> {
    private static final Logger LOG = Logger.getLogger(ContentListToWebSitemapUrlConverter.class);

    @Override
    public WebSitemapUrl convert(final List<String> value) throws DistrelecSitemapException {
        if (value == null || value.size() < 1) {
            throw new DistrelecSitemapException("Map doesn't contain an url");
        }

        WebSitemapUrl.Options urlOptions;

        try {
            urlOptions = new WebSitemapUrl.Options(value.get(0));

            try {
                urlOptions.lastMod(value.get(1));
            } catch (final ParseException e) {
                LOG.warn("Could not parse last modified date", e);
            }

            if (StringUtils.isNotEmpty(value.get(2))) {
                urlOptions.changeFreq(ChangeFreq.valueOf(value.get(2)));
            }

            if (StringUtils.isNotEmpty(value.get(3))) {
                final Double priority = Double.valueOf(value.get(3));
                if (priority.doubleValue() >= 0 && priority.doubleValue() <= 1) {
                    urlOptions.priority(priority);
                }
            }

            return urlOptions.build();
        } catch (final MalformedURLException e) {
            throw new DistrelecSitemapException("Error parsing url", e);
        }
    }
}
