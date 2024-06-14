/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.productfinder.converter;

import com.namics.distrelec.b2b.core.model.productfinder.DistProductFinderValueModel;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Converter for product finder values.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderValueDelegatingConverter implements Converter<DistProductFinderValueModel, DistProductFinderValueData> {

    @Autowired
    private TypeService typeService;

    private Map<String, Converter<DistProductFinderValueModel, DistProductFinderValueData>> converters;

    @Override
    public DistProductFinderValueData convert(final DistProductFinderValueModel source) {
        return getConverter(source).convert(source);
    }

    @Override
    public DistProductFinderValueData convert(final DistProductFinderValueModel source, final DistProductFinderValueData prototype) {
        return getConverter(source).convert(source, prototype);
    }

    private Converter<DistProductFinderValueModel, DistProductFinderValueData> getConverter(final DistProductFinderValueModel source) {
        for (final String key : converters.keySet()) {
            if (typeService.isAssignableFrom(key, source.getItemtype())) {
                return converters.get(key);
            }
        }

        throw new IllegalStateException("No appropriate converter for item type [" + source.getItemtype() + "] found");
    }

    public Map<String, Converter<DistProductFinderValueModel, DistProductFinderValueData>> getConverters() {
        return converters;
    }

    public void setConverters(final Map<String, Converter<DistProductFinderValueModel, DistProductFinderValueData>> converters) {
        this.converters = converters;
    }

}
