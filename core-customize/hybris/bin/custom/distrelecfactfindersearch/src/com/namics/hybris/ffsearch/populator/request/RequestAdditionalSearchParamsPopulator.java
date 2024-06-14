/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.populator.common.CustomParameterHelper;

import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Populates the additional search parmeters.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class RequestAdditionalSearchParamsPopulator implements Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> {

    private static final String LOG_PARAMETER_NAME = "log";
    private final static String[] VALID_LOG_PARAMS = new String[] { DistrelecfactfindersearchConstants.LOG_NAVIGATION,
            DistrelecfactfindersearchConstants.LOG_INTERNAL };

    private static final String WEB_USE_ATTRIBUTE_COUNT_PARAMETER_NAME = "webuseAttributeCount";
    private static final String WEB_USE_ATTRIBUTE_COUNT_ALL_VALUE = "-1";

    private static final String ADDITIONAL_FACET_PARAMETER_NAME = "explicitlyRequestedAttribute";

    private static final String FF_FOLLOW_SEARCH_PARAMETER_ACTIVE = "ff.followsearchparameter.active";

    /**
     * Session attribute key for follow search param
     */
    public static final String FF_FOLLOW_SEARCH_PARAMETER = "followSearchParameter";

    private SessionService sessionService;

    @Override
    public void populate(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        initializeTarget(target);
        populateAdditionalSearchParams(source, target);
    }

    private void initializeTarget(final SearchRequest target) {
        if (target.getSearchParams() == null) {
            target.setSearchParams(new Params());
        }
    }

    private void populateAdditionalSearchParams(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        if (isValidLogParam(source.getSearchQueryData().getAdditionalSearchParams().getLog())) {
            CustomParameterHelper.addCustomParameter(
                    target,
                    CustomParameterHelper.createCustomParameter(LOG_PARAMETER_NAME,
                            Collections.singletonList(source.getSearchQueryData().getAdditionalSearchParams().getLog())));
        }

        // Add custom parameter to load all available facets (used by Product Finder)
        if (BooleanUtils.isTrue(source.getSearchQueryData().getAdditionalSearchParams().getDisableWebUseAttributeCount())) {
            CustomParameterHelper.addCustomParameter(
                    target,
                    CustomParameterHelper.createCustomParameter(WEB_USE_ATTRIBUTE_COUNT_PARAMETER_NAME,
                            Collections.singletonList(WEB_USE_ATTRIBUTE_COUNT_ALL_VALUE)));
        }

        // Add custom parameter to load additional facet
        if (StringUtils.isNotBlank(source.getSearchQueryData().getAdditionalSearchParams().getAdditionalFacetCode())) {
            CustomParameterHelper.addCustomParameter(
                    target,
                    CustomParameterHelper.createCustomParameter(ADDITIONAL_FACET_PARAMETER_NAME,
                            Collections.singletonList(source.getSearchQueryData().getAdditionalSearchParams().getAdditionalFacetCode())));
        }

        // Add all other custom parameter
        if (MapUtils.isNotEmpty(source.getSearchQueryData().getAdditionalSearchParams().getOtherParams())) {
            for (final Map.Entry<String, List<String>> param : source.getSearchQueryData().getAdditionalSearchParams().getOtherParams().entrySet()) {
                CustomParameterHelper.addCustomParameter(target, CustomParameterHelper.createCustomParameter(param.getKey(), param.getValue()));
            }
        }

        if (source.getSearchQueryData().isTracking()) {
            CustomParameterHelper.addCustomParameter(
                    target,
                    CustomParameterHelper.createCustomParameter("queryFromSuggest",
                            Collections.singletonList(String.valueOf(source.getSearchQueryData().isTracking()))));
        }

        if (getFFfollowSearchParameterActive().booleanValue()) {
            if (getFFfollowSearchParameter().length() > 0) {
                target.getSearchParams().setFollowSearch(Integer.valueOf(getFFfollowSearchParameter()));

            }
        }

    }

    private Boolean getFFfollowSearchParameterActive() {
        if (getSessionService().getAttribute(FF_FOLLOW_SEARCH_PARAMETER_ACTIVE) == null) {
            return Boolean.FALSE;
        }

        return getSessionService().getAttribute(FF_FOLLOW_SEARCH_PARAMETER_ACTIVE);
    }

    private String getFFfollowSearchParameter() {
        if (getSessionService().getAttribute(FF_FOLLOW_SEARCH_PARAMETER) == null) {
            setFFfollowSearchParameter("");
        }

        return getSessionService().getAttribute(FF_FOLLOW_SEARCH_PARAMETER);
    }

    private void setFFfollowSearchParameter(final String value) {
        getSessionService().setAttribute(FF_FOLLOW_SEARCH_PARAMETER, value);
    }

    private boolean isValidLogParam(final String log) {
        if (StringUtils.isNotBlank(log) && ArrayUtils.contains(VALID_LOG_PARAMS, log)) {
            return true;
        }
        return false;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

}
