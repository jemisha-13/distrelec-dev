package com.namics.distrelec.b2b.facades.rendering.attributeconverters;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.rendering.attributeconverters.NavigationNodeToDataContentConverter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Locale;

public class DistNavigationNodeToDataContentConverter extends NavigationNodeToDataContentConverter {

    @Autowired
    @Qualifier("navNodesCacheManager")
    private CacheManager navNodesManager;

    @Autowired
    private I18NService i18NService;

    /**
     * Presumes single thread conversion. It is used to cache only root navigation nodes because same converters
     * are recursively called.
     */
    private final ThreadLocal<String> current = new ThreadLocal<>();

    @Override
    public NavigationNodeData convert(CMSNavigationNodeModel source) {
        String cacheKey = getCacheKey(source);
        Cache cache = navNodesManager.getCache("navNodes");
        SimpleValueWrapper cachedValue = (SimpleValueWrapper) cache.get(cacheKey);
        if (cachedValue != null) {
            return (NavigationNodeData) cachedValue.get();
        } else {
            if (current.get() == null) {
                try {
                    current.set(cacheKey);
                    NavigationNodeData nodeData = super.convert(source);
                    cache.put(cacheKey, nodeData);
                    return nodeData;
                } finally {
                    if (cacheKey.equals(current.get())) {
                        current.remove();
                    }
                }
            } else {
                return super.convert(source);
            }
        }
    }

    String getCacheKey(CMSNavigationNodeModel source) {
        Locale currentLocale = i18NService.getCurrentLocale();

        return new StringBuilder(source.getUid())
                       .append("-")
                       .append(source.getCatalogVersion().getCatalog().getId())
                       .append("-")
                       .append(source.getCatalogVersion().getVersion())
                       .append("-")
                       .append(currentLocale)
                       .toString();
    }
}
