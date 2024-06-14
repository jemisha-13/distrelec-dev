package com.namics.distrelec.b2b.backoffice.cockpitng.labels.impl;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Resource;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.CockpitConfigurationService;
import com.hybris.cockpitng.core.config.ConfigContext;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.Base;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.Labels;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.labels.LabelProvider;
import com.hybris.cockpitng.labels.impl.BackofficeCoreLabelService;
import com.namics.distrelec.b2b.backoffice.cockpitng.labels.DistLabelService;
import com.namics.distrelec.b2b.backoffice.constants.DistrelecB2BbackofficeConstants;

import de.hybris.platform.cache.impl.DefaultCache;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.regioncache.CacheController;
import de.hybris.platform.regioncache.key.legacy.LegacyCacheKey;
import de.hybris.platform.util.StandardDateRange;

public class DistBackofficeCoreLabelService extends BackofficeCoreLabelService implements DistLabelService {
    private static final Logger LOG = LoggerFactory.getLogger(DistBackofficeCoreLabelService.class);

    @Autowired
    @Qualifier("backofficeCacheManager")
    private CacheManager cacheManager;

    private Cache cache;

    @Autowired
    @Qualifier("cacheController")
    private CacheController cacheController;

    private CockpitConfigurationService cockpitConfigurationService;

    @Resource
    private CockpitLocaleService cockpitLocaleService;

    @Override
    public String getObjectDescription(Object object) {
        return super.getObjectDescription(object);
    }

    @Override
    public String getObjectLabel(Object object, String config) {
        return getLabelInternal(object, config, (provider) -> provider.getLabel(object), (labels) -> labels.getLabel());
    }

    @Override
    protected String getLabelInternal(Object object, Function<LabelProvider, String> provider,
                                      Function<Labels, String> handler) {
        return getLabelInternal(object, "base", provider, handler);
    }

    protected String getLabelInternal(Object object, String config, Function<LabelProvider, String> provider,
                                      Function<Labels, String> handler) {
        if (object != null && !(object instanceof String)) {
            // try to return label from cache
            LabelCacheKey labelCacheKey = null;
            Locale currentLocale = cockpitLocaleService.getCurrentLocale();

            if (object instanceof ItemModel) {
                ItemModel itemModel = (ItemModel) object;
                PK pk = itemModel.getPk();
                if (pk != null) {
                    Object[] key = { DefaultCache.CACHEKEY_JALOITEMCACHE, pk };
                    Object cachedObject = cacheController.get(new LegacyCacheKey(key, itemModel.getTenantId()));
                    if (cachedObject != null) {
                        labelCacheKey = new LabelCacheKey(cachedObject, currentLocale);
                    }
                }
            } else if (object instanceof HybrisEnumValue || object instanceof Locale || object instanceof Date
                    || object instanceof StandardDateRange || ClassUtils.isPrimitiveOrWrapper(object.getClass())
                    || object instanceof BigDecimal) {
                labelCacheKey = new LabelCacheKey(object, currentLocale);
            } else if (object instanceof FeatureValue) {
                FeatureValue featureValue = (FeatureValue) object;
                String fvCacheKey = featureValue.getProductFeaturePk().toString() + featureValue.getValue();
                labelCacheKey = new LabelCacheKey(fvCacheKey, currentLocale);
            } else {
                LOG.warn("Unsupported type " + object.getClass() + ": " + object);
            }

            if (labelCacheKey != null) {
                String cachedLabel = getCache().get(labelCacheKey, String.class);
                if (cachedLabel != null) {
                    // return cached label
                    return cachedLabel;
                }
            }

            String label = null;
            Labels labelConfig = this.getLabelConfiguration(object, config);
            if (labelConfig != null) {
                LabelProvider<Object> labelProvider = this.getLabelProvider(object, labelConfig);
                if (labelProvider != null) {
                    label = provider.apply(labelProvider);
                }

                if (labelConfig.getLabel() != null) {
                    Object value = this.getValueHandler().getValue(object, handler.apply(labelConfig), true);
                    if (value != null) {
                        label = value.toString();
                    }
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("{}{}", "No label configuration for type ", object.getClass().getName());
                }
            }

            // cache label
            if (label != null && labelCacheKey != null) {
                getCache().put(labelCacheKey, label);
                return label;
            }
        }

        String value = super.getLabelInternal(object, provider, handler);

        if (isEmpty(value) && object instanceof String) {
            return object.toString();
        }

        return value;
    }

