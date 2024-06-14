/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.search.AdvisorStatusData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import de.hybris.platform.converters.impl.AbstractConverter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Decodes the data from a query string to a {@link SearchQueryData}.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public class SearchQueryDecoder extends AbstractConverter<String, SearchQueryData> {

    private static final Logger LOG = LoggerFactory.getLogger(SearchQueryDecoder.class);

    @Override
    public void populate(final String source, final SearchQueryData target) {
        if (StringUtils.isNotBlank(source)) {
            final List<SearchQueryTermData> terms = new ArrayList<SearchQueryTermData>();
            // we can't use with keyValue since this would overwrite duplicate keys
            final Iterator<String> keyValues = Splitter.on('&').split(source).iterator();
            if (keyValues.hasNext()) {
                target.setFreeTextSearch(urlDecode(keyValues.next()));
            }
            while (keyValues.hasNext()) {
                final SearchQueryTermData term = getTermData(keyValues.next());
                if (StringUtils.isNotBlank(term.getKey()) && term.getKey().startsWith(DistrelecfactfindersearchConstants.ADVISOR_CAMPAIGN_PARAMETER_PREFIX)) {
                    setAdvisorParameter(target, term);
                } else if (StringUtils.isNotBlank(term.getKey()) && term.getKey().equals(DistrelecfactfindersearchConstants.LOG)) {
                    target.setLog(StringUtils.isEmpty(term.getValue()) ? null : term.getValue());
                } else {
                    terms.add(term);
                }
            }
            target.setFilterTerms(terms);
        }
    }

    private SearchQueryTermData getTermData(final String keyValuePair) {
        // use substring and not splitting to ensure values keyValuePair like 'InStock=>= 1' work
        final String key = StringUtils.substringBefore(keyValuePair, "=");
        final String value = StringUtils.substringAfter(keyValuePair, "=");

        final SearchQueryTermData termData = new SearchQueryTermData();
        termData.setKey(urlDecode(key));
        termData.setValue(urlDecode(value));
        return termData;
    }

    private void setAdvisorParameter(final SearchQueryData target, final SearchQueryTermData term) {
        if (DistrelecfactfindersearchConstants.ADVISOR_CAMPAIGN_PARAMETER_ANSWER_PATH_NAME.endsWith(term.getKey())) {
            getAdvisorStatus(target).setAnswerPath(term.getValue());
        } else if (DistrelecfactfindersearchConstants.ADVISOR_CAMPAIGN_PARAMETER_CAMPAIGN_ID_NAME.endsWith(term.getKey())) {
            getAdvisorStatus(target).setCampaignId(term.getValue());
        }
    }

    private AdvisorStatusData getAdvisorStatus(final SearchQueryData target) {
        AdvisorStatusData advisorStatus = target.getAdvisorStatus();
        if (advisorStatus == null) {
            advisorStatus = new AdvisorStatusData();
            target.setAdvisorStatus(advisorStatus);
        }
        return advisorStatus;
    }

    private String urlDecode(final String value) {
        try {
            return URLDecoder.decode(value, Charsets.UTF_8.name());
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Unable to decode value [" + value + "]", e);
            return value;
        }
    }

    @Override
    protected SearchQueryData createTarget() {
        return new SearchQueryData();
    }
}
