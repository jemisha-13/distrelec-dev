package com.namics.hybris.ffsearch.converter.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.session.impl.DefaultDistSession;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import com.namics.hybris.ffsearch.populator.common.CustomParameterHelper;
import com.namics.hybris.ffsearch.service.CookieSearchPortTypeWrapper;
import com.namics.hybris.ffsearch.service.impl.FactFinderAuthentication;

import de.factfinder.webservice.ws71.FFsearch.Result;
import de.factfinder.webservice.ws71.FFsearch.SearchResultStatus;
import de.factfinder.webservice.ws71.FFsearch.TrackingInformation;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.session.SessionService;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;

/**
 * Population Converter which uses the FactFinder instance to convert a {@link SearchRequest} to a {@link SearchResponse}. This is also
 * known as "executing a search request and fetching the result".
 */
public class SearchServiceConverter extends AbstractPopulatingConverter<SearchRequest, SearchResponse> {

    private static final Logger LOG = LogManager.getLogger(SearchServiceConverter.class);

    private static final String ALL_CATEGORY_SEARCH_CAMPAIGN_ID = "3007";

    public static final String FF_FOLLOW_SEARCH_PARAMETER = "followSearchParameter";

    public static final String FF_TRACKING_COOKIE = "f_fid";

    private FactFinderAuthentication authentication;

    private CommerceCategoryService commerceCategoryService;

    private CookieSearchPortTypeWrapper searchWebserviceClientWrapper;

    private SessionService sessionService;

    @Override
    public void populate(final SearchRequest source, final SearchResponse target) {

        target.setSearchRequest(source);
        try {

            final TrackingInformation trackingInformation = new TrackingInformation();
            String sessionId;
            if (source.getSessionId() != null) {
                sessionId = source.getSessionId();
            } else {
                sessionId = getFFId();
            }
            trackingInformation.setSessionID(sessionId);
            Result searchResult = searchWebserviceClientWrapper.getResult(source.getSearchParams(), source.getControlParams(),
                                                                          getAuthentication().getSearchAuth(), trackingInformation);
            if (checkSearchResultForActiveCatFilter(searchResult, source)) {
                source.getSearchParams().getFilters().getFilter().clear();
                List<String> values = new ArrayList<>();
                values.add(ALL_CATEGORY_SEARCH_CAMPAIGN_ID);
                CustomParameterHelper.addCustomParameter(source, CustomParameterHelper.createCustomParameter("CampaignTrigger", values));
                target.setSearchRequest(source);
                target.setFiltersRemovedGeneralSearch(true);
                searchResult = searchWebserviceClientWrapper.getResult(source.getSearchParams(), source.getControlParams(), getAuthentication().getSearchAuth(),
                                                                       trackingInformation);
            }
            target.setSearchResult(searchResult);
            target.setTimedOut(toBoolean(searchResult.isTimedOut()));

            if (searchResult.getSearchParams().getFollowSearch() != null) {
                setFFFollowSearchParameter(searchResult.getSearchParams().getFollowSearch().toString());
            }
        } catch (final Exception e) {
            final Result error = new Result();
            error.setResultStatus(SearchResultStatus.ERROR_OCCURED);
            target.setSearchResult(error);
            DistLogUtils.logError(LOG, "Failed to search in FactFinder \n SearchRequestParameters: [{}].\nwith Exception [{}].", e,
                                  ReflectionToStringBuilder.reflectionToString(source.getSearchParams(), ToStringStyle.MULTI_LINE_STYLE));
        }

        super.populate(source, target);
    }

    private String getFFId() {
        if (null != getSessionService().getCurrentSession()) {
            return getSessionService().getCurrentSession().getAttribute(FF_TRACKING_COOKIE);
        } else if (getSessionService().getCurrentSession() instanceof DefaultDistSession) {
            final DefaultDistSession defaultDistSession = (DefaultDistSession) getSessionService().getCurrentSession();
            return defaultDistSession.getJaloSession().getHttpSessionId();
        }
        return null;
    }

    private void setFFFollowSearchParameter(final String value) {
        getSessionService().setAttribute(FF_FOLLOW_SEARCH_PARAMETER, value);
    }

    private boolean checkSearchResultForActiveCatFilter(final Result searchResult, final SearchRequest source) {
        return source.getSearchType() != null && "text".equalsIgnoreCase(source.getSearchType().name())
               && (searchResult.getResultCount() == null || searchResult.getResultCount() <= 0)
               && source.getSearchParams().getFilters() != null
               && source.getSearchParams().getFilters().getFilter().get(0).getName().toLowerCase().contains("category");
    }

    @Override
    protected SearchResponse createTarget() {
        return new SearchResponse();
    }

    protected FactFinderAuthentication getAuthentication() {
        return authentication;
    }

    @Required
    public void setAuthentication(final FactFinderAuthentication authentication) {
        this.authentication = authentication;
    }

    public CommerceCategoryService getCommerceCategoryService() {
        return commerceCategoryService;
    }

    public void setCommerceCategoryService(CommerceCategoryService commerceCategoryService) {
        this.commerceCategoryService = commerceCategoryService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void setSearchWebserviceClientWrapper(CookieSearchPortTypeWrapper searchWebserviceClientWrapper) {
        this.searchWebserviceClientWrapper = searchWebserviceClientWrapper;
    }
}
