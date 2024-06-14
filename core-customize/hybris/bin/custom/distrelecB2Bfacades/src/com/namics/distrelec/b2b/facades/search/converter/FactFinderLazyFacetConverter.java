/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.google.common.base.Charsets;
import com.namics.hybris.ffsearch.data.facet.FactFinderLazyFacetData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;

/**
 * Converts {@link FactFinderLazyFacetData} POJOs.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class FactFinderLazyFacetConverter extends AbstractConverter<FactFinderLazyFacetData<SearchQueryData>, FactFinderLazyFacetData<SearchStateData>> {

    private static final Logger LOG = LoggerFactory.getLogger(FactFinderLazyFacetConverter.class);

    public static final String ADDITIONAL_FACET_PATH = "/facet";
    public static final String ADDITIONAL_FACET_PARAM_NAME = "additionalFacet";

    private Converter<SearchQueryData, SearchStateData> searchStateConverter;

    @Override
    protected FactFinderLazyFacetData<SearchStateData> createTarget() {
        return new FactFinderLazyFacetData<SearchStateData>();
    }

    @Override
    public void populate(final FactFinderLazyFacetData<SearchQueryData> source, final FactFinderLazyFacetData<SearchStateData> target) {
        target.setName(source.getName());
        target.setUnit(source.getUnit());
        setQuery(source, target);
    }

    private void setQuery(final FactFinderLazyFacetData<SearchQueryData> source, final FactFinderLazyFacetData<SearchStateData> target) {
        final SearchStateData searchState = getSearchStateConverter().convert(source.getQuery());

        final String newUrl = UriComponentsBuilder.fromUriString(searchState.getUrl()).path(ADDITIONAL_FACET_PATH)
                .queryParam(ADDITIONAL_FACET_PARAM_NAME, urlEncode(getFieldName(source))).build().toUriString();

        searchState.setUrl(newUrl);

        target.setQuery(searchState);
    }

    private String getFieldName(final FactFinderLazyFacetData<?> target) {
        if (StringUtils.isBlank(target.getUnit())) {
            return target.getName();
        } else if (target.getName() != null && target.getName().contains(FACTFINDER_UNIT_PREFIX)) {
            return target.getName();
        } else {
            return target.getName() + FACTFINDER_UNIT_PREFIX + target.getUnit();

        }
    }

    private static String urlEncode(final String value) {
        try {
            return URLEncoder.encode(value, Charsets.UTF_8.name());
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Could not encode value [" + value + "]", e);
            return value;
        }
    }

    public Converter<SearchQueryData, SearchStateData> getSearchStateConverter() {
        return searchStateConverter;
    }

    @Required
    public void setSearchStateConverter(final Converter<SearchQueryData, SearchStateData> searchStateConverter) {
        this.searchStateConverter = searchStateConverter;
    }

}