    protected final Cache getCache() {
        if (cache == null) {
            cache = cacheManager.getCache(DistrelecB2BbackofficeConstants.BACKOFFICE_LABEL_CACHE);
        }
        return cache;
    }

    protected Labels getLabelConfiguration(Object object, String config) {
        Labels ret = null;

        try {
            ConfigContext configContext = new DefaultConfigContext(config, this.getType(object));
            ConfigContext context = this.buildConfigurationContext(configContext, Base.class);
            Base base = cockpitConfigurationService.loadConfiguration(context, Base.class);
            ret = base.getLabels();
        } catch (CockpitConfigurationException var5) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("No base configuration for type %s", object.getClass().getName()), var5);
            }
        }

        if (ret == null) {
            LOG.debug("{}{}", "No label configuration for type ", object.getClass().getName());
        }

        return ret;
    }

    @Required
    public void setCockpitConfigurationService(CockpitConfigurationService cockpitConfigurationService) {
        super.setCockpitConfigurationService(cockpitConfigurationService);
        this.cockpitConfigurationService = cockpitConfigurationService;
    }

    @Override
    public void setPermissionFacade(PermissionFacade permissionFacade) {
        WrappedPermissionFacade wrappedPermissionFacade = new WrappedPermissionFacade(permissionFacade);
        super.setPermissionFacade(wrappedPermissionFacade);
    }

    /**
     * wraps permission facade to not check permissions often use by this label server - because we handle permissions
     * differently and it should improve backoffice performances
     */
    class WrappedPermissionFacade implements PermissionFacade {

        private final PermissionFacade permissionFacade;

        WrappedPermissionFacade(PermissionFacade permissionFacade) {
            this.permissionFacade = permissionFacade;
        }

        @Override
        public boolean canReadType(String s) {
            return true;
        }

        @Override
        public boolean canChangeType(String s) {
            return permissionFacade.canChangeType(s);
        }

        @Override
        public boolean canReadInstanceProperty(Object o, String s) {
            return true;
        }

        @Override
        public boolean canReadProperty(String s, String s1) {
            return true;
        }

        @Override
        public boolean canChangeInstanceProperty(Object o, String s) {
            return permissionFacade.canChangeInstanceProperty(o, s);
        }

        @Override
        public boolean canChangeProperty(String s, String s1) {
            return permissionFacade.canChangeProperty(s, s1);
        }

        @Override
        public boolean canCreateTypeInstance(String s) {
            return permissionFacade.canCreateTypeInstance(s);
        }

        @Override
        public boolean canRemoveInstance(Object o) {
            return permissionFacade.canRemoveInstance(o);
        }

        @Override
        public boolean canReadInstance(Object o) {
            return true;
        }

        @Override
        public boolean canChangeInstance(Object o) {
            return permissionFacade.canChangeInstance(o);
        }

        @Override
        public boolean canRemoveTypeInstance(String s) {
            return permissionFacade.canRemoveTypeInstance(s);
        }

        @Override
        public boolean canChangeTypePermission(String s) {
            return permissionFacade.canChangeTypePermission(s);
        }

        @Override
        public boolean canChangePropertyPermission(String s, String s1) {
            return permissionFacade.canChangePropertyPermission(s, s1);
        }

        @Override
        public Set<Locale> getReadableLocalesForInstance(Object o) {
            return permissionFacade.getReadableLocalesForInstance(o);
        }

        @Override
        public Set<Locale> getWritableLocalesForInstance(Object o) {
            return permissionFacade.getWritableLocalesForInstance(o);
        }

        @Override
        public Set<Locale> getAllReadableLocalesForCurrentUser() {
            return permissionFacade.getAllReadableLocalesForCurrentUser();
        }

        @Override
        public Set<Locale> getAllWritableLocalesForCurrentUser() {
            return permissionFacade.getAllWritableLocalesForCurrentUser();
        }
    }
}
