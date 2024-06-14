package com.distrelec.fusionsearch.response;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_ROOT_PATH_PATTERN;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.distrelec.fusion.search.dto.FusionDTO;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;

class QueryFilterUtil {

    private static final Logger LOG = LoggerFactory.getLogger(QueryFilterUtil.class);

    private QueryFilterUtil() {
        // protects from instancing
    }

    static String urlEncode(final String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Could not encode text [" + text + "]", e);
            return text;
        }
    }

    static boolean shouldRedirect(FusionDTO fusionDTO) {
        if (fusionDTO != null) {
            List<String> redirects = fusionDTO.getRedirect();
            return CollectionUtils.isNotEmpty(redirects) && redirects.size() > 0;
        } else {
            return false;
        }
    }

    static String buildQueryFilter(SearchQueryData searchQuery, String facetKey, String facetValue, boolean isSelected) {
        StringBuilder queryFilterBuilder = new StringBuilder();
        if (searchQuery != null) {
            List<SearchQueryTermData> filterTerms = searchQuery.getFilterTerms();
            filterTerms.stream()
                       .filter(searchQueryTerm -> !skipCategorySearchQueryTerm(searchQueryTerm, facetKey, isSelected))
                       .forEach(searchQueryTerm -> {
                           String searchQueryTermKey = searchQueryTerm.getKey();
                           String searchQueryTermValue = searchQueryTerm.getValue();
                           appendFilter(searchQueryTermKey, searchQueryTermValue, queryFilterBuilder);
                       });
        }
        return queryFilterBuilder.toString();
    }

    private static boolean skipCategorySearchQueryTerm(SearchQueryTermData searchQueryTerm, String facetKey, boolean isSelected) {
        if (isSelected) {

            String searchFacetKey = searchQueryTerm.getKey();
            if (CATEGORY_CODE_ROOT_PATH_PATTERN.matcher(facetKey)
                                               .matches()
                    && CATEGORY_CODE_ROOT_PATH_PATTERN.matcher(searchFacetKey)
                                                      .matches()) {
                int currentLevel = StringUtils.countMatches(facetKey, '/');
                int searchLevel = StringUtils.countMatches(searchFacetKey, '/');
                return currentLevel <= searchLevel;
            }
            return searchFacetKey.equals(facetKey);
        } else {
            return false;
        }
    }

    private static void appendFilter(String facetKey, String facetValue, StringBuilder queryFilterBuilder) {
        if (!queryFilterBuilder.isEmpty()) {
            queryFilterBuilder.append("&");
        }
        queryFilterBuilder.append("filter_").append(urlEncode(facetKey)).append("=").append(urlEncode(facetValue));
    }
}
