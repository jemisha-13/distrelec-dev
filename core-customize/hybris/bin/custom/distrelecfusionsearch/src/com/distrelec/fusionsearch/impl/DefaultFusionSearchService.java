package com.distrelec.fusionsearch.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.distrelec.fusionsearch.request.SearchRequestTuple;
import com.distrelec.fusionsearch.response.SearchResponseTuple;
import com.distrelec.solrfacetsearch.service.impl.DistFusionExportService;
import com.namics.distrelec.b2b.core.constants.DistConstants.Fusion;
import com.namics.distrelec.b2b.facades.search.FusionSearchService;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultFusionSearchService<ITEM extends ProductData> implements FusionSearchService<ITEM> {

    @Autowired
    private ConfigurationService configurationService;

    private Converter<SearchRequestTuple, MultiValuedMap<String, String>> fusionSearchRequestParamsConverter;

    private Converter<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> fusionSearchResponseConverter;

    private Converter<FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderProductSearchPageData<SearchStateData, ITEM>> productSearchPageConverter;

    private RestTemplate restTemplate;

    private Converter<SearchQueryPageableData<SearchQueryData>, SearchRequest> searchRequestConverter;

    private FactFinderSearchQueryDataMapper factFinderSearchQueryDataMapper;

    @Override
    public FactFinderProductSearchPageData<SearchStateData, ITEM> search(SearchQueryData searchQuery, PageableData pageableData) {
        factFinderSearchQueryDataMapper.updateFFParamsToFusionFormat(searchQuery);
        SearchQueryPageableData<SearchQueryData> searchQueryPageableData = buildSearchQueryPageableData(searchQuery, pageableData);
        SearchRequest searchRequest = searchRequestConverter.convert(searchQueryPageableData);
        SearchRequestTuple searchRequestTuple = new SearchRequestTupleImpl(searchQueryPageableData, searchRequest);
        MultiValuedMap<String, String> params = fusionSearchRequestParamsConverter.convert(searchRequestTuple);

        HttpEntity request = new HttpEntity(createSearchHttpHeader());
        String url = createURL(params);
        Map<String, String> paramsForUrl = paramsForUrl(params);
        ResponseEntity<SearchResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, request, SearchResponseDTO.class, paramsForUrl);

        SearchResponseDTO searchResponseDTO = response.getBody();
        SearchResponseTupleImpl searchResponseTuple = new SearchResponseTupleImpl(searchQueryPageableData, searchRequest, searchResponseDTO);

        FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> fusionSearchPageData = fusionSearchResponseConverter.convert(searchResponseTuple);
        FactFinderProductSearchPageData<SearchStateData, ITEM> searchPageData = productSearchPageConverter.convert(fusionSearchPageData);
        return searchPageData;
    }

    private SearchQueryPageableData<SearchQueryData> buildSearchQueryPageableData(SearchQueryData searchQuery, PageableData pageableData) {
        SearchQueryPageableData<SearchQueryData> searchQueryPageableData = new SearchQueryPageableData<>();
        searchQueryPageableData.setSearchQueryData(searchQuery);
        searchQueryPageableData.setPageableData(pageableData);
        return searchQueryPageableData;
    }

    private HttpHeaders createSearchHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(DistFusionExportService.HEADER_API_KEY, configurationService.getConfiguration().getString(Fusion.FUSION_SEARCH_API_KEY));
        return headers;
    }

    private String createURL(MultiValuedMap<String, String> params) {
        Configuration config = configurationService.getConfiguration();
        String searchUrl = config.getString(Fusion.FUSION_DIRECT_SEARCH_URL);
        String suffix = config.getString(Fusion.FUSION_PROFILE_SUFFIX);

        String url = searchUrl + "/search";
        if (StringUtils.isNotBlank(suffix)) {
            url += "_" + suffix;
        }
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        for (String paramKey : params.keys().uniqueSet()) {
            int valueId = 0;
            for (String paramValue : params.get(paramKey)) {
                uriBuilder.queryParam(paramKey, "{" + paramKey + valueId++ + "}");
            }
        }
        return uriBuilder.encode().toUriString();
    }

    private Map<String, String> paramsForUrl(MultiValuedMap<String, String> params) {
        Map<String, String> paramsForUrl = new HashMap<>();
        for (String paramKey : params.keys()) {
            int valueId = 0;
            Collection<String> paramValues = params.get(paramKey);
            for (String paramValue : paramValues) {
                paramsForUrl.put(paramKey + valueId++, paramValue);
            }
        }
        return paramsForUrl;
    }

    public void setFusionSearchRequestParamsConverter(Converter<SearchRequestTuple, MultiValuedMap<String, String>> fusionSearchRequestParamsConverter) {
        this.fusionSearchRequestParamsConverter = fusionSearchRequestParamsConverter;
    }

    public void setFusionSearchResponseConverter(Converter<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> fusionSearchResponseConverter) {
        this.fusionSearchResponseConverter = fusionSearchResponseConverter;
    }

    public void setProductSearchPageConverter(Converter<FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>, FactFinderProductSearchPageData<SearchStateData, ITEM>> productSearchPageConverter) {
        this.productSearchPageConverter = productSearchPageConverter;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setSearchRequestConverter(Converter<SearchQueryPageableData<SearchQueryData>, SearchRequest> searchRequestConverter) {
        this.searchRequestConverter = searchRequestConverter;
    }

    public void setFactFinderSearchQueryDataMapper(FactFinderSearchQueryDataMapper factFinderSearchQueryDataMapper) {
        this.factFinderSearchQueryDataMapper = factFinderSearchQueryDataMapper;
    }
}
