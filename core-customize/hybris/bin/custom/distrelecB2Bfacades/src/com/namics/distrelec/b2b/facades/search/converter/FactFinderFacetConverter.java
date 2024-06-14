/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Populating Converter for {@link FactFinderFacetData} which populates the QUERY to STATE.
 *
 * @param <QUERY>
 * @param <STATE>
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class FactFinderFacetConverter<QUERY, STATE> extends AbstractPopulatingConverter<FactFinderFacetData<QUERY>, FactFinderFacetData<STATE>> {

    private Converter<FactFinderFacetValueData<QUERY>, FactFinderFacetValueData<STATE>> facetValueConverter;

    @Override
    public void populate(final FactFinderFacetData<QUERY> source, final FactFinderFacetData<STATE> target) {
        target.setCode(formatCode(source));
        target.setType(source.getType());
        target.setName(source.getName());
        target.setIsViable(source.getIsViable());
        target.setHasSelectedElements(source.getHasSelectedElements());
        target.setHasMinMaxFilters(source.getHasMinMaxFilters());
        if (source.getValues() != null) {
            target.setValues(Converters.convertAll(source.getValues(), getFacetValueConverter()));
        }
        target.setUnit(source.getUnit());
        super.populate(source, target);
    }

    private String formatCode(final FactFinderFacetData<QUERY> source) {
        return isNotBlank(source.getCode()) ? source.getCode().replace(' ', '+') : null;
    }

    @Override
    protected FactFinderFacetData<STATE> createTarget() {
        return new FactFinderFacetData<STATE>();
    }

    // BEGIN GENERATED CODE

    protected Converter<FactFinderFacetValueData<QUERY>, FactFinderFacetValueData<STATE>> getFacetValueConverter() {
        return facetValueConverter;
    }

    @Required
    public void setFacetValueConverter(final Converter<FactFinderFacetValueData<QUERY>, FactFinderFacetValueData<STATE>> facetValueConverter) {
        this.facetValueConverter = facetValueConverter;
    }
}
