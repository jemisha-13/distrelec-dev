/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.google.common.base.Charsets;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.FILTER;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.SUBSTRING_FILTER;

/**
 * Encodes the data from a {@link SearchQueryData} object to a query term, to be used as a query term in the URL.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class SearchQueryEncoder implements Converter<SearchQueryData, String> {

    private static final Logger LOG = LoggerFactory.getLogger(SearchQueryEncoder.class);

    private static final String EQUAL_SIGN = "%3D";

    @Override
    public String convert(final SearchQueryData source) {
        if (source == null) {
            return StringUtils.EMPTY;
        }
        final StringBuilder query = new StringBuilder();
        if (StringUtils.isNotBlank(source.getFreeTextSearch())) {
            query.append(urlEncode(source.getFreeTextSearch()));
        }
        if (isValidSortString(source.getSort())) {
            query.append("&sort=").append(source.getSort());
        }
        convertFilters(source, query);
        convertAdvisorStatus(source, query);
        return query.toString();
    }

    private void convertFilters(final SearchQueryData source, final StringBuilder builder) {
        final List<SearchQueryTermData> terms = source.getFilterTerms();
        if (CollectionUtils.isEmpty(terms)) {
            return;
        }
        for (final SearchQueryTermData term : terms) {
            if (StringUtils.isNotBlank(term.getKey()) && StringUtils.isNotBlank(term.getValue())) {
                addFilterTerm(term, builder);
            }
        }
    }

    private void addFilterTerm(final SearchQueryTermData term, final StringBuilder builder) {
        final String termKey = term.getKey();

        builder.append('&');

        if (Boolean.TRUE.equals(term.getSubstring())) {
            builder.append(SUBSTRING_FILTER);
        } else {
            builder.append(FILTER);
        }

        builder.append(urlEncode(termKey));
        builder.append('=');
        // unit/value combinations need to be separated by '=' such that FactFinder understands
        if (StringUtils.contains(term.getValue(), FACTFINDER_UNIT_PREFIX)) {
            String[] unitValuePair = new String[2];
            if (StringUtils.contains(term.getValue(), EQUAL_SIGN)) {
                unitValuePair = StringUtils.split(term.getValue(), EQUAL_SIGN);
            } else if (StringUtils.contains(term.getValue(), '=')) {
                unitValuePair = StringUtils.split(term.getValue(), '=');
            }
            final String unit = unitValuePair[0];
            final String value = unitValuePair[1];
            final String concat = urlEncode(unit) + "%253D" + urlEncode(value);
            builder.append(concat);
        } else {
            final String termValue = urlEncode(term.getValue());
            builder.append(termValue);
        }
    }

    private static String urlEncode(final String value) {
        try {
            return URLEncoder.encode(value, Charsets.UTF_8.name());
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Unable to encode value [" + value + "]", e);
            return value;
        }
    }

    private boolean isValidSortString(final String sortValue) {
        return StringUtils.isNotBlank(sortValue) && !StringUtils.contains(sortValue, DistrelecfactfindersearchConstants.UNDEFINED);
    }

    private void convertAdvisorStatus(final SearchQueryData source, final StringBuilder query) {
        if (source.getAdvisorStatus() != null) {
            if (StringUtils.isNotBlank(source.getAdvisorStatus().getAnswerPath())) {
                appendParameter(query, DistrelecfactfindersearchConstants.ADVISOR_CAMPAIGN_PARAMETER_ANSWER_PATH_NAME, source.getAdvisorStatus()
                        .getAnswerPath());
            }
            if (StringUtils.isNotBlank(source.getAdvisorStatus().getCampaignId())) {
                appendParameter(query, DistrelecfactfindersearchConstants.ADVISOR_CAMPAIGN_PARAMETER_CAMPAIGN_ID_NAME, source.getAdvisorStatus()
                        .getCampaignId());
            }
        }
    }

    private void appendParameter(final StringBuilder query, final String key, final String value) {
        if (query.length() > 0) {
            query.append('&');
        }
        query.append(urlEncode(key)).append('=').append(urlEncode(value));
    }

    @Override
    public String convert(final SearchQueryData source, final String prototype) {
        return convert(source);
    }
}
