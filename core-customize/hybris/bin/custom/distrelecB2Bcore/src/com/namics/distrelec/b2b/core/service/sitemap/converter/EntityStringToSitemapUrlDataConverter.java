/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.converter;

import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.service.sitemap.DistrelecSitemapException;
import com.namics.distrelec.b2b.core.service.sitemap.EntityNames;
import com.namics.distrelec.b2b.core.service.sitemap.SitemapUrlData;
import com.namics.distrelec.b2b.core.service.sitemap.utils.DistSitemapEncodeUtil;

/**
 * Convert a Entity (product, category or content page) value to an url <br>
 * An entity is a line of a query result compliant to the following pattern:<br>
 * <ul>
 * <li>position 0: url</li>
 * <li>position 1: code</li>
 * <li>position 2: language</li>
 * <li>position 3: defaultLanguage</li>
 * <li>position 4: lastModifiedDate</li>
 * </ul>
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class EntityStringToSitemapUrlDataConverter implements SitemapUrlDataConverter<List<String>> {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(EntityStringToSitemapUrlDataConverter.class);

    @Override
    public SitemapUrlData convert(final List<String> value) throws DistrelecSitemapException {
        if (value == null) {
            throw new DistrelecSitemapException("Map doesn't contain an url");
        }

        final String entityName = StringUtils.isBlank(value.get(5)) ? null : value.get(5);
        String url = encodeURL(value, entityName);

        final SitemapUrlData result = new SitemapUrlData();
        result.setUrl(url);
        result.setCode(value.get(1));
        result.setLanguage(value.get(2));
        result.setDefaultLanguage(value.get(3));
        result.setLastModified(StringUtils.isBlank(value.get(4)) ? null : value.get(4));
        result.setEntity(StringUtils.isBlank(value.get(5)) ? null : value.get(5));
        return result;
    }

    private String encodeURL(final List<String> value, final String entityName) {
        String url = value.get(0);

        try {
            if (entityName.equals(EntityNames.CATEGORY.name())) {
                url = DistSitemapEncodeUtil.sanitizeUrlForCategory(url);
            } else if (entityName.equals(EntityNames.PRODUCT.name())) {
                url = DistSitemapEncodeUtil.sanitizeUrlForCategory(url);
            } else if (entityName.equals(EntityNames.MANUFACTURER.name())) {
                url = DistSitemapEncodeUtil.sanitizeUrlForManufacturer(url);
            }
        } catch (final MalformedURLException ex) {
            LOG.error("there were problem while encoding this url: " + url);
        }
        return url;
    }
}
