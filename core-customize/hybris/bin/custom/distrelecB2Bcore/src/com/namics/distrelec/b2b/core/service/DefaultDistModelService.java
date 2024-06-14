/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.namics.distrelec.b2b.core.interceptor.AfterSaveInterceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;

/**
 * TODO intended to be used for incremental FactFinder delete webservice calls. if needed, add implementation in distfactfinder extension
 * and add bean for overriding defaultmodelservice.
 */
public class DefaultDistModelService extends DefaultModelService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistModelService.class);

    /** {@inheritDoc} */
    @Override
    public void save(final Object model) {
        super.save(model);
        afterSave(Collections.singletonList(model));
    }

    /** {@inheritDoc} */
    @Override
    public void saveAll() {
        final Set<Object> models = getObjectsInModelContext();
        super.saveAll();
        afterSave(models);
    }

    /** {@inheritDoc} */
    @Override
    public void saveAll(final Collection<? extends Object> models) {
        super.saveAll(models);
        afterSave(models);
    }

    /** {@inheritDoc} */
    @Override
    public void saveAll(final Object... models) {
        super.saveAll(models);
        afterSave(Arrays.asList(models));
    }

    private void afterSave(final Collection<? extends Object> models) {
        final Map<String, List<AfterSaveInterceptor>> afterSaveInterceptorCache = Maps.newHashMap();

        for (final Object model : models) {
            if (model != null) {
                final String type = getSourceTypeFromModel(model);
                List<AfterSaveInterceptor> interceptors = afterSaveInterceptorCache.get(type);
                if (interceptors == null) {
                    interceptors = getInterceptors(type);
                    afterSaveInterceptorCache.put(type, interceptors);
                }
                for (final AfterSaveInterceptor interceptor : interceptors) {
                    if (interceptor != null) {
                        try {
                            interceptor.afterSave(model);
                        } catch (final InterceptorException e) {
                            LOG.warn("AfterSaveInterceptor exception for model " + model);
                        }
                    }
                }
            }
        }
    }

    private List<AfterSaveInterceptor> getInterceptors(final String type) {
        final List<AfterSaveInterceptor> interceptors = new ArrayList<AfterSaveInterceptor>();
        for (final ValidateInterceptor validateInterceptor : getInterceptorRegistry().getValidateInterceptors(type)) {
            if (validateInterceptor instanceof AfterSaveInterceptor) {
                interceptors.add((AfterSaveInterceptor) validateInterceptor);
            }
        }
        return interceptors;
    }

    private Set<Object> getObjectsInModelContext() {
        final Set<Object> newOnes = getModelContext().getNew();
        final Set<Object> modifiedOnes = getModelContext().getModified();

        final Set<Object> toSave;
        if (newOnes.isEmpty()) {
            toSave = modifiedOnes;
        } else if (modifiedOnes.isEmpty()) {
            toSave = newOnes;
        } else {
            toSave = new LinkedHashSet<Object>(newOnes.size() + modifiedOnes.size());
            toSave.addAll(newOnes);
            toSave.addAll(modifiedOnes);
        }
        return toSave;
    }
}
